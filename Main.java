import Exceptions.BadLikeException;
import Exceptions.EmptyPostException;
import Exceptions.PostNotFoundException;
import Exceptions.OutOfRangeException;
import Exceptions.UserNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MicroBlog mb = new MicroBlog();
        Map<String, Set<String>> mapFollow = null;
        List<Post> likeList = new LinkedList<>();
        List<Post> psList = new LinkedList<>();
        List<String> strList = null;
        Set<String> mentionedSet = null;

        // Post (con valori corretti)
        System.out.println("\n***TEST DEL COSTRUTTORE DI Post (con correct values)***");
        Post[] vPost = { // post che NON dovrebbero sollevare eccezioni
            new Post(new String("Andrea"), new String("Ciao a tutti, sono nuovo su MicroBlog!"), null),
            new Post(new String("Gianmarco"), new String("Ciao! Come state?"), null),
            new Post(new String("Pierluigi"), new String("Che brutto tempo oggi.. speriamo non piova"), null),
            new Post(new String("Gianmarco"), new String("Oggi palestra e domani piscina!"), null),
            new Post(new String("Luca"), new String("Il mio nome e' lungo 4 caratteri quindi non causa errori"), null),
            new Post(new String("AuthorDiLunghezza20!"), new String("Molto bello questo Social!"), null)
        };
        System.out.println("Ok");

        //printPostInfo (senza utenti menzionati)
        System.out.println("\n***TEST DEL METODO printPostInfo (senza utenti menzionati)***");
        for(int i = 0; i < vPost.length; ++i) {
            try {
                mb.printPostInfo(vPost[i]);
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            }
        }

        // Post (con valori errati)
        System.out.println("\n***TEST DEL COSTRUTTORE DI Post (con bad values)***");
        String[] badAuthors = { // authors che dovrebbero sollevare eccezioni (se passati al costruttore Post)
            new String("PR2"),
            null,
            new String("AuthorDiLunghezza21!!")
        };
        String[] badTexts = { // texts che dovrebbero sollevare eccezioni (se passati al costruttore Post)
            new String(""),
            null,
            new String("Post con piu di 140 caratteri....................... Post con piu di 140 caratteri....................... Post con piu di 140 caratteri.......................")
        };
        for(int i = 0; i < badAuthors.length && i < badTexts.length; ++i) {
            try {
                Post ps = new Post(badAuthors[i], "Testo senza errori!", null);
                System.out.println("**ATTENZIONE** " + ps.getAuthor() + " non ha causato errori.");
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (EmptyPostException | OutOfRangeException e) {
                System.out.println(e.getMessage());
            }
        }
        for(int i = 0; i < badAuthors.length && i < badTexts.length; ++i) {
            try {
                Post ps = new Post("AuthorQualunque", badTexts[i], null);
                System.out.println("**ATTENZIONE** " + ps.getText() + " non ha causato errori.");
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (EmptyPostException | OutOfRangeException e) {
                System.out.println(e.getMessage());
            }
        }
        
        // GuessFollower (con parametro null)
        System.out.println("\n***TEST DEL METODO GuessFollower (con parametro null)***");
        try {
            mapFollow = mb.guessFollowers(null);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }
        if(mapFollow != null)
            for(Map.Entry<String, Set<String>> iSet : mapFollow.entrySet()) {
                System.out.println(iSet.getKey() + ":");
                if(iSet.getValue() == null)
                    continue;
                for(String iStr : iSet.getValue())
                    if(iStr != null)
                        System.out.println("- " + iStr);
            }

        // GuessFollower (con rete sociale vuota)
        System.out.println("\n***TEST DEL METODO GuessFollower (con rete sociale vuota)***");
        for(Post ps : vPost)
            psList.add(ps);
        try {
            mapFollow = mb.guessFollowers(psList);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }
        if(!mapFollow.isEmpty())
            for(Map.Entry<String, Set<String>> iSet : mapFollow.entrySet()) {
                System.out.println(iSet.getKey() + ":");
                if(iSet.getValue() == null)
                    continue;
                for(String iStr : iSet.getValue())
                    if(iStr != null)
                        System.out.println("- " + iStr);
            }
        else
            System.out.println("Ok");
        
        // influencers (con rete sociale vuota)
        System.out.println("\n***TEST DEL METODO influencers (con rete sociale vuota)***");
        strList = mb.influencers();
        if(!strList.isEmpty())
            for(String str : strList)
                System.out.print(str + "  ");
        else
            System.out.println("Ok");
        
        // getMentionedUsers (senza menzionati)
        System.out.println("\n***TEST DEL METODO getMentionedUsers (senza menzionati)***");
        mentionedSet = mb.getMentionedUsers();
        if(!mentionedSet.isEmpty())
            for(String str : mentionedSet)
                System.out.print(str + "  ");
        else
            System.out.println("Ok");
        
        // addPost (per l'aggiunta di post alla rete)
        System.out.println("\n***TEST DEL METODO addPost (per l'aggiunta di post alla rete sociale)***");
        for(Post ps : psList) {
            try {
                mb.addPost(ps);
                System.out.println(ps.getAuthor() + " | " + ps.getText());
            } catch (UserNotFoundException | BadLikeException | PostNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        // addPost (per l'aggiunta di likes)
        System.out.println("\n***TEST DEL METODO addPost (per l'aggiunta di likes)***");
        // ECCEZIONI SOLLEVATE NELLE ULTIME DUE POSIZIONI
        // (la terz'ultima non solleva eccezioni ma il like non viene messo perche' gia' messo in precedenza)
        int[] mkLike  = {0,0,0,1,2,2,2,4,5,2,1,5}; // indici (in vPost) dei post i cui autori metteranno like
        int[] getLike = {2,3,5,5,4,1,0,0,3,1,3,5}; // indici (in vPost) dei post che prenderanno il like
        for(int i = 0; i < mkLike.length && i < getLike.length; ++i) {
            Post like;
            try {
                like = new Post(vPost[mkLike[i]].getAuthor(), "_like_" + vPost[getLike[i]].getId(), null);
                mb.addPost(like);
                likeList.add(like);
                System.out.println("A " + vPost[mkLike[i]].getAuthor() + " piace il post con ID: " + vPost[getLike[i]].getId());
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (OutOfRangeException | EmptyPostException | UserNotFoundException | BadLikeException | PostNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        // GuessFollower (con rete sociale vuota)
        System.out.println("\n***TEST DEL METODO GuessFollower (con rete sociale non vuota)***");
        //likeList.add(new Post("Antonio", "_like_Pierluigi3", null)); // like che non verra' messo (utente inesistente)
        try {
            mapFollow = mb.guessFollowers(psList);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }
        if(mapFollow != null)
            for(Map.Entry<String, Set<String>> iSet : mapFollow.entrySet()) {
                if(iSet.getKey() == null)
                    continue;
                System.out.println(iSet.getKey() + ":");
                if(iSet.getValue() == null)
                    continue;
                for(String str : iSet.getValue())
                    if(str != null)
                        System.out.println("- " + str);
            }

        // getLikes (con parametro null)
        System.out.println("\n***TEST DEL METODO getLikes (con parametro null)***");
        try {
            strList = mb.getLikes(null);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }

        // getLikes (con post assente in rete)
        System.out.println("\n***TEST DEL METODO getLikes (con post non presente nella rete sociale)***");
        try {
            strList = mb.getLikes(new Post("Filippo", "Ciao a tutti!", new HashSet<>()));
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        } catch (UserNotFoundException | BadLikeException e) {
            System.out.println(e.getMessage());
        }
        if(!strList.isEmpty())
            for(String str : strList)
                System.out.println("- " + str);
        else
            System.out.println("Ok");

        // getLikes (con post corretti)
        System.out.println("\n***TEST DEL METODO getLikes (con post corretti)***");
        for(int i = 0; i < psList.size(); ++i) {
            try{
                strList = mb.getLikes(psList.get(i));
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            }
            if(!strList.isEmpty()) {
                System.out.print("Post:  " + psList.get(i).getAuthor() + " | " + psList.get(i).getText() + " | " + "\n\t" + "Likes: ");
                for(String str : strList)
                    System.out.print(str + " ");
                System.out.println();
            }
        }
        
        // influencers (con rete non vuota)
        System.out.println("\n***TEST DEL METODO influencers (con rete sociale non vuota)***");
        strList = mb.influencers();
        if(!strList.isEmpty())
            for(String str : strList)
                System.out.print(str + "  ");

        // getMentionedUsers (con menzionati presenti)
        System.out.println("\n\n***TEST DEL METODO getMentionedUsers (con menzionati presenti)***");
        // AGGIUNTA POST CON MENZIONE DI UTENTI:
        Set<String> AndreaMentions = new HashSet<>();       // GOOD MENTIONS
        AndreaMentions.add("Pierluigi");
        AndreaMentions.add("Gianmarco");
        Set<String> GianmarcoMentions = new HashSet<>();    // GOOD MENTIONS
        GianmarcoMentions.add("AuthorDiLunghezza20!");
        Set<String> PierluigiMentions = new HashSet<>();    // GOOD MENTIONS
        PierluigiMentions.add("Andrea");
        PierluigiMentions.add("Luca");
        Set<String> GianlucaMentions = new HashSet<>();     // BAD MENTIONS
        GianlucaMentions.add("Gianluca");                   /* NON puo' mezionare se stesso */
        GianlucaMentions.add("Luca");                       /* NON puo' menzionare chi non segue */
        Post[] vPostMentions = { // NON dovrebbero sollevare eccezioni perche' l'user 'Gianluca' non ha ancora postato
            new Post(new String("Andrea"), new String("Oggi colloquio di lavoro! Secondo voi andra' bene?"), AndreaMentions),
            new Post(new String("Gianmarco"), new String("In questa quarantena non so mai che fare.. Organizziamo una videochiamata?"), GianmarcoMentions),
            new Post(new String("Pierluigi"), new String("Preferite Python o Java?"), PierluigiMentions),
            new Post(new String("Gianluca"), new String("Il mio QI e' pari a 118! Devo preccuparmi?"), GianlucaMentions)
        };
        for(Post ps : vPostMentions) {
            try {
                mb.addPost(ps);
            } catch (UserNotFoundException | BadLikeException | PostNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        mentionedSet = mb.getMentionedUsers();
        if(!mentionedSet.isEmpty())
            for(String str : mentionedSet)
                System.out.print("@" + str + "  ");
        
        // printPostInfo (con utenti menzionati)
        System.out.println("\n\n***TEST DEL METODO printPostInfo (con utenti menzionati)***");
        for(Post ps : vPostMentions) {
            try {
                mb.printPostInfo(ps);
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            }
        }

        // getMentionedUsers (con lista di post con menzioni di utenti)
        System.out.println("\n***TEST DEL METODO getMentionedUsers (con lista di post dei quali ottenere i menzionati)***");
        psList = new LinkedList<>();
        for(int i = 0; i < vPostMentions.length/2; ++i)
            psList.add(vPostMentions[i]);
        try {
            mentionedSet = mb.getMentionedUsers(psList);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }
        if(!mentionedSet.isEmpty())
            for(String str : mentionedSet)
                System.out.print("@" + str + "  ");
            
        // writtenBy(String) (con parametro null)
        System.out.println("\n\n***TEST DEL METODO writtenBy(String) (con parametro null)***");
        psList = null;
        try {
            psList = mb.writtenBy(null);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        if(psList != null)
            for(Post ps : psList)
                System.out.println("| " + ps.getText() + " |");
        
        // writtenBy(String) (con utente inesistente)
        System.out.println("\n***TEST DEL METODO writtenBy(String) (con utente inesistente)***");
        System.out.println("Post di Silvio:");
        try {
            psList = mb.writtenBy("Silvia");
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        if(psList != null)
            for(Post ps : psList)
                System.out.println("| " + ps.getText() + " |");
        
        // writtenBy(String) (con utente corretto)
        System.out.println("\n***TEST DEL METODO writtenBy(String) (con utente corretto)***");
        System.out.println("Post di Pierluigi:");
        try {
            psList = mb.writtenBy("Pierluigi");
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        if(psList != null)
            for(Post ps : psList)
                System.out.println("| " + ps.getText() + " |");

        // writtenBy(List<Post>,String) (con parametri corretti)
        System.out.println("\n***TEST DEL METODO writtenBy(List<Post>,String) (con parametri corretti)***");
        psList = new LinkedList<>();
        for(int i = 0; i < vPost.length; ++i)
            psList.add(vPost[i]);
        System.out.println("Post di Pierluigi (nella lista passata come parametro):");
        try {
            psList = mb.writtenBy(psList, "Pierluigi");
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        if(psList != null)
            for(Post ps : psList)
                System.out.println("| " + ps.getText() + " |");

        // containing (con parametro null)
        System.out.println("\n***TEST DEL METODO containing (con parametro null)***");
        psList = null;
        try {
            psList = mb.containing(null);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }
        if(psList != null)
            for(Post ps : psList)
                System.out.println("| " + ps.getText() + " |");

        // containing (con parametro corretto)
        System.out.println("\n***TEST DEL METODO containing (con parametro corretto)***");
        strList = new LinkedList<>();
        strList.add("quarantena");
        strList.add("java");
        strList.add("Python");
        strList.add("...");
        System.out.print("Parole cercate nei Post:  ");
        for(String str : strList)
            System.out.print("'" + str + "'" + "  ");
        System.out.println();
        try {
            psList = mb.containing(strList);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }
        if(psList != null)
            for(Post ps : psList)
                System.out.println("| " + ps.getText() + " |");
        
        // printAllPosts (della classe MicroBlog)
        System.out.println("\n***TEST DEL METODO printAllPosts (della classe MicroBlog)***");
        mb.printAllPosts();

        // postSignalation (con post inesistente)
        System.out.println("\n***TEST DEL METODO postSignalation (con post inesistente)***");
        MicroBlogSignalation mbs = new MicroBlogSignalation();
        // AGGIUNGO I POST ANCHE NELLA NUOVA RETE SOCIALE:
        for(int i = 0; i < vPost.length; ++i) { // dovrebbe sollevare le solite due eccezioni
            try {
                mbs.addPost(vPost[i]);
            } catch(NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (UserNotFoundException | BadLikeException | PostNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        for(int i = 0; i < mkLike.length && i < getLike.length; ++i) {
            Post like;
            try {
                like = new Post(vPost[mkLike[i]].getAuthor(), "_like_" + vPost[getLike[i]].getId(), null);
                mbs.addPost(like);
                likeList.add(like);
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (OutOfRangeException | EmptyPostException | UserNotFoundException | BadLikeException | PostNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        for(Post ps : vPostMentions) {
            try {
                mbs.addPost(ps);
            } catch(NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (UserNotFoundException | BadLikeException | PostNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        // segnalazione di un post inesistente
        Post post = new Post("Sabrina", "Testo qualunque... tanto non esiste nella rete sociale", null);
        try {
            if(mbs.postSignalation(post))
                System.out.println("Il post " + post.getId() + " CONTIENE 'bad contents'.");
            else
                System.out.println("Il post " + post.getId() + " NON CONTIENE 'bad contents'.");
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        } catch (PostNotFoundException e) {
            System.out.println(e.getMessage());
        }
        
        // postSignalation (con post esistenti, con anche menzionati)
        System.out.println("\n***TEST DEL METODO postSignalation (con post esistenti, precisamente con quelli aventi i menzionati)***");
        for(Post ps : vPostMentions) {
            try {
                if(mbs.postSignalation(ps))
                    System.out.println("Il post " + ps.getId() + " CONTIENE 'bad contents'.");
                else
                    System.out.println("Il post " + ps.getId() + " NON CONTIENE 'bad contents'.");
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (PostNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        // printAllPosts (della classe MicroBlogSignalation)
        System.out.println("\n***TEST DEL METODO printAllPosts (della classe MicroBlogSignalation)***");
        mbs.printAllPosts();

        return;
    }
}
