package vehiclesimulation;

import dynamics.*;

import vehiclesimulationcore.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <p>Company: edu</p>
 *
 * @author  Ahmad Zoubi, Mohammad Askar, Qousai Dabour, Usama Rmellawi, Yasser Almohammad
 * @version 1.0
 */
public class SimMainFrame extends JFrame {
    JPanel contentPane;
    BorderLayout borderLayout1 = new BorderLayout();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenuItem jMenuFileExit = new JMenuItem();
    JMenu jMenuHelp = new JMenu();
    JMenuItem jMenuHelpAbout = new JMenuItem();
    JToolBar jToolBar = new JToolBar();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JButton jButton3 = new JButton();
    ImageIcon image1 = new ImageIcon(vehiclesimulation.SimMainFrame.class.
                                     getResource("openFile.png"));
    ImageIcon image2 = new ImageIcon(vehiclesimulation.SimMainFrame.class.
                                     getResource("closeFile.png"));
    ImageIcon image3 = new ImageIcon(vehiclesimulation.SimMainFrame.class.
                                     getResource("help.png"));
    JLabel statusBar = new JLabel();

    JMenu view = new JMenu();
    JMenuItem menuOptions = new JMenuItem();
    JMenuItem menuTestBrakeSys = new JMenuItem();


    JMenuItem menuSim3D = new JMenuItem();
    Simulation3D sim3D = new Simulation3D();
    JMenu jMenu1 = new JMenu();
    JMenuItem menAddRemoveTerrain = new JMenuItem();
    JMenuItem menuAddRemoveGrid = new JMenuItem();
    JMenuItem menuAddRemoveTexture = new JMenuItem();
    JMenuItem menuSwitchVehicle = new JMenuItem();
    JMenuItem menuSwitchLight1 = new JMenuItem();
    JMenuItem menuSwitchLight2 = new JMenuItem();
    JMenuItem menuSwitchLight3 = new JMenuItem();
    JMenuItem menuTestMath = new JMenuItem();
    public SimMainFrame() {
        SimVals.reset();
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(borderLayout1);
        setSize(new Dimension(400, 50));
        setTitle("Vehicle Dynamics Simulation Program");
        statusBar.setText(" ");
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new
                                        SimMainFrame_jMenuFileExit_ActionAdapter(this));
        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new
                                         SimMainFrame_jMenuHelpAbout_ActionAdapter(this));
        menuOptions.setText("advanced options");
        menuOptions.addActionListener(new
                                      SimMainFrame_menuOptions_actionAdapter(this));
        menuTestBrakeSys.addActionListener(new
                SimMainFrame_menuTestBrakeSys_actionAdapter(this));
        menuTestBrakeSys.setText("test brake simulation");
        menuSim3D.setText("view Simulation3D window");
        menuSim3D.addActionListener(new SimMainFrame_menuSim3D_actionAdapter(this));
        jMenu1.setText("options");
        menAddRemoveTerrain.setText("add-remove terrain");
        menAddRemoveTerrain.addActionListener(new
                SimMainFrame_menAddRemoveTerrain_actionAdapter(this));
        menuAddRemoveGrid.setText("add-remove grid");
        menuAddRemoveGrid.addActionListener(new
                SimMainFrame_menuAddRemoveGrid_actionAdapter(this));
        menuAddRemoveTexture.setText("add-remove textures");
        menuAddRemoveTexture.addActionListener(new
                SimMainFrame_menuAddRemoveTexture_actionAdapter(this));
        menuSwitchVehicle.setText("switch vehicle details");
        menuSwitchVehicle.addActionListener(new
                SimMainFrame_menuSwitchVehicle_actionAdapter(this));
        menuSwitchLight1.setText("enable-disable light1");
        menuSwitchLight1.addActionListener(new
                SimMainFrame_menuEnableLight1_actionAdapter(this));
        menuSwitchLight2.setText("enable-disable light2");
        menuSwitchLight2.addActionListener(new
                SimMainFrame_menuSwitchLight2_actionAdapter(this));
        menuSwitchLight3.setText("enable-disable light3");
        menuSwitchLight3.addActionListener(new
                SimMainFrame_menuSwitchLight3_actionAdapter(this));
        menuTestMath.setText("test mathematical model");
        menuTestMath.addActionListener(new
                                       SimMainFrame_menuTestMath_actionAdapter(this));
        view.setText("view");
        jMenuBar1.add(jMenuFile);
        jMenuBar1.add(view);
        jMenuFile.add(jMenuFileExit);
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenuHelp);
        jMenuHelp.add(jMenuHelpAbout);
        setJMenuBar(jMenuBar1);
        jButton1.setIcon(image1);
        jButton1.setToolTipText("Open File");
        jButton2.setIcon(image2);
        jButton2.setToolTipText("Close File");
        jButton3.setIcon(image3);
        jButton3.setToolTipText("Help");
        jToolBar.add(jButton1);
        jToolBar.add(jButton2);
        jToolBar.add(jButton3);
        contentPane.add(jToolBar, BorderLayout.NORTH);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        view.add(menuTestBrakeSys);
        view.add(menuOptions);
        view.add(menuSim3D);
        view.add(menuTestMath);
        jMenu1.add(menAddRemoveTerrain);
        jMenu1.add(menuAddRemoveGrid);
        jMenu1.add(menuAddRemoveTexture);
        jMenu1.add(menuSwitchVehicle);
        jMenu1.addSeparator();
        jMenu1.add(menuSwitchLight1);
        jMenu1.add(menuSwitchLight2);
        jMenu1.add(menuSwitchLight3);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = sim3D.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        sim3D.setLocation((screenSize.width - frameSize.width) / 2,
                          (screenSize.height - frameSize.height) / 2);


    }

    /**
     * File | Exit action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Help | About action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
        SimMainFrame_AboutBox dlg = new SimMainFrame_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                        (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.pack();
        dlg.show();
    }

    public void menuTestBrakeSys_actionPerformed(ActionEvent e) {
        BrakingUI bui=new BrakingUI();


    // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = bui.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        bui.setLocation((screenSize.width - frameSize.width) / 2,
                        (screenSize.height - frameSize.height) / 2);
        bui.setVisible(true);

    }

    public void menuSim3D_actionPerformed(ActionEvent e) {


    // Center the window
    /*
        */
        sim3D.setVisible(true);
    }

    public void menAddRemoveTerrain_actionPerformed(ActionEvent e) {
         World.switchTerrain();
    }

    public void menuAddRemoveGrid_actionPerformed(ActionEvent e) {
        World.switchGrid();
    }

    public void menuAddRemoveTexture_actionPerformed(ActionEvent e) {
        World.switchTexture();
    }

    public void menuSwitchVehicle_actionPerformed(ActionEvent e) {
        World.switchVehicle();
    }

    public void menuEnableLight1_actionPerformed(ActionEvent e) {
        World.switchLight1();
    }

    public void menuSwitchLight2_actionPerformed(ActionEvent e) {
        World.switchLight2();
    }

    public void menuSwitchLight3_actionPerformed(ActionEvent e) {
        World.switchLight3();
    }

    SimulationCore sim=null;
    public void menuTestMath_actionPerformed(ActionEvent e) {
        sim=new SimulationCore();
        sim.start();
    }

    public void simlist_actionPerformed(ActionEvent e) {
     //   sim.simlist.start();
    }

    public void menuOptions_actionPerformed(ActionEvent e) {
        AdvancedInput input=new AdvancedInput();
        input.setVisible(true);
    }
}


class SimMainFrame_menuOptions_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuOptions_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuOptions_actionPerformed(e);
    }
}


class SimMainFrame_simlist_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_simlist_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.simlist_actionPerformed(e);
    }
}


class SimMainFrame_menuTestMath_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuTestMath_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuTestMath_actionPerformed(e);
    }
}


class SimMainFrame_menuSwitchLight3_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuSwitchLight3_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuSwitchLight3_actionPerformed(e);
    }
}


class SimMainFrame_menuEnableLight1_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuEnableLight1_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuEnableLight1_actionPerformed(e);
    }
}


class SimMainFrame_menuSwitchLight2_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuSwitchLight2_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuSwitchLight2_actionPerformed(e);
    }
}


class SimMainFrame_menuSwitchVehicle_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuSwitchVehicle_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuSwitchVehicle_actionPerformed(e);
    }
}


class SimMainFrame_menuAddRemoveTexture_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuAddRemoveTexture_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuAddRemoveTexture_actionPerformed(e);
    }
}


class SimMainFrame_menuAddRemoveGrid_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuAddRemoveGrid_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.menuAddRemoveGrid_actionPerformed(e);
    }
}


class SimMainFrame_menAddRemoveTerrain_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menAddRemoveTerrain_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menAddRemoveTerrain_actionPerformed(e);
    }
}


class SimMainFrame_menuSim3D_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuSim3D_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuSim3D_actionPerformed(e);
    }
}


class SimMainFrame_menuTestBrakeSys_actionAdapter implements ActionListener {
    private SimMainFrame adaptee;
    SimMainFrame_menuTestBrakeSys_actionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuTestBrakeSys_actionPerformed(e);
    }
}


class SimMainFrame_jMenuFileExit_ActionAdapter implements ActionListener {
    SimMainFrame adaptee;

    SimMainFrame_jMenuFileExit_ActionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuFileExit_actionPerformed(actionEvent);
    }
}


class SimMainFrame_jMenuHelpAbout_ActionAdapter implements ActionListener {
    SimMainFrame adaptee;

    SimMainFrame_jMenuHelpAbout_ActionAdapter(SimMainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
    }
}
