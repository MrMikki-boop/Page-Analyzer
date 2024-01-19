package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlCheckController;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.BaseRepository;

import hexlet.code.util.NamedRoutes;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public final class App {

    private static final String PORT_DEFAULT = "7070";
    private static final String JDBC_URL_DEFAULT = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;";

    public static void main(String[] args) throws IOException, SQLException {
        Javalin app = getApp();
        var port = getPort();
        app.start(port);
    }

    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", PORT_DEFAULT);
        return Integer.parseInt(port);
    }

    public static String getJdbcUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", JDBC_URL_DEFAULT);
    }

    public static boolean isProduction() {
        return System.getenv().getOrDefault("APP_ENV", "dev").equals("prod");
    }

    public static void setData(HikariConfig hikariConfig) {
        hikariConfig.setJdbcUrl(getJdbcUrl());

        if (isProduction()) {
            var username = System.getenv("JDBC_DATABASE_USERNAME");
            var password = System.getenv("JDBC_DATABASE_PASSWORD");
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() throws SQLException {

        var hikariConfig = new HikariConfig();
        setData(hikariConfig);

        var dataSource = new HikariDataSource(hikariConfig);

        var inputStream = App.class.getClassLoader().getResourceAsStream("schema.sql");
        assert inputStream != null;
        var reader = new BufferedReader(new InputStreamReader(inputStream));
        var sql = reader.lines().collect(Collectors.joining("\n"));

        log.info(sql);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> config.plugins.enableDevLogging());

        JavalinJte.init(createTemplateEngine());

        app.get(NamedRoutes.rootPath(), RootController::index);
        app.get(NamedRoutes.urlsPath(), UrlController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlController::show);
        app.post(NamedRoutes.urlsPath(), UrlController::create);
        app.post(NamedRoutes.urlChecksPath("{id}"), UrlCheckController::create);

        return app;
    }
}
