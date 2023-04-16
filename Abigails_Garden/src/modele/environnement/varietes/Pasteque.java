package modele.environnement.varietes;

import modele.SimulateurDate;
import modele.environnement.CaseCultivable;

public class Pasteque extends Legume{

    public Pasteque(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 5;
        satisfaction = 150;
        prixVente = 10;
        System.out.println("Pastèque plantée " + heurePlantation.getDateString());
        heureFinPousse = (heurePlantation.getTempsMinutes() + tempsPousse * 60) % 1440;
    }

    @Override
    public void setSatisfaction(CaseCultivable caseC) {
        if(satisfaction> 0 && satisfaction<300){
            if(caseC.getHumidite() >= 80 ){
                satisfaction += 1;
            }else if(caseC.getHumidite() <= 65)
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
        double heurePousse = (1440 + heureFinPousse - ((tempsPousse * 60) / 5) * 3) % 1440;

        if(heurePousse <= heureActuelle && heurePousse > heureActuelle - simDate.getSaut())
        {
            etatLegume = EtatLegume.pousse;
        }
        else if(heureFinPousse <= heureActuelle && heureFinPousse > heureActuelle - simDate.getSaut()) {
            etatLegume = EtatLegume.legume;
            System.out.println("La pastèque est prête à être ramassée !");
        }
    }
}
