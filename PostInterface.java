import java.util.HashSet;

public interface PostInterface {

    public void printInfo();

    public void printSignalatedInfo();

    public String getDataAndHours();

    public String getId();

    public String getText();

    public String getAuthor();

    public HashSet<String> getMentioned();

}