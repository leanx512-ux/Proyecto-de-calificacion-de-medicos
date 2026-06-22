package modelo;

import java.util.ArrayList;
import java.util.List;

public class Medico extends Usuario {
    private String matricula;
    private String dni;
    private String certificado;
    private Double puntPromMed;
    private final List<String> especialidades = new ArrayList<>();

    public Medico(String mail, String contrasena, String telefono, String nombre, String apellido,
                  String matricula, String dni, String certificado) {
        super(mail, contrasena, telefono, nombre, apellido, Rol.MEDICO);
        this.matricula = matricula;
        this.dni = dni;
        this.certificado = certificado;
    }

    public String getMatricula() { return matricula; }
    public String getDni() { return dni; }
    public String getCertificado() { return certificado; }
    public Double getPuntPromMed() { return puntPromMed; }
    public List<String> getEspecialidades() { return especialidades; }

    public void agregarEspecialidad(String especialidad) {
        if (especialidad != null && !especialidad.isBlank()
                && especialidades.stream().noneMatch(e -> e.equalsIgnoreCase(especialidad.trim()))) {
            especialidades.add(especialidad.trim());
        }
    }

    public void quitarEspecialidad(String especialidad) {
        if (especialidad != null) {
            especialidades.removeIf(e -> e.equalsIgnoreCase(especialidad.trim()));
        }
    }

    public void recalcularPromedio(List<Calificacion> calificaciones) {
        double suma = 0;
        int contador = 0;
        for (Calificacion c : calificaciones) {
            if (c.getMedico().getMail().equalsIgnoreCase(this.mail)
                    && c.getEstado() == EstadoValoracion.ACEPTADA) {
                suma += c.getPuntProm();
                contador++;
            }
        }
        this.puntPromMed = contador == 0 ? null : suma / contador;
    }
}
