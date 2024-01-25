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

    /**
     * Test to check if the data source exists and is of the correct type.
     */
    @Test
    public void testDataSourceExists() {
        assertThat(BaseRepository.dataSource).isNotNull();
        assertThat(BaseRepository.dataSource).isInstanceOf(HikariDataSource.class);
    }

    /**
     * Test to verify that the main page is displayed correctly.
     */
    @Test
    public void testShowMainPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string())
                    .contains("Check websites for SEO suitability for free");
        }));
    }

    /**
     * Test to check if the "Urls" page is displayed correctly.
     */
    @Test
    public void testUrlPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string()).contains("Urls");
        }));
    }

    /**
     * Test to verify the creation of a new URL page and its appearance in the database.
     */
    @Test
    public void testCreatePage() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");

            // Check that the specific entity has appeared in the database
            var createdUrl = UrlRepository.findByName("https://www.example.com");
            assertThat(createdUrl).isPresent(); // Ensure that the result is not empty
            assertThat(createdUrl.get().getName()).isEqualTo("https://www.example.com");
        });
    }

    /**
     * Test to verify the behavior when an incorrect URL is provided for creation.
     */
    @Test
    public void testCreateIncorrectPage() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=12345";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(UrlRepository.findAll()).isEmpty();
        });
    }

    /**
     * Test to check if the pagination for the "Urls" page is working as expected.
     */
    @Test
    public void testUrlPageNumber() {
        var mockServer = new MockWebServer();
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=" + mockServer.url("/test"));
            assertThat(client.get("/urls?page=1").code()).isEqualTo(200);
            assertThat(Objects.requireNonNull(client.get("/urls?page=1").body()).string()).contains("/urls?page=1");
        });
    }

    /**
     * Test to check the behavior when trying to access a non-existing URL.
     */
    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(999999999L));
            assertThat(response.code()).isEqualTo(404);
        });
    }

    /**
     * Test to verify that the "Urls" list page is accessible.
     */
    @Test
    public void testListUrls() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    /**
     * Test to verify the display of a specific URL page.
     */
    @Test
    public void testShow() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://google.com");
            UrlRepository.save(url);
            var id = UrlRepository.findByName("https://google.com").map(Url::getId).orElse(null);
            var response = client.get("/urls/" + id);
            assertThat(response.code()).isEqualTo(200);
        });
    }

    /**
     * Test to verify the creation of a new URL and its appearance on the page.
     */
    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=" + TEST_URL + "/12345");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("https://google.com");

            var response2 = client.post("/urls", "url=" + TEST_URL + "/12345");
            assertThat(response2.code()).isEqualTo(200);
            assertThat(response2.body().string())
                    .contains("Page Analyzer");
            assertThat(UrlRepository.checkUrlExist(TEST_URL)).isTrue();
        });
    }

    /**
     * Test to check the URL checking process, including title, h1, and description extraction.
     */
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

            assertThat(title).as("Check title").isEqualTo("This is a title");
            assertThat(h1).as("Check h1").isEqualTo("This is a header");
            assertThat(description).as("Check description").isEqualTo("This is a description");
        });
    }
}
