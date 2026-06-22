package servicio;

import modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiciosSistema {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Calificacion> calificaciones = new ArrayList<>();
    private final List<Reclamo> reclamos = new ArrayList<>();
    private final List<SolicitudAsociacion> solicitudesAsociacion = new ArrayList<>();
    private final List<EspecialidadMed> especialidadesMedicas = new ArrayList<>();
    private final List<EspecialidadCli> especialidadesClinicas = new ArrayList<>();
    private final Map<String, String> codigosRecuperacion = new HashMap<>();

    public Paciente registrarPaciente(String mail, String pass, String telefono, String nombre, String apellido) {
        validarMailDisponible(mail);
        validarTexto(pass, "La contrasena es obligatoria");
        Paciente paciente = new Paciente(mail, pass, telefono, nombre, apellido);
        usuarios.add(paciente);
        return paciente;
    }

    public Medico registrarMedico(String mail, String pass, String telefono, String nombre, String apellido,
                                  String matricula, String dni, String certificado) {
        validarMailDisponible(mail);
        validarTexto(pass, "La contrasena es obligatoria");
        validarTexto(certificado, "El certificado es obligatorio");
        Medico medico = new Medico(mail, pass, telefono, nombre, apellido, matricula, dni, certificado);
        usuarios.add(medico);
        return medico;
    }

    public Clinica registrarClinica(String mail, String pass, String telefono, String nombre, String apellido,
                                    String ubicacion) {
        validarMailDisponible(mail);
        validarTexto(pass, "La contrasena es obligatoria");
        validarTexto(ubicacion, "La ubicacion es obligatoria");
        Clinica clinica = new Clinica(mail, pass, telefono, nombre, apellido, ubicacion);
        usuarios.add(clinica);
        return clinica;
    }

    public AsistenteConsulta registrarAsistente(String mail, String pass, String telefono, String nombre, String apellido) {
        validarMailDisponible(mail);
        AsistenteConsulta asistente = new AsistenteConsulta(mail, pass, telefono, nombre, apellido);
        usuarios.add(asistente);
        return asistente;
    }

    public Administrador registrarAdministrador(String mail, String pass, String telefono, String nombre, String apellido) {
        validarMailDisponible(mail);
        Administrador administrador = new Administrador(mail, pass, telefono, nombre, apellido);
        usuarios.add(administrador);
        return administrador;
    }

    public Optional<Usuario> iniciarSesion(String mail, String pass) {
        return usuarios.stream()
                .filter(Usuario::estaActivo)
                .filter(u -> u.getMail().equalsIgnoreCase(mail) && u.getContrasena().equals(pass))
                .findFirst();
    }

    public Optional<Usuario> iniciarSesion(String mail, String pass, Rol rol) {
        return iniciarSesion(mail, pass)
                .filter(u -> u.getRol() == rol);
    }

    public Optional<Usuario> buscarPorMail(String mail) {
        if (mail == null) return Optional.empty();
        return usuarios.stream()
                .filter(u -> u.getMail().equalsIgnoreCase(mail))
                .findFirst();
    }

    public List<Usuario> consultarPerfiles(String texto) {
        String consulta = texto == null ? "" : texto.toLowerCase();

        return usuarios.stream()
                .filter(Usuario::estaActivo)
                .filter(u -> u instanceof Medico || u instanceof Clinica)
                .filter(u -> coincide(u, consulta))
                .collect(Collectors.toList());
    }

    private boolean coincide(Usuario usuario, String consulta) {
        if (usuario.getMail().toLowerCase().contains(consulta)) return true;
        if (usuario.getNombre() != null && usuario.getNombre().toLowerCase().contains(consulta)) return true;
        if (usuario.getApellido() != null && usuario.getApellido().toLowerCase().contains(consulta)) return true;

        if (usuario instanceof Medico medico) {
            if (medico.getMatricula() != null && medico.getMatricula().toLowerCase().contains(consulta)) return true;
            if (medico.getDni() != null && medico.getDni().toLowerCase().contains(consulta)) return true;
            return medico.getEspecialidades().stream().anyMatch(e -> e.toLowerCase().contains(consulta));
        }

        if (usuario instanceof Clinica clinica) {
            if (clinica.getUbicacion() != null && clinica.getUbicacion().toLowerCase().contains(consulta)) return true;
            return clinica.getEspecialidades().stream().anyMatch(e -> e.toLowerCase().contains(consulta));
        }

        return false;
    }

    public List<Calificacion> consultarCalificacionesAprobadasRecientes(String mailMedico) {
        return calificaciones.stream()
                .filter(c -> c.getMedico().getMail().equalsIgnoreCase(mailMedico))
                .filter(c -> c.getEstado() == EstadoValoracion.ACEPTADA)
                .sorted(Comparator.comparing(Calificacion::getFecha).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public String enviarCodigoRecuperacion(String mail) {
        Usuario usuario = buscarPorMail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente"));

        String codigo = String.valueOf(Math.abs((usuario.getMail() + LocalDate.now()).hashCode() % 900000) + 100000);
        codigosRecuperacion.put(usuario.getMail().toLowerCase(), codigo);
        return codigo;
    }

    public void cambiarContrasenaConCodigo(String mail, String codigo, String nuevaContrasena) {
        Usuario usuario = buscarPorMail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente"));
        String codigoGuardado = codigosRecuperacion.get(usuario.getMail().toLowerCase());

        if (codigoGuardado == null || !codigoGuardado.equals(codigo)) {
            throw new IllegalArgumentException("Codigo de recuperacion invalido");
        }

        validarTexto(nuevaContrasena, "La nueva contrasena es obligatoria");
        usuario.setContrasena(nuevaContrasena);
        codigosRecuperacion.remove(usuario.getMail().toLowerCase());
    }

    public Calificacion valorarMedico(String mailPaciente, String mailMedico, String comentario,
                                      int atencion, int comprension, int puntualidad, List<String> imagenes) {
        Usuario usuarioPaciente = buscarPorMail(mailPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Paciente inexistente"));
        Usuario usuarioMedico = buscarPorMail(mailMedico)
                .orElseThrow(() -> new IllegalArgumentException("Medico inexistente"));

        if (!(usuarioPaciente instanceof Paciente paciente)) {
            throw new IllegalArgumentException("Solo un paciente puede valorar medicos");
        }
        if (!(usuarioMedico instanceof Medico medico) || !medico.estaActivo()) {
            throw new IllegalArgumentException("El medico no existe o no esta validado");
        }
        validarTexto(comentario, "El comentario no puede estar vacio");

        Calificacion calificacion = new Calificacion(paciente, medico, comentario, atencion, comprension, puntualidad);
        if (imagenes != null) {
            imagenes.forEach(calificacion::agregarImagen);
        }
        calificaciones.add(calificacion);
        return calificacion;
    }

    public Calificacion valorarMedico(String mailPaciente, String mailMedico, String comentario,
                                      int atencion, int comprension, int puntualidad) {
        return valorarMedico(mailPaciente, mailMedico, comentario, atencion, comprension, puntualidad, List.of());
    }

    public void validarComentario(int indiceCalificacion, boolean aceptada) {
        Calificacion calificacion = obtenerCalificacion(indiceCalificacion);
        calificacion.setEstado(aceptada ? EstadoValoracion.ACEPTADA : EstadoValoracion.RECHAZADA);
        calificacion.getMedico().recalcularPromedio(calificaciones);
    }

    public Reclamo denunciarValoracion(String mailMedico, Calificacion calificacion, String descripcion) {
        Usuario usuario = buscarPorMail(mailMedico)
                .orElseThrow(() -> new IllegalArgumentException("Medico inexistente"));

        if (!(usuario instanceof Medico medico)) {
            throw new IllegalArgumentException("Solo un medico puede hacer reclamos");
        }
        if (calificacion == null || !calificacion.getMedico().getMail().equalsIgnoreCase(mailMedico)) {
            throw new IllegalArgumentException("El medico solo puede reclamar calificaciones propias");
        }
        validarTexto(descripcion, "La descripcion del reclamo es obligatoria");

        Reclamo reclamo = new Reclamo(medico, calificacion, descripcion);
        reclamos.add(reclamo);
        return reclamo;
    }

    public void atenderReclamo(int indiceReclamo, boolean aceptado) {
        Reclamo reclamo = obtenerReclamo(indiceReclamo);
        if (aceptado) {
            reclamo.aceptar();
            reclamo.getCalificacion().setEstado(EstadoValoracion.RECHAZADA);
        } else {
            reclamo.rechazar();
        }
        reclamo.getMedico().recalcularPromedio(calificaciones);
    }

    public SolicitudAsociacion solicitarAsociacionMedico(String mailClinica, String mailMedico) {
        Usuario usuarioClinica = buscarPorMail(mailClinica)
                .orElseThrow(() -> new IllegalArgumentException("Clinica inexistente"));
        Usuario usuarioMedico = buscarPorMail(mailMedico)
                .orElseThrow(() -> new IllegalArgumentException("Medico inexistente"));

        if (!(usuarioClinica instanceof Clinica clinica) || !clinica.estaActivo()) {
            throw new IllegalArgumentException("Solo una clinica validada puede solicitar asociaciones");
        }
        if (!(usuarioMedico instanceof Medico medico) || !medico.estaActivo()) {
            throw new IllegalArgumentException("El medico no existe o no esta validado");
        }
        if (clinica.getMedicosAsociados().stream().anyMatch(m -> m.equalsIgnoreCase(mailMedico))) {
            throw new IllegalArgumentException("El medico ya esta asociado a la clinica");
        }

        SolicitudAsociacion solicitud = new SolicitudAsociacion(clinica, medico);
        solicitudesAsociacion.add(solicitud);
        return solicitud;
    }

    public void responderSolicitudAsociacion(int indiceSolicitud, String mailMedico, boolean aceptada) {
        SolicitudAsociacion solicitud = obtenerSolicitudAsociacion(indiceSolicitud);
        if (!solicitud.getMedico().getMail().equalsIgnoreCase(mailMedico)) {
            throw new IllegalArgumentException("El medico solo puede responder sus propias solicitudes");
        }
        if (aceptada) {
            solicitud.aceptar();
        } else {
            solicitud.rechazar();
        }
    }

    public void desasociarMedicoDeClinica(String mailClinica, String mailMedico) {
        Usuario usuarioClinica = buscarPorMail(mailClinica)
                .orElseThrow(() -> new IllegalArgumentException("Clinica inexistente"));

        if (!(usuarioClinica instanceof Clinica clinica)) {
            throw new IllegalArgumentException("El usuario no es clinica");
        }

        clinica.eliminarMedico(mailMedico);
    }

    public void modificarPerfil(String mail, String nuevoNombre, String nuevoApellido, String nuevoTelefono) {
        Usuario usuario = buscarPorMail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente"));

        if (nuevoNombre != null && !nuevoNombre.isBlank()) usuario.setNombre(nuevoNombre);
        if (nuevoApellido != null && !nuevoApellido.isBlank()) usuario.setApellido(nuevoApellido);
        if (nuevoTelefono != null && !nuevoTelefono.isBlank()) usuario.setTelefono(nuevoTelefono);
    }

    public void validarPerfilUsuario(String mail) {
        validarPerfilUsuario(mail, true);
    }

    public void validarPerfilUsuario(String mail, boolean aceptado) {
        Usuario usuario = buscarPorMail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente"));
        if (aceptado) {
            usuario.validarPerfil();
        } else {
            usuario.rechazarPerfil();
        }
    }

    public void eliminarPerfilUsuario(String mail) {
        Usuario usuario = buscarPorMail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente"));
        usuario.setFechaBaja(LocalDate.now());
    }

    public List<Usuario> consultarUsuariosPendientes() {
        return usuarios.stream()
                .filter(Usuario::estaPendiente)
                .filter(u -> u instanceof Medico || u instanceof Clinica)
                .collect(Collectors.toList());
    }

    public List<Calificacion> consultarCalificacionesPendientes() {
        return calificaciones.stream()
                .filter(c -> c.getEstado() == EstadoValoracion.PENDIENTE)
                .collect(Collectors.toList());
    }

    public List<Reclamo> consultarReclamosPendientes() {
        return reclamos.stream()
                .filter(r -> r.getResultado() == EstadoReclamo.PENDIENTE)
                .collect(Collectors.toList());
    }

    public List<Usuario> consultarUsuariosRecientes() {
        return usuarios.stream()
                .sorted(Comparator.comparing(Usuario::getFechaAlta).reversed())
                .collect(Collectors.toList());
    }

    public List<Calificacion> consultarCalificacionesRecientes() {
        return calificaciones.stream()
                .sorted(Comparator.comparing(Calificacion::getFecha).reversed())
                .collect(Collectors.toList());
    }

    public List<Calificacion> consultarValoracionesAprobadasRecientes() {
        return calificaciones.stream()
                .filter(c -> c.getEstado() == EstadoValoracion.ACEPTADA)
                .sorted(Comparator.comparing(Calificacion::getFecha).reversed())
                .collect(Collectors.toList());
    }

    public List<SolicitudAsociacion> consultarSolicitudesPendientesParaMedico(String mailMedico) {
        return solicitudesAsociacion.stream()
                .filter(s -> s.getMedico().getMail().equalsIgnoreCase(mailMedico))
                .filter(s -> s.getEstado() == EstadoSolicitudAsociacion.PENDIENTE)
                .collect(Collectors.toList());
    }

    public EspecialidadMed crearEspecialidadMed(String nombre) {
        if (buscarEspecialidadMed(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad medica con ese nombre");
        }
        EspecialidadMed especialidad = new EspecialidadMed(nombre);
        especialidadesMedicas.add(especialidad);
        return especialidad;
    }

    public Optional<EspecialidadMed> buscarEspecialidadMed(String nombre) {
        if (nombre == null) return Optional.empty();
        return especialidadesMedicas.stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }

    public List<EspecialidadMed> consultarEspecialidadesMed() {
        return especialidadesMedicas.stream()
                .filter(EspecialidadMed::estaActiva)
                .collect(Collectors.toList());
    }

    public void modificarEspecialidadMed(String nombreActual, String nombreNuevo) {
        EspecialidadMed especialidad = buscarEspecialidadMed(nombreActual)
                .orElseThrow(() -> new IllegalArgumentException("Especialidad medica inexistente"));
        if (buscarEspecialidadMed(nombreNuevo).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad medica con ese nombre");
        }
        especialidad.setNombre(nombreNuevo);
    }

    public void eliminarEspecialidadMed(String nombre) {
        EspecialidadMed especialidad = buscarEspecialidadMed(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Especialidad medica inexistente"));
        especialidad.eliminar();
        usuarios.stream()
                .filter(u -> u instanceof Medico)
                .map(u -> (Medico) u)
                .forEach(m -> m.quitarEspecialidad(nombre));
    }

    public void asignarEspecialidadMed(String mailMedico, String nombreEspecialidad) {
        EspecialidadMed especialidad = buscarEspecialidadMed(nombreEspecialidad)
                .filter(EspecialidadMed::estaActiva)
                .orElseThrow(() -> new IllegalArgumentException("Especialidad medica inexistente o inactiva"));
        Usuario usuario = buscarPorMail(mailMedico)
                .orElseThrow(() -> new IllegalArgumentException("Medico inexistente"));
        if (!(usuario instanceof Medico medico)) {
            throw new IllegalArgumentException("El usuario no es medico");
        }
        medico.agregarEspecialidad(especialidad.getNombre());
    }

    public EspecialidadCli crearEspecialidadCli(String nombre) {
        if (buscarEspecialidadCli(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad de clinica con ese nombre");
        }
        EspecialidadCli especialidad = new EspecialidadCli(nombre);
        especialidadesClinicas.add(especialidad);
        return especialidad;
    }

    public Optional<EspecialidadCli> buscarEspecialidadCli(String nombre) {
        if (nombre == null) return Optional.empty();
        return especialidadesClinicas.stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }

    public List<EspecialidadCli> consultarEspecialidadesCli() {
        return especialidadesClinicas.stream()
                .filter(EspecialidadCli::estaActiva)
                .collect(Collectors.toList());
    }

    public void modificarEspecialidadCli(String nombreActual, String nombreNuevo) {
        EspecialidadCli especialidad = buscarEspecialidadCli(nombreActual)
                .orElseThrow(() -> new IllegalArgumentException("Especialidad de clinica inexistente"));
        if (buscarEspecialidadCli(nombreNuevo).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad de clinica con ese nombre");
        }
        especialidad.setNombre(nombreNuevo);
    }

    public void eliminarEspecialidadCli(String nombre) {
        EspecialidadCli especialidad = buscarEspecialidadCli(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Especialidad de clinica inexistente"));
        especialidad.eliminar();
        usuarios.stream()
                .filter(u -> u instanceof Clinica)
                .map(u -> (Clinica) u)
                .forEach(c -> c.quitarEspecialidad(nombre));
    }

    public void asignarEspecialidadCli(String mailClinica, String nombreEspecialidad) {
        EspecialidadCli especialidad = buscarEspecialidadCli(nombreEspecialidad)
                .filter(EspecialidadCli::estaActiva)
                .orElseThrow(() -> new IllegalArgumentException("Especialidad de clinica inexistente o inactiva"));
        Usuario usuario = buscarPorMail(mailClinica)
                .orElseThrow(() -> new IllegalArgumentException("Clinica inexistente"));
        if (!(usuario instanceof Clinica clinica)) {
            throw new IllegalArgumentException("El usuario no es clinica");
        }
        clinica.agregarEspecialidad(especialidad.getNombre());
    }

    private Calificacion obtenerCalificacion(int indiceCalificacion) {
        if (indiceCalificacion < 0 || indiceCalificacion >= calificaciones.size()) {
            throw new IllegalArgumentException("Calificacion inexistente");
        }
        return calificaciones.get(indiceCalificacion);
    }

    private Reclamo obtenerReclamo(int indiceReclamo) {
        if (indiceReclamo < 0 || indiceReclamo >= reclamos.size()) {
            throw new IllegalArgumentException("Reclamo inexistente");
        }
        return reclamos.get(indiceReclamo);
    }

    private SolicitudAsociacion obtenerSolicitudAsociacion(int indiceSolicitud) {
        if (indiceSolicitud < 0 || indiceSolicitud >= solicitudesAsociacion.size()) {
            throw new IllegalArgumentException("Solicitud inexistente");
        }
        return solicitudesAsociacion.get(indiceSolicitud);
    }

    private void validarMailDisponible(String mail) {
        validarTexto(mail, "El mail es obligatorio");
        if (buscarPorMail(mail).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese mail");
        }
    }

    private void validarTexto(String texto, String mensaje) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Calificacion> getCalificaciones() { return calificaciones; }
    public List<Reclamo> getReclamos() { return reclamos; }
    public List<SolicitudAsociacion> getSolicitudesAsociacion() { return solicitudesAsociacion; }
    public List<EspecialidadMed> getEspecialidadesMedicas() { return especialidadesMedicas; }
    public List<EspecialidadCli> getEspecialidadesClinicas() { return especialidadesClinicas; }
}
