import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import Exceptions.PostNotFoundException;

public class MicroBlogSignalation extends MicroBlog {
    /**
     * OVERVIEW: permette di creare oggetti mutable i quali permettono
     *           la gestione di un Social Network attraverso operazioni di
     *           aggiunta di post, aggiunta di like, aggiunta di utenti
     *           alla rete sociale e di segnalazione dei post.
     * TYPICAL ELEMENT: <postMap, socialMap, likeMap, wordsList, signaledList> :
     *          postMap = {(ID1,P1),...,(IDn,Pn)} :
     *                  (IDi,Pi) = coppia che associa all'ID (univoco) del post
     *                  i-esimo IDi il post i-esimo stesso.
     *          socialMap = {(U1,{Us1,Us2,..}),.....,(Um,{Us1,Us2,..})} :
     *                   (Ui,{Us1,...,Usj}) = coppia che associa all'utente i-esimo
     *                   tutti gli utenti seguiti da questo, ovvero quegli utenti
     *                   {Us1,...,Usj} a cui ha messo almeno un like ad almeno uno
     *                   dei loro post.
     *          likeMap = {(ID1,{U1,U2,..}),.....,(IDn,{U1,U2,..})} :
     *                  (IDi,{U1,...,Uk}) = coppia che associa ad l'ID del post
     *                  i-esimo tutti gli utenti Uj che hanno messo like al post.
     *          wordsList = (W1,W2,...) :
     *                  Wi = parola i-esima che il post non deve contenere
     *          signaledList = (Ps1,Ps2,...) :
     *                  Psi = post segnalato i-esimo
     */
    private final List<String> badWords;
    private List<String> badPostList;
    /**
     * AF: f(this) = <postMap, socialMap, likeMap, wordsList, signaledList> =   <   {(ID1,P1),...,(IDn,Pn)},
     *                                                                              {(U1,{Us1,Us2,..}),.....,(Um,{Us1,Us2,...})},
     *                                                                              {(ID1,{U1,U2,..}),.....,(IDn,{U1,U2,..})},
     *                                                                              (W1,W2,...),
     *                                                                              (Ps1,Ps2,...)  >    t.c.
     *      {(ID1,P1),...,(IDn,Pn)} :
     *          af(IDi) = Pi  forall i . (0 < i <= n)
     *      {(U1,{Us1,Us2,..}),.....,(Um,{Us1,Us2,...})} :
     *          af(Ui) = {Us1,Us2,..} : (0 <= #{Us1,Us2,..} <= m) forall i . (0 < i <= m)
     *      {(ID1,{U1,U2,..}),.....,(IDn,{U1,U2,..})} :
     *          af(IDi) = {U1,U2,..} : (0 <= #{U1,U2,..} <= m) forall i . (0 < i <= n)
     *      (W1,W2,...) : Wi of type String forall i . (i > 0)
     *      (Ps1,Ps2,...) : Psi of type Post forall i . (0 < i <= n)
     *      con: Pi = post i-esimo, IDi = id del post i-esimo, U = username/autore, Us = utente seguito,
     *           Wi = word i-esima, Psi = post segnalato i-esimo, n = #{post}, m = #{utenti}.
     * RI: RepInv(this) = (
     *          also && badWords != null && badPostList != null
     *          && badPostList.size <= #{postMap.key : postMap.key != null && postMap.key != ""}  forall key . postMap.containsKey(key)
     *      )
     */


    /**
     * @EFFECTS  inizializza tutti i campi privati della classe.
     *           Formalmente:
     *           init(postMap) && init(socialMap) && init(likeMap) && init(signaledList) && init(wordsList)
     */
    public MicroBlogSignalation() {
        super();
        badPostList = new LinkedList<>();
        List<String> strList = new LinkedList<>();
        strList.add("post");
        strList.add("Java");
        strList.add("quarantena");
        strList.add("programmazione");
        badWords = strList;
    }

    /**
     * @REQUIRES ps != null && postMap.containsKey(ps.id)
     * @THROWS   NullPointerException (unchecked) if ps == null
     * @THROWS   PostNotFoundException (checked) if !postMap.containsKey(ps.id)
     * @MODIFIES this t.c.
     *              post(signaledList) = pre(signaledList) U {ps} :
     *              if not[forall s . wordsList.contains(s) && !ps.text.contains(s)]
     * @EFFECTS  se il post passato come parametro non e' null e se il
     *           post e' presente nel social network, ritorna true se il
     *           post contiene almeno una delle parole presenti nella
     *           wordsList (e in tal caso aggiunge il post nella signaledList).
     *           Altrimenti ritorna false.
     *           Formalmente:
     *           ps != null && postMap.containsKey(ps)
     *           ==> {return(true) <== [not(forall s . wordsList.contains(s) && !ps.text.contains(s))
     *                                  ==> signaledList.add(ps)]} ||
     *           {return(false) <== [forall s . wordsList.contains(s) && !ps.text.contains(s)]}
     */
    public boolean postSignalation(Post ps) throws NullPointerException, PostNotFoundException {
        if(ps == null)
            throw new NullPointerException();
        if(!postMapContainsKey(ps.getId()))
            throw new PostNotFoundException("postSignalation");
        
        // controlla se nel post e' presente almeno 1 'bad word' (e in tal caso aggiunge il post alla lista dei segnalati)
        for(String str : badWords)
            if(ps.getText().contains(str)) {
                if(!badPostList.contains(ps.getId()))
                    badPostList.add(ps.getId());
                return true;
            }
        return false;
    }

    /**
     * @REQUIRES ps != null
     * @THROWS   NullPointerException (unchecked) if ps == null
     * @EFFECTS  stampa a video tutte le informazioni del post passato
     *           come parametro (se diverso da null).
     *           Formalmente:
     *           print(ps) <== ps != null
     */
    public void printPostInfo(Post ps) throws NullPointerException {
        if(ps == null)
            throw new NullPointerException();
        
        // se il post e' stato segnalato chiama il metodo di censura del testo
        if(!badPostList.contains(ps.getId()))
            ps.printInfo();
        else
            ps.printSignalatedInfo();
        
        // stampa gli utenti menzionati nel post (se presenti)
        if(ps.getMentioned() != null && !ps.getMentioned().isEmpty()) {
            System.out.print("       Mentioned: ");
            Set<String> tmpSet = socialMapOf(ps.getAuthor());
            for(String str : ps.getMentioned())
                if(tmpSet.contains(str))
                    System.out.print("@" + str + " ");
            System.out.println();
        }
    }
}