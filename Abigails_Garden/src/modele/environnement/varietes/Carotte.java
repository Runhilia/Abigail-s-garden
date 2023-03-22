package modele.environnement.varietes;

import modele.SimulateurDate;

public class Carotte extends Legume {

    public Carotte() {
        super();
        tempsPousse = 1;
        System.out.println("Carotte plantée " + heurePlantation.getDateString());
    }
    @Override
    public Varietes getVariete() {
        return Varietes.carotte;
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
            System.out.println("La carotte est prête à être ramassée !");
        }
    }
}