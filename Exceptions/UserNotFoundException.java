package Exceptions;
import java.lang.RuntimeException;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String s) {
        System.out.print(s);
    }

    public String getMessage() {
        return ": throws a UserNotFoundException";
    }
}