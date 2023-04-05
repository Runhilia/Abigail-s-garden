package VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

import modele.Inventaire.Inventaire;
import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.*;
import modele.environnement.Action;
import modele.environnement.meteo.SimulateurMeteo;
import modele.environnement.varietes.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle
 *
 */
public class VueControleurPotager extends JFrame implements Observer {
    private SimulateurPotager simulateurPotager; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private SimulateurMeteo simulateurMeteo;
    private SimulateurDate simulateurDate;

    // taille de la grille affichée
    private int sizeX;
    private int sizeY;

    // icones affichées dans la grille
    private ImageIcon icoTerre;
    private ImageIcon icoVide;
    private ImageIcon icoMur;
    private ImageIcon icoGraine;
    private ImageIcon icoPousse;
    private ImageIcon icoSalade;
    private ImageIcon icoCarotte;


    private HashMap<ImageIcon,String> mapLegumeIcone = new HashMap<ImageIcon,String>(); // permet de récupérer le nom du légume grâce à l'icône

    // Composants graphiques
    private JLabel[][] tabJLabel; // cases graphiques (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)
    private JLabel[][] labelInventaire;
    private Button arrosoir;
    private Button outil;
    private Button infoPlante;
    private Button pauseTemps;
    private Button accTemps;
    private JLabel momentJournee = new JLabel();
    private JLabel meteo = new JLabel();
    private JLabel affichageDate = new JLabel();
    private JLabel humide = new JLabel();
    private JLabel temperature = new JLabel();
    private JPanel infoCase= new JPanel();
    private JTextField warning = new JTextField();

    private JPanel plante = new JPanel();

    private Inventaire inventaire;



    public VueControleurPotager(SimulateurPotager _simulateurPotager) {
        sizeX = _simulateurPotager.SIZE_X;
        sizeY = _simulateurPotager.SIZE_Y;
        simulateurPotager = _simulateurPotager;
        simulateurMeteo = new SimulateurMeteo(simulateurPotager);
        simulateurDate = new SimulateurDate();

        inventaire = Inventaire.getInventaire();


        chargerLesIcones();
        placerLesComposantsGraphiques();
        //ajouterEcouteurClavier(); // si besoin
    }
/*
    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT : Controle4Directions.getInstance().setDirectionCourante(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : Controle4Directions.getInstance().setDirectionCourante(Direction.droite); break;
                    case KeyEvent.VK_DOWN : Controle4Directions.getInstance().setDirectionCourante(Direction.bas); break;
                    case KeyEvent.VK_UP : Controle4Directions.getInstance().setDirectionCourante(Direction.haut); break;
                }
            }
        });
    }
*/

    private void chargerLesIcones() {
        // image libre de droits utilisée pour les légumes : https://www.vecteezy.com/vector-art/2559196-bundle-of-fruits-and-vegetables-icons


        icoSalade = chargerIcone("Images/data.png", 0, 0, 120, 120);
        mapLegumeIcone.put(icoSalade,"Salade");
        icoCarotte = chargerIcone("Images/data.png", 390, 393, 120, 120);
        mapLegumeIcone.put(icoCarotte,"Carotte");
        icoVide = chargerIcone("Images/Vide.png");
        icoMur = chargerIcone("Images/Mur.png");
        icoTerre = chargerIcone("Images/terreNORMAL.png");
        icoGraine = chargerIcone("Images/graine.png");
        icoPousse = chargerIcone("Images/pousse.png");
    }

    private void placerLesComposantsGraphiques() {
        setTitle("A vegetable garden");
        setSize(680, 585);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre
        setResizable(false);

        JPanel utilitaire = this.createNorthPanel();
        JPanel infos = this.createSouthPanel();
        JPanel stock = this.createEastPanel();

        add(infos, BorderLayout.SOUTH);
        add(utilitaire, BorderLayout.NORTH);
        add(stock, BorderLayout.EAST);

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels, BorderLayout.CENTER);

        // écouter les évènements

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                final int xx = x; // constantes utiles au fonctionnement de la classe anonyme
                final int yy = y;
                tabJLabel[x][y].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(outil.isActif()){
                            simulateurPotager.actionUtilisateur(xx, yy, Action.RECOLTER);
                            tabJLabel[xx][yy].getGraphics().clearRect(0,0,50,50);
                            tabJLabel[xx][yy].getGraphics().drawImage(icoTerre.getImage(), 0, 0, null);

                        }else if(arrosoir.isActif()){
                            simulateurPotager.actionUtilisateur(xx, yy, Action.ARROSER);
                        }else if(infoPlante.isActif()){

                            humide.setText("Humidité : " + ((CaseCultivable) simulateurPotager.getPlateau()[xx][yy]).getHumidite()+"%");
                            humide.setFont(new Font("Arial", Font.BOLD, 15));

                            JLabel variete = new JLabel("Variété : ");
                            variete.setFont(new Font("Arial", Font.BOLD, 15));

                            JLabel varieteIcon = new JLabel();

                            Legume legumeCase = ((CaseCultivable) simulateurPotager.getPlateau()[xx][yy]).getLegume();
                            if(legumeCase != null) {
                                switch (legumeCase.getVariete()) {
                                    case salade:
                                        varieteIcon.setIcon(icoSalade);
                                        break;
                                    case carotte:
                                        varieteIcon.setIcon(icoCarotte);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            infoCase.remove(plante);
                            plante.removeAll();
                            infoCase.repaint();

                            infoCase.add(humide);
                            plante.add(variete);
                            plante.add(varieteIcon);
                            infoCase.add(plante);
                        }
                        else
                            simulateurPotager.actionUtilisateur(xx, yy, Action.PLANTER);

                    }
                });
            }
        }
    }

    public void afficherDate(){
        affichageDate.setText(simulateurDate.getDateString()); // On affiche la date
    }

    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() throws IOException {
        ImageIcon iconPlante= null;

        if(!infoPlante.isActif()){
            warning.setText("Cliquez sur i + une case");
            infoCase.add(warning);
            infoCase.remove(humide);
            infoCase.remove(plante);
            plante.removeAll();
            infoCase.repaint();

        }else{
            infoCase.remove(warning);
            infoCase.validate();
            infoCase.repaint();
        }

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (simulateurPotager.getPlateau()[x][y] instanceof CaseCultivable) { // si la case est cultivable
                    Legume legume = ((CaseCultivable) simulateurPotager.getPlateau()[x][y]).getLegume();
                    icoTerre = chargerIcone("Images/terre"+((CaseCultivable) simulateurPotager.getPlateau()[x][y]).getEtatTerre() +".png");
                    if (legume != null) {
                        if(legume.getEtatLegume() == EtatLegume.graine) {
                            iconPlante = icoGraine;

                        } else if(legume.getEtatLegume() == EtatLegume.pousse) {
                            iconPlante = icoPousse;
                        } else {
                            iconPlante = switch (legume.getVariete()) {
                                case salade -> icoSalade;
                                case carotte -> icoCarotte;
                            };
                        }
                        tabJLabel[x][y].getGraphics().drawImage(icoTerre.getImage(), 0, 0, null);
                        tabJLabel[x][y].getGraphics().drawImage(iconPlante.getImage(), 10, 10, null);
                    } else
                        tabJLabel[x][y].getGraphics().drawImage(icoTerre.getImage(), 0, 0, null);
                } else
                    tabJLabel[x][y].getGraphics().drawImage(icoVide.getImage(), 0, 0, null);
            }
        }

        int i=0;
        int j=0;
        for(Varietes v : Varietes.values()){
            if(i== 3)
                break;
            if(j == 3){
                i++;
                j=0;
            }
            switch (v){
                case carotte:
                    labelInventaire[i][j].setIcon(icoCarotte);
                    break;
                case salade:
                    labelInventaire[i][j].setIcon(icoSalade);
                    break;
                default:
                    break;
            }
            labelInventaire[i][j].setText(""+inventaire.getContenu().get(v));
            j++;
        }
    }

    public void mettreAJourMeteo(){
        switch(simulateurMeteo.getCalcMomentJournee()){
            case MATIN -> {
                momentJournee.setIcon(new ImageIcon("Images/matin.png"));
            }
            case JOURNEE -> {
                momentJournee.setIcon(new ImageIcon("Images/soleil.png"));
            }
            case SOIR -> {
                momentJournee.setIcon(new ImageIcon("Images/soir.png"));
            }
            case NUIT -> {
                momentJournee.setIcon(new ImageIcon("Images/nuit.png"));
            }
        }

        switch (simulateurMeteo.getCalcEtatMeteo()) {
            case SOLEIL -> meteo.setIcon(new ImageIcon("Images/soleil.png"));
            case PLUIE -> meteo.setIcon(new ImageIcon("Images/pluie.png"));
            case ECLAIRCIES -> meteo.setIcon(new ImageIcon("Images/soleilNuage.png"));
            case NUAGE -> meteo.setIcon(new ImageIcon("Images/nuage.png"));
        }
        temperature.setText(simulateurMeteo.getCalcTemperature()+"°C");
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            mettreAJourAffichage();
            mettreAJourMeteo();
            afficherDate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mettreAJourAffichage();
                    }
                }); 
        */

    }


    // chargement de l'image entière comme icone
    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurPotager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new ImageIcon(image);
    }

    // chargement d'une sous partie de l'image
    private ImageIcon chargerIcone(String urlIcone, int x, int y, int w, int h) {
        // charger une sous partie de l'image à partir de ses coordonnées dans urlIcone
        BufferedImage bi = getSubImage(urlIcone, x, y, w, h);
        // adapter la taille de l'image a la taille du composant (ici : 20x20)
        return new ImageIcon(bi.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH));
    }

    private BufferedImage getSubImage(String urlIcone, int x, int y, int w, int h) {
        BufferedImage image = null;

        try {
            File f = new File(urlIcone);
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurPotager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        BufferedImage bi = image.getSubimage(x, y, w, h);
        return bi;
    }

    public JPanel createNorthPanel() {
        JPanel utilitaire = new JPanel();
        JPanel utilitaireOutils = new JPanel();
        JPanel utilitaireTemps = new JPanel();

        utilitaireOutils.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx=0;
        arrosoir = this.ajoutBouton("Images/arrosoir");
        utilitaireOutils.add(arrosoir, gbc);

        gbc.gridx=1;
        outil = this.ajoutBouton("Images/outil");

        utilitaireOutils.add(outil, gbc);

        gbc.gridx=2;
        infoPlante = this.ajoutBouton("Images/info");
        utilitaireOutils.add(infoPlante, gbc);


        JComboBox<ImageIcon> graines = new JComboBox<ImageIcon>();
        graines.addItem(icoCarotte);
        graines.addItem(icoSalade);

        // Ajout d'un listener qui permet de récupérer le légume sélectionné
        graines.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            ImageIcon icon = (ImageIcon)cb.getSelectedItem();
            simulateurPotager.setLegumeSelectionne(mapLegumeIcone.get(icon));
        });

        gbc.gridx=3;
        utilitaireOutils.add(graines, gbc);
        utilitaire.add(utilitaireOutils);


        /** Panel de temps **/
        utilitaireTemps.setLayout(new GridBagLayout());
        GridBagConstraints gbcTemps = new GridBagConstraints();

        gbcTemps.insets = new Insets(5, 0, 5, 0);

        gbcTemps.gridx=0;
        pauseTemps = this.ajoutBouton("Images/tpsNorm");
        utilitaireTemps.add(pauseTemps, gbcTemps);

        gbcTemps.gridx=1;
        accTemps = this.ajoutBouton("Images/accelerer");
        utilitaireTemps.add(accTemps, gbcTemps);
        accTemps.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                simulateurDate.modifierVitesse(accTemps.isActif());
            }
        });

        utilitaire.add(utilitaireTemps);

        return utilitaire;
    }

    public JPanel createSouthPanel(){
        JPanel general = new JPanel();

        // Panel général
        GridBagConstraints gbcGeneral = new GridBagConstraints();
        gbcGeneral.insets = new Insets(5, 3, 5, 3);

        gbcGeneral.gridx=0;
        momentJournee.setIcon(new ImageIcon("Images/matin.png"));
        general.add(momentJournee, gbcGeneral);

        gbcGeneral.gridx=1;
        general.add(affichageDate, gbcGeneral);

        gbcGeneral.gridx=2;
        meteo.setIcon(new ImageIcon("Images/soleil.png"));
        general.add(meteo, gbcGeneral);


        gbcGeneral.gridx=3;
        temperature.setText("7°");
        temperature.setIcon(new ImageIcon("Images/boutonFond.png"));
        temperature.setHorizontalTextPosition(SwingConstants.CENTER);
        temperature.setForeground(Color.orange);
        temperature.setFont(new Font("Arial", Font.BOLD, 13));
        general.add(temperature, gbcGeneral);

        return general;
    }


    public JPanel createEastPanel(){
        JPanel global = new JPanel();
        global.setLayout(new GridLayout(3,1));

        /** Information de case **/
        GridLayout grid = new GridLayout(3,1);
        infoCase.setLayout(grid);
        infoCase.setPreferredSize(new Dimension(180,50));
        JLabel infoIcon = new JLabel(new ImageIcon("Images/infoClick.png"));
        infoCase.add(infoIcon);
        plante.setLayout(new GridLayout(1,2));

        warning.setEditable(false);
        warning.setBorder(null);
        infoCase.add(warning);
        infoCase.add(humide);
        infoCase.add(plante);


        global.add(infoCase);

        JPanel inventairePanel = new JPanel();
        inventairePanel.setLayout(new GridLayout(2,1));
        /** Inventaire **/
        JLabel invIcon = new JLabel(new ImageIcon("Images/inventaire.png"));
        inventairePanel.add(invIcon);

        labelInventaire = new JLabel[3][3];
        JComponent grilleJLabels = new JPanel(new GridLayout(3, 3));
        for(int i= 0; i<3; i++){
            for(int j=0; j<3; j++){
                JLabel l = new JLabel();

                labelInventaire[i][j] = l; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

                labelInventaire[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
                grilleJLabels.add(l);
            }
        }
        inventairePanel.add(grilleJLabels);




        global.add(inventairePanel);

        return global;
    }
    /**
     * Ajout d'un boutton avec une icone
     * @param urlIcone
     * @return
     */
    public Button ajoutBouton(String urlIcone) {
        Button button = new Button();
        ImageIcon iconBase = new ImageIcon(urlIcone+"Base.png");
        ImageIcon iconClick = new ImageIcon(urlIcone+"Click.png");
        button.setIcon(iconBase);
        button.setBounds(0, 0, 0, 10);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(button.getIcon().toString().equals(iconClick.toString())) {
                    button.setIcon(iconBase);
                    button.setActif(false);
                } else {
                    deselectionnerBoutons();
                    button.setIcon(iconClick);
                    button.setActif(true);
                }
            }
        });

        return button;
    }

    public void deselectionnerBoutons() {
        arrosoir.setIcon(new ImageIcon("Images/arrosoirBase.png"));
        arrosoir.setActif(false);
        outil.setIcon(new ImageIcon("Images/outilBase.png"));
        outil.setActif(false);
        infoPlante.setIcon(new ImageIcon("Images/infoBase.png"));
        infoPlante.setActif(false);
        pauseTemps.setIcon(new ImageIcon("Images/tpsNormBase.png"));
        pauseTemps.setActif(false);
        accTemps.setIcon(new ImageIcon("Images/accelererBase.png"));
        accTemps.setActif(false);
    }
}
