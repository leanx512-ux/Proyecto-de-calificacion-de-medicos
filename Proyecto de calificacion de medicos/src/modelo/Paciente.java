package modelo;

public class Paciente extends Usuario {
    public Paciente(String mail, String contrasena, String telefono, String nombre, String apellido) {
        super(mail, contrasena, telefono, nombre, apellido, Rol.PACIENTE);
        validarPerfil();
    }
}
