package modele.environnement;

import modele.SimulateurPotager;
import modele.environnement.varietes.*;

public class CaseCultivable extends Case {

    private Legume legume;
    public CaseCultivable(SimulateurPotager _simulateurPotager) {
        super(_simulateurPotager);
    }

    @Override
    public void actionUtilisateur() {
        if (legume == null) {
            legume = new Carotte();

        } else {
            legume = null;
        }
    }

    public Legume getLegume() {
        return legume;
    }

    @Override
    public void run() {
        if (legume != null) {
            legume.nextStep();
        }
    }
}