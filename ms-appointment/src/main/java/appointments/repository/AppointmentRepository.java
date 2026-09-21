package appointments.repository;

import appointments.model.Appointment;
import appointments.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByStatusAndAppointmentDateBetween(AppointmentStatus status, LocalDate from, LocalDate to);
    
    List<Appointment> findByStatus(AppointmentStatus status);
}