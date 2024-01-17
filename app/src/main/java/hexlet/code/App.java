package hexlet.code;

import io.javalin.Javalin;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.rendering.template.JavalinJte;

import java.net.URI;
import java.sql.Connection;
import java.util.List;

import static io.javalin.Javalin.create;

public class App {

    public static Javalin init() {
        return create(config -> {
            config.bundledPlugins.enableDevLogging();
        });
    }

    public static Javalin getApp() {
        Javalin app = init();

        // Инициализация шаблонизатора Jte
        JavalinJte.init(createTemplateEngine());

        app.get("/", ctx -> {
            ctx.render("/templates/main.jte");
        });

        app.post("/urls", ctx -> {
            String inputUrl = ctx.formParam("url");
            try {
                URI uri = new URI(inputUrl);
                String baseUrl = uri.getScheme() + "://"
                        + uri.getHost()
                        + (uri.getPort() != -1 ? ":" + uri.getPort() : "");
                Url url = new Url();
                url.setName(baseUrl);
                new UrlRepository().save(url);
                ctx.sessionAttribute("success", "Страница успешно добавлена");
                ctx.redirect("/");
            } catch (Exception e) {
                ctx.sessionAttribute("error", "Некорректный URL");
                ctx.redirect("/");
            }
        });

        app.get("/urls", ctx -> {
            List<Url> urls = new UrlRepository().getAllUrls();
            ctx.json(urls);
        });

        app.get("/urls/:id", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Url url = new UrlRepository().getUrlById(id);
            ctx.json(url);
        });

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        try (Connection connection = DatabaseConfig.getDataSource().getConnection()) {
            // Initialize database structure
            // You can use something like Flyway or Liquibase for more complex setups
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

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }
}
