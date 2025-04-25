import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

public class UserInfo implements Closeable {
    
    private final Scanner input = new Scanner(System.in);

    public String getFirstName() {
        System.out.print("Enter your first name: ");
        return getName();
    }

    public String getLastName() {
        System.out.print("Enter your last name: ");
        return getName();
    }

    public String getName() {
        return input.nextLine();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

}
