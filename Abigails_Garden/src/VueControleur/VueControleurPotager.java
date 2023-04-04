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
    private Button arrosoir;
    private Button outil;
    private Button infoPlante;
    private Button ralTemps;
    private Button pauseTemps;
    private Button accTemps;

    private JLabel momentJournee = new JLabel();
    private JLabel meteo = new JLabel();
    private JLabel affichageDate = new JLabel();
    private JLabel humide = new JLabel();
    private JLabel temperature = new JLabel();


    public VueControleurPotager(SimulateurPotager _simulateurPotager) {
        sizeX = _simulateurPotager.SIZE_X;
        sizeY = _simulateurPotager.SIZE_Y;
        simulateurPotager = _simulateurPotager;
        simulateurMeteo = new SimulateurMeteo(simulateurPotager);

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
        setSize(500, 585);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre
        setResizable(false);

        JPanel utilitaire = this.createNorthPanel();
        JPanel infos = this.createSouthPanel();

        add(infos, BorderLayout.SOUTH);
        add(utilitaire, BorderLayout.NORTH);

        JDialog jd = new JDialog();
        jd.setTitle("Informations sur la case");
        jd.setSize(100, 100);
        jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jd.setResizable(false);



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
                            humide.setText(((CaseCultivable) simulateurPotager.getPlateau()[xx][yy]).getHumidite()+"%");
                            humide.setIcon(new ImageIcon("Images/boutonFond.png"));
                            humide.setHorizontalTextPosition(SwingConstants.CENTER);
                            humide.setForeground(Color.orange);
                            humide.setFont(new Font("Arial", Font.BOLD, 15));

                            JPanel panel = new JPanel();
                            JTextField variete = new JTextField("Variété : ");
                            variete.setBorder(null);
                            variete.setEditable(false);

                            JLabel varieteLabel = new JLabel();
                            Legume legumeCase = ((CaseCultivable) simulateurPotager.getPlateau()[xx][yy]).getLegume();
                            if(legumeCase != null){
                                switch(legumeCase.getVariete()){
                                    case salade:
                                        varieteLabel.setIcon(icoSalade);
                                        break;
                                    case carotte:
                                        varieteLabel.setIcon(icoCarotte);
                                        break;
                                    default:
                                        break;
                                }
                            }

                            panel.add(variete);
                            panel.add(varieteLabel);

                            jd.add(humide, BorderLayout.CENTER);
                            jd.add(panel, BorderLayout.SOUTH);
                            jd.setVisible(true);


                        }
                        else
                            simulateurPotager.actionUtilisateur(xx, yy, Action.PLANTER);

                    }
                });
            }
        }
    }

    public void afficherDate(){
        SimulateurDate date = new SimulateurDate();
        affichageDate.setText(date.getDateString()); // On affiche la date
    }

    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() throws IOException {
        ImageIcon iconPlante= null;
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
            case SOLEIL -> {
                meteo.setIcon(new ImageIcon("Images/soleil.png"));
            }
            case PLUIE -> {
                meteo.setIcon(new ImageIcon("Images/pluie.png"));
                for(int i = 0; i < sizeX; i++){
                    for(int j = 0; j < sizeY; j++) {
                        if (simulateurPotager.getPlateau()[i][j] instanceof CaseCultivable) {
                            ((CaseCultivable) simulateurPotager.getPlateau()[i][j]).setHumiditeAvVal(5,"ajout");
                        }
                    }
                }
            }
            case ECLAIRCIES -> {
                meteo.setIcon(new ImageIcon("Images/soleilNuage.png"));
                for(int i = 0; i < sizeX; i++){
                    for(int j = 0; j < sizeY; j++) {
                        if (simulateurPotager.getPlateau()[i][j] instanceof CaseCultivable) {
                            ((CaseCultivable) simulateurPotager.getPlateau()[i][j]).setHumiditeAvVal(2,"baisse");
                        }
                    }
                }
            }
            case NUAGE -> {
                meteo.setIcon(new ImageIcon("Images/nuage.png"));
                for(int i = 0; i < sizeX; i++){
                    for(int j = 0; j < sizeY; j++) {
                        if (simulateurPotager.getPlateau()[i][j] instanceof CaseCultivable) {
                            ((CaseCultivable) simulateurPotager.getPlateau()[i][j]).setHumiditeAvVal(1,"ajout");
                        }
                    }
                }
            }
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
        graines.addItem(chargerIcone("Images/data.png", 390, 393, 120, 120));
        graines.addItem(chargerIcone("Images/data.png", 0, 0, 120, 120));

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
        ralTemps = this.ajoutBouton("Images/ral");
        utilitaireTemps.add(ralTemps, gbcTemps);

        gbcTemps.gridx=1;
        pauseTemps = this.ajoutBouton("Images/pause");
        utilitaireTemps.add(pauseTemps, gbcTemps);

        gbcTemps.gridx=2;
        accTemps = this.ajoutBouton("Images/acc");
        utilitaireTemps.add(accTemps, gbcTemps);

        utilitaire.add(utilitaireTemps);

        return utilitaire;
    }

    public JPanel createSouthPanel(){
        JPanel infos = new JPanel();
        JPanel general = new JPanel();
        JPanel inventaire = new JPanel();

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


        infos.add(general);

        /** Panel Inventaire **/
//        Button inven = this.ajoutBouton("Images/Pacman.png");
//        inventaire.add(inven);
        infos.add(inventaire);

        return infos;
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
        ralTemps.setIcon(new ImageIcon("Images/ralBase.png"));
        pauseTemps.setIcon(new ImageIcon("Images/pauseBase.png"));
        accTemps.setIcon(new ImageIcon("Images/accBase.png"));
    }
}
