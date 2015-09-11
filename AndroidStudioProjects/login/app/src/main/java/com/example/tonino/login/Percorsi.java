package com.example.tonino.login;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by tonino on 21/05/15.
 */
public class Percorsi {


    public static class Operators
    {
        String id;
        String name_operator;
        List<String> type;
        double latitudine;
        double longitudine;
        public Operators()
        {
            type = new ArrayList<String>();
            latitudine = 0;
            longitudine = 0;
            id = "";
        }
    }


    public static class Objective{
        int id;
        int validation_method;
        String name;
        String city;
        String description;
        String type;
        Double latitudine;
        Double longitudine;

        public Objective()
        {
            name = "";
            city = "";
            description = "";
            type = "";
            latitudine = 0.0;
            longitudine = 0.0;
        }

    }

    public static class PremiPercorso {
        int id;
        List<Integer> obiettivi_obbligatori = new ArrayList<Integer>();
        public PremiPercorso()
        {
            id = -1;
            obiettivi_obbligatori = new ArrayList<Integer>();
        }
    }

    public static class Percorso {
        int id;
        String title;
        String description;
        List<String> type;
        List<Integer> obiettivi;
        List<PremiPercorso> premi;
        Dictionary<Integer , List<Integer>> mandatory_ob;
        public Percorso()
        {
            obiettivi = new ArrayList<Integer>();
            premi = new ArrayList<PremiPercorso>();
            mandatory_ob = new Hashtable<Integer , List<Integer>>();
            type = new ArrayList<String>();
            title = "";
        }
    }

    public static class PremioS{
        int id;
        String name;
        String id_operator;
        String description;
        Double position_longitude;
        Double position_latitude;
        int visible;
        int percentage;
        //List<Integer> mandatory_ob;
        //Dictionary<String , >

        public PremioS()
        {
            //mandatory_ob = new ArrayList<Integer>();
            name = "";
            id_operator = "";
            description = "";
            position_latitude = 0.0;
            position_longitude = 0.0;
            visible = 1;
            percentage = 100;
        }
    }

}
