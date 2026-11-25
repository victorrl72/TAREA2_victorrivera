package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static Connection conexion = null;
    private static final Properties props = new Properties();

    // SE CARGA EL APPLICATIO PROPERTIES UNA VEZ AL ARRANCAR
    static {
        try (InputStream is = ConexionBD.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new RuntimeException("No se encontró el fichero application.properties en src/main/resources");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer application.properties: " + e.getMessage(), e);
        }
    }

    // Constructor privado Singleton
    private ConexionBD() {
    }

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");
                conexion = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    // metodo para leer cualquier propiedad 
    public static String getPropiedad(String clave) {
        return props.getProperty(clave);
    }
    
    public static void main(String[] args) {
        System.out.println(getConexion() != null ? "Conectado OK" : "Fallo");
        cerrarConexion();
    }
}