/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;


import modele.environnement.Action;
import modele.environnement.*;
import modele.environnement.meteo.SimulateurMeteo;

import java.awt.Point;


public class SimulateurPotager {

    public static final int SIZE_X = 10;
    public static final int SIZE_Y = 10;
    private String legumeSelectionne = "Carotte";
    private SimulateurMeteo simMet;
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées

    public SimulateurPotager() {

        initialisationDesEntites();

        simMet = new SimulateurMeteo(this);
    }

    public void setLegumeSelectionne(String legumeSelectionne) {
        this.legumeSelectionne = legumeSelectionne;
    }

    public String getLegumeSelectionne() {
        return legumeSelectionne;
    }
    
    public Case[][] getPlateau() {
        return grilleCases;
    }
    
    private void initialisationDesEntites() {

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                CaseCultivable cc = new CaseCultivable(this);
                addEntite(cc , x, y);

                Ordonnanceur.getOrdonnanceur().add(cc);
            }
        }

    }

    public void actionUtilisateur(int x, int y, Action typeAction) {
        if (grilleCases[x][y] != null) {
            grilleCases[x][y].actionUtilisateur(typeAction);
        }
    }

    private void addEntite(Case e, int x, int y) {
        grilleCases[x][y] = e;
        //map.put(e, new Point(x, y));
    }


    private Case objetALaPosition(Point p) {
        Case retour = null;
        return grilleCases[p.x][p.y];
    }

}
