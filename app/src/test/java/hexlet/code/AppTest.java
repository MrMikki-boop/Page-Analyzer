package hexlet.code;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    private Javalin app;

    private static final String TEST_URL = "https://google.com";
    private static final String TEST_HTML_FILENAME = "index.html";

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", fileName).toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeEach
    public final void setApp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testDataSourceExists() {
        assertThat(BaseRepository.dataSource).isNotNull();
        assertThat(BaseRepository.dataSource).isInstanceOf(HikariDataSource.class);
    }

    @Test
    public void testShowMainPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains("<p class=\"lead\">Check websites for SEO suitability for free</p>");
        }));
    }

    @Test
    public void testUrlPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Urls");
        }));
    }

    @Test
    public void testCreatePage() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");

            // Проверяем, что конкретная сущность появилась в БД
            var createdUrl = UrlRepository.findByName("https://www.example.com");
            assertThat(createdUrl).isPresent(); // Проверяем, что результат не пустой
            assertThat(createdUrl.get().getName()).isEqualTo("https://www.example.com");
        });
    }

    @Test
    public void testCreateIncorrectPage() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=12345";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(UrlRepository.findAll()).hasSize(0);
        });
    }

    @Test
    public void testUrlPageNumber() {
        var mockServer = new MockWebServer();
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=" + mockServer.url("/test"));
            assertThat(client.get("/urls?page=1").code()).isEqualTo(200);
            assertThat(Objects.requireNonNull(client.get("/urls?page=1").body()).string()).contains("/urls?page=1");
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(999999999L));
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testListUrls() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testShow() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://google.com");
            UrlRepository.save(url);
            var newUrl = UrlRepository.findByName("https://google.com");
            var id = newUrl.get().getId();
            var response = client.get("/urls/" + id);
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=" + TEST_URL + "/12345");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("<a href=\"/urls/1\">https://google.com</a>");

            var response2 = client.post("/urls", "url=" + TEST_URL + "/12345");
            assertThat(response2.code()).isEqualTo(200);
            assertThat(response2.body().string())
                    .contains("<a class=\"navbar-brand\" href=\"/\">Page Analyzer</a>");
            assertThat(UrlRepository.checkUrlExist(TEST_URL)).isTrue();
        });
    }

    @Test
    public void testCheckUrl() throws IOException, SQLException {
        var mockServer = new MockWebServer();
        var ckUrl = mockServer.url("/").toString();
        var mockResponse = new MockResponse().setBody(readFixture(TEST_HTML_FILENAME));
        mockServer.enqueue(mockResponse);

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + ckUrl;
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var formattedName = String.format("%s://%s", mockServer.url("/").url().getProtocol(),
                    mockServer.url("/").url().getAuthority());
            var addUrl = UrlRepository.findByName(formattedName).orElse(null);
            assertThat(addUrl).isNotNull();
            assertThat(addUrl.getName()).isEqualTo(formattedName);

            var response2 = client.post("/urls/" + addUrl.getId() + "/checks");
            assertThat(response2.code()).isEqualTo(200);

            var ursCheck = UrlCheckRepository.findByUrlId(addUrl.getId()).get(0);
            var title = ursCheck.getTitle();
            var h1 = ursCheck.getH1();
            var description = ursCheck.getDescription();

            assertThat(title).isEqualTo("This is a title");
            assertThat(h1).isEqualTo("This is a header");
            assertThat(description).isEqualTo("This is a description");
        });
    }
}
