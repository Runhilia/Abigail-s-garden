package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.CaseCultivable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Legume {

    protected EtatLegume etatLegume;
    protected double tempsPousse; // Temps de pousse d'un légume en heures réelles

    protected double heureFinPousse;
    protected double heurePlantation; // Heure de plantation du légume
    protected SimulateurDate simDate;


    protected int satisfaction;

    /**
     * Constructeur de la classe Legume
     */
    public Legume(SimulateurDate _simDate) {
        etatLegume = EtatLegume.graine;
        simDate = _simDate;
        heurePlantation = simDate.getTempsMinutes();
    }

    public abstract void setSatisfaction(CaseCultivable caseC);

    public abstract Varietes getVariete();

    public EtatLegume getEtatLegume() {
        return etatLegume;
    }
    public void nextStep(CaseCultivable caseC) {
        croissance();
        setSatisfaction(caseC);
    }

    protected abstract void croissance(); // définir selon les conditions

    public String getHeureFinPousse() {
        int heure = (int) (heureFinPousse/60);
        int minute = (int) (heureFinPousse%60);
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
}
