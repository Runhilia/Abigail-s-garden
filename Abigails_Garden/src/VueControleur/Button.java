package VueControleur;

import javax.swing.*;

public class Button extends JButton {
    private boolean actif;

    public Button() {
        super();
        actif = false;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
