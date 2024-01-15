package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.sql.Connection;
import java.util.Map;

import static io.javalin.Javalin.create;

public class App {

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static Javalin init() {
        Javalin app = create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        // Инициализация Jte
        JavalinJte.init(createTemplateEngine());

        return app;
    }

    public static Javalin getApp() {
        Javalin app = init();
        app.get("/", ctx -> {
            String message = "Welcome to the Jte World!";
            ctx.render("templates/main.jte", Map.of("message", message));
        });
        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        try (Connection connection = DatabaseConfig.getDataSource().getConnection()) {
            // Initialize database structure
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS urls ("
                            + "id SERIAL PRIMARY KEY,"
                            + "name VARCHAR(255) NOT NULL,"
                            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                            + ")"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        app.start(7070);
    }
}
