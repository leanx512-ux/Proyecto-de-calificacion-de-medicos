package modelo;

import java.time.LocalDate;

public class Reclamo {
    private final Medico medico;
    private final Calificacion calificacion;
    private final String descripcion;
    private final LocalDate fechaIni;
    private LocalDate fechaFin;
    private EstadoReclamo resultado;

    public Reclamo(Medico medico, Calificacion calificacion, String descripcion) {
        this.medico = medico;
        this.calificacion = calificacion;
        this.descripcion = descripcion;
        this.fechaIni = LocalDate.now();
        this.resultado = EstadoReclamo.PENDIENTE;
    }

    public Medico getMedico() {
        return medico;
    }

    public Calificacion getCalificacion() {
        return calificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaIni() {
        return fechaIni;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public EstadoReclamo getResultado() {
        return resultado;
    }

    public void aceptar() {
        this.resultado = EstadoReclamo.ACEPTADO;
        this.fechaFin = LocalDate.now();
    }

    public void rechazar() {
        this.resultado = EstadoReclamo.RECHAZADO;
        this.fechaFin = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Reclamo{" +
                "medico=" + medico.getNombre() +
                ", resultado=" + resultado +
                '}';
    }
}