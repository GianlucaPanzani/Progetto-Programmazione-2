package Exceptions;
import java.lang.RuntimeException;

public class BadLikeException extends RuntimeException {
    
    public BadLikeException(String s) {
        System.out.print(s);
    }

    public String getMessage() {
        return ": throws a BadLikeException";
    }
}