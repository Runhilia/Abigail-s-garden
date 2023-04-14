package modele.environnement.varietes;

import modele.SimulateurDate;

public class Salade extends Legume {

    public Salade(SimulateurDate _simDate) {
        super(_simDate);
        tempsPousse = 2;
        System.out.println("Salade plantée " + heurePlantation.getDateString());
        heureFinPousse = (heurePlantation.getTempsMinutes() + tempsPousse * 60) % 1440;
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
