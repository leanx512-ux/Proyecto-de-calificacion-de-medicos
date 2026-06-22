package modelo;

import java.time.LocalDate;

public class SolicitudAsociacion {
    private final Clinica clinica;
    private final Medico medico;
    private final LocalDate fechaSolicitud;
    private EstadoSolicitudAsociacion estado;

    public SolicitudAsociacion(Clinica clinica, Medico medico) {
        this.clinica = clinica;
        this.medico = medico;
        this.fechaSolicitud = LocalDate.now();
        this.estado = EstadoSolicitudAsociacion.PENDIENTE;
    }

    public Clinica getClinica() { return clinica; }
    public Medico getMedico() { return medico; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public EstadoSolicitudAsociacion getEstado() { return estado; }

    public void aceptar() {
        this.estado = EstadoSolicitudAsociacion.ACEPTADA;
        clinica.asociarMedico(medico.getMail());
    }

    public void rechazar() {
        this.estado = EstadoSolicitudAsociacion.RECHAZADA;
    }

    @Override
    public String toString() {
        return "SolicitudAsociacion{" +
                "clinica=" + clinica.getNombre() +
                ", medico=" + medico.getNombre() + " " + medico.getApellido() +
                ", estado=" + estado +
                '}';
    }
}
