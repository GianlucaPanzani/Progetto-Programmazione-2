import java.util.Map;
import java.util.Set;
import java.util.List;

public interface SocialNetwork {

    public Map<String, Set<String>> guessFollowers(List<Post> ps);

    public List<String> influencers();

    public Set<String> getMentionedUsers();

    public Set<String> getMentionedUsers(List<Post> ps);

    public List<Post> writtenBy(String username);

    public List<Post> writtenBy(List<Post> ps, String username);

    public List<Post> containing(List<String> words);
    
}