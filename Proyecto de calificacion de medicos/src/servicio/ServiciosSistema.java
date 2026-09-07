package servicio;

import BasedeDatos.BasedeDatos;
import modelo.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ServiciosSistema {

    private final Map<String, String> codigosRecuperacion = new HashMap<>();

    public Paciente registrarPaciente(String mail, String pass, String telefono, String nombre, String apellido) {
        validarMailDisponible(mail);
        validarTexto(pass, "La contraseña es obligatoria");

        String sqlUsuario = "INSERT INTO usuario (mail, contrasena, telefono, nombre, apellido, id_tipo_perfil, estado_perfil) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'PACIENTE'), 'VALIDADO')";
        String sqlPaciente = "INSERT INTO paciente (id_usuario) VALUES (?)";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement psUser = conn.prepareStatement(sqlUsuario, new String[]{"id_usuario"});
            psUser.setString(1, mail);
            psUser.setString(2, pass);
            psUser.setString(3, telefono);
            psUser.setString(4, nombre);
            psUser.setString(5, apellido);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                PreparedStatement psPac = conn.prepareStatement(sqlPaciente);
                psPac.setInt(1, idUsuario);
                psPac.executeUpdate();
                conn.commit();
                return new Paciente(mail, pass, telefono, nombre, apellido);
            } else {
                conn.rollback();
                throw new SQLException("No se pudo obtener el ID del usuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar paciente: " + e.getMessage(), e);
        }
    }

    public Medico registrarMedico(String mail, String pass, String telefono, String nombre, String apellido,
                                  String matricula, String dni, String certificado) {
        validarMailDisponible(mail);
        validarTexto(pass, "La contraseña es obligatoria");
        validarTexto(certificado, "El certificado es obligatorio");

        String sqlUsuario = "INSERT INTO usuario (mail, contrasena, telefono, nombre, apellido, id_tipo_perfil, estado_perfil) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'MEDICO'), 'PENDIENTE')";
        String sqlMedico = "INSERT INTO medico (id_usuario, matricula, dni, certificado, apellido) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement psUser = conn.prepareStatement(sqlUsuario, new String[]{"id_usuario"});
            psUser.setString(1, mail);
            psUser.setString(2, pass);
            psUser.setString(3, telefono);
            psUser.setString(4, nombre);
            psUser.setString(5, apellido);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                PreparedStatement psMed = conn.prepareStatement(sqlMedico);
                psMed.setInt(1, idUsuario);
                psMed.setString(2, matricula);
                psMed.setString(3, dni);
                psMed.setString(4, certificado);
                psMed.setString(5, apellido);
                psMed.executeUpdate();
                conn.commit();
                return new Medico(mail, pass, telefono, nombre, apellido, matricula, dni, certificado);
            } else {
                conn.rollback();
                throw new SQLException("No se pudo obtener el ID del usuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar médico: " + e.getMessage(), e);
        }
    }

    public Clinica registrarClinica(String mail, String pass, String telefono, String nombre, String apellido,
                                    String ubicacion) {
        validarMailDisponible(mail);
        validarTexto(pass, "La contraseña es obligatoria");
        validarTexto(ubicacion, "La ubicación es obligatoria");

        String sqlUsuario = "INSERT INTO usuario (mail, contrasena, telefono, nombre, id_tipo_perfil, estado_perfil) " +
                "VALUES (?, ?, ?, ?, (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'CLINICA'), 'PENDIENTE')";
        String sqlClinica = "INSERT INTO clinica (id_usuario, ubicacion) VALUES (?, ?)";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement psUser = conn.prepareStatement(sqlUsuario, new String[]{"id_usuario"});
            psUser.setString(1, mail);
            psUser.setString(2, pass);
            psUser.setString(3, telefono);
            psUser.setString(4, nombre);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                PreparedStatement psCli = conn.prepareStatement(sqlClinica);
                psCli.setInt(1, idUsuario);
                psCli.setString(2, ubicacion);
                psCli.executeUpdate();
                conn.commit();
                return new Clinica(mail, pass, telefono, nombre, apellido, ubicacion);
            } else {
                conn.rollback();
                throw new SQLException("No se pudo obtener el ID del usuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar clínica: " + e.getMessage(), e);
        }
    }

    public AsistenteConsulta registrarAsistente(String mail, String pass, String telefono, String nombre, String apellido) {
        validarMailDisponible(mail);

        String sqlUsuario = "INSERT INTO usuario (mail, contrasena, telefono, nombre, apellido, id_tipo_perfil, estado_perfil) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'ASISTENTE_CONSULTA'), 'VALIDADO')";
        String sqlAsistente = "INSERT INTO asistente_consulta (id_usuario) VALUES (?)";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement psUser = conn.prepareStatement(sqlUsuario, new String[]{"id_usuario"});
            psUser.setString(1, mail);
            psUser.setString(2, pass);
            psUser.setString(3, telefono);
            psUser.setString(4, nombre);
            psUser.setString(5, apellido);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                PreparedStatement psAsis = conn.prepareStatement(sqlAsistente);
                psAsis.setInt(1, idUsuario);
                psAsis.executeUpdate();
                conn.commit();
                return new AsistenteConsulta(mail, pass, telefono, nombre, apellido);
            } else {
                conn.rollback();
                throw new SQLException("No se pudo obtener el ID del usuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar asistente: " + e.getMessage(), e);
        }
    }

    public Administrador registrarAdministrador(String mail, String pass, String telefono, String nombre, String apellido) {
        validarMailDisponible(mail);

        String sqlUsuario = "INSERT INTO usuario (mail, contrasena, telefono, nombre, apellido, id_tipo_perfil, estado_perfil) " +
                "VALUES (?, ?, ?, ?, ?, (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'ADMINISTRADOR'), 'VALIDADO')";
        String sqlAdmin = "INSERT INTO administrador (id_usuario) VALUES (?)";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement psUser = conn.prepareStatement(sqlUsuario, new String[]{"id_usuario"});
            psUser.setString(1, mail);
            psUser.setString(2, pass);
            psUser.setString(3, telefono);
            psUser.setString(4, nombre);
            psUser.setString(5, apellido);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                PreparedStatement psAdmin = conn.prepareStatement(sqlAdmin);
                psAdmin.setInt(1, idUsuario);
                psAdmin.executeUpdate();
                conn.commit();
                return new Administrador(mail, pass, telefono, nombre, apellido);
            } else {
                conn.rollback();
                throw new SQLException("No se pudo obtener el ID del usuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar administrador: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> iniciarSesion(String mail, String pass) {
        String sql = "SELECT u.*, tp.nombre_p as rol FROM usuario u " +
                "JOIN tipo_perfil tp ON u.id_tipo_perfil = tp.id_tipo_perfil " +
                "WHERE u.mail = ? AND u.contrasena = ? AND u.estado_perfil = 'VALIDADO'";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mail);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario usuario = construirUsuarioDesdeResultSet(rs);
                return Optional.of(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // ============================================================
    // BÚSQUEDA Y CONSULTA DE USUARIOS
    // ============================================================

    public Optional<Usuario> buscarPorMail(String mail) {
        String sql = "SELECT u.*, tp.nombre_p as rol FROM usuario u " +
                "JOIN tipo_perfil tp ON u.id_tipo_perfil = tp.id_tipo_perfil " +
                "WHERE u.mail = ?";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mail);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario usuario = construirUsuarioDesdeResultSet(rs);
                return Optional.of(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Usuario> consultarPerfiles(String texto) {
        String consulta = texto == null ? "" : texto.toLowerCase();
        if (consulta.isEmpty()) return new ArrayList<>();

        String sql = "SELECT u.*, tp.nombre_p as rol FROM usuario u " +
                "JOIN tipo_perfil tp ON u.id_tipo_perfil = tp.id_tipo_perfil " +
                "WHERE u.estado_perfil = 'VALIDADO' " +
                "AND tp.nombre_p IN ('MEDICO', 'CLINICA') " +
                "AND (LOWER(u.nombre) LIKE ? OR LOWER(u.apellido) LIKE ? OR LOWER(u.mail) LIKE ?)";

        List<Usuario> resultados = new ArrayList<>();
        String likePattern = "%" + consulta + "%";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            ps.setString(2, likePattern);
            ps.setString(3, likePattern);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Usuario usuario = construirUsuarioDesdeResultSet(rs);
                resultados.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }


    public void modificarPerfil(String mail, String nombre, String apellido, String telefono, String ubicacion, String nuevaPassword) {
        StringBuilder sql = new StringBuilder("UPDATE usuario SET ");
        List<Object> params = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            sql.append("nombre = ?, ");
            params.add(nombre);
        }
        if (apellido != null && !apellido.isBlank()) {
            sql.append("apellido = ?, ");
            params.add(apellido);
        }
        if (telefono != null && !telefono.isBlank()) {
            sql.append("telefono = ?, ");
            params.add(telefono);
        }
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            if (nuevaPassword.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }
            sql.append("contrasena = ?, ");
            params.add(nuevaPassword);
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No hay datos para modificar");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE mail = ?");
        params.add(mail);

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.executeUpdate();

            if (ubicacion != null && !ubicacion.isBlank()) {
                String sqlUbicacion = "UPDATE clinica SET ubicacion = ? WHERE id_usuario = (SELECT id_usuario FROM usuario WHERE mail = ?)";
                PreparedStatement psUbic = conn.prepareStatement(sqlUbicacion);
                psUbic.setString(1, ubicacion);
                psUbic.setString(2, mail);
                psUbic.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al modificar perfil: " + e.getMessage(), e);
        }
    }

    // Versión simplificada para Main.java
    public void modificarPerfil(String mail, String nombre, String apellido, String telefono) {
        modificarPerfil(mail, nombre, apellido, telefono, null, null);
    }


    public void validarPerfilUsuario(String mail, boolean aceptado) {
        String estado = aceptado ? "VALIDADO" : "RECHAZADO";
        String sql = "UPDATE usuario SET estado_perfil = ? WHERE mail = ?";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, mail);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar perfil: " + e.getMessage(), e);
        }
    }

    public List<Usuario> consultarUsuariosPendientes() {
        String sql = "SELECT u.*, tp.nombre_p as rol FROM usuario u " +
                "JOIN tipo_perfil tp ON u.id_tipo_perfil = tp.id_tipo_perfil " +
                "WHERE u.estado_perfil = 'PENDIENTE' " +
                "AND tp.nombre_p IN ('MEDICO', 'CLINICA')";

        List<Usuario> pendientes = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario usuario = construirUsuarioDesdeResultSet(rs);
                pendientes.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendientes;
    }

    public EspecialidadMed crearEspecialidadMed(String nombre) {
        validarTexto(nombre, "El nombre es obligatorio");
        if (buscarEspecialidadMed(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad médica con ese nombre");
        }

        String sql = "INSERT INTO especialidad_med (nombre, fecha_crea) VALUES (?, SYSDATE)";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_especialidad_med"})) {
            ps.setString(1, nombre.trim());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return new EspecialidadMed(nombre.trim());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear especialidad médica: " + e.getMessage(), e);
        }
        return null;
    }

    public Optional<EspecialidadMed> buscarEspecialidadMed(String nombre) {
        if (nombre == null || nombre.isBlank()) return Optional.empty();
        String sql = "SELECT * FROM especialidad_med WHERE LOWER(nombre) = LOWER(?) AND fecha_baja IS NULL";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EspecialidadMed esp = new EspecialidadMed(rs.getString("nombre"));
                return Optional.of(esp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<EspecialidadMed> consultarEspecialidadesMed() {
        String sql = "SELECT * FROM especialidad_med WHERE fecha_baja IS NULL ORDER BY nombre";
        List<EspecialidadMed> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EspecialidadMed esp = new EspecialidadMed(rs.getString("nombre"));
                lista.add(esp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void modificarEspecialidadMed(String nombreActual, String nombreNuevo) {
        validarTexto(nombreActual, "El nombre actual es obligatorio");
        validarTexto(nombreNuevo, "El nombre nuevo es obligatorio");
        if (buscarEspecialidadMed(nombreNuevo).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad médica con ese nombre");
        }

        String sql = "UPDATE especialidad_med SET nombre = ? WHERE LOWER(nombre) = LOWER(?) AND fecha_baja IS NULL";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreNuevo.trim());
            ps.setString(2, nombreActual.trim());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Especialidad médica no encontrada");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al modificar especialidad médica: " + e.getMessage(), e);
        }
    }

    public void eliminarEspecialidadMed(String nombre) {
        validarTexto(nombre, "El nombre es obligatorio");
        String sql = "UPDATE especialidad_med SET fecha_baja = SYSDATE WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Especialidad médica no encontrada");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar especialidad médica: " + e.getMessage(), e);
        }
    }

    public void asignarEspecialidadMed(String mailMedico, String nombreEspecialidad) {
        validarTexto(mailMedico, "El mail del médico es obligatorio");
        validarTexto(nombreEspecialidad, "El nombre de la especialidad es obligatorio");

        String sqlInsert = "INSERT INTO medico_especialidad (id_usuario, id_especialidad_med) " +
                "VALUES ((SELECT id_usuario FROM usuario WHERE mail = ?), " +
                "(SELECT id_especialidad_med FROM especialidad_med WHERE LOWER(nombre) = LOWER(?) AND fecha_baja IS NULL))";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setString(1, mailMedico.trim());
            ps.setString(2, nombreEspecialidad.trim());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar especialidad médica: " + e.getMessage(), e);
        }
    }

    public EspecialidadCli crearEspecialidadCli(String nombre) {
        validarTexto(nombre, "El nombre es obligatorio");
        if (buscarEspecialidadCli(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad de clínica con ese nombre");
        }

        String sql = "INSERT INTO especialidad_cli (nombre, fecha_crea) VALUES (?, SYSDATE)";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_especialidad_cli"})) {
            ps.setString(1, nombre.trim());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return new EspecialidadCli(nombre.trim());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear especialidad de clínica: " + e.getMessage(), e);
        }
        return null;
    }

    public Optional<EspecialidadCli> buscarEspecialidadCli(String nombre) {
        if (nombre == null || nombre.isBlank()) return Optional.empty();
        String sql = "SELECT * FROM especialidad_cli WHERE LOWER(nombre) = LOWER(?) AND fecha_baja IS NULL";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EspecialidadCli esp = new EspecialidadCli(rs.getString("nombre"));
                return Optional.of(esp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<EspecialidadCli> consultarEspecialidadesCli() {
        String sql = "SELECT * FROM especialidad_cli WHERE fecha_baja IS NULL ORDER BY nombre";
        List<EspecialidadCli> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EspecialidadCli esp = new EspecialidadCli(rs.getString("nombre"));
                lista.add(esp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void modificarEspecialidadCli(String nombreActual, String nombreNuevo) {
        validarTexto(nombreActual, "El nombre actual es obligatorio");
        validarTexto(nombreNuevo, "El nombre nuevo es obligatorio");
        if (buscarEspecialidadCli(nombreNuevo).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad de clínica con ese nombre");
        }

        String sql = "UPDATE especialidad_cli SET nombre = ? WHERE LOWER(nombre) = LOWER(?) AND fecha_baja IS NULL";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreNuevo.trim());
            ps.setString(2, nombreActual.trim());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Especialidad de clínica no encontrada");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al modificar especialidad de clínica: " + e.getMessage(), e);
        }
    }

    public void eliminarEspecialidadCli(String nombre) {
        validarTexto(nombre, "El nombre es obligatorio");
        String sql = "UPDATE especialidad_cli SET fecha_baja = SYSDATE WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Especialidad de clínica no encontrada");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar especialidad de clínica: " + e.getMessage(), e);
        }
    }

    public void asignarEspecialidadCli(String mailClinica, String nombreEspecialidad) {
        validarTexto(mailClinica, "El mail de la clínica es obligatorio");
        validarTexto(nombreEspecialidad, "El nombre de la especialidad es obligatorio");

        String sqlInsert = "INSERT INTO clinica_especialidad (id_usuario, id_especialidad_cli) " +
                "VALUES ((SELECT id_usuario FROM usuario WHERE mail = ?), " +
                "(SELECT id_especialidad_cli FROM especialidad_cli WHERE LOWER(nombre) = LOWER(?) AND fecha_baja IS NULL))";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setString(1, mailClinica.trim());
            ps.setString(2, nombreEspecialidad.trim());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar especialidad de clínica: " + e.getMessage(), e);
        }
    }


    public Calificacion valorarMedico(String mailPaciente, String mailMedico, String comentario,
                                      int atencion, int comprension, int puntualidad, List<String> imagenes) {
        validarTexto(mailPaciente, "Mail del paciente requerido");
        validarTexto(mailMedico, "Mail del médico requerido");
        validarTexto(comentario, "El comentario no puede estar vacío");
        validarPuntajes(atencion, comprension, puntualidad);

        int idPaciente = obtenerIdUsuarioPorMail(mailPaciente);
        int idMedico = obtenerIdUsuarioPorMail(mailMedico);

        String sqlCalif = "INSERT INTO calificacion (punt_atencion, punt_comprension, punt_puntualidad, " +
                "punt_prom, comentario, fecha, estado_val, id_paciente, id_medico) " +
                "VALUES (?, ?, ?, ?, ?, SYSDATE, 'PENDIENTE', ?, ?)";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCalif, new String[]{"id_calificacion"})) {
            double promedio = (atencion + comprension + puntualidad) / 3.0;
            ps.setInt(1, atencion);
            ps.setInt(2, comprension);
            ps.setInt(3, puntualidad);
            ps.setDouble(4, promedio);
            ps.setString(5, comentario);
            ps.setInt(6, idPaciente);
            ps.setInt(7, idMedico);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idCalif = rs.getInt(1);
                if (imagenes != null && !imagenes.isEmpty()) {
                    String sqlImg = "INSERT INTO imagen (archivo, id_calificacion) VALUES (?, ?)";
                    PreparedStatement psImg = conn.prepareStatement(sqlImg);
                    for (String img : imagenes) {
                        if (img != null && !img.isBlank()) {
                            psImg.setString(1, img);
                            psImg.setInt(2, idCalif);
                            psImg.addBatch();
                        }
                    }
                    psImg.executeBatch();
                }
                return new Calificacion(
                        new Paciente(mailPaciente, "", "", "", ""),
                        new Medico(mailMedico, "", "", "", "", "", "", ""),
                        comentario, atencion, comprension, puntualidad
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al valorar médico: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Calificacion> getCalificaciones() {
        String sql = "SELECT c.*, u.nombre as p_nombre, u.apellido as p_apellido, u.mail as p_mail, " +
                "m.nombre as med_nombre, m.apellido as med_apellido, m.mail as med_mail " +
                "FROM calificacion c " +
                "JOIN usuario u ON c.id_paciente = u.id_usuario " +
                "JOIN usuario m ON c.id_medico = m.id_usuario";

        List<Calificacion> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("p_mail"), "", "",
                        rs.getString("p_nombre"), rs.getString("p_apellido")
                );
                Medico medico = new Medico(
                        rs.getString("med_mail"), "", "",
                        rs.getString("med_nombre"), rs.getString("med_apellido"),
                        "", "", ""
                );
                Calificacion cal = new Calificacion(
                        paciente, medico,
                        rs.getString("comentario"),
                        rs.getInt("punt_atencion"),
                        rs.getInt("punt_comprension"),
                        rs.getInt("punt_puntualidad")
                );
                cal.setEstado(EstadoValoracion.valueOf(rs.getString("estado_val")));
                lista.add(cal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Calificacion> consultarCalificacionesAprobadasRecientes(String mailMedico) {
        String sql = "SELECT c.*, u.nombre as p_nombre, u.apellido as p_apellido, u.mail as p_mail " +
                "FROM calificacion c " +
                "JOIN usuario u ON c.id_paciente = u.id_usuario " +
                "WHERE c.id_medico = (SELECT id_usuario FROM usuario WHERE mail = ?) " +
                "AND c.estado_val = 'ACEPTADA' " +
                "ORDER BY c.fecha DESC FETCH FIRST 5 ROWS ONLY";

        List<Calificacion> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mailMedico);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("p_mail"), "", "",
                        rs.getString("p_nombre"), rs.getString("p_apellido")
                );
                Medico medico = new Medico(mailMedico, "", "", "", "", "", "", "");
                Calificacion cal = new Calificacion(
                        paciente, medico,
                        rs.getString("comentario"),
                        rs.getInt("punt_atencion"),
                        rs.getInt("punt_comprension"),
                        rs.getInt("punt_puntualidad")
                );
                cal.setEstado(EstadoValoracion.ACEPTADA);
                lista.add(cal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Calificacion> consultarValoracionesAprobadasRecientes() {
        String sql = "SELECT c.*, u.nombre as p_nombre, u.apellido as p_apellido, u.mail as p_mail, " +
                "m.nombre as med_nombre, m.apellido as med_apellido, m.mail as med_mail " +
                "FROM calificacion c " +
                "JOIN usuario u ON c.id_paciente = u.id_usuario " +
                "JOIN usuario m ON c.id_medico = m.id_usuario " +
                "WHERE c.estado_val = 'ACEPTADA' " +
                "ORDER BY c.fecha DESC FETCH FIRST 5 ROWS ONLY";

        List<Calificacion> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("p_mail"), "", "",
                        rs.getString("p_nombre"), rs.getString("p_apellido")
                );
                Medico medico = new Medico(
                        rs.getString("med_mail"), "", "",
                        rs.getString("med_nombre"), rs.getString("med_apellido"),
                        "", "", ""
                );
                Calificacion cal = new Calificacion(
                        paciente, medico,
                        rs.getString("comentario"),
                        rs.getInt("punt_atencion"),
                        rs.getInt("punt_comprension"),
                        rs.getInt("punt_puntualidad")
                );
                cal.setEstado(EstadoValoracion.ACEPTADA);
                lista.add(cal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Calificacion> consultarCalificacionesRecientes() {
        String sql = "SELECT c.*, u.nombre as p_nombre, u.apellido as p_apellido, u.mail as p_mail, " +
                "m.nombre as med_nombre, m.apellido as med_apellido, m.mail as med_mail " +
                "FROM calificacion c " +
                "JOIN usuario u ON c.id_paciente = u.id_usuario " +
                "JOIN usuario m ON c.id_medico = m.id_usuario " +
                "ORDER BY c.fecha DESC";

        List<Calificacion> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("p_mail"), "", "",
                        rs.getString("p_nombre"), rs.getString("p_apellido")
                );
                Medico medico = new Medico(
                        rs.getString("med_mail"), "", "",
                        rs.getString("med_nombre"), rs.getString("med_apellido"),
                        "", "", ""
                );
                Calificacion cal = new Calificacion(
                        paciente, medico,
                        rs.getString("comentario"),
                        rs.getInt("punt_atencion"),
                        rs.getInt("punt_comprension"),
                        rs.getInt("punt_puntualidad")
                );
                cal.setEstado(EstadoValoracion.valueOf(rs.getString("estado_val")));
                lista.add(cal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Calificacion> consultarCalificacionesPendientes() {
        String sql = "SELECT c.*, u.nombre as p_nombre, u.apellido as p_apellido, u.mail as p_mail, " +
                "m.nombre as med_nombre, m.apellido as med_apellido, m.mail as med_mail " +
                "FROM calificacion c " +
                "JOIN usuario u ON c.id_paciente = u.id_usuario " +
                "JOIN usuario m ON c.id_medico = m.id_usuario " +
                "WHERE c.estado_val = 'PENDIENTE'";

        List<Calificacion> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("p_mail"), "", "",
                        rs.getString("p_nombre"), rs.getString("p_apellido")
                );
                Medico medico = new Medico(
                        rs.getString("med_mail"), "", "",
                        rs.getString("med_nombre"), rs.getString("med_apellido"),
                        "", "", ""
                );
                Calificacion cal = new Calificacion(
                        paciente, medico,
                        rs.getString("comentario"),
                        rs.getInt("punt_atencion"),
                        rs.getInt("punt_comprension"),
                        rs.getInt("punt_puntualidad")
                );
                cal.setEstado(EstadoValoracion.PENDIENTE);
                lista.add(cal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void validarComentario(int idCalificacion, boolean aceptada) {
        String estado = aceptada ? "ACEPTADA" : "RECHAZADA";
        String sql = "UPDATE calificacion SET estado_val = ?, fecha_val = SYSDATE WHERE id_calificacion = ?";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, idCalificacion);
            ps.executeUpdate();

            // Recalcular promedio del médico
            String sqlPromedio = "UPDATE medico SET punt_prom_med = " +
                    "(SELECT AVG(punt_prom) FROM calificacion WHERE id_medico = " +
                    "(SELECT id_medico FROM calificacion WHERE id_calificacion = ?) AND estado_val = 'ACEPTADA') " +
                    "WHERE id_usuario = (SELECT id_medico FROM calificacion WHERE id_calificacion = ?)";

            PreparedStatement psProm = conn.prepareStatement(sqlPromedio);
            psProm.setInt(1, idCalificacion);
            psProm.setInt(2, idCalificacion);
            psProm.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar calificación: " + e.getMessage(), e);
        }
    }


    public Reclamo denunciarValoracion(String mailMedico, int idCalificacion, String descripcion) {
        validarTexto(descripcion, "La descripción del reclamo es obligatoria");
        if (descripcion.length() > 1000) {
            throw new IllegalArgumentException("La descripción no puede superar los 1000 caracteres");
        }

        int idMedico = obtenerIdUsuarioPorMail(mailMedico);

        // Verificar que la calificación pertenezca al médico
        String sqlCheck = "SELECT id_medico FROM calificacion WHERE id_calificacion = ?";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            psCheck.setInt(1, idCalificacion);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next() || rs.getInt("id_medico") != idMedico) {
                throw new IllegalArgumentException("No puedes reclamar una calificación que no te pertenece");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar calificación: " + e.getMessage(), e);
        }

        String sql = "INSERT INTO reclamo (descripcion, fecha_ini, estado, id_medico, id_calificacion) " +
                "VALUES (?, SYSDATE, 'PENDIENTE', ?, ?)";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_reclamo"})) {
            ps.setString(1, descripcion);
            ps.setInt(2, idMedico);
            ps.setInt(3, idCalificacion);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idReclamo = rs.getInt(1);
                return new Reclamo(
                        new Medico(mailMedico, "", "", "", "", "", "", ""),
                        getCalificacionPorId(idCalificacion),
                        descripcion
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear reclamo: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Reclamo> getReclamos() {
        String sql = "SELECT r.*, u.nombre as med_nombre, u.apellido as med_apellido, u.mail as med_mail " +
                "FROM reclamo r JOIN usuario u ON r.id_medico = u.id_usuario";

        List<Reclamo> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Medico medico = new Medico(
                        rs.getString("med_mail"), "", "",
                        rs.getString("med_nombre"), rs.getString("med_apellido"),
                        "", "", ""
                );
                Calificacion cal = getCalificacionPorId(rs.getInt("id_calificacion"));
                Reclamo reclamo = new Reclamo(medico, cal, rs.getString("descripcion"));
                lista.add(reclamo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Reclamo> getReclamosPendientes() {
        String sql = "SELECT r.*, u.nombre as med_nombre, u.apellido as med_apellido, u.mail as med_mail " +
                "FROM reclamo r JOIN usuario u ON r.id_medico = u.id_usuario " +
                "WHERE r.estado = 'PENDIENTE'";

        List<Reclamo> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Medico medico = new Medico(
                        rs.getString("med_mail"), "", "",
                        rs.getString("med_nombre"), rs.getString("med_apellido"),
                        "", "", ""
                );
                Calificacion cal = getCalificacionPorId(rs.getInt("id_calificacion"));
                Reclamo reclamo = new Reclamo(medico, cal, rs.getString("descripcion"));
                lista.add(reclamo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void atenderReclamo(int indiceReclamo, boolean aceptado) {
        List<Reclamo> todos = getReclamos();
        if (indiceReclamo < 0 || indiceReclamo >= todos.size()) {
            throw new IllegalArgumentException("Reclamo no encontrado");
        }
        // No tenemos ID, lanzamos excepción
        throw new UnsupportedOperationException("Usar resolverReclamo(int idReclamo, boolean aceptar, String motivo)");
    }

    public void resolverReclamo(int idReclamo, boolean aceptado, String motivo) {
        validarTexto(motivo, "Debe proporcionar un motivo");
        if (motivo.length() > 3000) {
            throw new IllegalArgumentException("El motivo no puede superar los 3000 caracteres");
        }

        String estado = aceptado ? "ACEPTADO" : "RECHAZADO";
        String sql = "UPDATE reclamo SET estado = ?, fecha_fin = SYSDATE WHERE id_reclamo = ?";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, idReclamo);
            ps.executeUpdate();

            if (aceptado) {
                String sqlCalif = "UPDATE calificacion SET estado_val = 'RECHAZADA' WHERE id_calificacion = " +
                        "(SELECT id_calificacion FROM reclamo WHERE id_reclamo = ?)";
                PreparedStatement psCal = conn.prepareStatement(sqlCalif);
                psCal.setInt(1, idReclamo);
                psCal.executeUpdate();
                recalcularPromedioMedico(idReclamo);
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al resolver reclamo: " + e.getMessage(), e);
        }
    }


    public SolicitudAsociacion solicitarAsociacionMedico(String mailClinica, String mailMedico) {
        int idClinica = obtenerIdUsuarioPorMail(mailClinica);
        int idMedico = obtenerIdUsuarioPorMail(mailMedico);

        // Verificar que no esté ya asociado
        String sqlCheck = "SELECT * FROM solicitud_asociacion WHERE id_medico = ? AND id_clinica = ? AND estado_solicitud IN ('PENDIENTE', 'ACEPTADA')";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCheck)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idClinica);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                throw new IllegalArgumentException("Ya existe una solicitud pendiente o aceptada para este médico");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar solicitud: " + e.getMessage(), e);
        }

        String sql = "INSERT INTO solicitud_asociacion (fecha_solicitud, estado_solicitud, id_medico, id_clinica) " +
                "VALUES (SYSDATE, 'PENDIENTE', ?, ?)";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_solicitud"})) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idClinica);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return new SolicitudAsociacion(
                        new Clinica(mailClinica, "", "", "", "", ""),
                        new Medico(mailMedico, "", "", "", "", "", "", "")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al solicitar asociación: " + e.getMessage(), e);
        }
        return null;
    }

    public List<SolicitudAsociacion> getSolicitudesAsociacion() {
        String sql = "SELECT s.*, c.nombre as clinica_nombre, c.mail as clinica_mail, " +
                "m.nombre as medico_nombre, m.apellido as medico_apellido, m.mail as medico_mail " +
                "FROM solicitud_asociacion s " +
                "JOIN usuario c ON s.id_clinica = c.id_usuario " +
                "JOIN usuario m ON s.id_medico = m.id_usuario";

        List<SolicitudAsociacion> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Clinica clinica = new Clinica(
                        rs.getString("clinica_mail"), "", "",
                        rs.getString("clinica_nombre"), "", ""
                );
                Medico medico = new Medico(
                        rs.getString("medico_mail"), "", "",
                        rs.getString("medico_nombre"), rs.getString("medico_apellido"),
                        "", "", ""
                );
                SolicitudAsociacion solicitud = new SolicitudAsociacion(clinica, medico);
                lista.add(solicitud);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<SolicitudAsociacion> getSolicitudesPendientesParaMedico(String mailMedico) {
        String sql = "SELECT s.*, c.nombre as clinica_nombre, c.mail as clinica_mail " +
                "FROM solicitud_asociacion s " +
                "JOIN usuario c ON s.id_clinica = c.id_usuario " +
                "WHERE s.id_medico = (SELECT id_usuario FROM usuario WHERE mail = ?) " +
                "AND s.estado_solicitud = 'PENDIENTE'";

        List<SolicitudAsociacion> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mailMedico);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Clinica clinica = new Clinica(
                        rs.getString("clinica_mail"), "", "",
                        rs.getString("clinica_nombre"), "", ""
                );
                Medico medico = new Medico(mailMedico, "", "", "", "", "", "", "");
                SolicitudAsociacion solicitud = new SolicitudAsociacion(clinica, medico);
                lista.add(solicitud);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void responderSolicitudAsociacion(int idSolicitud, String mailMedico, boolean aceptada) {
        String estado = aceptada ? "ACEPTADA" : "RECHAZADA";
        String sql = "UPDATE solicitud_asociacion SET estado_solicitud = ? WHERE id_solicitud = ? AND id_medico = " +
                "(SELECT id_usuario FROM usuario WHERE mail = ?)";

        try (Connection conn = BasedeDatos.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, idSolicitud);
            ps.setString(3, mailMedico);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Solicitud no encontrada o no pertenece a este médico");
            }

            if (aceptada) {
                String sqlAsociar = "INSERT INTO clinica_medico (id_clinica, id_medico) " +
                        "VALUES ((SELECT id_clinica FROM solicitud_asociacion WHERE id_solicitud = ?), " +
                        "(SELECT id_medico FROM solicitud_asociacion WHERE id_solicitud = ?))";
                PreparedStatement psAsoc = conn.prepareStatement(sqlAsociar);
                psAsoc.setInt(1, idSolicitud);
                psAsoc.setInt(2, idSolicitud);
                psAsoc.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al responder solicitud: " + e.getMessage(), e);
        }
    }

    public void desasociarMedicoDeClinica(String mailClinica, String mailMedico) {
        int idClinica = obtenerIdUsuarioPorMail(mailClinica);
        int idMedico = obtenerIdUsuarioPorMail(mailMedico);

        String sql = "DELETE FROM clinica_medico WHERE id_clinica = ? AND id_medico = ?";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idClinica);
            ps.setInt(2, idMedico);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("El médico no está asociado a esta clínica");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al desasociar médico: " + e.getMessage(), e);
        }
    }


    public List<Usuario> consultarUsuariosValidados() {
        String sql = "SELECT u.*, tp.nombre_p as rol FROM usuario u " +
                "JOIN tipo_perfil tp ON u.id_tipo_perfil = tp.id_tipo_perfil " +
                "WHERE u.estado_perfil = 'VALIDADO'";
        List<Usuario> validados = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                validados.add(construirUsuarioDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return validados;
    }

    public List<Usuario> consultarUsuariosRecientes() {
        String sql = "SELECT u.*, tp.nombre_p as rol FROM usuario u " +
                "JOIN tipo_perfil tp ON u.id_tipo_perfil = tp.id_tipo_perfil " +
                "ORDER BY u.fecha_alta DESC FETCH FIRST 10 ROWS ONLY";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(construirUsuarioDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Map<String, Object> getEstadisticasRegistros() {
        Map<String, Object> resultado = new HashMap<>();
        Map<String, Map<String, Long>> porDia = new LinkedHashMap<>();

        String sqlTotal = "SELECT COUNT(*) as total FROM usuario WHERE fecha_alta >= SYSDATE - 7";
        String sqlMedicos = "SELECT COUNT(*) as total FROM usuario WHERE id_tipo_perfil = (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'MEDICO') AND fecha_alta >= SYSDATE - 7";
        String sqlClinicas = "SELECT COUNT(*) as total FROM usuario WHERE id_tipo_perfil = (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'CLINICA') AND fecha_alta >= SYSDATE - 7";
        String sqlPacientes = "SELECT COUNT(*) as total FROM usuario WHERE id_tipo_perfil = (SELECT id_tipo_perfil FROM tipo_perfil WHERE nombre_p = 'PACIENTE') AND fecha_alta >= SYSDATE - 7";

        try (Connection conn = BasedeDatos.getConnection()) {
            // Totales
            try (PreparedStatement ps = conn.prepareStatement(sqlTotal);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) resultado.put("total", rs.getLong("total"));
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlMedicos);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) resultado.put("medicos", rs.getLong("total"));
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlClinicas);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) resultado.put("clinicas", rs.getLong("total"));
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlPacientes);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) resultado.put("pacientes", rs.getLong("total"));
            }

            // Registros por día (últimos 7 días)
            String sqlDia = "SELECT " +
                    "COUNT(CASE WHEN tp.nombre_p = 'MEDICO' THEN 1 END) as medicos, " +
                    "COUNT(CASE WHEN tp.nombre_p = 'CLINICA' THEN 1 END) as clinicas, " +
                    "COUNT(CASE WHEN tp.nombre_p = 'PACIENTE' THEN 1 END) as pacientes " +
                    "FROM usuario u JOIN tipo_perfil tp ON u.id_tipo_perfil = tp.id_tipo_perfil " +
                    "WHERE TRUNC(u.fecha_alta) = ?";

            for (int i = 6; i >= 0; i--) {
                LocalDate dia = LocalDate.now().minusDays(i);
                String key = dia.toString();
                Map<String, Long> counts = new HashMap<>();
                try (PreparedStatement ps = conn.prepareStatement(sqlDia)) {
                    ps.setDate(1, java.sql.Date.valueOf(dia));
                    try (ResultSet rsDia = ps.executeQuery()) {
                        if (rsDia.next()) {
                            counts.put("medicos", rsDia.getLong("medicos"));
                            counts.put("clinicas", rsDia.getLong("clinicas"));
                            counts.put("pacientes", rsDia.getLong("pacientes"));
                        } else {
                            counts.put("medicos", 0L);
                            counts.put("clinicas", 0L);
                            counts.put("pacientes", 0L);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    counts.put("medicos", 0L);
                    counts.put("clinicas", 0L);
                    counts.put("pacientes", 0L);
                }
                porDia.put(key, counts);
            }
            resultado.put("porDia", porDia);

            // Promedios
            long total = (long) resultado.getOrDefault("total", 0L);
            long medicos = (long) resultado.getOrDefault("medicos", 0L);
            long clinicas = (long) resultado.getOrDefault("clinicas", 0L);
            long pacientes = (long) resultado.getOrDefault("pacientes", 0L);
            resultado.put("promedioMedicos", total == 0 ? 0 : (double) medicos / 7);
            resultado.put("promedioClinicas", total == 0 ? 0 : (double) clinicas / 7);
            resultado.put("promedioPacientes", total == 0 ? 0 : (double) pacientes / 7);

        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar error
        }
        return resultado;
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
            throw new IllegalArgumentException("Código de recuperación inválido");
        }
        cambiarContrasena(mail, nuevaContrasena);
        codigosRecuperacion.remove(usuario.getMail().toLowerCase());
    }

    public void cambiarContrasena(String mail, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        String sql = "UPDATE usuario SET contrasena = ? WHERE mail = ?";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevaContrasena);
            ps.setString(2, mail);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar contraseña: " + e.getMessage(), e);
        }
    }

    public void validarUsuario(String mail, boolean aceptar, String motivo) {
        validarPerfilUsuario(mail, aceptar);
        System.out.println("📧 Enviando correo a " + mail + " con motivo: " + motivo);
    }


    private void validarMailDisponible(String mail) {
        if (buscarPorMail(mail).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese mail");
        }
    }

    private void validarTexto(String texto, String mensaje) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarPuntajes(int atencion, int comprension, int puntualidad) {
        if (atencion < 1 || atencion > 5 || comprension < 1 || comprension > 5 || puntualidad < 1 || puntualidad > 5) {
            throw new IllegalArgumentException("Los puntajes deben estar entre 1 y 5");
        }
    }

    private int obtenerIdUsuarioPorMail(String mail) {
        String sql = "SELECT id_usuario FROM usuario WHERE mail = ?";
        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Usuario no encontrado: " + mail);
    }

    private Calificacion getCalificacionPorId(int idCalificacion) {
        String sql = "SELECT c.*, u.nombre as p_nombre, u.apellido as p_apellido, u.mail as p_mail, " +
                "m.nombre as med_nombre, m.apellido as med_apellido, m.mail as med_mail " +
                "FROM calificacion c " +
                "JOIN usuario u ON c.id_paciente = u.id_usuario " +
                "JOIN usuario m ON c.id_medico = m.id_usuario " +
                "WHERE c.id_calificacion = ?";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCalificacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getString("p_mail"), "", "",
                        rs.getString("p_nombre"), rs.getString("p_apellido")
                );
                Medico medico = new Medico(
                        rs.getString("med_mail"), "", "",
                        rs.getString("med_nombre"), rs.getString("med_apellido"),
                        "", "", ""
                );
                Calificacion cal = new Calificacion(
                        paciente, medico,
                        rs.getString("comentario"),
                        rs.getInt("punt_atencion"),
                        rs.getInt("punt_comprension"),
                        rs.getInt("punt_puntualidad")
                );
                cal.setEstado(EstadoValoracion.valueOf(rs.getString("estado_val")));
                return cal;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Calificación no encontrada: " + idCalificacion);
    }

    private void recalcularPromedioMedico(int idReclamo) {
        String sql = "UPDATE medico SET punt_prom_med = " +
                "(SELECT AVG(punt_prom) FROM calificacion " +
                "WHERE id_medico = (SELECT id_medico FROM reclamo WHERE id_reclamo = ?) AND estado_val = 'ACEPTADA') " +
                "WHERE id_usuario = (SELECT id_medico FROM reclamo WHERE id_reclamo = ?)";

        try (Connection conn = BasedeDatos.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReclamo);
            ps.setInt(2, idReclamo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Usuario construirUsuarioDesdeResultSet(ResultSet rs) throws SQLException {
        String rol = rs.getString("rol");
        String mail = rs.getString("mail");
        String contrasena = rs.getString("contrasena");
        String telefono = rs.getString("telefono");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        int idUsuario = rs.getInt("id_usuario");
        String estadoPerfil = rs.getString("estado_perfil");

        Usuario usuario = null;
        switch (rol.toUpperCase()) {
            case "PACIENTE":
                usuario = new Paciente(mail, contrasena, telefono, nombre, apellido);
                break;
            case "MEDICO": {
                String sqlMed = "SELECT matricula, dni, certificado FROM medico WHERE id_usuario = ?";
                try (PreparedStatement ps = rs.getStatement().getConnection().prepareStatement(sqlMed)) {
                    ps.setInt(1, idUsuario);
                    ResultSet rsMed = ps.executeQuery();
                    if (rsMed.next()) {
                        usuario = new Medico(mail, contrasena, telefono, nombre, apellido,
                                rsMed.getString("matricula"),
                                rsMed.getString("dni"),
                                rsMed.getString("certificado"));
                    }
                }
                break;
            }
            case "CLINICA": {
                String sqlCli = "SELECT ubicacion FROM clinica WHERE id_usuario = ?";
                try (PreparedStatement ps = rs.getStatement().getConnection().prepareStatement(sqlCli)) {
                    ps.setInt(1, idUsuario);
                    ResultSet rsCli = ps.executeQuery();
                    if (rsCli.next()) {
                        usuario = new Clinica(mail, contrasena, telefono, nombre, apellido,
                                rsCli.getString("ubicacion"));
                    }
                }
                break;
            }
            case "ASISTENTE_CONSULTA":
                usuario = new AsistenteConsulta(mail, contrasena, telefono, nombre, apellido);
                break;
            case "ADMINISTRADOR":
                usuario = new Administrador(mail, contrasena, telefono, nombre, apellido);
                break;
            default:
                throw new IllegalArgumentException("Rol desconocido: " + rol);
        }

        if (usuario != null && estadoPerfil != null) {
            switch (estadoPerfil) {
                case "VALIDADO":
                    usuario.validarPerfil();
                    break;
                case "RECHAZADO":
                    usuario.rechazarPerfil();
                    break;
            }
        }
        return usuario;
    }
}
