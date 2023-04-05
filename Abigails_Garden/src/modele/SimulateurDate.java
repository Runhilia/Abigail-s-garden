package modele;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulateurDate {

    private int heure,minute;
    private int saut = 1;
    private boolean heureModifiee = false;

    /**
     * Constructeur de la classe Date
     */

    public SimulateurDate()
    {
        LocalDateTime heureJourActuels = LocalDateTime.now(); // Récupère l'heure actuelle et le jour actuel
        this.minute = heureJourActuels.getSecond(); // Une minute ici correspond à une seconde en vrai
        this.heure = heureJourActuels.getMinute()%24; // Une heure ici correspond à une minute en vrai

        Runnable affichageDate = this::changerHeure;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(affichageDate,0,1, TimeUnit.SECONDS); // On répète la méthode toutes les secondes
    }

    public void changerHeure(){
        if(minute+saut < 60){
            minute += saut;
        }
        else{
            heureModifiee = true;
            minute = minute + saut - 60;
            if(heure != 23) {
                heure++;
            } else {
                heure = 0;
            }

        }
    }

    public void modifierVitesse(boolean accelere){
        if(accelere){
            saut = 5;
        } else{
            saut = 1;
        }
    }

    /**
     * @return le jour de la semaine et l'heure actuelle sous un format String
     */
    public String getDateString(){
        String date;
        if (minute < 10 && heure < 10)
        {
            date = " 0" + heure + ":0" + minute;
        }
        else if (minute < 10) {
            date = " " + heure + ":0" + minute;
        }
        else if (heure < 10) {
            date = " 0" + heure + ":" + minute;
        }
        else {
            date = " " + heure + ":" + minute;
        }
        return date;
    }

    /**
     * @return L'heure actuelle
     */
    public int getHeure() {
        return heure;
    }

    public boolean getHeureModifiee(){
        return heureModifiee;
    }

    public void setHeureModifiee(boolean b){
        heureModifiee = b;
    }

    /**
     * Convertit la date en minute
     * @return int
     */
    public int getTempsMinutes(){
        return heure*60+minute;
    }
}
