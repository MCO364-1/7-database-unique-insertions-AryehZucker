import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        String firstName, lastName;
        try (UserInfo userInfo = new UserInfo()) {
            firstName = userInfo.getFirstName();
            lastName = userInfo.getLastName();
        }

        try (DatabaseConnection database = new DatabaseConnection()) {
            try {
                database.insertName(firstName, lastName);
            } catch (SQLException e) {
                if (e.getErrorCode() == DatabaseConnection.CONSTRAINT_ERROR) {
                    System.out.println("Name already exists in table");
                } else {
                    throw e;
                }
            }
            database.displayNameStats();
        }
    }

}
