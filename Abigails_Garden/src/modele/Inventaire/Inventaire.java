package modele.Inventaire;

import modele.environnement.varietes.Varietes;

import java.util.HashMap;

public class Inventaire {
    HashMap<Varietes, Integer> contenu = new HashMap<>();
    private static Inventaire instance = null;

    private Inventaire(){
        for(Varietes v: Varietes.values()){
            contenu.put(v, 0);
        }
    }

    public void addSalade(int nb){
        int nbSalade = contenu.get(Varietes.salade);
        contenu.replace(Varietes.salade, nbSalade, nbSalade+nb);
    }

    public void addCarotte(int nb){
        int nbCarotte = contenu.get(Varietes.carotte);
        contenu.replace(Varietes.carotte, nbCarotte, nbCarotte+nb);
    }

    public static Inventaire getInventaire(){
        if(instance == null)
            instance = new Inventaire();
        return instance;
    }

    public HashMap<Varietes, Integer> getContenu(){
        return this.contenu;
    }


    public String toString(){
        return "Il y a " + contenu.get(Varietes.carotte) + " dans l'inventaire";
    }
}
