package modelo;

import java.time.LocalDate;

public class EspecialidadMed {
    private String nombre;
    private final LocalDate fechaCrea;
    private LocalDate fechaBaja;

    public EspecialidadMed(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la especialidad medica es obligatorio");
        }
        this.nombre = nombre.trim();
        this.fechaCrea = LocalDate.now();
    }

    public String getNombre() { return nombre; }
    public LocalDate getFechaCrea() { return fechaCrea; }
    public LocalDate getFechaBaja() { return fechaBaja; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la especialidad medica es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public void eliminar() {
        this.fechaBaja = LocalDate.now();
    }

    public boolean estaActiva() {
        return fechaBaja == null;
    }

    @Override
    public String toString() {
        return "EspecialidadMed{" +
                "nombre='" + nombre + '\'' +
                ", activa=" + estaActiva() +
                '}';
    }
}
