package modele.environnement;

import modele.Instance.Inventaire;
import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.meteo.SimulateurMeteo;
import modele.environnement.varietes.*;

public class CaseCultivable extends Case {

    private Legume legume;
    int humidite = 50;
    private EtatTerre etatTerre = EtatTerre.NORMAL;
    private Inventaire inventaire;

    public CaseCultivable(SimulateurPotager _simulateurPotager, SimulateurDate _simulateurDate, SimulateurMeteo _simulateurMeteo) {
        super(_simulateurPotager,_simulateurDate,_simulateurMeteo);
    }

    @Override
    public void actionUtilisateur(Action typeAction) {
         inventaire = Inventaire.getInventaire();
        switch (typeAction) {
            case RECOLTER -> {
                if (legume != null && legume.getEtatLegume().equals(EtatLegume.legume)) {
                    switch (legume.getVariete()) {
                        case carotte -> inventaire.addCarotte(1);
                        case salade -> inventaire.addSalade(1);
                        case pasteque -> inventaire.addPasteque(1);
                    }
                    legume = null;

                }
            }
            case PLANTER -> {
                if (legume == null) {
                    switch (simulateurPotager.getLegumeSelectionne()) {
                        case "Carotte" -> legume = new Carotte(simulateurDate);
                        case "Salade" -> legume = new Salade(simulateurDate);
                        case "Pasteque" -> legume = new Pasteque(simulateurDate);
                    }
                }
            }
            case ARROSER -> this.setHumiditeAvVal(15, "ajout");
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
        if(operation.equals("ajout"))
            humidite += val;
	    else if (operation.equals("baisse") )
            humidite -= val;
        if(humidite > 100)
            humidite =100;
        else if(humidite < 0)
            humidite =0;
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
            legume.nextStep(this);
        }
    }
}
