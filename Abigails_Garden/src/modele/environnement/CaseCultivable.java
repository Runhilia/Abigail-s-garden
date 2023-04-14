package modele.environnement;

import modele.Inventaire.Inventaire;
import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.varietes.*;

public class CaseCultivable extends Case {

    private Legume legume;
    int humidite = 50;
    private EtatTerre etatTerre = EtatTerre.NORMAL;
    private Inventaire inventaire;

    public CaseCultivable(SimulateurPotager _simulateurPotager,SimulateurDate _simulateurDate) {
        super(_simulateurPotager,_simulateurDate);
    }

    @Override
    public void actionUtilisateur(Action typeAction) {
         inventaire = Inventaire.getInventaire();
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
                                case "Carotte" -> legume = new Carotte(simulateurDate);
                                case "Salade" -> legume = new Salade(simulateurDate);
                            }
                        }
                        break;
                    case ARROSER:
                        this.setHumiditeAvTaux(80);
                        break;
                    default:
                        break;

                }
        }

        public Legume getLegume() {
        return legume;
    }

    public int getHumidite() {
        return humidite;
    }

    public void setHumiditeAvTaux(int pourcentage) {
        this.humidite = pourcentage;
        if(humidite > 100) {
            humidite = 100;
        }
        this.setEtatTerre();
    }
    public void setHumiditeAvVal(int val, String operation) {
        if(operation.equals("ajout") && humidite < 100)
            humidite += val;
	    else if (operation.equals("baisse") && humidite >0)
            humidite -= val;
        this.setEtatTerre();
    }

    public EtatTerre getEtatTerre() {
        return etatTerre;
    }

    public void setEtatTerre() {
        if (humidite <= 20) {
            etatTerre = EtatTerre.SEC;
        } else if (humidite >= 70) {
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
