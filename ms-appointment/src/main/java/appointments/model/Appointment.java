package appointments.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ATENCIONES")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PATIENT_ID", nullable = false)
    private String patientId;

    @Column(name = "SERVICE_ID", nullable = false)
    private Long serviceId;

    @Column(name = "BOX_ID")
    private Long boxId;

    @Column(name = "APPOINTMENT_DATE", nullable = false)
    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AppointmentStatus status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = AppointmentStatus.SOLICITADA;
        }
    }

}
