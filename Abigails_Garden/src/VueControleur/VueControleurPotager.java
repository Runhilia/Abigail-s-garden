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

import modele.Instance.Inventaire;
import modele.Instance.Magasin;
import modele.SimulateurDate;
import modele.SimulateurPotager;
import modele.environnement.*;
import modele.environnement.Action;
import modele.environnement.meteo.EnumMeteo;
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
    private boolean dixMinutesPassees = false;

    // icones affichées dans la grille
    private ImageIcon icoTerre;
    private ImageIcon icoVide;
    private ImageIcon icoMur;
    private ImageIcon icoGraine;
    private ImageIcon icoPousse;
    private ImageIcon icoSalade;
    private ImageIcon icoCarotte;
    private ImageIcon icoPasteque;


    private HashMap<ImageIcon,String> mapLegumeIcone = new HashMap<ImageIcon,String>(); // permet de récupérer le nom du légume grâce à l'icône

    // Composants graphiques
    private JLabel[][] tabJLabel; // cases graphiques (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)
    private JLabel[][] labelInventaire;
    private JLabel[][] labelMagasin;
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
    private JLabel finPoussePlante = new JLabel();

    private Inventaire inventaire;
    private Magasin magasin;
    private JSpinner nbLegume;
    private Varietes legumeVente;
    private JLabel argent;



    public VueControleurPotager(SimulateurPotager _simulateurPotager, SimulateurDate _simulateurDate, SimulateurMeteo _simulateurMeteo) {
        sizeX = _simulateurPotager.SIZE_X;
        sizeY = _simulateurPotager.SIZE_Y;
        simulateurPotager = _simulateurPotager;
        simulateurDate = _simulateurDate;
        simulateurMeteo = _simulateurMeteo;

        inventaire = Inventaire.getInventaire();
        magasin = Magasin.getMagasin();

        chargerLesIcones();
        placerLesComposantsGraphiques();
    }

    private void chargerLesIcones() {
        // image libre de droits utilisée pour les légumes : https://www.vecteezy.com/vector-art/2559196-bundle-of-fruits-and-vegetables-icons


        icoSalade = chargerIcone("Images/data.png", 0, 0, 120, 120);
        mapLegumeIcone.put(icoSalade,"Salade");
        icoCarotte = chargerIcone("Images/data.png", 390, 393, 120, 120);
        mapLegumeIcone.put(icoCarotte,"Carotte");
        icoPasteque = chargerIcone("Images/data.png", 1960, 20, 120, 120);
        mapLegumeIcone.put(icoPasteque,"Pasteque");

        icoVide = chargerIcone("Images/Vide.png");
        icoMur = chargerIcone("Images/Mur.png");
        icoTerre = chargerIcone("Images/terreNORMAL.png");
        icoGraine = chargerIcone("Images/graine.png");
        icoPousse = chargerIcone("Images/pousse.png");
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Abigail's Garden");
        setSize(721, 655);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre
        setResizable(false);
        setIconImage(chargerIcone("Images/iconeJeu.png").getImage());

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
                            humide.setFont(new Font("Arial", Font.BOLD, 12));


                            JLabel variete = new JLabel("Variété : ");
                            variete.setFont(new Font("Arial", Font.BOLD, 12));

                            JLabel varieteIcon = new JLabel();

                            Legume legumeCase = ((CaseCultivable) simulateurPotager.getPlateau()[xx][yy]).getLegume();
                            if(legumeCase != null) {
                                switch (legumeCase.getVariete()) {
                                    case salade -> varieteIcon.setIcon(icoSalade);
                                    case carotte -> varieteIcon.setIcon(icoCarotte);
                                    case pasteque -> varieteIcon.setIcon(icoPasteque);
                                    default -> {
                                    }
                                }
                                finPoussePlante.setText("Fin de pousse : " + ((CaseCultivable) simulateurPotager.getPlateau()[xx][yy]).getLegume().getHeureFinPousse());
                                finPoussePlante.setFont(new Font("Arial", Font.BOLD, 12));
                            }else{
                                finPoussePlante.setText(" ");
                                variete.setText(" ");
                            }
                            infoCase.remove(plante);
                            plante.removeAll();
                            infoCase.repaint();

                            infoCase.add(humide);
                            plante.add(variete);
                            plante.add(varieteIcon);
                            infoCase.add(plante);
                            infoCase.add(finPoussePlante);
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
            infoCase.remove(finPoussePlante);
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
                                case pasteque -> icoPasteque;
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
                    labelMagasin[i][j].setIcon(icoCarotte);
                    labelMagasin[i][j].setName("carotte");
                    break;
                case salade:
                    labelInventaire[i][j].setIcon(icoSalade);
                    labelMagasin[i][j].setIcon(icoSalade);
                    labelMagasin[i][j].setName("salade");
                    break;
                case pasteque:
                    labelInventaire[i][j].setIcon(icoPasteque);
                    labelMagasin[i][j].setIcon(icoPasteque);
                    labelMagasin[i][j].setName("pasteque");
                    break;
                default:
                    break;
            }
            labelInventaire[i][j].setText(""+inventaire.getContenu().get(v));
            labelMagasin[i][j].setText(""+magasin.getPrixVente(v));

            int ii = i;
            int jj= j;
            labelMagasin[i][j].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for(int a=0; a<3; a++){
                        for(int b=0; b<3; b++){
                            labelMagasin[a][b].setBorder(BorderFactory.createLineBorder(Color.black));
                        }
                    }
                    labelMagasin[ii][jj].setBorder(BorderFactory.createLineBorder(Color.red));
                    if(labelMagasin[ii][jj].getName().equals("carotte")){
                        nbLegume.setValue(Integer.parseInt(""+inventaire.getContenu().get(Varietes.carotte)));
                        legumeVente = Varietes.carotte;
                    }

                    if(labelMagasin[ii][jj].getName().equals("salade")) {
                        nbLegume.setValue(Integer.parseInt("" + inventaire.getContenu().get(Varietes.salade)));
                        legumeVente = Varietes.salade;
                    }

                    if(labelMagasin[ii][jj].getName().equals("pasteque")) {
                        nbLegume.setValue(Integer.parseInt("" + inventaire.getContenu().get(Varietes.pasteque)));
                        legumeVente = Varietes.pasteque;
                    }
                }
            });
            j++;
        }
        argent.setText(""+inventaire.getArgent());
    }

    /**
     * Modification de l'humidité des cases cultivables
     */
    public void changeHumidite(EnumMeteo etatMeteo){
        // On change l'humidité toutes les 10 minutes
        if(simulateurDate.getMinute()%10 - simulateurDate.getSaut() < 0) {
            if (!dixMinutesPassees) {
                dixMinutesPassees = true;
                switch (etatMeteo) {
                    case SOLEIL -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simulateurPotager.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simulateurPotager.getPlateau()[i][j]).setHumiditeAvVal(5, "baisse");
                                }
                            }
                        }
                    }
                    case ECLAIRCIES -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simulateurPotager.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simulateurPotager.getPlateau()[i][j]).setHumiditeAvVal(3, "baisse");
                                }
                            }
                        }
                    }
                    case PLUIE -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simulateurPotager.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simulateurPotager.getPlateau()[i][j]).setHumiditeAvVal(10, "ajout");
                                }
                            }
                        }
                    }
                    case NUAGE -> {
                        for (int i = 0; i < SimulateurPotager.SIZE_X; i++) {
                            for (int j = 0; j < SimulateurPotager.SIZE_Y; j++) {
                                if (simulateurPotager.getPlateau()[i][j] instanceof CaseCultivable) {
                                    ((CaseCultivable) simulateurPotager.getPlateau()[i][j]).setHumiditeAvVal(1, "baisse");
                                }
                            }
                        }
                    }
                }
            }
        }
        else{
            dixMinutesPassees = false;
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
        EnumMeteo etatMeteo = simulateurMeteo.getCalcEtatMeteo();

        switch (etatMeteo) {
            case SOLEIL -> meteo.setIcon(new ImageIcon("Images/soleil.png"));
            case PLUIE -> meteo.setIcon(new ImageIcon("Images/pluie.png"));
            case ECLAIRCIES -> meteo.setIcon(new ImageIcon("Images/soleilNuage.png"));
            case NUAGE -> meteo.setIcon(new ImageIcon("Images/nuage.png"));
        }
        changeHumidite(etatMeteo);
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


        arrosoir = this.ajoutBouton("Images/arrosoir");
        utilitaireOutils.add(arrosoir, gbc);

        outil = this.ajoutBouton("Images/outil");

        utilitaireOutils.add(outil, gbc);

        infoPlante = this.ajoutBouton("Images/info");
        utilitaireOutils.add(infoPlante, gbc);


        JComboBox<ImageIcon> graines = new JComboBox<ImageIcon>();
        graines.addItem(icoCarotte);
        graines.addItem(icoSalade);
        graines.addItem(icoPasteque);

        // Ajout d'un listener qui permet de récupérer le légume sélectionné
        graines.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            ImageIcon icon = (ImageIcon)cb.getSelectedItem();
            simulateurPotager.setLegumeSelectionne(mapLegumeIcone.get(icon));
        });

        utilitaire.add(utilitaireOutils);

        JLabel vide = new JLabel();
        vide.setText("             ");
        utilitaire.add(vide);


        /** Panel de temps **/
        utilitaireTemps.setLayout(new GridBagLayout());

        pauseTemps = this.ajoutBouton("Images/tpsNorm");
        utilitaireTemps.add(pauseTemps, gbc);
        pauseTemps.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(pauseTemps.isActif()){
                    simulateurDate.modifierVitesse(0);
                }
                else{
                    simulateurDate.modifierVitesse(1);
                }
            }
        });

        accTemps = this.ajoutBouton("Images/tpsAccelerer");
        utilitaireTemps.add(accTemps, gbc);
        accTemps.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(accTemps.isActif()){
                    simulateurDate.modifierVitesse(5);
                }
                else{
                    simulateurDate.modifierVitesse(1);
                }
            }
        });

        utilitaire.add(utilitaireTemps);

        JLabel vide2 = new JLabel();
        vide2.setText("                ");
        utilitaire.add(vide2);

        utilitaire.add(graines);

        return utilitaire;
    }

    public JPanel createSouthPanel(){
        JPanel general = new JPanel();

        // Panel général
        GridBagConstraints gbcGeneral = new GridBagConstraints();
        gbcGeneral.insets = new Insets(5, 3, 5, 3);

        momentJournee.setIcon(new ImageIcon("Images/matin.png"));
        general.add(momentJournee, gbcGeneral);

        general.add(affichageDate, gbcGeneral);

        JLabel vide = new JLabel();
        general.add(vide);

        meteo.setIcon(new ImageIcon("Images/soleil.png"));
        general.add(meteo, gbcGeneral);

        temperature.setIcon(new ImageIcon("Images/boutonFond.png"));
        temperature.setHorizontalTextPosition(SwingConstants.CENTER);
        temperature.setForeground(Color.orange);
        temperature.setFont(new Font("Arial", Font.BOLD, 13));
        general.add(temperature, gbcGeneral);

        return general;
    }


    public JPanel createEastPanel(){
        JPanel global = new JPanel();
        global.setLayout(new GridBagLayout());
        GridBagConstraints gbcGlobal = new GridBagConstraints();

        /** Information de case **/
        infoCase.setLayout(new GridLayout(4,1));
        infoCase.setPreferredSize(new Dimension(180,60));
        JLabel infoIcon = new JLabel(new ImageIcon("Images/infoClick.png"));
        infoCase.add(infoIcon);
        plante.setLayout(new GridLayout(1,2));

        warning.setEditable(false);
        warning.setBorder(null);

        infoCase.add(warning);
        infoCase.add(humide);
        infoCase.add(plante);
        infoCase.add(finPoussePlante);

        gbcGlobal.fill = GridBagConstraints.HORIZONTAL;
        gbcGlobal.gridy = 0;
        gbcGlobal.ipady = 60;
        global.add(infoCase, gbcGlobal);

        // Inventaire et Magasin
        JPanel inventairePanel = new JPanel();
        JPanel magasinPanel = new JPanel();
        inventairePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcInv = new GridBagConstraints();

        magasinPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMag = new GridBagConstraints();

        JLabel invIcon = new JLabel(new ImageIcon("Images/inventaire.png"));
        gbcInv.gridy = 0;
        gbcInv.gridx = 1;
        gbcInv.weighty = 0.1;
        gbcInv.anchor = GridBagConstraints.PAGE_START;
        inventairePanel.add(invIcon, gbcInv);

        JLabel magIcon = new JLabel(new ImageIcon("Images/magasin.png"));
        gbcMag.gridy = 0;
        gbcMag.ipady= 5;
        gbcMag.weighty = 0.1;
        gbcMag.anchor = GridBagConstraints.PAGE_START;
        magasinPanel.add(magIcon, gbcMag);

        labelInventaire = new JLabel[3][3];
        labelMagasin = new JLabel[3][3];
        JComponent grilleJLabelsInv = new JPanel(new GridLayout(3, 3));
        JComponent grilleJLabelsMag = new JPanel(new GridLayout(3, 3));
        for(int x= 0; x<3; x++){
            for(int y=0; y<3; y++){
                JLabel i = new JLabel();
                i.setMinimumSize(new Dimension(55,20));
                i.setPreferredSize(new Dimension(55,20));
                JLabel m = new JLabel();
                m.setMinimumSize(new Dimension(55,20));
                m.setPreferredSize(new Dimension(55,20));

                labelInventaire[x][y] = i; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                labelMagasin[x][y]= m;

                labelInventaire[x][y].setBorder(BorderFactory.createLineBorder(Color.black));
                labelMagasin[x][y].setBorder(BorderFactory.createLineBorder(Color.black));

                grilleJLabelsInv.add(i);
                grilleJLabelsMag.add(m);
            }
        }

        gbcInv.gridy = 1;
        gbcInv.anchor = GridBagConstraints.CENTER;
        inventairePanel.add(grilleJLabelsInv, gbcInv);

        gbcMag.gridy = 1;
        gbcMag.anchor = GridBagConstraints.CENTER;
        magasinPanel.add(grilleJLabelsMag, gbcMag);

        argent = new JLabel();
        argent.setIcon(new ImageIcon("Images/monnaie.png"));
        argent.setText("" + inventaire.getArgent());
        argent.setFont(new Font("Arial", Font.BOLD, 15));

        gbcInv.gridy = 2;
        gbcInv.anchor = GridBagConstraints.WEST;
        inventairePanel.add(argent, gbcInv);

        gbcGlobal.gridy = 1;
        gbcGlobal.ipady = 50;
        global.add(inventairePanel, gbcGlobal);

        nbLegume = new JSpinner();
        nbLegume.setMinimumSize(new Dimension(50, 20));
        nbLegume.setPreferredSize(new Dimension(50, 20));
        JButton vendre = new JButton("Vendre !");

        vendre.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((int) nbLegume.getValue() <= inventaire.getContenu().get(legumeVente) && (int) nbLegume.getValue()>=0) {
                    int gain = (int) nbLegume.getValue() * magasin.getPrixVente(legumeVente);

                    switch (legumeVente) {
                        case carotte -> inventaire.removeCarotte((int) nbLegume.getValue());
                        case salade -> inventaire.removeSalade((int) nbLegume.getValue());
                        case pasteque -> inventaire.removePasteque((int) nbLegume.getValue());
                    }
                    inventaire.setArgent(gain);
                    for(int i=0; i<3; i++){
                        for(int j=0; j<3; j++){
                            labelMagasin[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
                            nbLegume.setValue(0);
                        }
                    }
                }
            }
        });

        gbcMag.gridy = 2;
        magasinPanel.add(nbLegume, gbcMag);
        gbcMag.gridy = 3;
        magasinPanel.add(vendre, gbcMag);

        gbcGlobal.gridy = 2;
        global.add(magasinPanel, gbcGlobal);

        return global;
    }

    /**
     * Ajout d'un boutton avec une icone
     * @param urlIcone Icone du bouton
     * @return le bouton créé
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
                    if(urlIcone.contains("tps")){
                        deselectionnerBoutonsVitesse();
                    }
                    else{
                        deselectionnerBoutonsOutils();
                    }
                    button.setIcon(iconClick);
                    button.setActif(true);
                }
            }
        });

        return button;
    }

    public void deselectionnerBoutonsOutils() {
        arrosoir.setIcon(new ImageIcon("Images/arrosoirBase.png"));
        arrosoir.setActif(false);
        outil.setIcon(new ImageIcon("Images/outilBase.png"));
        outil.setActif(false);
        infoPlante.setIcon(new ImageIcon("Images/infoBase.png"));
        infoPlante.setActif(false);
    }

    public void deselectionnerBoutonsVitesse() {
        pauseTemps.setIcon(new ImageIcon("Images/tpsNormBase.png"));
        pauseTemps.setActif(false);
        accTemps.setIcon(new ImageIcon("Images/tpsAccelererBase.png"));
        accTemps.setActif(false);
    }
}
