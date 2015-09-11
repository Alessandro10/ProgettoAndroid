package com.example.tonino.login;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.sromku.simple.fb.SimpleFacebook;

import org.apache.http.client.HttpClient;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by tonino on 17/04/15.
 */
public class Salva {

    private static String idFacebook = "";

    public static void setIdFacebook(String id)
    {
        idFacebook = id;
    }

    public static String getIdFacebook()
    {
        return idFacebook;
    }

    private static String username = "";
    private static String password = "";

    public static void setUsername(String user)
    {
        username = user;
    }

    public static void setPassword(String psw)
    {
        password = psw;
    }

    public static String getUsername()
    {
        return username;
    }

    public static String getPassword()
    {
        return password;
    }

    private static Dictionary<Integer, Dictionary<Integer , Integer>> []superDict =
            new Hashtable[4];
    private static String []ordina = {"Obbiettivo" , "Obbiettivo" , "Obbiettivo"};
    private static int []ore = {23 , 23 , 23};
    private static int []minuti = {59 , 59 , 59};
    private static int []distanza = {15 , 15 , 15};
    private static Boolean []setRicerca = {false , false , false};
    private static String []testoricerca = {"","",""};
    private static Boolean []settaggio = {false , false , false};
    private static int fragmentSelected = 0;
    private static int nameFragment = 0;
    private static String []titoli = {"Home" , "Premi" , "Percorsi"};
    private static String []textPO = {"" , ""};
    private static Boolean []ricercaPO = {false , false};
    private static String []titleTab = {"Vicinanza" , "Consigli" , "Ricerca"};
    private static int nameTab = 0;
    private static int posTab = 0;
    private static Dictionary<Integer ,
            Percorsi.Percorso> dictPercorso =
            new Hashtable<Integer , Percorsi.Percorso>();
    private static GoogleMap map;
    private static Location pos;
    private static Boolean onRotation = false;

    private static boolean []tempoScaduto = {false , false , false};

    private static boolean []aggiorna = {false , false , false};

    private static boolean []settaggioMultiple = {false , false , false};

    private static String nomeUtente = "";

    private static int cancellaPremio = -1;

    private static SimpleFacebook mSimpleFacebook;

    public static void setSimpleFacebook(SimpleFacebook m2)
    {
        mSimpleFacebook = m2;
    }

    public static SimpleFacebook getmSimpleFacebook()
    {
        return mSimpleFacebook;
    }

    public static void setCancellaPremio(int cancellaPremio2)
    {
        cancellaPremio = cancellaPremio2;
    }

    public static int getCancellaPremio()
    {
        return cancellaPremio;
    }

    public static void setNomeUtente(String nomeUtente2)
    {
        nomeUtente = nomeUtente2;
    }

    public static String getNomeUtente()
    {
        return nomeUtente;
    }


    public static void setSettaggioMultiple(boolean bool , int type)
    {
        settaggioMultiple[type] = bool;
    }

    public static boolean getSettaggioMultiple(int type)
    {
        return settaggioMultiple[type];
    }



    public static void setAggiorna(boolean bool, int type)
    {
        aggiorna[type] = bool;
    }

    public static boolean getAggiorna(int type)
    {
        return aggiorna[type];
    }

    public static void setTempoScaduto(boolean bool, int type)
    {
        tempoScaduto[type] = bool;
    }

    public static boolean getTempoScaduto(int type)
    {
        return tempoScaduto[type];
    }

    private static boolean accountFB = false;

    public static void setAccountFB(boolean bool)
    {
        accountFB = bool;
    }

    public static boolean getAccountFB()
    {
        return accountFB;
    }

    private static Dictionary<Integer , Dictionary<Integer, Percorsi.Percorso>> ArrayPercorsi = new
            Hashtable<Integer , Dictionary<Integer, Percorsi.Percorso>>();

    private static Dictionary< Integer , Dictionary<String , Percorsi.Operators>> ArrayOperators = new
            Hashtable<Integer , Dictionary<String, Percorsi.Operators>>();

    private static Dictionary<Integer , Dictionary<Integer , Percorsi.Objective>> ArrayObiettivo =
            new Hashtable<Integer , Dictionary<Integer , Percorsi.Objective>>();

    private static Dictionary<Integer , Dictionary<Integer , Percorsi.PremioS>> ArrayPremio =
            new Hashtable<Integer , Dictionary<Integer , Percorsi.PremioS>>();

    private static Dictionary<Integer ,  List<String>> ArrayListTipoPercorsi =
            new Hashtable<Integer ,  List<String>> ();

    private static Dictionary<Integer ,  List<String>> ArrayListTipoPremi =
            new Hashtable<Integer ,  List<String>> ();

    private static HttpClient client;

    private static boolean boolRicerca[][] = new boolean[3][];

    private static String [][]Pr_string = new String[4][];

    private static String [][]Ob_string = new String[4][];

    private static Dictionary<Integer, Percorsi.Percorso> dictPercorsoDaPassare =
            new Hashtable<Integer, Percorsi.Percorso>();

    private static GoogleMap Map;

    private static String qrcode;

    private static List<String> listaDeiTagsPreferiti = new ArrayList<String>();

    private static Dictionary<Integer , List<String>> dictListpreferitiPercorsi = new Hashtable<Integer , List<String>>();
    private static Dictionary<Integer , List<String>> dictListpreferitiPremi = new Hashtable<Integer , List<String>>();

    public static void setListPreferiti(List<String> listapreferitiPercorsi , List<String> listapreferitiPremi , int type)
    {
        dictListpreferitiPercorsi.put(type , listapreferitiPercorsi);
        dictListpreferitiPremi.put(type , listapreferitiPremi);
    }

    public static List<String> getListPreferitiPercorsi(int type)
    {
        return dictListpreferitiPercorsi.get(type);
    }

    public static List<String> getListPreferitiPremi(int type)
    {
        return dictListpreferitiPremi.get(type);
    }

    public static void setListaDeiTagsPreferiti(List<String> listaDeiTagsPreferiti2)
    {
        listaDeiTagsPreferiti = listaDeiTagsPreferiti2;
    }

    public static List<String> getListaDeiTagsPreferiti()
    {
        return listaDeiTagsPreferiti;
    }

    public static void setQrcode(String qrcode2)
    {
        qrcode = qrcode2;
    }

    public static String getQrcode()
    {
        return qrcode;
    }

    public static void setGMap(GoogleMap map2)
    {
        Map = map2;
    }

    public static GoogleMap getGMap()
    {
        return Map;
    }

    public static void setDictPercorsoDaPassare( Dictionary<Integer, Percorsi.Percorso> dictPercorsoDaPassare2)
    {
        dictPercorsoDaPassare = dictPercorsoDaPassare2;
    }

    public static Dictionary<Integer, Percorsi.Percorso> getDictPercorsoDaPassare()
    {
        return dictPercorsoDaPassare;
    }

    public static void setPr_string(String []Pr_string2 , int type)
    {
        Pr_string[type] = Pr_string2;
    }

    public static void setOb_string(String []Ob_string2 , int type)
    {
        Ob_string[type] = Ob_string2;
    }

    public static String[] getPr_string(int type)
    {
        return Pr_string[type];
    }

    public static String[] getOb_string(int type)
    {
        return Ob_string[type];
    }

    public static void setBoolRicerca(boolean[] bool2 , int type)
    {
        boolRicerca[type] = bool2;
    }

    public static boolean[] getBooleRicerca(int type)
    {
        return boolRicerca[type];
    }

    /*public static void getSuperDictRicerca(int type)

    public static Dictionary getSuperDictRicerca(int type)
    {
        return dictSuperRegistrazione.get(type);
    }*/

    public static void setHttClient(HttpClient client2)
    {
        client = client2;
    }

    public static HttpClient getHttpClient()
    {
        return client;
    }

    private static Dictionary<Integer, Dictionary<Integer , Integer>> superDictRegistrazione;

    private static String []ArraytipiRegistrazione;

    private static boolean []boolRegistrazione;

    public static void setDizionarioRegistrazione(Dictionary<Integer, Dictionary<Integer , Integer>> superDictRegistrazione2)
    {
        superDictRegistrazione = superDictRegistrazione2;
    }

    public static void setBoolRegistrazione(boolean []bool2)
    {
        boolRegistrazione = bool2;
    }

    public static void setArraytipiRegistrazione(String []array)
    {
        ArraytipiRegistrazione = array;
    }

    public static void setPercorsoReceive(Dictionary<Integer, Percorsi.Percorso> dictP , int type)
    {
        //dictPercorsoReceive = dictP;
        ArrayPercorsi.put(type , dictP);
    }

    public static String[] getArrayTipiRegistrazione()
    {
        return ArraytipiRegistrazione;
    }

    public static boolean[] getBoolRegistrazione(){
        return boolRegistrazione;
    }

    public static Dictionary<Integer, Dictionary<Integer , Integer>> getDizionarioRegistrazione()
    {
        return superDictRegistrazione;
    }

    public static void setObiettivoReceive(Dictionary<Integer , Percorsi.Objective> dictO, int type )
    {
        //dictOb = dictO;
        ArrayObiettivo.put(type , dictO);
    }

    public static void setPremioReceive(Dictionary<Integer , Percorsi.PremioS> dictPre2, int type)
    {
        //dictPre = dictPre2;
        ArrayPremio.put(type , dictPre2);
    }

    public static void setOperatoriReceive(Dictionary<String , Percorsi.Operators>
                                                   dictOperators2 , int type)
    {
        ArrayOperators.put(type , dictOperators2);
        //dictOperators = dictOperators2;
    }

    public static void setTipiPercorsoReceive(List<String> tipiPercorsi2 , int type)
    {
        ArrayListTipoPercorsi.put(type , tipiPercorsi2);
        //tipiPercorsi = tipiPercorsi2;
    }

    public static void setTipiPremiReceive(List<String> tipiPremi2 , int type)
    {
        ArrayListTipoPremi.put(type , tipiPremi2);
        //tipiPremi = tipiPremi2;
    }

    public static Dictionary<Integer, Percorsi.Percorso> getPercorsoReceive(int type)
    {
        return ArrayPercorsi.get(type);
        //return dictPercorsoReceive;
    }

    public static Dictionary<Integer , Percorsi.Objective> getObiettivoReceive(int type)
    {
        return ArrayObiettivo.get(type);
        //return  dictOb;
    }

    public static Dictionary<Integer , Percorsi.PremioS> getPremioReceive(int type)
    {
        return ArrayPremio.get(type);
        //return dictPre;
    }

    public static Dictionary<String , Percorsi.Operators> getOperatoriReceive(int type)
    {
        return ArrayOperators.get(type);
        //return dictOperators;
    }

    public static List<String> getTipiPercorsoReceive(int type)
    {
        return ArrayListTipoPercorsi.get(type);
        //return tipiPercorsi;
    }

    public static List<String> getTipiPremiReceive(int type)
    {
        return ArrayListTipoPremi.get(type);
        //return tipiPremi;
    }

    public static void OnRotation(Boolean value)
    {
        onRotation = value;
    }

    public static Boolean getOnRotation()
    {
        return onRotation;
    }

    public static void salvaMap(GoogleMap map2)
    {
        map = map;
    }

    public static void setPosizione(Location pos2)
    {
        pos = pos2;
    }

    public static Location getPosizione()
    {
        return pos;
    }

    public static GoogleMap getMap() {

        return map;
    }

    public static void salvaDizionarioPercorso(Dictionary<Integer ,
            Percorsi.Percorso> dict)
    {
        dictPercorso = dict;
    }

    public static Dictionary<Integer , Percorsi.Percorso> getDictPercorso(){
        return dictPercorso;
    }

    public static String getTestoRicercaPO(int type)
    {
        return textPO[type];
    }

    public static Boolean getSettaggioPO(int type)
    {
        return ricercaPO[type];
    }

    public static String getTitleTab()
    {
        return titleTab[nameTab];
    }

    public static String getTitleFragment()
    {
        return titoli[nameFragment];
    }

    public static int getPositionTab()
    {
        return nameTab;
    }

    public static void salvaNumTab(int number)
    {
        nameTab = number;
    }

    public static void salvaNumberFragment(int position)
    {
        nameFragment = position;
    }

    public static void salvataggio(int titleFragment , int type , Dictionary<Integer, Dictionary<Integer , Integer>> dizionario
            , int mHour , int mMinute , int km , Boolean ricercasettata , String testo
            , String ordinamento)
    {
        superDict[type] = dizionario;
        ore[type] = mHour;
        minuti[type] = mMinute;
        distanza[type] = km;
        setRicerca[type] = ricercasettata;
        testoricerca[type] = testo;
        ordina[type] = ordinamento;
        settaggio[type] = true;
        nameFragment = titleFragment;
    }

    public static void salvataggio(int title , int type, String ricerca)
    {
        nameFragment = title;
        textPO[type] = ricerca;
        ricercaPO[type] = true;
    }

    public static void salvaTabSelected(int position)
    {
        posTab = position;
    }

    public static int getTabSelected()
    {
        return posTab;
    }

    public static String getTabNameSelected(){ return titleTab[posTab]; }

    public static String getTestoRicerca(int type){
        return textPO[type];
    }

    public static Boolean getSetRicerca(int type){
        return ricercaPO[type];
    }

    public static Boolean getSettaggio(int type)
    {
        return settaggio[type];
    }

    public static Boolean getRicerca(int type)
    {
        return setRicerca[type];
    }

    public static int getOre(int type)
    {
        return ore[type];
    }

    public static int getMinuti(int type)
    {
        return minuti[type];
    }

    public static int getDistanza(int type)
    {
        return distanza[type];
    }

    public static Dictionary<Integer, Dictionary<Integer , Integer>> getDictionary(int type) {
        return superDict[type];
    }


    public static String getOrdine(int type)
    {
        return ordina[type];
    }

    public static String getTestoricerca(int type)
    {
        return testoricerca[type];
    }

}
