package catalog.controller;

import catalog.model.Box;
import catalog.model.MedicalService;
import catalog.model.ServiceQuota;
import catalog.repository.BoxRepository;
import catalog.repository.MedicalServiceRepository;
import catalog.repository.ServiceQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog/admin")
@RequiredArgsConstructor
public class CatalogAdminController {

    private final BoxRepository boxRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final ServiceQuotaRepository quotaRepository;

    @PostMapping("/boxes")
    public ResponseEntity<Box> createBox(@RequestBody Box box) {
        Box saved = boxRepository.save(box);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/boxes")
    public ResponseEntity<List<Box>> getAllBoxes() {
        return ResponseEntity.ok(boxRepository.findAll());
    }

    @GetMapping("/boxes/{id}")
    public ResponseEntity<?> getBoxById(@PathVariable Long id) {
        return boxRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/boxes/{id}")
    public ResponseEntity<?> updateBox(@PathVariable Long id, @RequestBody Box boxDetails) {
        return boxRepository.findById(id).map(existingBox -> {
            if (boxDetails.getBoxNumber() != null) {
                existingBox.setBoxNumber(boxDetails.getBoxNumber());
            }
            if (boxDetails.getBoxType() != null) {
                existingBox.setBoxType(boxDetails.getBoxType());
            }
            if (boxDetails.getIsAvailable() != null) {
                existingBox.setIsAvailable(boxDetails.getIsAvailable());
            }
            Box updated = boxRepository.save(existingBox);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/boxes/{id}")
    public ResponseEntity<?> deleteBox(@PathVariable Long id) {
        if (!boxRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        boxRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }




    @PostMapping("/quotas")
    public ResponseEntity<?> createQuota(@RequestBody Map<String, Object> payload) {
        Object serviceIdObj = payload.get("serviceId");
        Object boxIdObj = payload.get("boxId");
        Object quotasObj = payload.get("availableQuotas");

        if (serviceIdObj == null || boxIdObj == null || quotasObj == null) {
            return ResponseEntity.badRequest().body("Los campos 'serviceId', 'boxId' y 'availableQuotas' son obligatorios.");
        }

        Long serviceId = Long.valueOf(serviceIdObj.toString());
        Long boxId = Long.valueOf(boxIdObj.toString());
        Integer availableQuotas = Integer.valueOf(quotasObj.toString());

        MedicalService service = medicalServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + serviceId));

        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new RuntimeException("Box no encontrado con ID: " + boxId));

        ServiceQuota quota = quotaRepository.findByMedicalServiceId(serviceId)
                .orElseGet(ServiceQuota::new);

        quota.setMedicalService(service);
        quota.setBox(box);
        quota.setAvailableQuotas(availableQuotas);

        return ResponseEntity.status(HttpStatus.CREATED).body(quotaRepository.save(quota));
    }

    @GetMapping("/quotas")
    public ResponseEntity<List<ServiceQuota>> getAllQuotas() {
        return ResponseEntity.ok(quotaRepository.findAll());
    }
    @GetMapping("/quotas/{id}")
    public ResponseEntity<?> getQuotaById(@PathVariable Long id) {
        return quotaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/quotas/service/{serviceId}")
    public ResponseEntity<?> getQuotaByServiceId(@PathVariable Long serviceId) {
        return quotaRepository.findByMedicalServiceId(serviceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/quotas/{id}")
    public ResponseEntity<?> updateQuota(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return quotaRepository.findById(id).map(quota -> {
            if (payload.containsKey("availableQuotas")) {
                quota.setAvailableQuotas(Integer.valueOf(payload.get("availableQuotas").toString()));
            }
            if (payload.containsKey("boxId")) {
                Long boxId = Long.valueOf(payload.get("boxId").toString());
                Box box = boxRepository.findById(boxId)
                        .orElseThrow(() -> new RuntimeException("Box no encontrado con ID: " + boxId));
                quota.setBox(box);
            }
            if (payload.containsKey("serviceId")) {
                Long serviceId = Long.valueOf(payload.get("serviceId").toString());
                MedicalService service = medicalServiceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + serviceId));
                quota.setMedicalService(service);
            }
            ServiceQuota updated = quotaRepository.save(quota);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/quotas/{id}")
    public ResponseEntity<?> deleteQuota(@PathVariable Long id) {
        if (!quotaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        quotaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}