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

    private final Connection connection;

    public DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(CONNECTION_URL);
    }

    /**
     * @return false if the name is already in the database
     */
    public boolean insertName(String firstName, String lastName) throws SQLException {
        if (nameExistsInTable(firstName, lastName)) {
            return false;
        }
        try (PreparedStatement insertStatement = connection
                .prepareStatement("INSERT INTO People (FirstName, LastName) Values (?, ?)")) {
            insertStatement.setString(1, firstName);
            insertStatement.setString(2, lastName);
            insertStatement.executeUpdate();
        }
        return true;
    }

    private boolean nameExistsInTable(String firstName, String lastName) throws SQLException {
        try (PreparedStatement checkStatement = connection
                .prepareStatement("SELECT COUNT(*) FROM People WHERE FirstName = ? AND LastName = ?")) {
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
