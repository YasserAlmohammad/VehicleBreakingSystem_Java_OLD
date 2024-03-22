package vehiclesimulation;

import vehiclesimulationcore.*;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <p>Company: FIT</p>
 *
 * @author Yasser Almohammad(Graphics Design and implementation), Ahmad Zoubi,
 *   Usama Rmelawi, Mohammad Askar,Qousai Dabour (vehicle case study, core
 *   functionality)
 * @version 1.0
 */
public class Simulation3D extends JFrame {
    BorderLayout borderLayout1 = new BorderLayout();
    World world=null;
    public Simulation3D() {
        try {
            resetWorld();
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void resetWorld(){
        world = new World();
    }
    private void jbInit() throws Exception {
        getContentPane().setLayout(borderLayout1);
        //World Creation

        this.setTitle("Simulation3D Window");
        getContentPane().add("Center", world.canvas);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setSize(700,500);

    }
}
