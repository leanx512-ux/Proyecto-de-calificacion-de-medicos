package modelo;

public class AsistenteConsulta extends Usuario {
    public AsistenteConsulta(String mail, String contrasena, String telefono, String nombre, String apellido) {
        super(mail, contrasena, telefono, nombre, apellido, Rol.ASISTENTE_CONSULTA);
        validarPerfil();
    }
}
