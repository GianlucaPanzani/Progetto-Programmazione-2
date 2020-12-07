package Exceptions;
import java.lang.RuntimeException;

public class PostNotFoundException extends RuntimeException {
    
    public PostNotFoundException(String s) {
        System.out.print(s);
    }

    public String getMessage() {
        return ": throws a PostNotFoundException";
    }
}