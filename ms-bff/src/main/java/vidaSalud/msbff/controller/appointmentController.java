package vidaSalud.msbff.controller;

import vidaSalud.msbff.client.appointmentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController 
@RequestMapping ("/api/bff/appointments")
public class appointmentController {
    @Autowired 
    private appointmentClient appointmentsClient;

    @GetMapping 
    public ResponseEntity<Object> getAllAppointments() {
        Object response = appointmentsClient.getAllAppointments();
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<Object> getAppointmentById(@PathVariable ("id") Long id) {
        Object response = appointmentsClient.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping 
    public ResponseEntity<Object> createAppointment(@RequestBody Object request) {
        Object response = appointmentsClient.createAppointment(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping ("/{id}/status")
    public ResponseEntity<Object> updateAppointment(@PathVariable ("id") Long id, @RequestBody Object request) {
        Object response = appointmentsClient.updateAppointment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable ("id") Long id) {
        appointmentsClient.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

}
