package vehiclesimulation;

import java.awt.BorderLayout;
import java.awt.Frame;
import dynamics.*;
import javax.swing.JDialog;
import javax.swing.JPanel;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import java.awt.*;

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
public class AdvancedInput extends JDialog {
    JPanel panel1 = new JPanel();
    XYLayout xYLayout1 = new XYLayout();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JLabel jLabel8 = new JLabel();
    JLabel jLabel9 = new JLabel();
    JLabel jLabel10 = new JLabel();
    JTextField txtMiuS0 = new JTextField();
    JTextField txtMiuS1 = new JTextField();
    JTextField txtMiuK0 = new JTextField();
    JTextField txtMiuK1 = new JTextField();
    JTextField txtSegma00 = new JTextField();
    JTextField txtSegma01 = new JTextField();
    JTextField txtSegma10 = new JTextField();
    JTextField txtSegma11 = new JTextField();
    JTextField txtSegma20 = new JTextField();
    JTextField txtSegma21 = new JTextField();
    JButton btnSubmit = new JButton();
    public AdvancedInput(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            pack();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public AdvancedInput() {
        this(new Frame(), "AdvancedInput", false);
    }

    private void jbInit() throws Exception {
        panel1.setLayout(xYLayout1);
        jLabel1.setText("miuS[0]");
        jLabel2.setText("miuS[1]");
        jLabel3.setText("miuK[0]");
        jLabel4.setText("miuK[1]");
        jLabel5.setText("Segma_0[0]");
        jLabel6.setText("Segma_0[1]");
        jLabel7.setText("Segma_1[0]");
        jLabel8.setText("Segma_1[1]");
        jLabel9.setText("Segma_2[0]");
        jLabel10.setText("Segma_2[1]");
        txtMiuS0.setText("jTextField1");
        txtMiuS1.setText("jTextField1");
        txtMiuK0.setText("jTextField1");
        txtMiuK1.setText("jTextField1");
        txtSegma00.setText("jTextField1");
        txtSegma01.setText("jTextField1");
        txtSegma10.setText("jTextField1");
        txtSegma11.setText("jTextField1");
        txtSegma20.setText("jTextField1");
        txtSegma21.setText("jTextField1");
        btnSubmit.setText("submit");
        btnSubmit.addActionListener(new AdvancedInput_btnSubmit_actionAdapter(this));
        txtSegma11.addActionListener(new AdvancedInput_txtSegma11_actionAdapter(this));
        panel1.add(jLabel1, new XYConstraints(12, 10, -1, -1));
        panel1.add(jLabel2, new XYConstraints(12, 39, -1, -1));
        panel1.add(jLabel3, new XYConstraints(12, 66, -1, -1));
        panel1.add(jLabel4, new XYConstraints(12, 93, -1, -1));
        panel1.add(jLabel9, new XYConstraints(11, 241, -1, -1));
        panel1.add(jLabel10, new XYConstraints(9, 264, -1, -1));
        panel1.add(jLabel5, new XYConstraints(8, 123, -1, -1));
        panel1.add(jLabel6, new XYConstraints(7, 147, -1, -1));
        panel1.add(jLabel7, new XYConstraints(5, 175, -1, -1));
        panel1.add(jLabel8, new XYConstraints(3, 209, -1, -1));
        panel1.add(btnSubmit, new XYConstraints(270, 258, -1, -1));
        panel1.add(txtMiuS0, new XYConstraints(106, 10, 125, 21));
        panel1.add(txtSegma10, new XYConstraints(108, 171, 123, 23));
        panel1.add(txtSegma21, new XYConstraints(110, 262, 118, 23));
        panel1.add(txtSegma20, new XYConstraints(109, 235, 119, 22));
        panel1.add(txtSegma11, new XYConstraints(109, 204, 121, 23));
        panel1.add(txtSegma01, new XYConstraints(108, 146, 122, 21));
        panel1.add(txtSegma00, new XYConstraints(107, 121, 122, 21));
        panel1.add(txtMiuK1, new XYConstraints(107, 94, 125, -1));
        panel1.add(txtMiuK0, new XYConstraints(106, 68, 127, 21));
        panel1.add(txtMiuS1, new XYConstraints(105, 41, 125, -1));
        this.getContentPane().add(panel1, java.awt.BorderLayout.CENTER);
        initVals();
    }

    public void initVals(){
        txtMiuS0.setText(""+SimVals.miuS[0]);
        txtMiuS1.setText(""+SimVals.miuS[1]);

        txtMiuK0.setText(""+SimVals.miuK[0]);
        txtMiuK1.setText(""+SimVals.miuK[1]);

        txtSegma00.setText(""+SimVals.segma_0[0]);
        txtSegma01.setText(""+SimVals.segma_0[1]);

        txtSegma10.setText(""+SimVals.segma_1[0]);
        txtSegma11.setText(""+SimVals.segma_1[1]);

        txtSegma20.setText(""+SimVals.segma_2[0]);
        txtSegma21.setText(""+SimVals.segma_2[1]);
    }

    public void btnSubmit_actionPerformed(ActionEvent e) {
        try{
            SimVals.miuS[0]=Double.parseDouble(txtMiuS0.getText());
            SimVals.miuS[1]=Double.parseDouble(txtMiuS1.getText());
            SimVals.miuK[0]=Double.parseDouble(txtMiuK0.getText());
            SimVals.miuK[1]=Double.parseDouble(txtMiuK1.getText());

            SimVals.segma_0[0]=Double.parseDouble(txtSegma00.getText());
            SimVals.segma_0[1]=Double.parseDouble(txtSegma01.getText());

            SimVals.segma_1[0]=Double.parseDouble(txtSegma10.getText());
            SimVals.segma_1[1]=Double.parseDouble(txtSegma11.getText());

            SimVals.segma_2[0]=Double.parseDouble(txtSegma20.getText());
            SimVals.segma_2[1]=Double.parseDouble(txtSegma21.getText());
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this,"check input again");
        }
    }

    public void txtSegma11_actionPerformed(ActionEvent e) {

    }
}


class AdvancedInput_txtSegma11_actionAdapter implements ActionListener {
    private AdvancedInput adaptee;
    AdvancedInput_txtSegma11_actionAdapter(AdvancedInput adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.txtSegma11_actionPerformed(e);
    }
}


class AdvancedInput_btnSubmit_actionAdapter implements ActionListener {
    private AdvancedInput adaptee;
    AdvancedInput_btnSubmit_actionAdapter(AdvancedInput adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnSubmit_actionPerformed(e);
    }
}
