import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import Exceptions.UserNotFoundException;
import Exceptions.BadLikeException;
import Exceptions.PostNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class MicroBlog implements SocialNetwork {
    /**
     * OVERVIEW: permette di creare oggetti mutable i quali permettono
     *           la gestione di un Social Network attraverso operazioni di
     *           aggiunta di post, aggiunta di like e aggiunta di utenti
     *           alla rete socialestessa.
     * TYPICAL ELEMENT: <postMap, socialMap, likeMap> :
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
     */
    private Map<String, Post> postMap;
    private Map<String, Set<String>> reteSociale;
    private Map<String, Set<String>> likeMap;
    /**
     * AF:  f(this) = < {(ID1,P1),...,(IDn,Pn)} U {(U1,{Us1,Us2,..}),...,(Um,{Us1,Us2,..})} U {(ID1,{U1,U2,..}),...,(IDn,{U1,U2,..})} >   t.c.
     *      {(ID1,P1),...,(IDn,Pn)} :
     *          af(IDi) = Pi  forall i . 0 < i <= n
     *      {(U1,{Us1,Us2,..}),.....,(Um,{Us1,Us2,...})} :
     *          af(Ui) = {Us1,Us2,..} : (0 <= #{Us1,Us2,..} <= m) forall i . 0 < i <= m
     *      {(ID1,{U1,U2,..}),.....,(IDn,{U1,U2,..})} :
     *          af(IDi) = {U1,U2,..} : (0 <= #{U1,U2,..} <= m) forall i . 0 < i <= n
     *      con: Pi = post i-esimo, IDi = id del post i-esimo, U = username/autore, Us = utente seguito,
     *           n = #{post}, m = #{utenti}.
     * RI: RepInv(this) = (
     *          RI(Post) && postMap != null && socialMap != null && likeMap != null
     *          && #{postMap.key : postMap.key != null && postMap.key != ""} >= #{socialMap.key : socialMap.key != null && socialMap.key != ""}
     *          && #{postMap.key : postMap.key != null && postMap.key != ""}/2 >= #{likeMap.key : likeMap.key != null && likeMap.key != ""}
     *          && postMap(key_i) != postMap(key_j) forall key_i != key_j . (0 < i,j <= n)
     *          && socialMap(key_i) != socialMap(key_j) forall key_i != key_j . (0 < i,j <= m)
     *          && likeMap(key_i) != likeMap(key_j) forall key_i != key_j . (0 < i,j <= n)
     *          && postMap(key_i) of type Post forall key_i . (0 < i <= n)
     *          && likeMap(key_i) of type {String1,String2,...} forall key_i . (0 < i <= n)
     *          && socialMap(key_i) of type {String1,String2,...} forall key_i . (0 < i <= m)
     *          && postMap(key_i) != null forall key_i . postMap.containsKey(key_i) && (0 < i <= n)
     *          && socialMap(key_i) != null forall key_i . socialMap.containsKey(key_i) && (0 < i <= m)
     *          && likeMap(key_i) != null forall key_i . likeMap.containsKey(key_i) && (0 < i <= n)
     *          && forall ps . format(ps) = "_like_pID" ==> pID != ps.id
     *      )
     *      con: everyMap.key  = chiave generica di everyMap
     *           everyMap(key) = oggetto associato alla chiave key in everyMap
     */

    /**
     * @EFFECTS  crea una rete sociale vuota inizializzando i campi
     *           privati della classe.
     *           Formalmente:
     *           init(postMap) && init(socialMap) && init(likeMap)
     */
    public MicroBlog() {
        postMap     = new HashMap<>();
        reteSociale = new HashMap<>();
        likeMap     = new HashMap<>();
    }

    /**
     * @REQUIRES ps != null
     * @THROWS   NullPointerException (unchecked) if ps == null
     * @THROWS   UserNotFoundException (checked) if !socialMap.containsKey(ps.author)
     *                                              && !socialMap(ps.author).containsValue(a)
     *                                              forall a . ps.mentionedUsers.contains(a) && a = author
     * @THROWS   BadUserException (checked) if (ps.author likes p with ps.author = p.author)
     *                                         || (ps.author likes p with likeMap.containsKey(p.id))
     * @THROWS   PostNotFoundException (checked) if ps.author likes p
     *                                              && !postMap.containsKey(p.id)
     * @MODIFIES this t.c.
     *              post(postMap) = pre(postMap) U {ps} :
     *                  forall executions
     *              post(socialMap) = pre(postMap) U {(ps.author,{})} :
     *                  if !socialMap.containsKey(ps.author)
     *              post(socialMap(ps.author)) = pre(socialMap(ps.author)) U {p} :
     *                  if socialMap.containsKey(ps.author) && (ps.text = "_like_pID")
     *              post(likeMap) = pre(likeMap) U {(p.id, {ps.author})} :
     *                  if !socialMap.containsKey(p.id) && (ps.text = "_like_pID")
     *              post(likeMap(p.id)) = pre(likeMap(p.id)) U {ps.author} :
     *                  if socialMap.containsKey(p.id) && (ps.text = "_like_pID")
     * @EFFECTS  se il post ps passato come parametro non e' null e se gli utenti
     *           menzionati in ps non sono inesistenti lo aggiunge alla postMap
     *           e aggiunge il rispettivo autore alla socialMap se assente.
     *           Inoltre, se ps e' un like: aggiunge alla likeMap il post al
     *           quale e' stato messo like, aggiunge l'autore di ps all'insieme
     *           degli utenti nella likeMap che hanno messo like a quel post e
     *           infine aggiunge, tra utenti seguiti dall'autore di ps nella
     *           socialMap, l'autore del post a cui e' stato messo like.
     *           Formalmente:
     *           {ps != null && [forall user . ps.mentionedUsers.contains(user) ==> socialMap.containsKey(user)]}
     *           ==> {
     *                  [!socialMap.containsKey(ps.author) ==> socialMap.put(ps.author, {})]
     *                  && postMap.put(ps.id,ps) 
     *                  && [
     *                         ps.text = "_like_pID" ==> postMap.containsKey(pID)
     *                                                   && (!likeMap.containsKey(pID) ==> likeMap.put(pID, {}))
     *                                                   && likeMap(pID).add(ps.author)
     *                     ]
     *                  && socialMap(ps.author).add(p.author)
     *               }
     *           con: pID = id del post a cui e' stato messo like.
     */
    public void addPost(Post ps) throws NullPointerException, UserNotFoundException, BadLikeException, PostNotFoundException {
        if(ps == null)
            throw new NullPointerException();
        
        // caso lista menzionati non null e non vuota con controllo della presenza dei menzionati tra i follows dell'autore del post
        if(ps.getMentioned() != null && !ps.getMentioned().isEmpty() && reteSociale.containsKey(ps.getAuthor()))
            for(String author: ps.getMentioned())
                if(!reteSociale.get(ps.getAuthor()).contains(author))
                    throw new UserNotFoundException("addPost(Post)");
        
        // aggiunta dell'autore nella rete sociale (se assente)
        reteSociale.putIfAbsent(ps.getAuthor(), new HashSet<String>());

        // caso gestione del post contenente un like ad un altro post
        if(ps.getText().startsWith("_like_") && postMap.containsKey(ps.getText().substring(6))) {
            // like a se stesso || like ad un like
            if(ps.getText().substring(6).startsWith(ps.getAuthor()) || postMap.get(ps.getText().substring(6)).getText().contains("_like_"))
                throw new BadLikeException("addPost(Post)");
            int postExistFlag = 0;
            // ricerca del post nella mappa di post e se non presente setta flag per sollevare un eccezione
            for(Map.Entry<String, Post> p: postMap.entrySet())
                if(p.getKey().equals(ps.getText().substring(6)))
                    postExistFlag = 1;
            if(postExistFlag == 0)
                throw new PostNotFoundException("addPost(Post)");
            reteSociale.get(ps.getAuthor()).add(postMap.get(ps.getText().substring(6)).getAuthor());
            likeMap.putIfAbsent(ps.getText().substring(6), new HashSet<String>());
            likeMap.get(ps.getText().substring(6)).add(ps.getAuthor());
        }

        // aggiunta del post nella mappa di post (se assente)
        postMap.putIfAbsent(ps.getId(), ps);
    }

    /**
     * @REQUIRES s != null
     * @THROWS   NullPointerException (unchecked) if s == null
     * @EFFECTS  ritorna true se la stringa passata come parametro e'
     *           presente tra i post nel MicroBlog come chiave, false
     *           altrimenti.
     *           Formalmente:
     *           [return(true) <== s != null && postMap.containsKey(s)] ||
     *           [return(false) <== s != null && !postMap.containsKey(s)]
     */
    public boolean postMapContainsKey(String s) throws NullPointerException {
        if(s == null)
            throw new NullPointerException();
        return postMap.containsKey(s);
    }

    /**
     * @REQUIRES s != null
     * @THROWS   NullPointerException (unchecked) if s == null
     * @EFFECTS  ritorna la rete sociale associata all'utente passato
     *           come parametro.
     *           Formalmente:
     *           return(userSet) <== {
     *                                  user != null && socialMap.containsKey(user)
     *                                  ==> [forall u . socialMap.containsValue(u) ==> userSet.add(u)]
     *                               }
     */
    public Set<String> socialMapOf(String username) throws NullPointerException {
        if(username == null)
            throw new NullPointerException();
        Set<String> tmpSet = new HashSet<>();
        if(reteSociale.containsKey(username))
            tmpSet.addAll(reteSociale.get(username));
        return tmpSet;
    }

    /**
     * @REQUIRES ps != null
     * @THROWS   NullPointerException (unchecked) if ps == null
     * @EFFECTS  se il post passato come parametro e' diverso da null,
     *           ritorna una lista di stringhe contenenti gli autori che
     *           hanno messo like a tale post.
     *           Formalmente:
     *           return(likeList) <== {
     *                                      ps != null && likeMap.containsKey(ps.id) ==>
     *                                      [forall a . likeMap(ps.id).contains(a) ==> likeList.add(a))]
     *                                }
     */
    public List<String> getLikes(Post ps) throws NullPointerException {
        if(ps == null)
            throw new NullPointerException();
        LinkedList<String> likeList = new LinkedList<>();

        // caso di assenza di like (return di lista vuota)
        if(!likeMap.containsKey(ps.getId()))
            return likeList;
        
        // caso di presenza di like (aggiunta dei like alla lista di ritorno)
        for(String str : likeMap.get(ps.getId()))
            likeList.add(str);
        return likeList;
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
        
        // stampa le informazioni del post (tranne i menzionati)
        ps.printInfo();

        // stampa i menzionati del post (se presenti)
        if(ps.getMentioned() != null && !ps.getMentioned().isEmpty()) {
            System.out.print("       Mentioned: ");
            for(String str : ps.getMentioned())
                if(reteSociale.get(ps.getAuthor()).contains(str))
                    System.out.print("@" + str + " ");
            System.out.println();
        }
    }

    /**
     * @EFFECTS  stampa tutti i post caricati su MicroBlog con tutte le
     *           informazioni associate ai post (se non ci sono post uguali
     *           a null all'interno del MicroBlog).
     *           Formalmente:
     *           [print(p) <== !socialMap.isEmpty]  forall p . postMap.containsValue(p)
     */
    public void printAllPosts() {
        for(Map.Entry<String, Post> ps : postMap.entrySet()) {
            try {
                printPostInfo(ps.getValue());
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            }
        }
    }

    /**
     * @REQUIRES psList != null
     * @THROWS   NullPointerException (unchecked) if psList == null
     * @EFFECTS  ritorna una mappa contenente un insieme di utenti, cioe' gli
     *           autori dei post della lista passata come parametro (se diversa
     *           da null), ad ognuno dei quali e' associato l'insieme di utenti
     *           che lo seguono. Gli utenti non presenti nella rete sociale non
     *           verranno aggiunti.
     *           Formalmente:
     *           return(returnMap) <== {
     *                                  psList != null ==>  [
     *                                                          forall p . 
     *                                                          psList.contains(p) &&
     *                                                          socialMap.containsKey(p.author) ==>
     *                                                          [
     *                                                              forall a . socialMap.containsKey(a) &&
     *                                                                         socialMap(a).contains(p.author) &&
     *                                                                         a != p.author ==>
     *                                                                              returnMap.put(p.author,a);
     *                                                          ]
     *                                                      ]
     *                                 }
     *           (con: returnMap = oggetto di ritorno contenente associazioni utente -> {follows1,follows2,...})
     */
    public Map<String, Set<String>> guessFollowers(List<Post> psList) throws NullPointerException {
        if(psList == null)
            throw new NullPointerException();
        HashMap<String, Set<String>> tmpMap = new HashMap<>();
        Set<String> tmpSet = new HashSet<>();

        // ricerca gli utenti che seguono gli autori dei post passati come parametro
        for(Post ps : psList) {
            for(Map.Entry<String, Set<String>> iSet : reteSociale.entrySet())
                if(iSet.getValue().contains(ps.getAuthor()))
                    tmpSet.add(iSet.getKey());
            tmpMap.putIfAbsent(ps.getAuthor(), tmpSet);
            tmpSet = new HashSet<>();
        }
        return tmpMap;
    }

    /**
     * @EFFECTS  se socialMap e' vuota ritorna null, altrimenti ritorna
     *           la lista di utenti il cui numero di followers supera
     *           la media dei followers, calcolata tra tutti gli utenti
     *           all'interno del MicroBlog.
     *           Formalmente:
     *           return(influencersList) <== [forall a . socialMap.containsKey(a) && (#followers(a) > mediaFollowers) ==> influencersList.add(a)]
     */
    public List<String> influencers() {
        LinkedList<String> strList = new LinkedList<>();

        // caso rete sociale vuota (ritorno lista vuota)
        if(reteSociale.isEmpty())
            return strList;
        float media = 0;                                        // valore che conterra' la media di followers degli utenti
        int cont = 0;                                           // contatore degli utenti presenti nella rete sociale
        Map<String, Integer> influencersMap = new HashMap<>();  // struttura dati contenente coppie: (author, #followers)

        // calcola il numero di followers di ogni utente e li aggiunge ad influencersMap
        for(Map.Entry<String, Set<String>> iSetExt : reteSociale.entrySet()) {
            String author = iSetExt.getKey();
            int followers = 0;
            for(Map.Entry<String, Set<String>> iSetInt : reteSociale.entrySet())
                for(String str : iSetInt.getValue())
                    if(str.equals(author))
                        followers++;
            media += followers;
            cont++;
            influencersMap.put(author, followers);
        }
        // media di followers
        media = media/cont;

        // aggiunge alla struttura dati di ritorno chi ha piu' followers della media (in influencersMap)
        for(Map.Entry<String, Integer> influencerNum : influencersMap.entrySet())
            if(influencerNum.getValue().intValue() > media)
                strList.add(new String(influencerNum.getKey()).concat(" ").concat(String.valueOf(influencerNum.getValue().intValue())));
        return strList;
    }

    /**
     * @EFFECTS  ritorna l'insieme degli utenti menzionati nei post
     *           presenti sulla rete.
     *           Formalmente:
     *           [
     *              forall a,p . socialMap.containsKey(a) &&
     *                           postMap.containsKey(p.id) &&
     *                           postMap(p.id).mentions.contains(a)
     *                           ==> userSet.add(a)
     *           ] ==> return(userSet)
     *           
     */
    public Set<String> getMentionedUsers() {
        HashSet<String> tmpSet = new HashSet<>();

        // scansiona tutti i post nella rete memorizzandone tutti i menzionati dai vari utenti (tranne le ripetizioni)
        for(Map.Entry<String, Post> ps: postMap.entrySet()) {
            if(ps.getValue().getMentioned() != null)
                for(String str : ps.getValue().getMentioned())
                    if(reteSociale.get(ps.getValue().getAuthor()).contains(str) && !ps.getValue().getAuthor().equals(str))
                        tmpSet.add(str);
        }
        return tmpSet;
    }

    /**
     * @REQUIRES  psList != null
     * @THROWS   NullPointerException (unchecked) if psList == null
     * @EFFECTS  se la lista passata come parametro non e' null, ritorna
     *           l'insieme di utenti menzionati nei post contenuti
     *           nella lista.
     *           Formalmente:
     *           {
     *              psList != null ==> [forall p,a .
     *                                  socialMap.containsKey(a) && postMap.containsKey(p.id) &&
     *                                  psList.contains(p.id) && psList(p).mentions.contains(a)
     *                                  ==> userSet.add(a)]
     *           } ==> return(userSet)
     */
    public Set<String> getMentionedUsers(List<Post> psList) throws NullPointerException {
        if(psList == null)
            throw new NullPointerException();
        HashSet<String> tmpSet = new HashSet<>();

        // cerca gli utenti menzionati nei post di 'psList' e li aggiunge a 'tmpSet'
        for(Post ps : psList)
            if(ps.getMentioned() != null && !ps.getMentioned().isEmpty())
                for(String str : ps.getMentioned())
                    if(reteSociale.get(ps.getAuthor()).contains(str))
                        tmpSet.add(str);
        return tmpSet;
    }

    /**
     * @REQUIRES username != null && socialMap.containsKey(username)
     * @THROWS   NullPointerException (unchecked) if username == null
     * @THROWS   UserNotFoundException (checked) if !socialMap.containsKey(username)
     * @EFFECTS  se l'username e' diverso da null ed e' presente nella rete
     *           sociale, ritorna la lista di post scritti e caricati sulla
     *           rete dall'utente passato come paramentro.
     *           Formalmente:
     *           return(postList) <== {
     *                                  username != null && socialMap.containsKey(username) 
     *                                  ==> [
     *                                          forall p . 
     *                                          postMap.containsKey(p.id)
     *                                          && p.author == username
     *                                          ==> postList.add(p)
     *                                      ]
     *                                }
     */
    public List<Post> writtenBy(String username) throws NullPointerException, UserNotFoundException {
        if(username == null)
            throw new NullPointerException();
        if(!reteSociale.containsKey(username))
            throw new UserNotFoundException("writtenBy(String)");
        LinkedList<Post> tmpList = new LinkedList<>();

        // aggiunge i post a 'tmpList' appartenenti all'utente passato come parametro
        for(Map.Entry<String, Post> ps: postMap.entrySet())
            if(ps.getKey().startsWith(username))
                tmpList.add(ps.getValue());
        return tmpList;
    }

    /**
     * @REQUIRES psList != null && username != null && socialMap.containsKey(username)
     * @THROWS   NullPointerException (unchecked) if username == null || psList == null
     * @THROWS   UserNotFoundException (checked) if !socialMap.containsKey(username)
     * @EFFECTS  se l'username e la lista psList passati come parametro sono
     *           diversi da null e l'utente e' presente nella rete sociale,
     *           ritorna la lista di post scritti e caricati sulla rete sociale
     *           dall'utente passato come paramentro che sono presenti anche
     *           sulla lista.
     *           Formalmente:
     *           return(returnList) <== {
     *                                      username != null && psList != null && socialMap.containsKey(username) 
     *                                      ==> [
     *                                              forall p . 
     *                                              psList.contains(p)
     *                                              && postMap.containsKey(p.id)
     *                                              && p.author == username
     *                                              ==> returnList.add(p)
     *                                          ]
     *                                  }
     */
    public List<Post> writtenBy(List<Post> psList, String username) throws NullPointerException, UserNotFoundException {
        if(username == null || psList == null)
            throw new NullPointerException();
        if(!reteSociale.containsKey(username))
            throw new UserNotFoundException("writtenBy(List<Post>, String)");
        LinkedList<Post> tmpList = new LinkedList<>();

        // aggiunge a 'tmpList' tutti i post appartenenti all'utente 'username' presenti in 'psList'
        for(Post ps: psList)
            if(ps.getAuthor().equals(username))
                tmpList.add(ps);
        return tmpList;
    }

    /**
     * @REQUIRES words != null
     * @THROWS   NullPointerException (unchecked) if words == null
     * @EFFECTS  viene passata come paramentro una lista contenente le parole
     *           da ricercare nel testo dei post. Se la lista wordsList
     *           passata come parametro e' diversa da null aggiunge in
     *           un'altra listai post contenenti le parole presenti in
     *           wordsList.
     *           Formalmente:
     *           return(returnList) <== {
     *                                      words != null ==> [
     *                                                          forall s,p .
     *                                                          wordsList.contains(s)
     *                                                          && postMap.containsKey(p.id)
     *                                                          && p.text.contains(s)
     *                                                          ==> returnList.add(p)
     *                                                        ]
     *                                  }
     */
    public List<Post> containing(List<String> words) throws NullPointerException {
        if(words == null)
            throw new NullPointerException();
        LinkedList<Post> tmpList = new LinkedList<>();

        // aggiunge a 'tmpList' tutti i post contenenti almeno 1 delle parole presenti in 'words'
        for(Map.Entry<String, Post> ps: postMap.entrySet())
            for(String str: words)
                if(!str.isEmpty() && ps.getValue().getText().contains(str)) {
                    tmpList.add(ps.getValue());
                    break;
                }
        return tmpList;
    }

}