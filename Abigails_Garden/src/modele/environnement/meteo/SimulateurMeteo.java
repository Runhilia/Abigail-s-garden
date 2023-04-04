package modele.environnement.meteo;

import modele.Ordonnanceur;
import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.CaseCultivable;

import java.util.Random;

public class SimulateurMeteo implements Runnable {
    private SimulateurPotager simPot;

    private EnumMomentJournee momentJourneeActuel = EnumMomentJournee.JOURNEE;
    private EnumMeteo etatMeteoActuel = EnumMeteo.SOLEIL;
    private int temperature;

    public SimulateurMeteo(SimulateurPotager _simPot) {
        Ordonnanceur.getOrdonnanceur().add(this);
        simPot = _simPot;
        setMeteoInitiale();
    }

    public EnumMomentJournee getMomentJournee() {
        return momentJourneeActuel;
    }

    public EnumMomentJournee getCalcMomentJournee(){
        SimulateurDate simDate = new SimulateurDate();
        if(simDate.getHeure() >= 6 && simDate.getHeure() < 9){
            momentJourneeActuel = EnumMomentJournee.MATIN;
        }
        else if(simDate.getHeure() >= 18 && simDate.getHeure() < 21){
            momentJourneeActuel = EnumMomentJournee.SOIR;
        }
        else if(simDate.getHeure() >= 9 && simDate.getHeure() < 18){
            momentJourneeActuel = EnumMomentJournee.JOURNEE;
        }
        else{
            momentJourneeActuel = EnumMomentJournee.NUIT;
        }
        return momentJourneeActuel;
    }

    public EnumMeteo getEtatMeteo() {
        return etatMeteoActuel;
    }

    public EnumMeteo getCalcEtatMeteo() {
        EnumMeteo etatMeteo;
        SimulateurDate simDate = new SimulateurDate();

        // On change l'état de la météo toutes les heures
        if(simDate.getMinute() == 0){
            Random rand = new Random();
            if(getMomentJournee() == EnumMomentJournee.NUIT){
                int n = rand.nextInt(2);
                etatMeteo = switch (n) { // 50% de chance de rester dans l'état actuel
                    case 0 -> EnumMeteo.PLUIE;
                    case 1 -> EnumMeteo.NUAGE;
                    default -> etatMeteoActuel;
                };
            }
            else{
                int n2 = rand.nextInt(6);
                etatMeteo = switch (n2) { // 50% de chance de rester dans l'état actuel
                    case 0 -> EnumMeteo.SOLEIL;
                    case 1 -> EnumMeteo.ECLAIRCIES;
                    case 2 -> EnumMeteo.PLUIE;
                    case 3 -> EnumMeteo.NUAGE;
                    default -> etatMeteoActuel;
                };
            }

            // Modification de l'humidité des cases cultivables
            switch (etatMeteo) {
                case SOLEIL -> {
                    for (int i = 0; i < simPot.SIZE_X; i++) {
                        for (int j = 0; j < simPot.SIZE_Y; j++) {
                            if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(5, "baisse");
                            }
                        }
                    }
                }
                case ECLAIRCIES -> {
                    for (int i = 0; i < simPot.SIZE_X; i++) {
                        for (int j = 0; j < simPot.SIZE_Y; j++) {
                            if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(2, "baisse");
                            }
                        }
                    }
                }
                case PLUIE -> {
                    for (int i = 0; i < simPot.SIZE_X; i++) {
                        for (int j = 0; j < simPot.SIZE_Y; j++) {
                            if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(5, "ajout");
                            }
                        }
                    }
                }
                case NUAGE -> {
                    for (int i = 0; i < simPot.SIZE_X; i++) {
                        for (int j = 0; j < simPot.SIZE_Y; j++) {
                            if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(1, "ajout");
                            }
                        }
                    }
                }
            }

        }
        else{
            etatMeteo = etatMeteoActuel;
        }
        etatMeteoActuel = etatMeteo;

        return etatMeteoActuel;
    }

    public int getCalcTemperature(){
        EnumMeteo etatMeteo = getEtatMeteo();
        EnumMomentJournee momentJournee = getMomentJournee();
        SimulateurDate simDate = new SimulateurDate();
        Random rand = new Random();

        // On change la température toutes les heures
        if(simDate.getMinute() == 0){
            switch (momentJournee) {
                case MATIN -> {
                    int n = rand.nextInt(2); // 50% de chance de changer la température
                    if (n == 0) {
                        temperature++;
                    }
                }
                case SOIR -> {
                    int n2 = rand.nextInt(2); // 50% de chance de changer la température
                    if (n2 == 0) {
                        temperature--;
                    }
                }
                case NUIT -> {
                    switch (etatMeteo) {
                        case PLUIE -> temperature -= 2;
                        case NUAGE -> temperature--;
                    }
                }
                case JOURNEE -> {
                    switch (etatMeteo) {
                        case SOLEIL -> temperature += 2;
                        case ECLAIRCIES -> {
                            if(simDate.getHeure() % 2 == 0) temperature += 2;
                            else temperature++;
                        }
                        case PLUIE, NUAGE -> temperature++;
                    }
                }
            }
        }
        return temperature;
    }

    public void setMeteoInitiale(){
        switch (getCalcMomentJournee()) {
            case MATIN -> {
                temperature = 8;
                etatMeteoActuel = EnumMeteo.ECLAIRCIES;
            }
            case SOIR -> {
                temperature = 12;
                etatMeteoActuel = EnumMeteo.PLUIE;
            }
            case NUIT -> {
                temperature = 5;
                etatMeteoActuel = EnumMeteo.NUAGE;
            }
            case JOURNEE -> {
                temperature = 15;
                etatMeteoActuel = EnumMeteo.SOLEIL;
            }
        }
    }

    @Override
    public void run() {

    }
}
