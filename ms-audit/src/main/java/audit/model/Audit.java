package audit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_AUDIT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EVENT_ID")
    private String eventId;

    @Column(name = "AGGREGATE_ID", nullable = false)
    private String aggregateId;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Column(name = "NEW_STATUS")
    private String newStatus;

    @Column(name = "EXECUTED_BY")
    private String executedBy;

    @Column(name = "EVENT_TIMESTAMP")
    private String eventTimestamp;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}