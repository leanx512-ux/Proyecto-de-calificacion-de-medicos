package modelo;

import java.util.ArrayList;
import java.util.List;

public class Clinica extends Usuario {
    private String ubicacion;
    private final List<String> especialidades = new ArrayList<>();
    private final List<String> medicosAsociados = new ArrayList<>();

    public Clinica(String mail, String contrasena, String telefono, String nombre, String apellido, String ubicacion) {
        super(mail, contrasena, telefono, nombre, apellido, Rol.CLINICA);
        this.ubicacion = ubicacion;
    }

    public String getUbicacion() { return ubicacion; }
    public List<String> getEspecialidades() { return especialidades; }
    public List<String> getMedicosAsociados() { return medicosAsociados; }

    public void agregarEspecialidad(String esp) {
        if (esp != null && !esp.isBlank()
                && especialidades.stream().noneMatch(e -> e.equalsIgnoreCase(esp.trim()))) {
            especialidades.add(esp.trim());
        }
    }

    public void quitarEspecialidad(String esp) {
        if (esp != null) {
            especialidades.removeIf(e -> e.equalsIgnoreCase(esp.trim()));
        }
    }

    public void asociarMedico(String mailMedico) {
        if (mailMedico != null && !mailMedico.isBlank() && !medicosAsociados.contains(mailMedico)) {
            medicosAsociados.add(mailMedico);
        }
    }

    public void eliminarMedico(String mailMedico) {
        medicosAsociados.remove(mailMedico);
    }
}
