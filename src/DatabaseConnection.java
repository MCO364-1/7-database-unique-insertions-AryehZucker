import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection implements AutoCloseable {

    private static final String CONNECTION_URL = "jdbc:sqlserver://database-1.ckxf3a0k0vuw.us-east-1.rds.amazonaws.com;"
            + "database=Zucker;"
            + "user=" + System.getenv("DB_USER") + ";"
            + "password=" + System.getenv("DB_PASSWD") + ";"
            + "encrypt=true;trustServerCertificate=true;loginTimeout=30;";
    private static final String CHECK_SQL = "SELECT COUNT(*) FROM People WHERE FirstName = ? AND LastName = ?";
    private static final String INSERT_SQL = "INSERT INTO People (FirstName, LastName) Values (?, ?)";

    private final Connection connection;

    public DatabaseConnection() throws SQLException {
        this.connection =  DriverManager.getConnection(CONNECTION_URL);
    }

    public boolean insertNameIntoDB(String firstName, String lastName) throws SQLException {
        if (nameExistsInTable(firstName, lastName)) {
            return false;
        }
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_SQL)) {
            insertStatement.setString(1, firstName);
            insertStatement.setString(2, lastName);
            insertStatement.executeUpdate();
        }
        return true;
    }

    private boolean nameExistsInTable(String firstName, String lastName) throws SQLException {
        try (PreparedStatement checkStatement = connection.prepareStatement(CHECK_SQL)) {
            checkStatement.setString(1, firstName);
            checkStatement.setString(2, lastName);
            ResultSet results = checkStatement.executeQuery();
            results.next();
            if (results.getInt(1) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

}
