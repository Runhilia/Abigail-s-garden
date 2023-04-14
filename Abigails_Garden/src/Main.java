
import VueControleur.VueControleurPotager;
import modele.Ordonnanceur;
import modele.SimulateurDate;
import modele.SimulateurPotager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class Main {
    public static void main(String[] args) {
        SimulateurDate simulateurDate = new SimulateurDate();
        SimulateurPotager simulateurPotager = new SimulateurPotager(simulateurDate);
        VueControleurPotager vc = new VueControleurPotager(simulateurPotager,simulateurDate);
        vc.setVisible(true);
        Ordonnanceur.getOrdonnanceur().addObserver(vc);
        Ordonnanceur.getOrdonnanceur().start(300);
    }
}
