package src.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Opens connections to the database.
 * <p>
 * Connection settings are {@code loginUsers_DB_URL}, {@code loginUsers_DB_USUARIO} (username) and
 * {@code loginUsers_DB_CLAVE} (password). Each one is read from the environment variables first;
 * if it is not there, it is read from a {@code .env} file in the working directory (or in a
 * {@code loginUsers} subfolder). This way the app works from VS Code's "Run" button, from
 * Run and Debug, from Eclipse or from the terminal.
 */
public final class ConnectionDB {

    private static final String URL_KEY = "loginUsers_DB_URL";
    private static final String USER_KEY = "loginUsers_DB_USUARIO";
    private static final String PASSWORD_KEY = "loginUsers_DB_CLAVE";

    /** Values loaded from the .env file, read once on first use. */
    private static Map<String, String> envFile;

    private ConnectionDB() {
    }

    /**
     * Opens a new database connection. The caller must close it.
     *
     * @return a new connection
     * @throws IllegalStateException if any of the required settings is missing
     * @throws SQLException          if the connection cannot be opened
     */
    public static Connection open() throws SQLException {
        String url = setting(URL_KEY);
        String user = setting(USER_KEY);
        String password = setting(PASSWORD_KEY);

        // Fail fast with a clear message if the local MySQL configuration is missing
        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                "Falta la configuración de MySQL: defina " + URL_KEY + ", "
                + USER_KEY + " y " + PASSWORD_KEY + " en el archivo .env "
                + "(copie .env.example como .env)");
        }

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Returns a setting from the environment variables, or from the .env file if it is not set.
     *
     * @param key the setting name
     * @return the value, or {@code null} if it is not defined anywhere
     */
    private static String setting(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            return value;
        }
        return loadEnvFile().get(key);
    }

    /**
     * Reads the .env file once. Lines look like {@code KEY=value}; empty lines and lines starting
     * with {@code #} are ignored, and surrounding quotes are removed from values.
     *
     * @return the key/value pairs found, or an empty map if there is no .env file
     */
    private static synchronized Map<String, String> loadEnvFile() {
        if (envFile != null) {
            return envFile;
        }
        envFile = new HashMap<>();

        Path[] candidates = {Paths.get(".env"), Paths.get("loginUsers", ".env")};
        for (Path path : candidates) {
            if (!Files.isRegularFile(path)) {
                continue;
            }
            try {
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                for (String line : lines) {
                    String trimmed = line.trim();
                    int equals = trimmed.indexOf('=');
                    if (trimmed.isEmpty() || trimmed.startsWith("#") || equals <= 0) {
                        continue;
                    }
                    String key = trimmed.substring(0, equals).trim();
                    String value = trimmed.substring(equals + 1).trim();
                    if (value.length() >= 2 && (value.startsWith("\"") && value.endsWith("\"")
                            || value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    envFile.put(key, value);
                }
            } catch (IOException e) {
                System.err.println("No se pudo leer " + path.toAbsolutePath() + ": " + e.getMessage());
            }
            break;   // use the first .env found
        }
        return envFile;
    }
}
