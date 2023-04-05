package modele.environnement.varietes;

import modele.SimulateurDate;

public class Carotte extends Legume {

    public Carotte() {
        super();
        tempsPousse = 0.5;
        System.out.println("Carotte plantée " + heurePlantation.getDateString());
    }
    @Override
    public Varietes getVariete() {
        return Varietes.carotte;
    }

    @Override
    protected void croissance() {
        int heureActuelle = simDate.getTempsMinutes();
        double heureFinPousse =  (heurePlantation.getTempsMinutes() + tempsPousse * 60) % 1440;

        if(heureActuelle == heurePlantation.getTempsMinutes() + (tempsPousse * 60) / 5)
        {
            etatLegume = EtatLegume.pousse;
        }
        else if(heureActuelle == heureFinPousse) {
            etatLegume = EtatLegume.legume;
            System.out.println("La carotte est prête à être ramassée !");
        }
    }
}
