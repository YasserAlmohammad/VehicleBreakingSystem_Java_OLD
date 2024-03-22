package vehiclesimulation;


import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 */
public class VehicleDynamicsSimulation {
    boolean packFrame = false;

    /**
     * Construct and show the application.
     */
    public VehicleDynamicsSimulation() {
        SimMainFrame frame = new SimMainFrame();
        // Validate frames that have preset sizes
        // Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
//        frame.setLocation((screenSize.width - frameSize.width) / 2,
//                          (screenSize.height - frameSize.height) / 2);
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                          20);
        frame.setVisible(true);
    }

    /**
     * Application entry point.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                 //   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                new VehicleDynamicsSimulation();
            }
        });
    }
}
