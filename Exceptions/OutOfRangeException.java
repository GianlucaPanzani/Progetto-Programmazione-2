package Exceptions;
import java.lang.RuntimeException;

public class OutOfRangeException extends RuntimeException {

    public OutOfRangeException(String s) {
        System.out.print(s);
    }

    public String getMessage() {
        return ": throws an OutOfRangeException";
    }
}