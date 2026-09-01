package catalog.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "service_quotas")
@Data
public class service_quota {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
}
