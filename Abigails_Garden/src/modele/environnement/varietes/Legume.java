package modele.environnement.varietes;

import modele.SimulateurDate;

public abstract class Legume {

    protected EtatLegume etatLegume;
    protected int tempsPousse; // Temps de pousse d'un légume en heures réelles
    protected SimulateurDate heurePlantation; // Heure de plantation du légume

    /**
     * Constructeur de la classe Legume
     */
    public Legume() {
        etatLegume = EtatLegume.graine;
        heurePlantation = new SimulateurDate();
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
