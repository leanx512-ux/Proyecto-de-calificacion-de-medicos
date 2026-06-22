package modelo;

import java.time.LocalDate;

public abstract class Usuario {
    protected String mail;
    protected String contrasena;
    protected String telefono;
    protected LocalDate fechaAlta;
    protected LocalDate fechaBaja;
    protected String nombre;
    protected String apellido;
    protected Rol rol;
    protected EstadoPerfil estadoPerfil;

    public Usuario(String mail, String contrasena, String telefono, String nombre, String apellido, Rol rol) {
        this.mail = mail;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.fechaAlta = LocalDate.now();
        this.estadoPerfil = EstadoPerfil.PENDIENTE;
    }

    public String getMail() { return mail; }
    public String getContrasena() { return contrasena; }
    public String getTelefono() { return telefono; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public LocalDate getFechaBaja() { return fechaBaja; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public Rol getRol() { return rol; }
    public EstadoPerfil getEstadoPerfil() { return estadoPerfil; }

    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public void validarPerfil() { this.estadoPerfil = EstadoPerfil.VALIDADO; }
    public void rechazarPerfil() { this.estadoPerfil = EstadoPerfil.RECHAZADO; }

    public boolean estaPendiente() {
        return estadoPerfil == EstadoPerfil.PENDIENTE;
    }

    public boolean estaActivo() {
        return fechaBaja == null && estadoPerfil == EstadoPerfil.VALIDADO;
    }

    @Override
    public String toString() {
        return rol + " | " + nombre + " " + apellido + " | " + mail + " | " + estadoPerfil;
    }
}
