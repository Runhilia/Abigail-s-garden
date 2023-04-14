package modele.environnement.meteo;

import modele.Ordonnanceur;
import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.CaseCultivable;

import java.util.Random;

public class SimulateurMeteo implements Runnable {
    private final SimulateurPotager simPot;
    private final SimulateurDate simDate;

    private EnumMomentJournee momentJourneeActuel = EnumMomentJournee.JOURNEE;
    private EnumMeteo etatMeteoActuel = EnumMeteo.SOLEIL;
    private int temperature;
    private boolean heurePassee = false;
    private boolean heurePassee2 = false;

    public SimulateurMeteo(SimulateurPotager _simPot,SimulateurDate _simDate) {
        Ordonnanceur.getOrdonnanceur().add(this);
        simPot = _simPot;
        simDate = _simDate;
        setMeteoInitiale();
    }

    public EnumMomentJournee getMomentJournee() {
        return momentJourneeActuel;
    }

    public EnumMomentJournee getCalcMomentJournee(){
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
        EnumMeteo etatMeteoTemp;

        // On change l'état de la météo toutes les heures
        if(simDate.getMinute() - simDate.getSaut() < 0){
            if(!heurePassee){
                heurePassee = true;
                Random rand = new Random();
                if(getMomentJournee() == EnumMomentJournee.NUIT){
                    int n = rand.nextInt(2);
                    etatMeteoTemp = switch (n) { // 50% de chance de rester dans l'état actuel
                        case 0 -> EnumMeteo.PLUIE;
                        case 1 -> EnumMeteo.NUAGE;
                        default -> etatMeteoActuel;
                    };
                }
                else{
                    int n2 = rand.nextInt(6);
                    etatMeteoTemp = switch (n2) { // 50% de chance de rester dans l'état actuel
                        case 0 -> EnumMeteo.SOLEIL;
                        case 1 -> EnumMeteo.ECLAIRCIES;
                        case 2 -> EnumMeteo.PLUIE;
                        case 3 -> EnumMeteo.NUAGE;
                        default -> etatMeteoActuel;
                    };
                }

                // Modification de l'humidité des cases cultivables
                switch (etatMeteoTemp) {
                    case SOLEIL -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(5, "baisse");
                                }
                            }
                        }
                    }
                    case ECLAIRCIES -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(2, "baisse");
                                }
                            }
                        }
                    }
                    case PLUIE -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(5, "ajout");
                                }
                            }
                        }
                    }
                    case NUAGE -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simPot.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simPot.getPlateau()[i][j]).setHumiditeAvVal(1, "ajout");
                                }
                            }
                        }
                    }
                }
            }
            else{
                etatMeteoTemp = etatMeteoActuel;
            }
        }
        else{
            etatMeteoTemp = etatMeteoActuel;
            heurePassee = false;
        }
        etatMeteoActuel = etatMeteoTemp;

        return etatMeteoActuel;
    }

    public int getCalcTemperature(){
        EnumMeteo etatMeteo = getEtatMeteo();
        EnumMomentJournee momentJournee = getMomentJournee();
        Random rand = new Random();

        // On change la température toutes les heures
        if(simDate.getMinute() - simDate.getSaut() < 0){
            if(!heurePassee2){
                heurePassee2 = true;
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
        }
        else{
            heurePassee2 = false;
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
