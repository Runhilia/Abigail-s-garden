package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.SimulateurPotager;

public abstract class Legume {

    protected EtatLegume etatLegume;
    protected double tempsPousse; // Temps de pousse d'un légume en heures réelles
    protected double heureFinPousse;
    protected SimulateurDate heurePlantation; // Heure de plantation du légume
    protected SimulateurDate simDate;

    /**
     * Constructeur de la classe Legume
     */
    public Legume(SimulateurDate _simDate) {
        etatLegume = EtatLegume.graine;
        heurePlantation = _simDate;
        simDate = _simDate;
    }

    public abstract Varietes getVariete();

    public EtatLegume getEtatLegume() {
        return etatLegume;
    }
    public void nextStep() {
        croissance();
    }

    protected abstract void croissance(); // définir selon les conditions
}
