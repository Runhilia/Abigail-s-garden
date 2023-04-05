package modele.environnement.varietes;

import modele.SimulateurDate;

public class Salade extends Legume {

    public Salade() {
        super();
        tempsPousse = 1;
        System.out.println("Salade plantée " + heurePlantation.getDateString());
    }
    @Override
    public Varietes getVariete() {
        return Varietes.salade;
    }

    @Override
    protected void croissance() {
        int heureActuelle = simDate.getTempsMinutes();
        double heureFinPousse = (heurePlantation.getTempsMinutes() + tempsPousse * 60) % 1440;

        if(heureActuelle == heurePlantation.getTempsMinutes() + (tempsPousse * 60) / 5)
        {
            etatLegume = EtatLegume.pousse;
        }
        else if(heureActuelle == heureFinPousse) {
            etatLegume = EtatLegume.legume;
            System.out.println("La salade est prête à être ramassée !");
        }
    }
}
