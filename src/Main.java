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
            boolean success = database.insertNameIntoDB(firstName, lastName);
            if (!success) {
                System.out.println("Name already exists in table");
            }
        }
    }

}
