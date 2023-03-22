package modele;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

public class SimulateurDate {

    private int heure,minute;

    private String jour;

    // Jour de la semaine
    DateFormatSymbols dfs = new DateFormatSymbols(Locale.FRENCH);
    String[] joursSemaine = dfs.getWeekdays();

    /**
     * Constructeur de la classe Date
     */

    public SimulateurDate()
    {
        LocalDateTime heureJourActuels = LocalDateTime.now(); // Récupère l'heure actuelle et le jour actuel
        this.minute = heureJourActuels.getSecond(); // Une minute ici correspond à une second en vrai
        this.heure = heureJourActuels.getMinute()%24; // Une heure ici correspond à une minute en vrai
        this.jour = joursSemaine[(heureJourActuels.getDayOfWeek().getValue()+1)%7]; // Récupère le jour de la semaine
    }

    /**
     * @return le jour de la semaine et l'heure actuelle sous un format String
     */
    public String getDateString(){
        String date;
        if (minute < 10 && heure < 10)
        {
            date = jour + " 0" + heure + ":0" + minute;
        }
        else if (minute < 10) {
            date = jour + " " + heure + ":0" + minute;
        }
        else if (heure < 10) {
            date = jour + " 0" + heure + ":" + minute;
        }
        else {
            date = jour + " " + heure + ":" + minute;
        }
        return date;
    }

    /**
     * @return L'heure actuelle
     */
    public int getHeure() {
        return heure;
    }

    /**
     * Convertit la date en minute
     * @return int
     */
    public int convertTempsVersMinute(){
        return heure*60+minute;
    }
}
