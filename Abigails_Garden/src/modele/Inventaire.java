package modele;

public class Inventaire {
    private int nbSalade;
    private int nbCarotte;
    private static Inventaire instance = null;

    private Inventaire(){
        nbCarotte = 0;
        nbSalade =0;
    }

    public int getNbSalade(){
        return this.nbSalade;
    }

    public int getNbCarotte(){
        return this.nbCarotte;
    }

    public void addSalade(int nb){
        this.nbSalade+= nb;
    }

    public void addCarotte(int nb){
        this.nbCarotte+= nb;
    }

    public static Inventaire getInventaire(){
        if(instance == null)
            instance = new Inventaire();
        return instance;
    }

    public String toString(){
        return "Il y a " + this.nbCarotte + " dans l'inventaire";
    }
}
