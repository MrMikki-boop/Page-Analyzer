package hexlet.code;

import java.sql.Connection;

public abstract class BaseRepository {
    protected Connection connection;

    public BaseRepository(Connection connection) {
        this.connection = connection;
    }
}
