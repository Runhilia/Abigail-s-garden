package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.environnement.CaseCultivable;

public class Pasteque extends Legume{

    public Pasteque(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 5;
        satisfaction = 75;
        System.out.println("Pastèque plantée " + simDate.getDateString());
        heureFinPousse = (heurePlantation + tempsPousse * 60) % 1440;
    }

    @Override
    public void setSatisfaction(CaseCultivable caseC) {
        if(satisfaction> 0 && satisfaction<150){
            if(caseC.getHumidite() >= 85 ){
                satisfaction += 1;
            }else if(caseC.getHumidite() <= 60)
                satisfaction -= 1;
        }
    }

    @Override
    public Varietes getVariete() {
        return Varietes.pasteque;
    }

    @Override
    protected void croissance() {
        int heureActuelle = simDate.getTempsMinutes();
        if(satisfaction < 40 && satisfaction != 0){
            tempsPousse = 5.15;
        }else if(satisfaction > 120){
            tempsPousse = 4.85;
        }else if (satisfaction > 40 && satisfaction < 120 && tempsPousse != 0){
            tempsPousse = 5;
        }else if(satisfaction ==0){
            etatLegume = EtatLegume.mort;
        }
        heureFinPousse =  (heurePlantation + tempsPousse * 60) % 1440;

        double heurePousse = (1440 + heureFinPousse - ((tempsPousse * 60) / 5) * 3) % 1440;

        if((heurePousse <= heureActuelle && heurePousse > heureActuelle - simDate.getSaut()) && etatLegume!=EtatLegume.mort)
        {
            etatLegume = EtatLegume.pousse;
        }
        else if(heureFinPousse <= heureActuelle && heureFinPousse > heureActuelle - simDate.getSaut() && etatLegume!=EtatLegume.mort) {
            etatLegume = EtatLegume.legume;
            System.out.println("La pastèque est prête à être ramassée !");
        }
    }
}
