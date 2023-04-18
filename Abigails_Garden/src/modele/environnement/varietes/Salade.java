package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.environnement.CaseCultivable;

public class Salade extends Legume {

    public Salade(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 2;
        satisfaction = 150;
        System.out.println("Salade plantée " + simDate.getDateString());
        heureFinPousse = (heurePlantation + tempsPousse * 60) % 1440;
    }

    @Override
    public void setSatisfaction(CaseCultivable caseC) {
        if(satisfaction> 0 && satisfaction<300){
            if(caseC.getHumidite() >= 80 ){
                satisfaction += 1;
            }else if(caseC.getHumidite() <= 65)
                satisfaction -= 1;

//            System.out.println(satisfaction);
        }
    }

    @Override
    public Varietes getVariete() {
        return Varietes.salade;
    }

    @Override
    protected void croissance() {
        int heureActuelle = simDate.getTempsMinutes();
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
