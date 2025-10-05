package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de utilidad para la gestión de conexiones a la base de datos
 * 
 * Responsabilidades:
 * - Proporcionar conexiones a la base de datos MySQL
 * - Centralizar la configuracion de conexión (URL, usuario, password)
 * - Verificar el estado de la conexión
 * - Implementar patrón Singleton para la configuración
 * 
 * Características:
 * - Clase final para evitar herencia
 * - Contructor privado para evitar instanciación
 * - Método estáticos para acceso global
 * 
 */

public final class DatabaseConnection {
    // Configuración de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/Cine_DB";
    private static final String USER = "root";
    private static final String PASS = "root";

    // Constructor privado para evitar instanciación
    private DatabaseConnection(){}
    
    /**
     * Obtiene una conexión a la base de datos
     * 
     * @return Connection objeto de conexión JDBC
     * @throws SQLException Si ocurre error al establecer la conexión
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
    /**
     * Testea la conexión a la base de datos
     * 
     * @return boolean true si la conexión es exitosa, false en caso contrario
     */
    public static boolean databaseTest() {
        try (Connection c = get()) {
            // Retorna true si la conexión no es nula y está abierta
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            // imprime el stack trace para el debugging
            e.printStackTrace();
            return false;
        }
    }
    
}
