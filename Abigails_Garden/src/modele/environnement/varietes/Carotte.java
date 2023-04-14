package modele.environnement.varietes;

import modele.SimulateurDate;

public class Carotte extends Legume {

    public Carotte(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 0.5;
        System.out.println("Carotte plantée " + heurePlantation.getDateString());
        heureFinPousse =  (heurePlantation.getTempsMinutes() + tempsPousse * 60) % 1440;
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
