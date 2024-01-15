package hexlet.code;

import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AppTest {

    private static Javalin app;

    @BeforeAll
    public static void setup() {
        app = App.getApp();
        app.start(7070);
    }

    @AfterAll
    public static void tearDown() {
        app.stop();
    }
}
