package cfp.pkg402.dao;

import java.sql.*;

public class DatabaseConfig {

    // Configuracion de la base de datos
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cfp402";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "root"; 
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Pool de conection
    private static Connection connection = null;

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver MySQL: " + e.getMessage());
        }
    }

    // Get conne a la base
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    
    // Close conne a la base
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }

}
