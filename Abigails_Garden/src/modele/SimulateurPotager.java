/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;


import modele.environnement.Case;
import modele.environnement.CaseCultivable;

import java.awt.Point;
import java.util.Random;


public class SimulateurPotager {

    public static final int SIZE_X = 10;
    public static final int SIZE_Y = 10;

    private SimulateurMeteo simMet;

    // private HashMap<Case, Point> map = new  HashMap<Case, Point>(); // permet de récupérer la position d'une entité à partir de sa référence
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées
    public SimulateurPotager() {

        initialisationDesEntites();

        simMet = new SimulateurMeteo(this);

    }


    
    public Case[][] getPlateau() {
        return grilleCases;
    }
    
    private void initialisationDesEntites() {

        Random rnd = new Random();

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                CaseCultivable cc = new CaseCultivable(this);
                addEntite(cc , x, y);
                if (rnd.nextBoolean()) {
                    cc.actionUtilisateur();
                }

                Ordonnanceur.getOrdonnanceur().add(cc);

            }
        }

    }

    public void actionUtilisateur(int x, int y) {
        if (grilleCases[x][y] != null) {
            grilleCases[x][y].actionUtilisateur();
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
