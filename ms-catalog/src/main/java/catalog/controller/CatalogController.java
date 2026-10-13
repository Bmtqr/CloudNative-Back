package catalog.controller;

import catalog.model.MedicalService;
import catalog.repository.MedicalServiceRepository;
import catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog/services")
@RequiredArgsConstructor
public class CatalogController {

    private final MedicalServiceRepository medicalServiceRepository;

    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<MedicalService>> getServices() {
        return ResponseEntity.ok(catalogService.getAllServices());
    }

    @PostMapping
    public ResponseEntity<MedicalService> createService(@RequestBody MedicalService medicalService) {
        MedicalService created = catalogService.createService(medicalService);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> body) {
        try {
            Double price = body.containsKey("price") ? Double.valueOf(body.get("price").toString()) : null;
            Integer quota = body.containsKey("quota") ? Integer.valueOf(body.get("quota").toString()) : null;

            MedicalService updated = catalogService.updateServiceAndQuota(id, price, quota);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/decrease-quota")
    public ResponseEntity<?> decreaseQuota(@PathVariable Long id) {
        try {
            Long boxId = catalogService.decreaseQuota(id);
            return ResponseEntity.ok(Map.of("boxId", boxId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        if (!medicalServiceRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        medicalServiceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

