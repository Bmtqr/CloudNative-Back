package catalog.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "boxes")
@Data
public class box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "box_number")
    private String boxNumber;

    @Column(name = "box_type")
    private String boxType;

    @Column(name = "is_available")
    private Boolean isAvailable;
    
}
