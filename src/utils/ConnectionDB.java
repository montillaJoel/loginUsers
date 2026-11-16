package src.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens connections to the database.
 * <p>
 * Connection settings are read from the environment variables {@code loginUsers_DB_URL},
 * {@code loginUsers_DB_USUARIO} (username) and {@code loginUsers_DB_CLAVE} (password).
 */
public final class ConnectionDB {

    /**
     * Opens a new database connection. The caller must close it.
     *
     * @return a new connection
     * @throws IllegalStateException if any of the required environment variables is missing
     * @throws SQLException          if the connection cannot be opened
     */
    public static Connection open() throws SQLException {
        String url = System.getenv("loginUsers_DB_URL");
        String user = System.getenv("loginUsers_DB_USUARIO");
        String password = System.getenv("loginUsers_DB_CLAVE");

        // Fail fast with a clear message if the local MySQL configuration is missing
        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                "Missing local MySQL configuration: set loginUsers_DB_URL, "
                + "loginUsers_DB_USUARIO and loginUsers_DB_CLAVE");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
