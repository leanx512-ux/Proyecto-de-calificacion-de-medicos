import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import modelo.Usuario;
import modelo.Paciente;
import modelo.Medico;
import modelo.Clinica;
import modelo.Calificacion;
import modelo.SolicitudAsociacion;
import modelo.EspecialidadCli;
import modelo.EspecialidadMed;
import modelo.Reclamo;
import servicio.ServiciosSistema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiServer {

    private static final ServiciosSistema sistema = new ServiciosSistema();
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        cargarDatosDePrueba();

        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);

        // ============ ENDPOINTS ============
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/registro/paciente", new RegistroPacienteHandler());
        server.createContext("/api/registro/medico", new RegistroMedicoHandler());
        server.createContext("/api/registro/clinica", new RegistroClinicaHandler());
        server.createContext("/api/perfiles", new BuscarPerfilesHandler());
        server.createContext("/api/perfil", new ObtenerPerfilHandler());
        server.createContext("/api/valorar", new ValorarMedicoHandler());
        server.createContext("/api/calificaciones", new ObtenerCalificacionesHandler());
        server.createContext("/api/calificacion", new ObtenerCalificacionPorIndiceHandler());
        server.createContext("/api/calificaciones/todas", new TodasLasCalificacionesHandler());
        server.createContext("/api/calificaciones/pendientes", new CalificacionesPendientesHandler());
        server.createContext("/api/calificaciones/validar", new ValidarCalificacionHandler());
        server.createContext("/api/perfil/modificar", new ModificarPerfilHandler());
        server.createContext("/api/asociacion/solicitar", new SolicitarAsociacionHandler());
        server.createContext("/api/clinica", new DesvincularMedicoHandler());
        server.createContext("/api/solicitudes/pendientes", new SolicitudesPendientesHandler());
        server.createContext("/api/solicitudes", new ResponderSolicitudHandler());
        server.createContext("/api/especialidades/med", new EspecialidadMedHandler());
        server.createContext("/api/especialidades/cli", new EspecialidadCliHandler());
        server.createContext("/api/reclamos", new CrearReclamoHandler());
        server.createContext("/api/reclamos/pendientes", new ReclamosPendientesHandler());
        server.createContext("/api/reclamo", new ObtenerReclamoHandler());
        server.createContext("/api/reclamo/resolver", new ResolverReclamoHandler());
        server.createContext("/api/usuarios/pendientes", new UsuariosPendientesHandler());
        server.createContext("/api/usuarios/validados", new UsuariosValidadosHandler());
        server.createContext("/api/usuarios/estadisticas", new EstadisticasRegistrosHandler());
        server.createContext("/api/usuarios/validar", new ValidarUsuarioHandler());
        server.createContext("/api/usuario", new BuscarUsuarioHandler());
        server.createContext("/api/cambiar-contrasena", new CambiarContrasenaHandler());

// Handlers correspondientes (implementar usando ServiciosSistema)

        server.setExecutor(null);
        server.start();
        System.out.println("=========================================");
        System.out.println("✅ Servidor API corriendo en http://localhost:4567");
        System.out.println("✅ CORS habilitado para: http://127.0.0.1:5500");
        System.out.println("=========================================");
        System.out.println("📋 Endpoints disponibles:");
        System.out.println("  POST /api/login");
        System.out.println("  POST /api/registro/paciente");
        System.out.println("  POST /api/registro/medico");
        System.out.println("  POST /api/registro/clinica");
        System.out.println("  GET  /api/perfiles?texto=...");
        System.out.println("  GET  /api/perfil/{email}");
        System.out.println("  POST /api/valorar");
        System.out.println("  GET  /api/calificaciones/{email}");
        System.out.println("=========================================");


    }

    // ============================================================
    //  HANDLERS
    // ============================================================

    // ---------- LOGIN ----------
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            Map<String, String> creds = parseBody(exchange);
            String email = creds.get("email");
            String password = creds.get("password");

            Optional<Usuario> usuarioOpt = sistema.iniciarSesion(email, password);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("usuario", usuario);
                response.put("token", "fake-jwt-token");
                sendResponse(exchange, 200, gson.toJson(response));
            } else {
                sendResponse(exchange, 401, "{\"error\":\"Credenciales incorrectas\"}");
            }
        }
    }

    // ---------- REGISTRO PACIENTE ----------
    static class RegistroPacienteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            Map<String, String> data = parseBody(exchange);
            try {
                Paciente paciente = sistema.registrarPaciente(
                        data.get("email"),
                        data.get("password"),
                        data.get("telefono"),
                        data.get("nombre"),
                        data.get("apellido")
                );
                sistema.validarPerfilUsuario(paciente.getMail(), true);
                sendResponse(exchange, 201, "{\"mensaje\":\"Usuario creado con éxito\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // ---------- REGISTRO MÉDICO ----------
    static class RegistroMedicoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            Map<String, String> data = parseBody(exchange);
            try {
                Medico medico = sistema.registrarMedico(
                        data.get("email"),
                        data.get("password"),
                        data.get("telefono"),
                        data.get("nombre"),
                        data.get("apellido"),
                        data.get("matricula"),
                        data.get("dni"),
                        data.get("certificado")
                );
                sendResponse(exchange, 201, "{\"mensaje\":\"Usuario pendiente a revisión\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // ---------- REGISTRO CLÍNICA ----------
    static class RegistroClinicaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            Map<String, String> data = parseBody(exchange);
            try {
                Clinica clinica = sistema.registrarClinica(
                        data.get("email"),
                        data.get("password"),
                        data.get("telefono"),
                        data.get("nombre"),
                        data.get("apellido"),
                        data.get("ubicacion")
                );
                sendResponse(exchange, 201, "{\"mensaje\":\"Usuario pendiente a revisión\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // ---------- BUSCAR PERFILES ----------
    static class BuscarPerfilesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String texto = "";
            if (query != null && query.startsWith("texto=")) {
                texto = query.substring(6);
                try {
                    texto = URLDecoder.decode(texto, "UTF-8");
                } catch (Exception e) {
                    // Si falla, usar el texto original
                }
            }

            try {
                List<Usuario> resultados = sistema.consultarPerfiles(texto);
                sendResponse(exchange, 200, gson.toJson(resultados));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // ---------- OBTENER PERFIL ----------
    static class ObtenerPerfilHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String mail = path.substring("/api/perfil/".length());
            try {
                mail = URLDecoder.decode(mail, "UTF-8");
            } catch (Exception e) {
                // Si falla, usar el mail original
            }

            try {
                Optional<Usuario> usuarioOpt = sistema.buscarPorMail(mail);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();

                    // Si es médico, agregar calificaciones recientes
                    if (usuario instanceof Medico) {
                        Medico medico = (Medico) usuario;
                        List<Calificacion> calificaciones = sistema.consultarCalificacionesAprobadasRecientes(medico.getMail());
                        Map<String, Object> response = new HashMap<>();
                        // Copiar todos los campos del médico
                        response.put("mail", medico.getMail());
                        response.put("nombre", medico.getNombre());
                        response.put("apellido", medico.getApellido());
                        response.put("telefono", medico.getTelefono());
                        response.put("rol", medico.getRol());
                        response.put("estadoPerfil", medico.getEstadoPerfil());
                        response.put("fechaAlta", medico.getFechaAlta());
                        response.put("matricula", medico.getMatricula());
                        response.put("dni", medico.getDni());
                        response.put("certificado", medico.getCertificado());
                        response.put("puntPromMed", medico.getPuntPromMed());
                        response.put("especialidades", medico.getEspecialidades());
                        response.put("calificacionesRecientes", calificaciones);
                        sendResponse(exchange, 200, gson.toJson(response));
                    } else if (usuario instanceof Clinica) {
                        Clinica clinica = (Clinica) usuario;
                        Map<String, Object> response = new HashMap<>();
                        response.put("mail", clinica.getMail());
                        response.put("nombre", clinica.getNombre());
                        response.put("apellido", clinica.getApellido());
                        response.put("telefono", clinica.getTelefono());
                        response.put("rol", clinica.getRol());
                        response.put("estadoPerfil", clinica.getEstadoPerfil());
                        response.put("fechaAlta", clinica.getFechaAlta());
                        response.put("ubicacion", clinica.getUbicacion());
                        response.put("especialidades", clinica.getEspecialidades());

                        // Obtener datos completos de los médicos asociados
                        List<Map<String, Object>> medicosAsociados = new ArrayList<>();
                        for (String mailMedico : clinica.getMedicosAsociados()) {
                            Optional<Usuario> medicoOpt = sistema.buscarPorMail(mailMedico);
                            if (medicoOpt.isPresent() && medicoOpt.get() instanceof Medico) {
                                Medico m = (Medico) medicoOpt.get();
                                Map<String, Object> medicoData = new HashMap<>();
                                medicoData.put("mail", m.getMail());
                                medicoData.put("nombre", m.getNombre());
                                medicoData.put("apellido", m.getApellido());
                                medicoData.put("matricula", m.getMatricula());
                                medicoData.put("especialidades", m.getEspecialidades());
                                medicoData.put("puntPromMed", m.getPuntPromMed());
                                medicosAsociados.add(medicoData);
                            }
                        }
                        response.put("medicosAsociados", medicosAsociados);
                        sendResponse(exchange, 200, gson.toJson(response));
                    } else {
                        sendResponse(exchange, 200, gson.toJson(usuario));
                    }
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Usuario no encontrado\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // ---------- VALORAR MÉDICO ----------
    static class ValorarMedicoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Manejar preflight (OPTIONS)
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            try {
                Map<String, Object> data = parseBodyMap(exchange);

                // Obtener el mail del paciente desde la sesión (por ahora, lo pasamos en el body)
                // En una implementación real, esto vendría del token JWT
                String mailPaciente = (String) data.get("mailPaciente");
                String mailMedico = (String) data.get("mailMedico");
                String comentario = (String) data.get("comentario");
                int atencion = ((Number) data.get("atencion")).intValue();
                int comprension = ((Number) data.get("comprension")).intValue();
                int puntualidad = ((Number) data.get("puntualidad")).intValue();
                List<String> imagenes = (List<String>) data.get("imagenes");

                // Validar que el paciente existe y es paciente
                Optional<Usuario> pacienteOpt = sistema.buscarPorMail(mailPaciente);
                if (pacienteOpt.isEmpty() || !(pacienteOpt.get() instanceof Paciente)) {
                    sendResponse(exchange, 400, "{\"error\":\"El usuario no es un paciente válido\"}");
                    return;
                }

                // Validar que el médico existe
                Optional<Usuario> medicoOpt = sistema.buscarPorMail(mailMedico);
                if (medicoOpt.isEmpty() || !(medicoOpt.get() instanceof Medico)) {
                    sendResponse(exchange, 400, "{\"error\":\"El médico no existe\"}");
                    return;
                }

                // Crear la calificación
                Calificacion calificacion = sistema.valorarMedico(
                        mailPaciente, mailMedico, comentario,
                        atencion, comprension, puntualidad, imagenes
                );

                sendResponse(exchange, 201, "{\"mensaje\":\"Calificación enviada a revisión\"}");

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // ---------- OBTENER CALIFICACIONES ----------
    static class ObtenerCalificacionesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String mailMedico = path.substring("/api/calificaciones/".length());
            try {
                mailMedico = URLDecoder.decode(mailMedico, "UTF-8");
            } catch (Exception e) {
                // mantener original
            }

            try {
                // Obtener todas las calificaciones del sistema
                List<Calificacion> todas = sistema.getCalificaciones();
                List<Calificacion> calificaciones = new java.util.ArrayList<>();

                // Filtramos manualmente para evitar problemas con lambdas
                for (Calificacion c : todas) {
                    Medico medico = c.getMedico();
                    if (medico != null && medico.getMail() != null && medico.getMail().equalsIgnoreCase(mailMedico)) {
                        calificaciones.add(c);
                    }
                }

                // Crear respuesta simplificada
                List<Map<String, Object>> responseList = new java.util.ArrayList<>();
                for (Calificacion cal : calificaciones) {
                    Map<String, Object> item = new HashMap<>();
                    Paciente paciente = cal.getPaciente();
                    item.put("id", paciente.getMail() + cal.getFecha().toString());

                    Map<String, String> pacienteInfo = new HashMap<>();
                    pacienteInfo.put("nombre", paciente.getNombre());
                    pacienteInfo.put("apellido", paciente.getApellido());
                    item.put("paciente", pacienteInfo);

                    item.put("comentario", cal.getComentario());
                    item.put("puntProm", cal.getPuntProm());
                    item.put("puntAtencion", cal.getPuntAtencion());
                    item.put("puntComprension", cal.getPuntComprension());
                    item.put("puntPuntualidad", cal.getPuntPuntualidad());
                    item.put("fecha", cal.getFecha().toString());
                    item.put("estado", cal.getEstado().toString());
                    item.put("indiceGlobal", sistema.getCalificaciones().indexOf(cal));
                    responseList.add(item);
                }

                sendResponse(exchange, 200, gson.toJson(responseList));
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    static class ModificarPerfilHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            Map<String, Object> data = parseBodyMap(exchange);
            try {
                String mail = (String) data.get("mail");
                String nombre = (String) data.get("nombre");
                String apellido = (String) data.get("apellido");
                String telefono = (String) data.get("telefono");
                String ubicacion = (String) data.get("ubicacion");
                String nuevaPassword = (String) data.get("nuevaPassword");

                sistema.modificarPerfil(mail, nombre, apellido, telefono, ubicacion, nuevaPassword);
                sendResponse(exchange, 200, "{\"mensaje\":\"Perfil actualizado\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }

        }
    }

    static class SolicitarAsociacionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            Map<String, Object> body = parseBodyMap(exchange);
            try {
                String clinica = (String) body.get("clinica");
                String medico = (String) body.get("medico");
                SolicitudAsociacion solicitud = sistema.solicitarAsociacionMedico(clinica, medico);
                sendResponse(exchange, 201, "{\"mensaje\":\"Solicitud enviada\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class DesvincularMedicoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"DELETE".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 6) {
                sendResponse(exchange, 400, "{\"error\":\"URL inválida\"}");
                return;
            }
            try {
                String clinicaMail = parts[3];
                String medicoMail = parts[5];
                sistema.desasociarMedicoDeClinica(clinicaMail, medicoMail);
                sendResponse(exchange, 200, "{\"mensaje\":\"Médico desvinculado\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class SolicitudesPendientesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String mail = path.substring("/api/solicitudes/pendientes/".length());
            try {
                mail = URLDecoder.decode(mail, "UTF-8");
            } catch (Exception e) {}
            try {
                List<SolicitudAsociacion> solicitudes = sistema.getSolicitudesPendientesParaMedico(mail);
                List<Map<String, Object>> response = new ArrayList<>();
                for (SolicitudAsociacion s : solicitudes) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("clinica", s.getClinica().getMail());
                    item.put("clinicaNombre", s.getClinica().getNombre());
                    item.put("fecha", s.getFechaSolicitud().toString());
                    response.add(item);
                }
                sendResponse(exchange, 200, gson.toJson(response));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class ResponderSolicitudHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendResponse(exchange, 400, "{\"error\":\"URL inválida\"}");
                return;
            }
            int idSolicitud;
            try {
                idSolicitud = Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"ID inválido\"}");
                return;
            }
            Map<String, Object> body = parseBodyMap(exchange);
            boolean aceptar = Boolean.parseBoolean(body.get("aceptar").toString());
            String mailMedico = (String) body.get("mailMedico");
            try {
                sistema.responderSolicitudAsociacion(idSolicitud, mailMedico, aceptar);
                sendResponse(exchange, 200, "{\"mensaje\":\"Solicitud respondida\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class EspecialidadMedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Manejar OPTIONS (preflight)
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                // GET: listar todas las especialidades activas
                if (method.equalsIgnoreCase("GET")) {
                    List<EspecialidadMed> especialidades = sistema.consultarEspecialidadesMed();
                    sendResponse(exchange, 200, gson.toJson(especialidades));
                    return;
                }

                // POST: crear nueva especialidad
                if (method.equalsIgnoreCase("POST")) {
                    Map<String, String> body = parseBody(exchange);
                    String nombre = body.get("nombre");
                    if (nombre == null || nombre.isBlank()) {
                        sendResponse(exchange, 400, "{\"error\":\"El nombre es obligatorio\"}");
                        return;
                    }
                    EspecialidadMed creada = sistema.crearEspecialidadMed(nombre);
                    sendResponse(exchange, 201, gson.toJson(creada));
                    return;
                }

                // PUT: modificar especialidad
                if (method.equalsIgnoreCase("PUT")) {
                    Map<String, String> body = parseBody(exchange);
                    String nombreActual = body.get("nombreActual");
                    String nombreNuevo = body.get("nombreNuevo");
                    if (nombreActual == null || nombreNuevo == null) {
                        sendResponse(exchange, 400, "{\"error\":\"Faltan datos\"}");
                        return;
                    }
                    sistema.modificarEspecialidadMed(nombreActual, nombreNuevo);
                    sendResponse(exchange, 200, "{\"mensaje\":\"Especialidad modificada\"}");
                    return;
                }

                // DELETE: eliminar especialidad (dar de baja)
                if (method.equalsIgnoreCase("DELETE")) {
                    // path: /api/especialidades/med/{nombre}
                    String nombre = path.substring("/api/especialidades/med/".length());
                    nombre = URLDecoder.decode(nombre, "UTF-8");
                    sistema.eliminarEspecialidadMed(nombre);
                    sendResponse(exchange, 200, "{\"mensaje\":\"Especialidad eliminada\"}");
                    return;
                }

                // Metodo no permitido
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class EspecialidadCliHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if (method.equalsIgnoreCase("GET")) {
                    List<EspecialidadCli> especialidades = sistema.consultarEspecialidadesCli();
                    sendResponse(exchange, 200, gson.toJson(especialidades));
                    return;
                }

                if (method.equalsIgnoreCase("POST")) {
                    Map<String, String> body = parseBody(exchange);
                    String nombre = body.get("nombre");
                    if (nombre == null || nombre.isBlank()) {
                        sendResponse(exchange, 400, "{\"error\":\"El nombre es obligatorio\"}");
                        return;
                    }
                    EspecialidadCli creada = sistema.crearEspecialidadCli(nombre);
                    sendResponse(exchange, 201, gson.toJson(creada));
                    return;
                }

                if (method.equalsIgnoreCase("PUT")) {
                    Map<String, String> body = parseBody(exchange);
                    String nombreActual = body.get("nombreActual");
                    String nombreNuevo = body.get("nombreNuevo");
                    if (nombreActual == null || nombreNuevo == null) {
                        sendResponse(exchange, 400, "{\"error\":\"Faltan datos\"}");
                        return;
                    }
                    sistema.modificarEspecialidadCli(nombreActual, nombreNuevo);
                    sendResponse(exchange, 200, "{\"mensaje\":\"Especialidad modificada\"}");
                    return;
                }

                if (method.equalsIgnoreCase("DELETE")) {
                    String nombre = path.substring("/api/especialidades/cli/".length());
                    nombre = URLDecoder.decode(nombre, "UTF-8");
                    sistema.eliminarEspecialidadCli(nombre);
                    sendResponse(exchange, 200, "{\"mensaje\":\"Especialidad eliminada\"}");
                    return;
                }

                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class CalificacionesPendientesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            try {
                List<Calificacion> pendientes = sistema.consultarCalificacionesPendientes();
                List<Map<String, Object>> response = new ArrayList<>();
                for (Calificacion cal : pendientes) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", cal.getPaciente().getMail() + cal.getFecha().toString());
                    item.put("paciente", Map.of(
                            "nombre", cal.getPaciente().getNombre(),
                            "apellido", cal.getPaciente().getApellido(),
                            "mail", cal.getPaciente().getMail()
                    ));
                    item.put("medico", Map.of(
                            "nombre", cal.getMedico().getNombre(),
                            "apellido", cal.getMedico().getApellido(),
                            "mail", cal.getMedico().getMail(),
                            "matricula", cal.getMedico().getMatricula()
                    ));
                    item.put("comentario", cal.getComentario());
                    item.put("puntAtencion", cal.getPuntAtencion());
                    item.put("puntComprension", cal.getPuntComprension());
                    item.put("puntPuntualidad", cal.getPuntPuntualidad());
                    item.put("puntProm", cal.getPuntProm());
                    item.put("fecha", cal.getFecha().toString());
                    item.put("estado", cal.getEstado().toString());
                    // Guardar índice para usar en validar
                    item.put("indice", sistema.getCalificaciones().indexOf(cal));
                    response.add(item);
                }
                sendResponse(exchange, 200, gson.toJson(response));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class ObtenerCalificacionPorIndiceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            // path: /api/calificacion/{indice}
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendResponse(exchange, 400, "{\"error\":\"URL inválida\"}");
                return;
            }
            try {
                int indice = Integer.parseInt(parts[3]);
                List<Calificacion> todas = sistema.getCalificaciones();
                if (indice < 0 || indice >= todas.size()) {
                    sendResponse(exchange, 404, "{\"error\":\"Calificación no encontrada\"}");
                    return;
                }
                Calificacion cal = todas.get(indice);
                // Construir respuesta
                Map<String, Object> response = new HashMap<>();
                response.put("paciente", Map.of(
                        "nombre", cal.getPaciente().getNombre(),
                        "apellido", cal.getPaciente().getApellido(),
                        "mail", cal.getPaciente().getMail()
                ));
                response.put("medico", Map.of(
                        "nombre", cal.getMedico().getNombre(),
                        "apellido", cal.getMedico().getApellido(),
                        "mail", cal.getMedico().getMail()
                ));
                response.put("comentario", cal.getComentario());
                response.put("puntProm", cal.getPuntProm());
                response.put("puntAtencion", cal.getPuntAtencion());
                response.put("puntComprension", cal.getPuntComprension());
                response.put("puntPuntualidad", cal.getPuntPuntualidad());
                response.put("fecha", cal.getFecha().toString());
                response.put("estado", cal.getEstado().toString());
                sendResponse(exchange, 200, gson.toJson(response));
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"Índice inválido\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
        static class TodasLasCalificacionesHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    sendCorsResponse(exchange);
                    return;
                }
                if (!"GET".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                    return;
                }

                try {
                    List<Calificacion> todas = sistema.getCalificaciones();
                    List<Map<String, Object>> response = new ArrayList<>();
                    for (Calificacion cal : todas) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", cal.getPaciente().getMail() + cal.getFecha().toString());
                        item.put("paciente", Map.of(
                                "nombre", cal.getPaciente().getNombre(),
                                "apellido", cal.getPaciente().getApellido()
                        ));
                        item.put("medico", Map.of(
                                "nombre", cal.getMedico().getNombre(),
                                "apellido", cal.getMedico().getApellido()
                        ));
                        item.put("comentario", cal.getComentario());
                        item.put("puntAtencion", cal.getPuntAtencion());
                        item.put("puntComprension", cal.getPuntComprension());
                        item.put("puntPuntualidad", cal.getPuntPuntualidad());
                        item.put("puntProm", cal.getPuntProm());
                        item.put("fecha", cal.getFecha().toString());
                        item.put("estado", cal.getEstado().toString());
                        response.add(item);
                    }
                    sendResponse(exchange, 200, gson.toJson(response));
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        }


    static class ValidarCalificacionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            Map<String, Object> body = parseBodyMap(exchange);
            try {
                int indice = ((Number) body.get("indice")).intValue();
                boolean aceptar = (boolean) body.get("aceptar");
                String motivo = (String) body.get("motivo");

                sistema.validarComentario(indice, aceptar);

                // Si se rechaza y hay motivo, guardarlo o enviar email
                if (!aceptar && motivo != null && !motivo.isBlank()) {
                    System.out.println("Motivo de rechazo: " + motivo);
                    // Aquí puedes implementar el envío de email
                }

                sendResponse(exchange, 200, "{\"mensaje\":\"Calificación " + (aceptar ? "aceptada" : "rechazada") + "\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class CrearReclamoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            Map<String, Object> body = parseBodyMap(exchange);
            try {
                String mailMedico = (String) body.get("mailMedico");
                int idCalificacion = ((Number) body.get("idCalificacion")).intValue(); // ← CAMBIO: ahora es idCalificacion
                String descripcion = (String) body.get("descripcion");

                if (descripcion == null || descripcion.isBlank()) {
                    sendResponse(exchange, 400, "{\"error\":\"La descripción es obligatoria\"}");
                    return;
                }
                if (descripcion.length() > 1000) {
                    sendResponse(exchange, 400, "{\"error\":\"La descripción no puede superar los 1000 caracteres\"}");
                    return;
                }

                Reclamo reclamo = sistema.denunciarValoracion(mailMedico, idCalificacion, descripcion);
                sendResponse(exchange, 201, "{\"mensaje\":\"Reclamo guardado con éxito\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class ReclamosPendientesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            try {
                List<Reclamo> pendientes = sistema.getReclamosPendientes();
                List<Map<String, Object>> response = new ArrayList<>();
                for (Reclamo r : pendientes) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("indice", sistema.getReclamos().indexOf(r));
                    item.put("medico", Map.of(
                            "nombre", r.getMedico().getNombre(),
                            "apellido", r.getMedico().getApellido(),
                            "mail", r.getMedico().getMail()
                    ));
                    item.put("descripcion", r.getDescripcion());
                    item.put("fecha", r.getFechaIni().toString());
                    item.put("calificacion", Map.of(
                            "comentario", r.getCalificacion().getComentario(),
                            "puntProm", r.getCalificacion().getPuntProm(),
                            "puntAtencion", r.getCalificacion().getPuntAtencion(),
                            "puntComprension", r.getCalificacion().getPuntComprension(),
                            "puntPuntualidad", r.getCalificacion().getPuntPuntualidad(),
                            "paciente", Map.of(
                                    "nombre", r.getCalificacion().getPaciente().getNombre(),
                                    "apellido", r.getCalificacion().getPaciente().getApellido(),
                                    "mail", r.getCalificacion().getPaciente().getMail()
                            )
                    ));
                    response.add(item);
                }
                sendResponse(exchange, 200, gson.toJson(response));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class ObtenerReclamoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendResponse(exchange, 400, "{\"error\":\"URL inválida\"}");
                return;
            }
            try {
                int indice = Integer.parseInt(parts[3]);
                List<Reclamo> todos = sistema.getReclamos();
                if (indice < 0 || indice >= todos.size()) {
                    sendResponse(exchange, 404, "{\"error\":\"Reclamo no encontrado\"}");
                    return;
                }
                Reclamo r = todos.get(indice);
                Map<String, Object> response = new HashMap<>();
                response.put("indice", indice);
                response.put("medico", Map.of(
                        "nombre", r.getMedico().getNombre(),
                        "apellido", r.getMedico().getApellido(),
                        "mail", r.getMedico().getMail()
                ));
                response.put("descripcion", r.getDescripcion());
                response.put("fecha", r.getFechaIni().toString());
                response.put("calificacion", Map.of(
                        "comentario", r.getCalificacion().getComentario(),
                        "puntProm", r.getCalificacion().getPuntProm(),
                        "puntAtencion", r.getCalificacion().getPuntAtencion(),
                        "puntComprension", r.getCalificacion().getPuntComprension(),
                        "puntPuntualidad", r.getCalificacion().getPuntPuntualidad(),
                        "paciente", Map.of(
                                "nombre", r.getCalificacion().getPaciente().getNombre(),
                                "apellido", r.getCalificacion().getPaciente().getApellido(),
                                "mail", r.getCalificacion().getPaciente().getMail()
                        )
                ));
                sendResponse(exchange, 200, gson.toJson(response));
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"Índice inválido\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class ResolverReclamoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            Map<String, Object> body = parseBodyMap(exchange);
            try {
                int indice = ((Number) body.get("indice")).intValue();
                boolean aceptar = (boolean) body.get("aceptar");
                String motivo = (String) body.get("motivo");

                sistema.resolverReclamo(indice, aceptar, motivo);
                sendResponse(exchange, 200, "{\"mensaje\":\"Reclamo resuelto\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    static class UsuariosPendientesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            try {
                List<Usuario> pendientes = sistema.consultarUsuariosPendientes();
                List<Map<String, Object>> response = new ArrayList<>();
                for (Usuario u : pendientes) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("mail", u.getMail());
                    item.put("nombre", u.getNombre());
                    item.put("apellido", u.getApellido());
                    item.put("telefono", u.getTelefono());
                    item.put("rol", u.getRol().toString());
                    item.put("fechaAlta", u.getFechaAlta().toString());
                    if (u instanceof Medico) {
                        Medico m = (Medico) u;
                        item.put("dni", m.getDni());
                        item.put("matricula", m.getMatricula());
                        item.put("certificado", m.getCertificado());
                    }
                    if (u instanceof Clinica) {
                        Clinica c = (Clinica) u;
                        item.put("ubicacion", c.getUbicacion());
                    }
                    response.add(item);
                }
                sendResponse(exchange, 200, gson.toJson(response));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class UsuariosValidadosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            try {
                List<Usuario> validados = sistema.consultarUsuariosValidados();
                List<Map<String, Object>> response = new ArrayList<>();
                for (Usuario u : validados) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("mail", u.getMail());
                    item.put("nombre", u.getNombre());
                    item.put("apellido", u.getApellido());
                    item.put("rol", u.getRol().toString());
                    item.put("fechaAlta", u.getFechaAlta().toString());
                    response.add(item);
                }
                sendResponse(exchange, 200, gson.toJson(response));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class EstadisticasRegistrosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            try {
                Map<String, Object> stats = sistema.getEstadisticasRegistros();
                sendResponse(exchange, 200, gson.toJson(stats));
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class ValidarUsuarioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            Map<String, Object> body = parseBodyMap(exchange);
            try {
                String mail = (String) body.get("mail");
                boolean aceptar = (boolean) body.get("aceptar");
                String motivo = (String) body.get("motivo");
                if (mail == null || mail.isBlank()) {
                    sendResponse(exchange, 400, "{\"error\":\"Mail requerido\"}");
                    return;
                }
                if (motivo == null || motivo.isBlank()) {
                    sendResponse(exchange, 400, "{\"error\":\"Debe proporcionar un motivo\"}");
                    return;
                }
                if (motivo.length() > 3000) {
                    sendResponse(exchange, 400, "{\"error\":\"El motivo no puede superar los 3000 caracteres\"}");
                    return;
                }
                sistema.validarUsuario(mail, aceptar, motivo);
                sendResponse(exchange, 200, "{\"mensaje\":\"Usuario validado\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class BuscarUsuarioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String email = path.substring("/api/usuario/".length());
            try {
                email = URLDecoder.decode(email, "UTF-8");
                Optional<Usuario> usuario = sistema.buscarPorMail(email);
                if (usuario.isPresent()) {
                    sendResponse(exchange, 200, "{\"existe\":true, \"mail\":\"" + usuario.get().getMail() + "\"}");
                } else {
                    sendResponse(exchange, 404, "{\"existe\":false, \"error\":\"Usuario no encontrado\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }


    static class CambiarContrasenaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendCorsResponse(exchange);
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}");
                return;
            }
            Map<String, Object> body = parseBodyMap(exchange);
            try {
                String mail = (String) body.get("mail");
                String nuevaContrasena = (String) body.get("nuevaContrasena");
                if (mail == null || mail.isBlank()) {
                    sendResponse(exchange, 400, "{\"error\":\"Mail requerido\"}");
                    return;
                }
                if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
                    sendResponse(exchange, 400, "{\"error\":\"La nueva contraseña debe tener al menos 6 caracteres\"}");
                    return;
                }
                sistema.cambiarContrasena(mail, nuevaContrasena);
                sendResponse(exchange, 200, "{\"mensaje\":\"Contraseña cambiada con éxito\"}");
            } catch (Exception e) {
                sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }


    //  UTILIDADES


    @SuppressWarnings("unchecked")
    private static Map<String, String> parseBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        if (body == null || body.isEmpty()) {
            return new HashMap<>();
        }
        return gson.fromJson(body, Map.class);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseBodyMap(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        if (body == null || body.isEmpty()) {
            return new HashMap<>();
        }
        return gson.fromJson(body, Map.class);
    }

    private static void sendCorsResponse(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(204, -1);
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static void cargarDatosDePrueba() {
        try {
            sistema.registrarPaciente("paciente@mail.com", "1234", "111111", "Juan", "Perez");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Paciente ya existe: " + e.getMessage());
        }

        try {
            sistema.registrarMedico("medico@mail.com", "1234", "222222", "Ana", "Lopez",
                    "M-100", "30111222", "certificado.pdf");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Médico ya existe: " + e.getMessage());
        }

        try {
            sistema.registrarClinica("clinica@mail.com", "1234", "333333", "Clinica Norte", "Clinica",
                    "Mendoza");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Clínica ya existe: " + e.getMessage());
        }

        try {
            sistema.registrarAsistente("asistente@mail.com", "1234", "444444", "Sofia", "Asistente");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Asistente ya existe: " + e.getMessage());
        }

        try {
            sistema.registrarAdministrador("admin@mail.com", "1234", "555555", "Admin", "Sistema");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Administrador ya existe: " + e.getMessage());
        }

        // Validar perfiles
        try {
            sistema.validarPerfilUsuario("paciente@mail.com", true);
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo validar paciente: " + e.getMessage());
        }
        try {
            sistema.validarPerfilUsuario("medico@mail.com", true);
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo validar médico: " + e.getMessage());
        }
        try {
            sistema.validarPerfilUsuario("clinica@mail.com", true);
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo validar clínica: " + e.getMessage());
        }

        // Especialidades
        try {
            sistema.crearEspecialidadMed("Cardiologia");
        } catch (Exception e) {
            System.out.println("⚠️ Especialidad médica ya existe: " + e.getMessage());
        }

        try {
            sistema.crearEspecialidadCli("Guardia");
        } catch (Exception e) {
            System.out.println("⚠️ Especialidad clínica ya existe: " + e.getMessage());
        }

        try {
            sistema.asignarEspecialidadMed("medico@mail.com", "Cardiologia");
        } catch (Exception e) {
            System.out.println("⚠️ Especialidad médica ya asignada al médico: " + e.getMessage());
        }

        try {
            sistema.asignarEspecialidadCli("clinica@mail.com", "Guardia");
        } catch (Exception e) {
            System.out.println("⚠️ Especialidad clínica ya asignada a la clínica: " + e.getMessage());
        }

        // Calificación de prueba
        try {
            Calificacion calificacion = sistema.valorarMedico("paciente@mail.com", "medico@mail.com",
                    "Muy buena atencion", 5, 5, 4, List.of("turno.jpg"));
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo crear calificación de prueba: " + e.getMessage());
        }
    }
}



