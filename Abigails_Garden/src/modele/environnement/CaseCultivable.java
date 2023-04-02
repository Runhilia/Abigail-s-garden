package modele.environnement;

import modele.Inventaire;
import modele.SimulateurPotager;
import modele.environnement.varietes.*;

public class CaseCultivable extends Case {

    private Legume legume;
    public CaseCultivable(SimulateurPotager _simulateurPotager) {
        super(_simulateurPotager);
    }
    int humide = 50;
    EtatTerre etatTerre = EtatTerre.NORMAL;
    @Override
    public void actionUtilisateur(Action typeAction) {
        Inventaire inventaire = Inventaire.getInventaire();
        switch(typeAction){
            case RECOLTER :
                if(legume != null && legume.getEtatLegume().equals(EtatLegume.legume)) {
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
                        this.setHumideAvTaux(80);
                        break;
                    default:
                        break;

                }
        }

        public Legume getLegume() {
        return legume;
    }

    public int getHumide() {
        return humide;
    }

    public void setHumideAvTaux(int pourcentage) {
        this.humide = pourcentage;
        if(humide > 100) {
            humide = 100;
        }
        this.setEtatTerre();
    }
    public void setHumideAvVal(int val, String operation) {
        if(operation == "ajout" && humide < 100)
            humide += val;
	    else if (operation == "baisse" && humide>0)
            humide -= val;
        this.setEtatTerre();
    }

    public EtatTerre getEtatTerre() {
        return etatTerre;
    }

    public void setEtatTerre() {
        if (humide <= 30) {
            etatTerre = EtatTerre.SEC;
        } else if (humide >= 70) {
            etatTerre = EtatTerre.HUMIDE;
        } else {
            etatTerre = EtatTerre.NORMAL;
        }
    }


    @Override
    public void run() {
        if (legume != null) {
            legume.nextStep();
        }
    }
}
