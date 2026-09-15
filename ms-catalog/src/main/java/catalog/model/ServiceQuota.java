package catalog.model;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "service_quotas")
@Data
public class ServiceQuota {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la prestación (MedicalService)
    @ManyToOne
    @JoinColumn(name = "service_id")
    private MedicalService medicalService;

    // Relación con el box
    @ManyToOne
    @JoinColumn(name = "box_id")
    private Box box;

    // Cantidad de cupos disponibles
    @Column(name = "available_quotas")
    private Integer availableQuotas;
}