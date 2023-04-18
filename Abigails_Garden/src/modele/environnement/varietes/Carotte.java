package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.environnement.CaseCultivable;

public class Carotte extends Legume {

    public Carotte(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 0.5;
        satisfaction = 75;
        System.out.println("Carotte plantée " + simDate.getDateString());
        heureFinPousse =  (heurePlantation + tempsPousse * 60) % 1440;

    }

    @Override
    public void setSatisfaction(CaseCultivable caseC) {
        if(satisfaction> 0 && satisfaction<150){
            if(caseC.getHumidite() >= 60){
                satisfaction += 1;
            }else if(caseC.getHumidite() <= 40)
                satisfaction -= 1;
        }
    }

    @Override
    public Varietes getVariete() {
        return Varietes.carotte;
    }

    @Override
    protected void croissance() {
        int heureActuelle = simDate.getTempsMinutes();
        if(satisfaction < 40){
            tempsPousse = 0.65;
        }else if(satisfaction > 110){
            tempsPousse = 0.35;
        }else if (satisfaction > 40 && satisfaction < 110){
            tempsPousse = 0.5;
        }
        heureFinPousse =  (heurePlantation + tempsPousse * 60) % 1440;

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
