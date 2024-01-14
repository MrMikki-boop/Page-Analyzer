package hexlet.code;

import io.javalin.Javalin;

import java.sql.Connection;

import static io.javalin.Javalin.create;

public class App {
    public static Javalin init() {
        return create(config -> {
            config.bundledPlugins.enableDevLogging();
        });
    }

    public static Javalin getApp() {
        Javalin app = init();
        app.get("/", ctx -> ctx.result("Hello, World!"));
        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        try (Connection connection = DatabaseConfig.getDataSource().getConnection()) {
            // Initialize database structure
            // You can use something like Flyway or Liquibase for more complex setups
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS urls (" +
                            "id SERIAL PRIMARY KEY," +
                            "name VARCHAR(255) NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        app.start(7070);
    }
}
