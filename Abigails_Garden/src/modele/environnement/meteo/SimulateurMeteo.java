package modele.environnement.meteo;

import modele.Ordonnanceur;
import modele.SimulateurDate;
import modele.SimulateurPotager;

import java.util.Random;

public class SimulateurMeteo implements Runnable {
    private SimulateurPotager simPot;

    private EnumMomentJournee momentJourneeActuel = EnumMomentJournee.JOURNEE;
    private EnumMeteo etatMeteoActuel = EnumMeteo.SOLEIL;
    private int temperature = 20;

    public SimulateurMeteo(SimulateurPotager _simPot) {
        Ordonnanceur.getOrdonnanceur().add(this);
        simPot = _simPot;
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
            int n = rand.nextInt(6);
            etatMeteo = switch (n) { // 50% de chance de rester dans l'état actuel
                case 0 -> EnumMeteo.values()[0];
                case 1 -> EnumMeteo.values()[1];
                case 2 -> EnumMeteo.values()[2];
                case 3 -> EnumMeteo.values()[3];
                default -> etatMeteoActuel;
            };
        }
        else{
            etatMeteo = etatMeteoActuel;
        }
        etatMeteoActuel = etatMeteo;
        return etatMeteoActuel;
    }

    public int getTemperature(){
        return temperature;
    }

    public int getCalcTemperature(){
        EnumMeteo etatMeteo = getCalcEtatMeteo();
        EnumMomentJournee momentJournee = getCalcMomentJournee();
        SimulateurDate simDate = new SimulateurDate();
        switch (etatMeteo){
            case SOLEIL:
                temperature++;
                break;
            case NUAGE:
                break;
            case PLUIE:
                temperature--;
                break;
            case ECLAIRCIES:
                temperature++;
                break;
        }
        return temperature;
    }

    @Override
    public void run() {

    }
}
