package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.environnement.CaseCultivable;

public class Salade extends Legume {

    public Salade(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 2;
        satisfaction = 75;
        System.out.println("Salade plantée " + simDate.getDateString());
        heureFinPousse = (heurePlantation + tempsPousse * 60) % 1440;
    }

    @Override
    public void setSatisfaction(CaseCultivable caseC) {
        if(satisfaction> 0 && satisfaction<150){
            if(caseC.getHumidite() >= 70 ){
                satisfaction += 1;
            }else if(caseC.getHumidite() <= 50)
                satisfaction -= 1;
        }
    }

    @Override
    public Varietes getVariete() {
        return Varietes.salade;
    }

    @Override
    protected void croissance() {
        int heureActuelle = simDate.getTempsMinutes();
        if(satisfaction < 30){
            tempsPousse = 2.15;
        }else if(satisfaction > 100){
            tempsPousse = 1.85;
        }else if (satisfaction > 30 && satisfaction < 100){
            tempsPousse = 2;
        }
        heureFinPousse =  (heurePlantation + tempsPousse * 60) % 1440;

        double heurePousse = (1440 + heureFinPousse - ((tempsPousse * 60) / 5) * 3) % 1440;

        if(heurePousse <= heureActuelle && heurePousse > heureActuelle - simDate.getSaut())
        {
            etatLegume = EtatLegume.pousse;
        }
        else if(heureFinPousse <= heureActuelle && heureFinPousse > heureActuelle - simDate.getSaut()) {
            etatLegume = EtatLegume.legume;
            System.out.println("La salade est prête à être ramassée !");
        }
    }
}
