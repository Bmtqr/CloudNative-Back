package report.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_METRIC_STATUS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusMetric {

    @Id
    @Column(name = "STATUS_NAME", nullable = false, unique = true)
    private String statusName;

    @Column(name = "TOTAL_COUNT", nullable = false)
    private Long totalCount;

    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;
}