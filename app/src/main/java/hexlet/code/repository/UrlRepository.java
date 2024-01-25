package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.ResultSet;
import java.util.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, url.getName());
            var createdAt = new Timestamp(new Date().getTime());
            preparedStatement.setTimestamp(2, createdAt);
            preparedStatement.executeUpdate();
        }
    }

    public static boolean checkUrlExist(String url) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, url);
            var resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public static List<Url> findAll() throws SQLException {
        String sql = "SELECT * FROM urls";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                result.add(url);
            }
            return result;
        }
    }

    private static Optional<Url> mapResultSetToUrl(ResultSet resultSet) throws SQLException {
        var id = resultSet.getLong("id");
        var name = resultSet.getString("name");
        var createdAt = resultSet.getTimestamp("created_at");
        var url = new Url(name);
        url.setId(id);
        url.setCreatedAt(createdAt);
        return Optional.of(url);
    }

    public static Optional<Url> find(long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? mapResultSetToUrl(resultSet) : Optional.empty();
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, created_at FROM urls WHERE name = ?";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            var resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? mapResultSetToUrl(resultSet) : Optional.empty();
        }
    }

    public static void destroy(long id) throws SQLException {
        String sql = "DELETE FROM urls WHERE id = ?";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
