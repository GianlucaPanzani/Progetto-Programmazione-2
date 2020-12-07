import java.sql.Timestamp;
import java.util.HashSet;
import Exceptions.EmptyPostException;
import Exceptions.OutOfRangeException;
import java.util.Set;

public class Post implements PostInterface {
    /**
     * OVERVIEW: permette di rappresentare un post sottoforma di oggetto immutable al
     *           quale si associa: ID univoco, testo, autore del post e
     *           data/ora di creazione. Talvolta puo' contenere anche una lista
     *           di nomi che rappresentano le persone menzionate dall'autore.
     * TYPICAL ELEMENT: <id, autore, testo, clock, mentions>
     *                  con: mentions = {menzionato1,...,menzionatoN}
     */
    private final String id;
    private final String author;
    private final String text;
    private final Timestamp timestamp;
    private final Set<String> mentioned;
    private static int counter;
    /**
     * AF: f(this) =  <id, autore, testo, clock, mentions>
     *      a(id)       = stringa univoca e identificativa del post.
     *      a(author)   = stringa che identifica l'autore del post.
     *      a(text)     = stringa di testo del post.
     *      a(clock)    = (data,ora) -> coppia che identifica il momento di creazione del post.
     *      a(mentions) = {m1,...,mN} t.c.
     *                    mi = menzionato i-esimo, forall i  t.c.  0 < i < N+1.
     *                    N  = numero >= 0 di utenti menzionati dall'autore.
     * RI: RepInv(this) = (
     *           id != null && author != null && (3 < author.lenght < 21)
     *           && text != null && (0 < text.lenght < 141) && timestamp != null 
     *           && counter > -1 && [PostObject.id != this.id forall PostObject != this]
     *           && [forall mi,mj . mentions.contains(mi) && mentions.contains(mj) ==> mi != mj]
     *      )
     */

    /**
     * @REQUIRES author != null && text != null && (3 < author.lenght < 21)
     *           && (0 < text.lenght < 141)
     * @THROWS   NullPointerException (unchecked) if author == null || text == null
     * @THROWS   EmptyPostException (checked) if text.lenght == 0
     * @THROWS   OutOfRangeException (checked) if author.length < 4 || author.lenght > 20 || text.length > 140
     * @MODIFIES this
     * @EFFECTS  crea un nuovo Post con autore 'author', testo 'text' (con
     *           author e text paramentri del metodo) e genera un ID univoco
     *           associato al post. Inoltre aggiunge i menzionati se presenti.
     *           Formalmente:
     *           [author != null && text != null && (3 < author.length < 21) && (0 < text.length < 141)]
     *           ==> [this.author = author && this.text = text && this.mentions = mentions && set(id) && set(clock)]
     */
    public Post(String author, String text, Set<String> mentioned) throws NullPointerException, EmptyPostException, OutOfRangeException {
        if(author == null || text == null)
            throw new NullPointerException();
        if(text.length() == 0)
            throw new EmptyPostException("Post(String, String)");
        if(author.length() < 4 || author.length() > 20 || text.length() > 140)
            throw new OutOfRangeException("Post(String, String)");
        
        // inizializzazione dei campi privati
        counter++;
        this.author = new String(author);
        this.text   = new String(text);
        if(mentioned != null)
            this.mentioned = new HashSet<>(mentioned);
        else
            this.mentioned = null;
        
        // generazione dell'id univoco del post
        String concat1 = new String(this.author);
        id = new String(concat1.concat(String.valueOf(counter)));
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * @EFFECTS stampa una stringa contenente l'autore, l'id, la data/ora di
     *          creazione e il testo del post.
     *          Formalmente:
     *          print(ps.author U ps.id U ps.clock U ps.text)
     */
    public void printInfo() {
        System.out.println(getAuthor() + " | Id:" + getId() + " | " + getDataAndHours() + " | " + getText());
        return;
    }

    /**
     * @EFFECTS stampa una stringa contenente l'autore, l'id, la data/ora di
     *          creazione e il testo del post con testo oscurato (solitamente
     *          chiamato a causa di una segnalazione del post).
     *          Formalmente:
     *          print(ps.author U ps.id U ps.clock U defaultText)
     *          con: defaultText = testo di default sostitutivo al testo effettivo.
     */
    public void printSignalatedInfo() {
        System.out.println(getAuthor() + " | Id:" + getId() + " | " + getDataAndHours() + " | " + "IL TESTO E' STATO SEGNALATO PER CONTENUTI INAPPROPRIATI");
        return;
    }

    /**
     * @EFFECTS stringa contenente data e ora di creazione del post.
     *          Formalmente:
     *          return(clock)
     */
    public String getDataAndHours() {
        return new String(timestamp.toString());
    }

    /**
     * @EFFECTS stringa contenente l'id del post.
     *          Formalmente:
     *          return(id)
     */
    public String getId() {
        return new String(id);
    }

    /**
     * @EFFECTS stringa contenente il testo del post.
     *          Formalmente:
     *          return(text)
     */
    public String getText() {
        return new String(text);
    }

    /**
     * @EFFECTS stringa contenente l'autore del post.
     *          Formalmente:
     *          return(author)
     */
    public String getAuthor() {
        return new String(author);
    }

    /**
     * @EFFECTS insieme di stringhe che rappresentano gli utenti menzionati
     *          nel post.
     *          Formalmente:
     *          [return(mentions) <== mentions != null && mentions.size > 0] ||
     *          [return(null) <== mentions == null || mentions.size == 0]
     */
    public HashSet<String> getMentioned() {
        if(mentioned != null && mentioned.size() > 0)
            return new HashSet<String>(mentioned);
        else
            return null;
    }
}