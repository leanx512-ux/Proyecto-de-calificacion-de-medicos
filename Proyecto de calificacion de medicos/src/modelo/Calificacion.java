package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Calificacion {
    private final Paciente paciente;
    private final Medico medico;
    private final String comentario;
    private final int puntAtencion;
    private final int puntComprension;
    private final int puntPuntualidad;
    private final int puntProm;
    private final LocalDate fecha;
    private EstadoValoracion estado;
    private final List<String> imagenes = new ArrayList<>();

    public Calificacion(Paciente paciente, Medico medico, String comentario,
                        int puntAtencion, int puntComprension, int puntPuntualidad) {
        validarPuntaje(puntAtencion);
        validarPuntaje(puntComprension);
        validarPuntaje(puntPuntualidad);
        this.paciente = paciente;
        this.medico = medico;
        this.comentario = comentario;
        this.puntAtencion = puntAtencion;
        this.puntComprension = puntComprension;
        this.puntPuntualidad = puntPuntualidad;
        this.puntProm = (puntAtencion + puntComprension + puntPuntualidad) / 3;
        this.fecha = LocalDate.now();
        this.estado = EstadoValoracion.PENDIENTE;
    }

    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public String getComentario() { return comentario; }
    public int getPuntAtencion() { return puntAtencion; }
    public int getPuntComprension() { return puntComprension; }
    public int getPuntPuntualidad() { return puntPuntualidad; }
    public int getPuntProm() { return puntProm; }
    public LocalDate getFecha() { return fecha; }
    public EstadoValoracion getEstado() { return estado; }
    public List<String> getImagenes() { return imagenes; }

    public void agregarImagen(String rutaArchivo) {
        if (rutaArchivo != null && !rutaArchivo.isBlank() && imagenes.size() < 6) {
            imagenes.add(rutaArchivo.trim());
        }
    }

    private void validarPuntaje(int puntaje) {
        if (puntaje < 1 || puntaje > 5) {
            throw new IllegalArgumentException("Los puntajes deben estar entre 1 y 5 estrellas");
        }
    }

    public void setEstado(EstadoValoracion estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Calificacion{" +
                "paciente=" + paciente.getNombre() +
                ", medico=" + medico.getNombre() +
                ", puntProm=" + puntProm +
                ", estado=" + estado +
                '}';
    }
}
