
import VueControleur.VueControleurPotager;
import modele.Ordonnanceur;
import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.meteo.SimulateurMeteo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class Main {
    public static void main(String[] args) {
        SimulateurDate simulateurDate = new SimulateurDate();
        SimulateurMeteo simulateurMeteo = new SimulateurMeteo(simulateurDate);
        SimulateurPotager simulateurPotager = new SimulateurPotager(simulateurDate,simulateurMeteo);

        VueControleurPotager vc = new VueControleurPotager(simulateurPotager,simulateurDate,simulateurMeteo);
        vc.setVisible(true);
        Ordonnanceur.getOrdonnanceur().addObserver(vc);
        Ordonnanceur.getOrdonnanceur().start(300);
    }
}
