package report.model;

import java.time.LocalDateTime;

public class AppointmentEvent {
    private String eventId;
    private String appointmentId;
    private String status; // SOLICITADA, CONFIRMADA, EN_ESPERA, EN_ATENCIÓN, CERRADA, CANCELADA
    private String serviceId;
    private String timestamp;

    public AppointmentEvent() {}

    // Getters y Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}