package modele.Instance;

import modele.environnement.varietes.Varietes;

import java.util.HashMap;

public class Inventaire {
    private HashMap<Varietes, Integer> contenu = new HashMap<>();
    private static Inventaire instance = null;
    private int argent;

    private Inventaire(){
        for(Varietes v: Varietes.values()){
            contenu.put(v, 0);
        }
        argent =0;
    }

    public void addSalade(int nb){
        int nbSalade = contenu.get(Varietes.salade);
        contenu.replace(Varietes.salade, nbSalade, nbSalade+nb);
    }

    public void addCarotte(int nb){
        int nbCarotte = contenu.get(Varietes.carotte);
        contenu.replace(Varietes.carotte, nbCarotte, nbCarotte+nb);
    }

    public void addPasteque(int nb){
        int nbPasteque = contenu.get(Varietes.pasteque);
        contenu.replace(Varietes.pasteque, nbPasteque, nbPasteque+nb);
    }

    public void removeSalade(int nb){
        int nbSalade = contenu.get(Varietes.salade);
        contenu.replace(Varietes.salade, nbSalade, nbSalade-nb);
    }

    public void removeCarotte(int nb){
        int nbCarotte = contenu.get(Varietes.carotte);
        contenu.replace(Varietes.carotte, nbCarotte, nbCarotte-nb);
    }

    public void removePasteque(int nb){
        int nbPasteque = contenu.get(Varietes.pasteque);
        contenu.replace(Varietes.pasteque, nbPasteque, nbPasteque-nb);
    }

    public static Inventaire getInventaire(){
        if(instance == null)
            instance = new Inventaire();
        return instance;
    }

    public HashMap<Varietes, Integer> getContenu(){
        return this.contenu;
    }


    public int getArgent() {
        return argent;
    }

    public void setArgent(int argent) {
        this.argent += argent;
    }


}
