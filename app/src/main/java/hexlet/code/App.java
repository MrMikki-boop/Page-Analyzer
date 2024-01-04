package hexlet.code;

import io.javalin.Javalin;



public final class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(8080);
    }

    private static Javalin getApp() {

        Javalin app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        app.get("/", ctx -> {
            ctx.result("Hello World");
        });

        return app;

    }

}