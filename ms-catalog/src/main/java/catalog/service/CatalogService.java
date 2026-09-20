package catalog.service;

import catalog.model.MedicalService;
import catalog.model.ServiceQuota;
import catalog.repository.MedicalServiceRepository;
import catalog.repository.ServiceQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final MedicalServiceRepository serviceRepository;
    private final ServiceQuotaRepository quotaRepository;

    // Obtener todas las prestaciones
    public List<MedicalService> getAllServices() {
        return serviceRepository.findAll();
    }

    // Crear una prestación
    public MedicalService createService(MedicalService medicalService) {
        // Por defecto, al crear, la dejamos activa
        medicalService.setIsActive(true);
        return serviceRepository.save(medicalService);
    }

    // Actualizar precio y cupos (Requerimiento específico del PDF)
    public MedicalService updateServiceAndQuota(Long id, Double price, Integer additionalQuotas) {
        // 1. Actualizar el precio de la prestación
        MedicalService medicalService = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestación no encontrada"));
        
        if (price != null) {
            medicalService.setPrice(price);
            serviceRepository.save(medicalService);
        }

        // 2. Actualizar los cupos asociados (si se envían en la petición)
        if (additionalQuotas != null) {
            Optional<ServiceQuota> quotaOpt = quotaRepository.findByMedicalServiceId(id);
            if (quotaOpt.isPresent()) {
                ServiceQuota quota = quotaOpt.get();
                quota.setAvailableQuotas(additionalQuotas);
                quotaRepository.save(quota);
            }
        }

        return medicalService;
    }

    // Método para disminuir un cupo (será llamado por ms-appointments)
    public Long decreaseQuota(Long serviceId) {
        ServiceQuota quota = quotaRepository.findByMedicalServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException("Cupos no encontrados para esta prestación"));

        if (quota.getAvailableQuotas() <= 0) {
            throw new IllegalStateException("No hay cupos disponibles");
        }

        quota.setAvailableQuotas(quota.getAvailableQuotas() - 1);
        quotaRepository.save(quota);

        return quota.getBox() != null ? quota.getBox().getId() : null;
    }
}