package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.environnement.CaseCultivable;

public class Carotte extends Legume {

    public Carotte(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 0.5;
        satisfaction = 150;
        System.out.println("Carotte plantée " + heurePlantation.getDateString());
        heureFinPousse =  (heurePlantation.getTempsMinutes() + tempsPousse * 60) % 1440;

    }

    @Override
    public void setSatisfaction(CaseCultivable caseC) {
        if(satisfaction> 0 && satisfaction<300){
            if(caseC.getHumidite() >= 85 ){
                satisfaction += 1;
            }else if(caseC.getHumidite() <= 60)
                satisfaction -= 1;
//
//            System.out.println(satisfaction);
        }
    }

    @Override
    public Varietes getVariete() {
        return Varietes.carotte;
    }

    @Override
    protected void croissance() {
        int heureActuelle = simDate.getTempsMinutes();

        if(heureActuelle == (1440 + heureFinPousse - ((tempsPousse * 60) / 5) * 3) % 1440)
        {
            etatLegume = EtatLegume.pousse;
        }
        else if(heureFinPousse <= heureActuelle && heureFinPousse > heureActuelle - simDate.getSaut()) {
            etatLegume = EtatLegume.legume;
            System.out.println("La carotte est prête à être ramassée !");
        }
    }
}
