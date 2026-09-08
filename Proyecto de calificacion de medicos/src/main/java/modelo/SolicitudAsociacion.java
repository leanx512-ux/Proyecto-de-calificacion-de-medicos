package modelo;

public class SolicitudAsociacion {
    private final Medico medico;
    private final Clinica clinica;
    private final String motivo;
    private boolean aceptada;

    public SolicitudAsociacion(Medico medico, Clinica clinica, String motivo) {
        this.medico = medico;
        this.clinica = clinica;
        this.motivo = motivo;
        this.aceptada = false;
    }

    public Medico getMedico() {
        return medico;
    }

    public Clinica getClinica() {
        return clinica;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public void aceptar() {
        this.aceptada = true;
    }
}
