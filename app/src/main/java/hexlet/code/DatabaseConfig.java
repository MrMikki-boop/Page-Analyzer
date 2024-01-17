package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {

    // Измените параметры подключения к базе данных, если это необходимо
    private static final String JDBC_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";

    private static final String JDBC_USERNAME = "sa";
    private static final String JDBC_PASSWORD = "";

    private static final HikariConfig CONFIG = new HikariConfig();

    static {
        CONFIG.setJdbcUrl(JDBC_URL);
        CONFIG.setUsername(JDBC_USERNAME);
        CONFIG.setPassword(JDBC_PASSWORD);
    }

    private static final HikariDataSource DATA_SOURCE = new HikariDataSource(CONFIG);

    public static HikariDataSource getDataSource() {
        return DATA_SOURCE;
    }
}
