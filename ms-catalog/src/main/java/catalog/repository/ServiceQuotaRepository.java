package catalog.repository;

import catalog.model.ServiceQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceQuotaRepository extends JpaRepository<ServiceQuota, Long> {
    
    // Método para buscar el cupo específico de una prestación
    Optional<ServiceQuota> findByMedicalServiceId(Long medicalServiceId);
}