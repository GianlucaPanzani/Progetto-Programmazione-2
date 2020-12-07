package Exceptions;
import java.lang.RuntimeException;

public class EmptyPostException extends RuntimeException {

    public EmptyPostException(String s) {
        System.out.print(s);
    }

    public String getMessage() {
        return ": throws an EmptyPostException";
    }
}