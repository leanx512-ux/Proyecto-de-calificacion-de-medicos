import modelo.*;
import servicio.ServiciosSistema;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ServiciosSistema sistema = new ServiciosSistema();
        cargarDatosDePrueba(sistema);

        Scanner sc = new Scanner(System.in);

        while (true) {
            mostrarInicio();
            int opcion = leerEntero(sc, "Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> iniciarSesion(sc, sistema, Rol.PACIENTE);
                    case 2 -> iniciarSesion(sc, sistema, Rol.MEDICO);
                    case 3 -> iniciarSesion(sc, sistema, Rol.CLINICA);
                    case 4 -> iniciarSesion(sc, sistema, Rol.ASISTENTE_CONSULTA);
                    case 5 -> iniciarSesion(sc, sistema, Rol.ADMINISTRADOR);
                    case 6 -> {
                        Usuario usuario = registrarUsuario(sc, sistema);
                        if (usuario != null) {
                            pantallaPrincipal(sc, sistema, usuario);
                        }
                    }
                    case 7 -> recuperarContrasena(sc, sistema);
                    case 0 -> {
                        System.out.println("Fin.");
                        return;
                    }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void cargarDatosDePrueba(ServiciosSistema sistema) {
        sistema.registrarPaciente("paciente@mail.com", "1234", "111111", "Juan", "Perez");
        sistema.registrarMedico("medico@mail.com", "1234", "222222", "Ana", "Lopez",
                "M-100", "30111222", "certificado.pdf");
        sistema.registrarClinica("clinica@mail.com", "1234", "333333", "Clinica Norte", "Clinica",
                "Mendoza");
        sistema.registrarAsistente("asistente@mail.com", "1234", "444444", "Sofia", "Asistente");
        sistema.registrarAdministrador("admin@mail.com", "1234", "555555", "Admin", "Sistema");

        sistema.validarPerfilUsuario("medico@mail.com", true);
        sistema.validarPerfilUsuario("clinica@mail.com", true);

        sistema.crearEspecialidadMed("Cardiologia");
        sistema.crearEspecialidadCli("Guardia");
        sistema.asignarEspecialidadMed("medico@mail.com", "Cardiologia");
        sistema.asignarEspecialidadCli("clinica@mail.com", "Guardia");

        Calificacion calificacion = sistema.valorarMedico("paciente@mail.com", "medico@mail.com",
                "Muy buena atencion", 5, 5, 4, List.of("turno.jpg"));
        sistema.validarComentario(sistema.getCalificaciones().indexOf(calificacion), true);
    }

    private static void mostrarInicio() {
        System.out.println("\n--- INICIO ---");
        System.out.println("1. Iniciar sesion como paciente");
        System.out.println("2. Iniciar sesion como medico");
        System.out.println("3. Iniciar sesion como clinica");
        System.out.println("4. Iniciar sesion como asistente de consulta");
        System.out.println("5. Iniciar sesion como administrador");
        System.out.println("6. Registrarse");
        System.out.println("7. Recuperar contrasena");
        System.out.println("0. Salir");
    }

    private static void iniciarSesion(Scanner sc, ServiciosSistema sistema, Rol rol) {
        String mail = leerTexto(sc, "Mail: ");
        String contrasena = leerTexto(sc, "Contrasena: ");

        Usuario usuario = sistema.iniciarSesion(mail, contrasena, rol)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas o perfil no validado"));

        System.out.println("Sesion iniciada: " + usuario);
        pantallaPrincipal(sc, sistema, usuario);
    }

    private static void pantallaPrincipal(Scanner sc, ServiciosSistema sistema, Usuario usuario) {
        while (true) {
            System.out.println("\n--- CONSULTAR PERFILES ---");
            consultarPerfiles(sc, sistema);
            mostrarValoracionesAprobadasRecientes(sistema);

            System.out.println("\n--- ACCESOS ---");
            System.out.println("1. Ver mi perfil");
            System.out.println("2. Capacidades de " + usuario.getRol());
            System.out.println("3. Buscar perfiles otra vez");
            System.out.println("0. Cerrar sesion");
            int opcion = leerEntero(sc, "Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> mostrarPerfilPropio(usuario);
                    case 2 -> abrirCapacidades(sc, sistema, usuario);
                    case 3 -> { }
                    case 0 -> { return; }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void abrirCapacidades(Scanner sc, ServiciosSistema sistema, Usuario usuario) {
        if (!usuario.estaActivo()) {
            System.out.println("Tu perfil todavia no esta validado. Solo podes consultar perfiles hasta la revision.");
            return;
        }

        switch (usuario.getRol()) {
            case PACIENTE -> menuPaciente(sc, sistema, (Paciente) usuario);
            case MEDICO -> menuMedico(sc, sistema, (Medico) usuario);
            case CLINICA -> menuClinica(sc, sistema, (Clinica) usuario);
            case ASISTENTE_CONSULTA -> menuAsistente(sc, sistema);
            case ADMINISTRADOR -> menuAdministrador(sc, sistema);
        }
    }

    private static Usuario registrarUsuario(Scanner sc, ServiciosSistema sistema) {
        System.out.println("\n--- REGISTRO ---");
        System.out.println("1. Paciente");
        System.out.println("2. Medico");
        System.out.println("3. Clinica");
        int opcion = leerEntero(sc, "Tipo de cuenta: ");

        return switch (opcion) {
            case 1 -> registrarPaciente(sc, sistema);
            case 2 -> registrarMedico(sc, sistema);
            case 3 -> registrarClinica(sc, sistema);
            default -> {
                System.out.println("Opcion invalida.");
                yield null;
            }
        };
    }

    private static Paciente registrarPaciente(Scanner sc, ServiciosSistema sistema) {
        String nombre = leerTexto(sc, "Nombre: ");
        String apellido = leerTexto(sc, "Apellido: ");
        String mail = leerTexto(sc, "Mail: ");
        String telefono = leerTexto(sc, "Telefono: ");
        String contrasena = leerContrasenaConfirmada(sc);

        Paciente paciente = sistema.registrarPaciente(mail, contrasena, telefono, nombre, apellido);
        System.out.println("Usuario paciente creado con exito.");
        return paciente;
    }

    private static Medico registrarMedico(Scanner sc, ServiciosSistema sistema) {
        String nombre = leerTexto(sc, "Nombre: ");
        String apellido = leerTexto(sc, "Apellido: ");
        String mail = leerTexto(sc, "Mail: ");
        String telefono = leerTexto(sc, "Telefono: ");
        String dni = leerTexto(sc, "DNI: ");
        String matricula = leerTexto(sc, "Numero de matricula: ");
        String certificado = leerTexto(sc, "Certificado o foto/documento: ");
        String contrasena = leerContrasenaConfirmada(sc);

        Medico medico = sistema.registrarMedico(mail, contrasena, telefono, nombre, apellido, matricula, dni, certificado);
        System.out.println("Usuario medico enviado a revision por el asistente de consulta.");
        return medico;
    }

    private static Clinica registrarClinica(Scanner sc, ServiciosSistema sistema) {
        String nombreClinica = leerTexto(sc, "Nombre de la clinica: ");
        String mail = leerTexto(sc, "Mail: ");
        String ubicacion = leerTexto(sc, "Ubicacion: ");
        String telefono = leerTexto(sc, "Telefono: ");
        String contrasena = leerContrasenaConfirmada(sc);

        Clinica clinica = sistema.registrarClinica(mail, contrasena, telefono, nombreClinica, "Clinica", ubicacion);
        System.out.println("Usuario clinica enviado a revision por el asistente de consulta.");
        return clinica;
    }

    private static String leerContrasenaConfirmada(Scanner sc) {
        String contrasena = leerTexto(sc, "Contrasena: ");
        String confirmacion = leerTexto(sc, "Confirmar contrasena: ");
        if (!contrasena.equals(confirmacion)) {
            throw new IllegalArgumentException("Las contrasenas no coinciden");
        }
        return contrasena;
    }

    private static void recuperarContrasena(Scanner sc, ServiciosSistema sistema) {
        String mail = leerTexto(sc, "Mail: ");
        String codigo = sistema.enviarCodigoRecuperacion(mail);
        System.out.println("Codigo enviado al mail: " + codigo);

        String codigoIngresado = leerTexto(sc, "Ingrese el codigo recibido: ");
        String nuevaContrasena = leerContrasenaConfirmada(sc);
        sistema.cambiarContrasenaConCodigo(mail, codigoIngresado, nuevaContrasena);
        System.out.println("Contrasena modificada con exito.");
    }

    private static void mostrarPerfilPropio(Usuario usuario) {
        System.out.println("\n--- MI PERFIL ---");
        System.out.println(usuario);
        System.out.println("Telefono: " + usuario.getTelefono());
        System.out.println("Fecha de alta: " + usuario.getFechaAlta());

        if (usuario instanceof Medico medico) {
            System.out.println("DNI: " + medico.getDni());
            System.out.println("Matricula: " + medico.getMatricula());
            System.out.println("Certificado: " + medico.getCertificado());
            System.out.println("Especialidades: " + medico.getEspecialidades());
            System.out.println("Promedio: " + (medico.getPuntPromMed() == null ? "Sin calificaciones aprobadas" : medico.getPuntPromMed()));
        }

        if (usuario instanceof Clinica clinica) {
            System.out.println("Ubicacion: " + clinica.getUbicacion());
            System.out.println("Especialidades: " + clinica.getEspecialidades());
            System.out.println("Medicos asociados: " + clinica.getMedicosAsociados());
        }
    }

    private static void mostrarValoracionesAprobadasRecientes(ServiciosSistema sistema) {
        System.out.println("\n--- VALORACIONES APROBADAS RECIENTES ---");
        List<Calificacion> valoraciones = sistema.consultarValoracionesAprobadasRecientes();
        if (valoraciones.isEmpty()) {
            System.out.println("No hay valoraciones aprobadas todavia.");
            return;
        }

        for (Calificacion valoracion : valoraciones) {
            System.out.println(valoracion.getFecha() +
                    " | Paciente: " + valoracion.getPaciente().getNombre() +
                    " | Medico: " + valoracion.getMedico().getNombre() + " " + valoracion.getMedico().getApellido() +
                    " | Promedio: " + valoracion.getPuntProm() +
                    " | Comentario: " + valoracion.getComentario());
        }
    }

    private static void menuPaciente(Scanner sc, ServiciosSistema sistema, Paciente paciente) {
        while (true) {
            System.out.println("\n--- CAPACIDADES PACIENTE ---");
            System.out.println("1. Valorar medico");
            System.out.println("2. Modificar mi perfil");
            System.out.println("0. Volver a pantalla principal");
            int opcion = leerEntero(sc, "Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> valorarMedico(sc, sistema, paciente);
                    case 2 -> modificarPerfil(sc, sistema, paciente);
                    case 0 -> { return; }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void menuMedico(Scanner sc, ServiciosSistema sistema, Medico medico) {
        while (true) {
            System.out.println("\n--- CAPACIDADES MEDICO ---");
            System.out.println("1. Ver mis calificaciones");
            System.out.println("2. Reclamar una calificacion");
            System.out.println("3. Responder solicitudes de clinicas");
            System.out.println("4. Modificar mi perfil");
            System.out.println("0. Volver a pantalla principal");
            int opcion = leerEntero(sc, "Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> listarCalificacionesDeMedico(sistema, medico);
                    case 2 -> reclamarCalificacion(sc, sistema, medico);
                    case 3 -> responderSolicitudes(sc, sistema, medico);
                    case 4 -> modificarPerfil(sc, sistema, medico);
                    case 0 -> { return; }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void menuClinica(Scanner sc, ServiciosSistema sistema, Clinica clinica) {
        while (true) {
            System.out.println("\n--- CAPACIDADES CLINICA ---");
            System.out.println("1. Solicitar asociacion de medico");
            System.out.println("2. Eliminar medico asociado");
            System.out.println("3. Ver medicos asociados");
            System.out.println("4. Modificar mi perfil");
            System.out.println("0. Volver a pantalla principal");
            int opcion = leerEntero(sc, "Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> solicitarAsociacion(sc, sistema, clinica);
                    case 2 -> eliminarMedicoAsociado(sc, sistema, clinica);
                    case 3 -> clinica.getMedicosAsociados().forEach(System.out::println);
                    case 4 -> modificarPerfil(sc, sistema, clinica);
                    case 0 -> { return; }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void menuAsistente(Scanner sc, ServiciosSistema sistema) {
        while (true) {
            System.out.println("\n--- MENU ASISTENTE DE CONSULTA ---");
            System.out.println("1. Reporte de calificaciones recientes");
            System.out.println("2. Reporte de usuarios recientes");
            System.out.println("3. Validar perfiles pendientes");
            System.out.println("4. Validar calificaciones pendientes");
            System.out.println("5. Atender reclamos pendientes");
            System.out.println("0. Volver a pantalla principal");
            int opcion = leerEntero(sc, "Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> sistema.consultarCalificacionesRecientes().forEach(System.out::println);
                    case 2 -> sistema.consultarUsuariosRecientes().forEach(System.out::println);
                    case 3 -> validarPerfilPendiente(sc, sistema);
                    case 4 -> validarCalificacionPendiente(sc, sistema);
                    case 5 -> atenderReclamoPendiente(sc, sistema);
                    case 0 -> { return; }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void menuAdministrador(Scanner sc, ServiciosSistema sistema) {
        while (true) {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("1. ABM especialidad medica");
            System.out.println("2. ABM especialidad clinica");
            System.out.println("0. Volver a pantalla principal");
            int opcion = leerEntero(sc, "Opcion: ");

            try {
                switch (opcion) {
                    case 1 -> menuEspecialidadMed(sc, sistema);
                    case 2 -> menuEspecialidadCli(sc, sistema);
                    case 0 -> { return; }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void consultarPerfiles(Scanner sc, ServiciosSistema sistema) {
        String texto = leerTexto(sc, "Buscar por nombre, apellido, mail, matricula, dni, ubicacion o especialidad: ");
        List<Usuario> perfiles = sistema.consultarPerfiles(texto);
        for (Usuario perfil : perfiles) {
            System.out.println(perfil);
            if (perfil instanceof Medico medico) {
                System.out.println("  Especialidades: " + medico.getEspecialidades());
                System.out.println("  Promedio: " + (medico.getPuntPromMed() == null ? "Sin calificaciones aprobadas" : medico.getPuntPromMed()));
                sistema.consultarCalificacionesAprobadasRecientes(medico.getMail())
                        .forEach(c -> System.out.println("  Reciente: " + c));
            }
            if (perfil instanceof Clinica clinica) {
                System.out.println("  Ubicacion: " + clinica.getUbicacion());
                System.out.println("  Especialidades: " + clinica.getEspecialidades());
                System.out.println("  Medicos asociados: " + clinica.getMedicosAsociados());
            }
        }
    }

    private static void valorarMedico(Scanner sc, ServiciosSistema sistema, Paciente paciente) {
        String mailMedico = leerTexto(sc, "Mail del medico: ");
        String comentario = leerTexto(sc, "Comentario: ");
        int atencion = leerEntero(sc, "Estrellas por atencion (1-5): ");
        int comprension = leerEntero(sc, "Estrellas por comprension (1-5): ");
        int puntualidad = leerEntero(sc, "Estrellas por puntualidad (1-5): ");
        int cantidadImagenes = leerEntero(sc, "Cantidad de imagenes/documentos de respaldo (0-6): ");
        List<String> imagenes = new ArrayList<>();

        for (int i = 0; i < cantidadImagenes && i < 6; i++) {
            imagenes.add(leerTexto(sc, "Ruta o nombre del archivo " + (i + 1) + ": "));
        }

        Calificacion calificacion = sistema.valorarMedico(paciente.getMail(), mailMedico, comentario,
                atencion, comprension, puntualidad, imagenes);
        System.out.println("Calificacion enviada a revision: " + calificacion);
    }

    private static void listarCalificacionesDeMedico(ServiciosSistema sistema, Medico medico) {
        for (int i = 0; i < sistema.getCalificaciones().size(); i++) {
            Calificacion calificacion = sistema.getCalificaciones().get(i);
            if (calificacion.getMedico().getMail().equalsIgnoreCase(medico.getMail())) {
                System.out.println(i + ". " + calificacion + " | Comentario: " + calificacion.getComentario());
            }
        }
    }

    private static void reclamarCalificacion(Scanner sc, ServiciosSistema sistema, Medico medico) {
        listarCalificacionesDeMedico(sistema, medico);
        int indice = leerEntero(sc, "Indice de calificacion a reclamar: ");
        String descripcion = leerTexto(sc, "Explique el motivo del reclamo: ");
        Calificacion calificacion = sistema.getCalificaciones().get(indice);
        System.out.println("Reclamo enviado a revision: " +
                sistema.denunciarValoracion(medico.getMail(), calificacion, descripcion));
    }

    private static void solicitarAsociacion(Scanner sc, ServiciosSistema sistema, Clinica clinica) {
        String mailMedico = leerTexto(sc, "Mail del medico: ");
        System.out.println("Solicitud enviada: " + sistema.solicitarAsociacionMedico(clinica.getMail(), mailMedico));
    }

    private static void eliminarMedicoAsociado(Scanner sc, ServiciosSistema sistema, Clinica clinica) {
        String mailMedico = leerTexto(sc, "Mail del medico asociado a eliminar: ");
        sistema.desasociarMedicoDeClinica(clinica.getMail(), mailMedico);
        System.out.println("Medico eliminado de asociaciones.");
    }

    private static void responderSolicitudes(Scanner sc, ServiciosSistema sistema, Medico medico) {
        List<SolicitudAsociacion> solicitudes = sistema.consultarSolicitudesPendientesParaMedico(medico.getMail());
        for (int i = 0; i < solicitudes.size(); i++) {
            System.out.println(i + ". " + solicitudes.get(i));
        }
        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes pendientes.");
            return;
        }

        int indiceLocal = leerEntero(sc, "Indice de solicitud: ");
        boolean aceptar = leerSiNo(sc, "Aceptar asociacion? (s/n): ");
        int indiceGlobal = sistema.getSolicitudesAsociacion().indexOf(solicitudes.get(indiceLocal));
        sistema.responderSolicitudAsociacion(indiceGlobal, medico.getMail(), aceptar);
        System.out.println("Solicitud respondida.");
    }

    private static void validarPerfilPendiente(Scanner sc, ServiciosSistema sistema) {
        List<Usuario> pendientes = sistema.consultarUsuariosPendientes();
        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println(i + ". " + pendientes.get(i));
        }
        if (pendientes.isEmpty()) {
            System.out.println("No hay perfiles pendientes.");
            return;
        }

        int indice = leerEntero(sc, "Indice de perfil: ");
        boolean aceptar = leerSiNo(sc, "Aceptar perfil? (s/n): ");
        sistema.validarPerfilUsuario(pendientes.get(indice).getMail(), aceptar);
        System.out.println("Perfil revisado.");
    }

    private static void validarCalificacionPendiente(Scanner sc, ServiciosSistema sistema) {
        List<Calificacion> pendientes = sistema.consultarCalificacionesPendientes();
        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println(i + ". " + pendientes.get(i) + " | Comentario: " + pendientes.get(i).getComentario());
        }
        if (pendientes.isEmpty()) {
            System.out.println("No hay calificaciones pendientes.");
            return;
        }

        int indiceLocal = leerEntero(sc, "Indice de calificacion: ");
        boolean aceptar = leerSiNo(sc, "Aceptar calificacion? (s/n): ");
        int indiceGlobal = sistema.getCalificaciones().indexOf(pendientes.get(indiceLocal));
        sistema.validarComentario(indiceGlobal, aceptar);
        System.out.println("Calificacion revisada.");
    }

    private static void atenderReclamoPendiente(Scanner sc, ServiciosSistema sistema) {
        List<Reclamo> pendientes = sistema.consultarReclamosPendientes();
        for (int i = 0; i < pendientes.size(); i++) {
            Reclamo reclamo = pendientes.get(i);
            System.out.println(i + ". " + reclamo + " | Motivo: " + reclamo.getDescripcion());
        }
        if (pendientes.isEmpty()) {
            System.out.println("No hay reclamos pendientes.");
            return;
        }

        int indiceLocal = leerEntero(sc, "Indice de reclamo: ");
        boolean aceptar = leerSiNo(sc, "Aceptar reclamo? (s/n): ");
        int indiceGlobal = sistema.getReclamos().indexOf(pendientes.get(indiceLocal));
        sistema.atenderReclamo(indiceGlobal, aceptar);
        System.out.println("Reclamo revisado.");
    }

    private static void modificarPerfil(Scanner sc, ServiciosSistema sistema, Usuario usuario) {
        String nombre = leerTexto(sc, "Nuevo nombre: ");
        String apellido = leerTexto(sc, "Nuevo apellido: ");
        String telefono = leerTexto(sc, "Nuevo telefono: ");
        sistema.modificarPerfil(usuario.getMail(), nombre, apellido, telefono);
        System.out.println("Perfil modificado.");
    }

    private static void menuEspecialidadMed(Scanner sc, ServiciosSistema sistema) {
        System.out.println("\n--- ABM ESPECIALIDAD MEDICA ---");
        System.out.println("1. Crear");
        System.out.println("2. Consultar");
        System.out.println("3. Modificar");
        System.out.println("4. Eliminar");
        System.out.println("5. Asignar a medico");
        int opcion = leerEntero(sc, "Opcion: ");

        switch (opcion) {
            case 1 -> System.out.println("Creada: " + sistema.crearEspecialidadMed(leerTexto(sc, "Nombre: ")));
            case 2 -> sistema.consultarEspecialidadesMed().forEach(System.out::println);
            case 3 -> {
                String actual = leerTexto(sc, "Nombre actual: ");
                String nuevo = leerTexto(sc, "Nombre nuevo: ");
                sistema.modificarEspecialidadMed(actual, nuevo);
                System.out.println("Especialidad medica modificada.");
            }
            case 4 -> {
                sistema.eliminarEspecialidadMed(leerTexto(sc, "Nombre: "));
                System.out.println("Especialidad medica eliminada.");
            }
            case 5 -> {
                String mailMedico = leerTexto(sc, "Mail medico: ");
                String nombre = leerTexto(sc, "Especialidad: ");
                sistema.asignarEspecialidadMed(mailMedico, nombre);
                System.out.println("Especialidad asignada.");
            }
            default -> System.out.println("Opcion invalida.");
        }
    }

    private static void menuEspecialidadCli(Scanner sc, ServiciosSistema sistema) {
        System.out.println("\n--- ABM ESPECIALIDAD CLINICA ---");
        System.out.println("1. Crear");
        System.out.println("2. Consultar");
        System.out.println("3. Modificar");
        System.out.println("4. Eliminar");
        System.out.println("5. Asignar a clinica");
        int opcion = leerEntero(sc, "Opcion: ");

        switch (opcion) {
            case 1 -> System.out.println("Creada: " + sistema.crearEspecialidadCli(leerTexto(sc, "Nombre: ")));
            case 2 -> sistema.consultarEspecialidadesCli().forEach(System.out::println);
            case 3 -> {
                String actual = leerTexto(sc, "Nombre actual: ");
                String nuevo = leerTexto(sc, "Nombre nuevo: ");
                sistema.modificarEspecialidadCli(actual, nuevo);
                System.out.println("Especialidad clinica modificada.");
            }
            case 4 -> {
                sistema.eliminarEspecialidadCli(leerTexto(sc, "Nombre: "));
                System.out.println("Especialidad clinica eliminada.");
            }
            case 5 -> {
                String mailClinica = leerTexto(sc, "Mail clinica: ");
                String nombre = leerTexto(sc, "Especialidad: ");
                sistema.asignarEspecialidadCli(mailClinica, nombre);
                System.out.println("Especialidad asignada.");
            }
            default -> System.out.println("Opcion invalida.");
        }
    }

    private static String leerTexto(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine();
    }

    private static int leerEntero(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return Integer.parseInt(sc.nextLine());
    }

    private static boolean leerSiNo(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim().equalsIgnoreCase("s");
    }
}

