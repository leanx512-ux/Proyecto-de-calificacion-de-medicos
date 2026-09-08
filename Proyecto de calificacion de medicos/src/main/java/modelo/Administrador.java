package modelo;

public class Administrador extends Usuario {
    public Administrador(String mail, String contrasena, String telefono, String nombre, String apellido) {
        super(mail, contrasena, telefono, nombre, apellido, Rol.ADMINISTRADOR);
        validarPerfil();
    }
}
