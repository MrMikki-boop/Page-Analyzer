package hexlet.code;

import io.javalin.Javalin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {

    @Test
    void testApp() {
        Javalin app = App.getApp();

        // Добавьте свои тесты здесь
        assertNotNull(app);
        // Например, можно добавить тесты для проверки конфигурации приложения или маршрутов
    }
}
