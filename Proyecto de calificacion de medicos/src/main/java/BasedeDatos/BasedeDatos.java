package BasedeDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class BasedeDatos {

    // Configuración para tu base de datos local en Docker
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "leaneitor512";

    static {
        try {
            // Cargar el driver de Oracle (opcional en JDBC 4+)
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC de Oracle no encontrado", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    // Ejecutar una consulta SELECT y devolver el resultado como String (para pruebas)
    public static String ejecutarConsultaString(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString(1)).append("\n");
            }
            return sb.toString();
        } catch (SQLException e) {
            throw new RuntimeException("Error en consulta: " + e.getMessage(), e);
        }
    }

    // Ejecutar UPDATE/INSERT/DELETE y devolver número de filas afectadas
    public static int ejecutarActualizacion(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error en actualización: " + e.getMessage(), e);
        }
    }

    // Ejecutar una consulta y devolver ResultSet (para uso más avanzado)
    public static ResultSet ejecutarQuery(String sql) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    // Método para probar la conexión
    public static void main(String[] args) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT '¡Conexión JDBC exitosa!' FROM DUAL")) {
            if (rs.next()) {
                System.out.println("✅ " + rs.getString(1));
            }
            // Probar crear una tabla simple para verificar permisos
            stmt.execute("CREATE TABLE prueba (id NUMBER)");
            System.out.println("✅ Tabla de prueba creada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
