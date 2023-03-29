package modele.environnement;

import modele.Inventaire;
import modele.SimulateurPotager;
import modele.environnement.varietes.*;

public class CaseCultivable extends Case {

    private Legume legume;
    public CaseCultivable(SimulateurPotager _simulateurPotager) {
        super(_simulateurPotager);
    }

    @Override
    public void actionUtilisateur(Action typeAction) {
        Inventaire inventaire = Inventaire.getInventaire();
        switch(typeAction){
            case RECOLTER :
                if(legume != null) {
                    switch (legume.getVariete()) {
                        case carotte -> inventaire.addCarotte(1);
                        case salade -> inventaire.addSalade(1);
                    }
                    System.out.println(inventaire.toString());
                    legume = null;

                }
                    break;
                    case PLANTER:
                        if (legume == null)
                        {
                            switch (simulateurPotager.getLegumeSelectionne()) {
                                case "Carotte" -> legume = new Carotte();
                                case "Salade" -> legume = new Salade();
                            }
                        }
                        break;
                    case ARROSER:
                        break;
                    default:
                        break;

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
