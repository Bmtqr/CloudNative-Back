package catalog.service;

import catalog.model.Box;
import catalog.model.MedicalService;
import catalog.model.ServiceQuota;
import catalog.repository.BoxRepository;
import catalog.repository.MedicalServiceRepository;
import catalog.repository.ServiceQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final MedicalServiceRepository serviceRepository;
    private final ServiceQuotaRepository quotaRepository;
    private final BoxRepository boxRepository;

    @Transactional(readOnly = true)
    public List<MedicalService> getAllServices() {
        return serviceRepository.findAll();
    }

    @Transactional
    public MedicalService createService(MedicalService medicalService) {
        medicalService.setIsActive(true);
        return serviceRepository.save(medicalService);
    }

    @Transactional
    public MedicalService updateServiceAndQuota(Long id, Double price, Integer additionalQuotas) {
        MedicalService medicalService = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestación no encontrada"));
        
        if (price != null) {
            medicalService.setPrice(price);
            serviceRepository.save(medicalService);
        }

        if (additionalQuotas != null) {
            ServiceQuota quota = quotaRepository.findByMedicalServiceId(id)
                    .orElseGet(() -> {
                        ServiceQuota newQuota = new ServiceQuota();
                        newQuota.setMedicalService(medicalService);
                        
                        // Asignar primer box disponible si existe en BD
                        Optional<Box> defaultBox = boxRepository.findAll().stream().findFirst();
                        defaultBox.ifPresent(newQuota::setBox);
                        
                        return newQuota;
                    });

            quota.setAvailableQuotas(additionalQuotas);
            quotaRepository.save(quota);
        }

        return medicalService;
    }
    
    @Transactional
    public boolean deleteService(Long id) {
        return serviceRepository.findById(id).map(service -> {
            service.setIsActive(false);
            serviceRepository.save(service);
            return true;
        }).orElse(false);
    }

    @Transactional
    public Long decreaseQuota(Long serviceId) {
        ServiceQuota quota = quotaRepository.findByMedicalServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException("Cupos no encontrados para esta prestación"));

        if (quota.getAvailableQuotas() <= 0) {
            throw new IllegalStateException("No hay cupos disponibles");
        }

        quota.setAvailableQuotas(quota.getAvailableQuotas() - 1);
        quotaRepository.save(quota);

        return (quota.getBox() != null) ? quota.getBox().getId() : 1L;
    }
}