import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection implements AutoCloseable {

    private static final String CONNECTION_URL = "jdbc:sqlserver://database-1.ckxf3a0k0vuw.us-east-1.rds.amazonaws.com;"
            + "database=Zucker;"
            + "user=" + System.getenv("DB_USER") + ";"
            + "password=" + System.getenv("DB_PASSWD") + ";"
            + "encrypt=true;trustServerCertificate=true;loginTimeout=30;";

    public static final int CONSTRAINT_ERROR = 2627;

    private final Connection connection;

    public DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(CONNECTION_URL);
    }

    public void insertName(String firstName, String lastName) throws SQLException {
        try (PreparedStatement insertStatement = connection
                .prepareStatement("INSERT INTO People (FirstName, LastName) Values (?, ?)")) {
            insertStatement.setString(1, firstName);
            insertStatement.setString(2, lastName);
            insertStatement.executeUpdate();
        }
    }

    public void displayNameStats() throws SQLException {
        displayTotalRecords();
        displayRecordsPerFirstInitial();
    }

    private void displayTotalRecords() throws SQLException {
        try (Statement totalQuery = connection.createStatement()) {
            ResultSet results = totalQuery.executeQuery("SELECT COUNT(*) FROM People");
            results.next();
            System.out.println("Total entries: " + results.getInt(1));
        }
    }

    private void displayRecordsPerFirstInitial() throws SQLException {
        try (PreparedStatement firstInitialQuery = connection
                .prepareStatement("SELECT COUNT(*) FROM People WHERE UPPER(LEFT(FirstName, 1)) = ?")) {
            for (char c = 'A'; c <= 'Z'; c++) {
                firstInitialQuery.setString(1, Character.toString(c));
                ResultSet results = firstInitialQuery.executeQuery();
                results.next();
                System.out.println(c + ": " + results.getString(1));
            }
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

}
