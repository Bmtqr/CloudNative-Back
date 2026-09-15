package appointments.repository;

import appointments.model.Appointment;
import appointments.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Búsqueda con filtros según requerimiento de la pauta
    List<Appointment> findByStatusAndAppointmentDateBetween(
        AppointmentStatus status, 
        LocalDateTime from, 
        LocalDateTime to
    );
    
    List<Appointment> findByStatus(AppointmentStatus status);
}