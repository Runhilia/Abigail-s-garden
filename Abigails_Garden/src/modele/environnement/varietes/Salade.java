package modele.environnement.varietes;

import modele.SimulateurDate;

import java.util.Date;

public class Salade extends Legume {

    public Salade() {
        super();
        tempsPousse = 5;
        System.out.println("Salade plantée " + heurePlantation.getDateString());
    }
    @Override
    public Varietes getVariete() {
        return Varietes.salade;
    }

    @Override
    protected void croissance() {
        int heureActuelle = new SimulateurDate().convertTempsVersMinute();
        int heureFinPousse = heurePlantation.convertTempsVersMinute() + tempsPousse * 60;

        if(heureActuelle == heurePlantation.convertTempsVersMinute() + (tempsPousse * 60) / 5)
        {
            etatLegume = EtatLegume.pousse;
        }
        else if(heureActuelle == heureFinPousse) {
            etatLegume = EtatLegume.legume;
            System.out.println("La salade est prête à être ramassée !");
        }
    }
}
