package appointments.service;
import appointments.model.Appointment;
import appointments.model.AppointmentStatus;
import appointments.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public Appointment createAppointment(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.SOLICITADA);
        return repository.save(appointment);
    }

    public Optional<Appointment> getById(Long id) {
        return repository.findById(id);
    }

    public List<Appointment> getAll(AppointmentStatus status, LocalDateTime from, LocalDateTime to) {
        if (status != null && from != null && to != null) {
            return repository.findByStatusAndAppointmentDateBetween(status, from, to);
        } else if (status != null) {
            return repository.findByStatus(status);
        }
        return repository.findAll();
    }

    public Appointment updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada con ID: " + id));

        // Regla clave: No se puede pasar a EN_ATENCIÓN sin CONFIRMAR
        if (newStatus == AppointmentStatus.EN_ATENCION && appointment.getStatus() != AppointmentStatus.CONFIRMADA) {
            throw new IllegalStateException("No se puede pasar al estado EN_ATENCIÓN si la atención no fue CONFIRMADA previamente.");
        }

        appointment.setStatus(newStatus);
        return repository.save(appointment);
    }
}