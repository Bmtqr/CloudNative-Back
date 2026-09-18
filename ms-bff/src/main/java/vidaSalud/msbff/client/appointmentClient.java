package vidaSalud.msbff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient (name = "appointments-client", url = "${appointments.url}")
public interface appointmentClient {

    @GetMapping ("/api/appointments") 
    Object getAllAppointments();

    @GetMapping ("/api/appointments/{id}")
    Object getAppointmentById(@PathVariable ("id") Long id);

    @PostMapping ("/api/appointments")
    Object createAppointment(@RequestBody Object request);

    @PutMapping ("/api/appointments/{id}/status") /*Pendiente */
    Object updateAppointment(@PathVariable ("id") Long id, @RequestBody Object request);

    @DeleteMapping ("/api/appointments/{id}")
    void deleteAppointment(@PathVariable ("id") Long id);

}
