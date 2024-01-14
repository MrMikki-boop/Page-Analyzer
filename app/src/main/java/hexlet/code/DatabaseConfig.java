package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class DatabaseConfig {
    private static final HikariConfig CONFIG = new HikariConfig();

    static {
        // Задаем максимальное количество соединений в пуле
        CONFIG.setMaximumPoolSize(10);
        // Задаем таймаут на установку соединения с базой данных
        CONFIG.setConnectionTimeout(5000);
        // Задаем время ожидания неактивных соединений перед закрытием
        CONFIG.setIdleTimeout(600000);
        // Задаем максимальное время жизни соединения в пуле
        CONFIG.setMaxLifetime(1800000);
    }

    public static HikariDataSource getDataSource() {
        if (System.getenv("JDBC_DATABASE_URL") != null) {
            // Если переменная окружения JDBC_DATABASE_URL установлена, используем ее значение (подходит для продакшена)
            CONFIG.setJdbcUrl(System.getenv("JDBC_DATABASE_URL"));
        } else {
            // В противном случае (например, в локальной среде разработки) используем встроенную базу данных H2
            CONFIG.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            CONFIG.setDriverClassName("org.h2.Driver");
        }
        return new HikariDataSource(CONFIG);
    }

    public static Connection getConnection() throws Exception {
        // Возвращает соединение с базой данных, полученное из настроенного HikariDataSource
        return getDataSource().getConnection();
    }
}
