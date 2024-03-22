package vehiclesimulation;

import vehiclesimulationcore.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import com.borland.jbcl.layout.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

import dynamics.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class BrakingUI extends JFrame {

    public JPanel contentPane;
    public BorderLayout borderLayout1 = new BorderLayout();
    public TitledBorder titledBorder1 = new TitledBorder("");
    public FlowLayout flowLayout1 = new FlowLayout();
    public TitledBorder titledBorder2 = new TitledBorder("");
    public TitledBorder titledBorder3 = new TitledBorder("");
    public TitledBorder titledBorder4 = new TitledBorder("");
    public TitledBorder titledBorder5 = new TitledBorder("");
    public TitledBorder titledBorder6 = new TitledBorder("");
    public TitledBorder titledBorder7 = new TitledBorder("");
    public TitledBorder titledBorder8 = new TitledBorder("");
    public TitledBorder titledBorder9 = new TitledBorder("");
    public TitledBorder titledBorder10 = new TitledBorder("");
    public TitledBorder titledBorder11 = new TitledBorder("");
    public boolean isOnline = false;
    JPanel inputs = new JPanel();
    JPanel others = new JPanel();
    JPanel commands = new JPanel();
    XYLayout xYLayout1 = new XYLayout();
    XYLayout xYLayout2 = new XYLayout();
    XYLayout xYLayout3 = new XYLayout();
    JLabel jLabel1 = new JLabel();
    JTextField txtTBFL = new JTextField();
    JButton btnBeginSim = new JButton();
    JButton btnDrawXY = new JButton();
    BorderLayout borderLayout2 = new BorderLayout();
    JButton BtnStopSim = new JButton();
    JButton BtnReset = new JButton();
    JTextField jTextField2 = new JTextField();
    JLabel jLabel2 = new JLabel();
    JTextField txtET = new JTextField();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JTextField txtVMass = new JTextField();
    JTextField txtInitSpeed = new JTextField();
    public BrakingUI() {
        Samples.ui = this;
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        contentPane.setLayout(borderLayout2);
        this.setResizable(false);
        setSize(new Dimension(800, 600));
        setTitle("General Vehicle Braking Simulation");
        inputs.setLayout(xYLayout1);
        commands.setLayout(xYLayout2);
        others.setLayout(xYLayout3);
        jLabel1.setText("max Braking Torque");
        txtTBFL.setPreferredSize(new Dimension(60, 21));
        txtTBFL.setText("jTextField1");
        btnBeginSim.setText("update info");
        btnBeginSim.addActionListener(new BrakingUI_btnBeginSim_actionAdapter(this));
        btnDrawXY.setText("draw XY diagram");
        btnDrawXY.addActionListener(new BrakingUI_jButton2_actionAdapter(this));
        //inputs.setBackground(new Color(212, 208, 202));
        inputs.setPreferredSize(new Dimension(67, 290));
        commands.setBackground(Color.orange);
        commands.setPreferredSize(new Dimension(375, 70));
        others.setBackground(UIManager.getColor(
                "InternalFrame.activeTitleGradient"));
        others.setPreferredSize(new Dimension(73, 210));
        BtnStopSim.setText("stop simulation");
        BtnStopSim.addActionListener(new BrakingUI_BtnStopSim_actionAdapter(this));
        BtnReset.setText("reset values");
        BtnReset.addActionListener(new BrakingUI_BtnReset_actionAdapter(this));
        jTextField2.setText("jTextField2");
        jLabel2.setText("Max Engine Torque");
        txtET.setText("jTextField3");
        txtET.addActionListener(new BrakingUI_jTextField3_actionAdapter(this));
        jLabel3.setText("Vehicle Mass");
        jLabel4.setText("Initial Speed");
        txtVMass.setText("jTextField1");
        txtInitSpeed.setText("jTextField1");
        jLabel5.setText("Max Braking Torque");
        jLabel6.setText("Max Braking Torque");
        jLabel7.setText("Max Braking Torque");
        txtTBFR.setText("jTextField1");
        txtTBBR.setText("jTextField3");
        txtTBBL.setText("jTextField4");
        jLabel8.setText("max steer angle (rad)");
        jLabel9.setText("front right wheel");
        jLabel10.setText("back right wheel");
        jLabel11.setText("back left wheel");
        jLabel12.setText("front left wheel");
        jLabel13.setText("max steer angles(rad)");
        txtSteerFL.setText("jTextField1");
        jLabel14.setText("front right wheel");
        txtSteerFR.setText("jTextField3");
        jLabel16.setText("maxSteerTime (s.)");
        labeli.setText("steer after (s.)");
        txtMaxSteer.setText("jTextField1");
        txtSteerAfter.setText("jTextField3");
        jLabel17.setText("time elpsed");
        jLabel18.setText(" distance");
        txtTimeElapsed.setText("jTextField1");
        txtXDistance.setText("jTextField3");
        btnBeginSim.addActionListener(new BrakingUI_btnBeginSim_actionAdapter(this));
        jLabel19.setText("Spring.K(coefficient)");
        jLabel20.setText("Spring.Q ( damping)");
        length.setToolTipText("");
        length.setText("SpringLength");
        txtSpringCo.setText("jTextField1");
        txtSpringDamp.setText("jTextField1");
        txtSpringLength.setText("jTextField1");
        BtnXTime.setText("draw X-TIME");
        BtnXTime.addActionListener(new BrakingUI_jButton1_actionAdapter(this));
        BtnResetDraw.setText("reset drawing data");
        BtnResetDraw.addActionListener(new BrakingUI_BtnResetDraw_actionAdapter(this));
        jLabel21.setToolTipText("");
        jLabel21.setText("engine torque");
        txtEngineTorque.setText("jTextField1");
        jLabel15.setText("front left wheel");
        checkEnableABS.setText("ABS");
        checkEnableABS.addActionListener(new
                                         BrakingUI_checkEnableABS_actionAdapter(this));
        checkEnableOnline.setText("online");
        checkEnableOnline.addActionListener(new
                                            BrakingUI_checkEnableOnline_actionAdapter(this));
        btnDrawYTime.setText("draw Y-Time");
        btnDrawYTime.addActionListener(new BrakingUI_btnDrawYTime_actionAdapter(this));
        btnDrawXZ.setText("draw X-Z");
        btnDrawXZ.addActionListener(new BrakingUI_btnDrawXZ_actionAdapter(this));
        checkOptimum.setText("optimum ( no display)");
        jLabel22.setText("average one loop execution time(millis)");
        txtExecTime.setText("jTextField1");
        contentPane.setPreferredSize(new Dimension(600, 600));
        jLabel23.setText("speed");
        jLabel24.setText("pos");
        lblSpeed.setText("jLabel25");
        lblPos.setText("jLabel25");
        checkEnableInfo.setText("enable information ");
        btnDrawSlipTimeFL.setText("draw sliip-time (FLWheel)");
        btnDrawSlipTimeFL.addActionListener(new
                                            BrakingUI_btnDrawSlipTimeFL_actionAdapter(this));
        btnDrawSlipTractiveFL.setToolTipText("");
        btnDrawSlipTractiveFL.setText("draw slip-tractive(FLWheel)");
        btnDrawSlipTractiveFL.addActionListener(new
                                                BrakingUI_btnDrawSlipTractiveFL_actionAdapter(this));
        btnDrawSlipTractiveFR.setToolTipText("");
        btnDrawSlipTractiveFR.setText("draw slip-tractive(FRWheel)");
        btnDrawSlipTractiveFR.addActionListener(new
                                                BrakingUI_btnDrawSlipTractiveFR_actionAdapter(this));
        btnDrawSlipTimeFR.setText("draw sliip-time (FRWheel)");
        btnDrawSlipTimeFR.addActionListener(new
                                            BrakingUI_btnDrawSlipTimeFR_actionAdapter(this));
        btnDrawSlipTractiveBL.setToolTipText("");
        btnDrawSlipTractiveBL.setText("draw slip-tractive(BLWheel)");
        btnDrawSlipTractiveBL.addActionListener(new
                                                BrakingUI_btnDrawSlipTractiveBL_actionAdapter(this));
        btnDrawSlipTimeBL.setToolTipText("");
        btnDrawSlipTimeBL.setText("draw sliip-time (BLWheel)");
        btnDrawSlipTimeBL.addActionListener(new
                                            BrakingUI_btnDrawSlipTimeBL_actionAdapter(this));
        btnDrawSlipTractiveBR.setToolTipText("");
        btnDrawSlipTractiveBR.setText("draw slip-tractive(BRWheel)");
        btnDrawSlipTractiveBR.addActionListener(new
                                                BrakingUI_btnDrawSlipTractiveBR_actionAdapter(this));
        btnDrawSlipTimeBR.setText("draw sliip-time (BRWheel)");
        btnDrawSlipTimeBR.addActionListener(new
                                            BrakingUI_btnDrawSlipTimeBR_actionAdapter(this));
        btnDrawSlipTractiveLatFL.setToolTipText("");
        btnDrawSlipTractiveLatFL.setText("draw slip-tractiveLat(FLWheel)");
        btnDrawSlipTractiveLatFL.addActionListener(new
                BrakingUI_btnDrawSlipTractiveLatFL_actionAdapter(this));
        btnDrawSlipTractiveLatFR.setToolTipText("");
        btnDrawSlipTractiveLatFR.setText("draw slip-tractiveLat(FRWheel)");
        btnDrawSlipTractiveLatFR.addActionListener(new
                BrakingUI_btnDrawSlipTractiveLatFR_actionAdapter(this));
        btnDrawSlipTractiveLatBL.setToolTipText("");
        btnDrawSlipTractiveLatBL.setText("draw slip-tractiveLat(BLWheel)");
        btnDrawSlipTractiveLatBL.addActionListener(new
                BrakingUI_btnDrawSlipTractiveLatBL_actionAdapter(this));
        btnDrawSlipTractiveLatBR.setToolTipText("");
        btnDrawSlipTractiveLatBR.setText("draw slip-tractiveLat(BRWheel)");
        btnDrawSlipTractiveLatBR.addActionListener(new
                BrakingUI_btnDrawSlipTractiveLatBR_actionAdapter(this));
        comSamples.setPreferredSize(new Dimension(100, 19));
      //  comSamples.setEditor(null);
        comSamples.addActionListener(new BrakingUI_comSamples_actionAdapter(this));
        txtTBBL.addActionListener(new BrakingUI_txtTBBL_actionAdapter(this));
        jLabel25.setText("select sample");
        jLabel26.setText("select option");
        jLabel27.setFont(new java.awt.Font("Dialog", Font.BOLD | Font.ITALIC,
                                           14));
        jLabel27.setToolTipText("");
        jLabel27.setText("output Information");
        checkNew.setText("enable backbrake");
        checkNew.addActionListener(new BrakingUI_checkNew_actionAdapter(this));
        contentPane.add(inputs, java.awt.BorderLayout.NORTH);
        contentPane.add(commands, java.awt.BorderLayout.CENTER);
        commands.add(jLabel27, new XYConstraints(314, 22, 200, 33));
        contentPane.add(others, java.awt.BorderLayout.SOUTH);
        inputs.add(jLabel15, new XYConstraints(8, 8, -1, -1));
        inputs.add(jLabel1, new XYConstraints(8, 22, -1, 17));
        inputs.add(jLabel9, new XYConstraints(8, 43, 93, -1));
        initVals();
        inputs.add(jLabel5, new XYConstraints(8, 57, -1, -1));
        inputs.add(jLabel10, new XYConstraints(7, 78, 98, -1));
        inputs.add(jLabel6, new XYConstraints(8, 92, -1, 16));
        inputs.add(jLabel11, new XYConstraints(8, 113, 86, -1));
        inputs.add(jLabel7, new XYConstraints(8, 127, -1, -1));
        inputs.add(jLabel12, new XYConstraints(8, 148, 89, -1));
        inputs.add(jLabel13, new XYConstraints(8, 162, -1, -1));
        inputs.add(jLabel14, new XYConstraints(7, 184, -1, -1));
        inputs.add(txtTBBR, new XYConstraints(149, 80, 64, 24));
        inputs.add(txtTBFL, new XYConstraints(149, 10, 64, 24));
        inputs.add(txtTBFR, new XYConstraints(149, 45, 64, 24));
        inputs.add(txtTBBL, new XYConstraints(149, 115, 64, 24));
        inputs.add(txtSteerFL, new XYConstraints(149, 150, 64, 24));
        inputs.add(txtSteerFR, new XYConstraints(149, 185, 64, 24));
        inputs.add(jLabel8, new XYConstraints(8, 198, 125, 14));
        inputs.add(length, new XYConstraints(257, 223, 87, -1));
        inputs.add(jLabel3, new XYConstraints(257, 42, 92, 16));
        inputs.add(jLabel21, new XYConstraints(257, 256, 80, -1));
        inputs.add(txtSpringLength, new XYConstraints(383, 220, 64, 24));
        inputs.add(txtEngineTorque, new XYConstraints(383, 250, 64, 24));
        inputs.add(txtET, new XYConstraints(383, 10, 64, 24));
        inputs.add(txtVMass, new XYConstraints(383, 40, 64, 24));
        inputs.add(txtInitSpeed, new XYConstraints(383, 70, 64, 24));
        inputs.add(txtMaxSteer, new XYConstraints(383, 100, 64, 24));
        inputs.add(txtSteerAfter, new XYConstraints(383, 130, 64, 24));
        inputs.add(txtSpringCo, new XYConstraints(383, 160, 64, 24));
        inputs.add(txtSpringDamp, new XYConstraints(383, 190, 64, 24));
        inputs.add(jLabel2, new XYConstraints(257, 14, 116, -1));
        inputs.add(jLabel4, new XYConstraints(257, 74, 100, -1));
        inputs.add(jLabel16, new XYConstraints(257, 104, 114, -1));
        inputs.add(labeli, new XYConstraints(257, 132, 107, -1));
        inputs.add(jLabel19, new XYConstraints(257, 164, 120, -1));
        inputs.add(jLabel20, new XYConstraints(257, 193, 120, -1));
        inputs.add(checkEnableABS, new XYConstraints(580, 37, 113, -1));
        inputs.add(checkEnableOnline, new XYConstraints(580, 60, 113, -1));
        inputs.add(checkOptimum, new XYConstraints(580, 83, 148, -1));
        inputs.add(checkEnableInfo, new XYConstraints(580, 106, 148, -1));
        inputs.add(comSamples, new XYConstraints(580, 130, 148, -1));
        inputs.add(jLabel26, new XYConstraints(485, 18, 83, 23));
        inputs.add(jLabel25, new XYConstraints(485, 128, 92, 19));
        comSamples.addItem("sample1");
        comSamples.addItem("sample2");
        comSamples.addItem("sample3");
        comSamples.addItem("sample4");
        comSamples.addItem("sample5");
        comSamples.addItem("sample6");
        comSamples.addItem("sample7");
        comSamples.setSelectedIndex(1);
        inputs.add(btnBeginSim, new XYConstraints(485, 165, 243, -1));
        inputs.add(BtnReset, new XYConstraints(485, 190, 243, -1));
        inputs.add(BtnStopSim, new XYConstraints(485, 215, 243, -1));
        inputs.add(checkNew, new XYConstraints(579, 16, -1, -1));
        others.add(jLabel17, new XYConstraints(472, 12, -1, -1));
        others.add(btnDrawXY, new XYConstraints(8, 2, 209, -1));
        others.add(BtnXTime, new XYConstraints(8, 25, 209, -1));
        others.add(btnDrawYTime, new XYConstraints(8, 49, 209, -1));
        others.add(btnDrawXZ, new XYConstraints(8, 73, 209, -1));
        others.add(jLabel18, new XYConstraints(472, 37, -1, -1));
        others.add(jLabel24, new XYConstraints(8, 194, -1, -1));
        others.add(lblPos, new XYConstraints(44, 197, -1, -1));
        others.add(btnDrawSlipTractiveBR, new XYConstraints(220, 145, 163, -1));
        others.add(btnDrawSlipTractiveFR, new XYConstraints(220, 97, 163, -1));
        others.add(btnDrawSlipTractiveBL, new XYConstraints(220, 121, 163, -1));
        others.add(btnDrawSlipTractiveLatFL, new XYConstraints(387, 97, -1, -1));
        others.add(btnDrawSlipTractiveLatFR, new XYConstraints(387, 121, -1, -1));
        others.add(btnDrawSlipTractiveLatBL, new XYConstraints(387, 145, -1, -1));
        others.add(lblSpeed, new XYConstraints(529, 195, -1, -1));
        others.add(jLabel23, new XYConstraints(471, 193, -1, -1));
        others.add(txtTimeElapsed, new XYConstraints(629, 6, 64, 24));
        others.add(txtXDistance, new XYConstraints(629, 33, 64, 24));
        others.add(txtExecTime, new XYConstraints(629, 60, 64, 26));
        others.add(jLabel22, new XYConstraints(345, 69, 212, -1));
        others.add(btnDrawSlipTimeFL, new XYConstraints(8, 97, 209, -1));
        others.add(btnDrawSlipTimeFR, new XYConstraints(8, 121, 209, -1));
        others.add(btnDrawSlipTimeBL, new XYConstraints(8, 145, 209, -1));
        others.add(btnDrawSlipTimeBR, new XYConstraints(8, 169, 209, -1));
        others.add(btnDrawSlipTractiveLatBR, new XYConstraints(387, 169, -1, -1));
        others.add(BtnResetDraw, new XYConstraints(222, 5, 209, -1));
    }

    public void jTextField3_actionPerformed(ActionEvent e) {

    }

    public void BtnReset_actionPerformed(ActionEvent e) {
        initVals();
    }

    //reset to the default values
    public void initVals() {
        SimVals.reset();
        setUIFromSimVals();
    }

    //get values from user interface and set them into the SimVals
    public void updateVals() {
        //  SimVals.reset();
        SimVals.initialSpeed = Double.parseDouble(txtInitSpeed.getText());
        SimVals.maxEngineTorque = Double.parseDouble(txtET.getText());
        SimVals.vehicleMass = Double.parseDouble(txtVMass.getText());

        SimVals.maxBrakeTorqueBL = Double.parseDouble(txtTBBL.getText());
        SimVals.maxBrakeTorqueBR = Double.parseDouble(txtTBBR.getText());
        SimVals.maxBrakeTorqueFL = Double.parseDouble(txtTBFL.getText());
        SimVals.maxBrakeTorqueFR = Double.parseDouble(txtTBFR.getText());

        SimVals.maxSteerFL = Double.parseDouble(txtSteerFL.getText());
        SimVals.maxSteerFR = Double.parseDouble(txtSteerFR.getText());

        SimVals.beginSteerAfter = Double.parseDouble(txtSteerAfter.getText());
        SimVals.maxSteerTime = Double.parseDouble(txtMaxSteer.getText());

        SimVals.springCoeff = Double.parseDouble(txtSpringCo.getText());
        SimVals.springDamping = Double.parseDouble(txtSpringDamp.getText());
        SimVals.springLength = Double.parseDouble(txtSpringLength.getText());

        SimVals.engineTorque = Double.parseDouble(txtEngineTorque.getText());

        SimVals.isOptimum = checkOptimum.isSelected();
        SimVals.enableInfo = checkEnableInfo.isSelected();
    }

    public void setUIFromSimVals() {
        txtET.setText("" + SimVals.maxEngineTorque);
        txtInitSpeed.setText("" + SimVals.initialSpeed);

        txtTBFL.setText("" + SimVals.maxBrakeTorqueFL);
        txtTBFR.setText("" + SimVals.maxBrakeTorqueFR);
        txtTBBR.setText("" + SimVals.maxBrakeTorqueBR);
        txtTBBL.setText("" + SimVals.maxBrakeTorqueBL);

        txtSteerFL.setText("" + SimVals.maxSteerFL);
        txtSteerFR.setText("" + SimVals.maxSteerFR);

        txtVMass.setText("" + SimVals.vehicleMass);

        txtSteerAfter.setText("" + SimVals.beginSteerAfter);
        txtMaxSteer.setText("" + SimVals.maxSteerTime);

        txtSpringCo.setText("" + SimVals.springCoeff);
        txtSpringDamp.setText("" + SimVals.springDamping);
        txtSpringLength.setText("" + SimVals.springLength);

        txtEngineTorque.setText("" + SimVals.engineTorque);

        txtTimeElapsed.setText("0");
        txtXDistance.setText("0");
        txtExecTime.setText("0");
        lblPos.setText("0,0,0");
        lblSpeed.setText("0");

    }

    public SimulationCore sim = null;
    JLabel jLabel5 = new JLabel();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JTextField txtTBFR = new JTextField();
    JTextField txtTBBR = new JTextField();
    JTextField txtTBBL = new JTextField();
    JLabel jLabel8 = new JLabel();
    JLabel jLabel9 = new JLabel();
    JLabel jLabel10 = new JLabel();
    JLabel jLabel11 = new JLabel();
    JLabel jLabel12 = new JLabel();
    JLabel jLabel13 = new JLabel();
    JTextField txtSteerFL = new JTextField();
    JLabel jLabel14 = new JLabel();
    JTextField txtSteerFR = new JTextField();
    JLabel jLabel16 = new JLabel();
    JLabel labeli = new JLabel();
    JTextField txtMaxSteer = new JTextField();
    JTextField txtSteerAfter = new JTextField();
    JLabel jLabel17 = new JLabel();
    JLabel jLabel18 = new JLabel();
    public static JTextField txtTimeElapsed = new JTextField();
    public static JTextField txtXDistance = new JTextField();


    JLabel jLabel19 = new JLabel();
    JLabel jLabel20 = new JLabel();
    JLabel length = new JLabel();
    JTextField txtSpringCo = new JTextField();
    JTextField txtSpringDamp = new JTextField();
    JTextField txtSpringLength = new JTextField();
    static int i = 0;
    JButton BtnXTime = new JButton();
    JButton BtnResetDraw = new JButton();
    Calendar prevTime = Calendar.getInstance();
    JLabel jLabel21 = new JLabel();
    JTextField txtEngineTorque = new JTextField();
    JLabel jLabel15 = new JLabel();
    JCheckBox checkEnableABS = new JCheckBox();
    JCheckBox checkEnableOnline = new JCheckBox();
    JButton btnDrawYTime = new JButton();
    JButton btnDrawXZ = new JButton();
    JCheckBox checkOptimum = new JCheckBox();
    JLabel jLabel22 = new JLabel();
    public static JTextField txtExecTime = new JTextField();
    JLabel jLabel23 = new JLabel();
    JLabel jLabel24 = new JLabel();
    public static JLabel lblSpeed = new JLabel();
    public static JLabel lblPos = new JLabel();
    JCheckBox checkEnableInfo = new JCheckBox();
    JButton btnDrawSlipTimeFL = new JButton();
    JButton btnDrawSlipTractiveFL = new JButton();
    JButton btnDrawSlipTractiveFR = new JButton();
    JButton btnDrawSlipTimeFR = new JButton();
    JButton btnDrawSlipTractiveBL = new JButton();
    JButton btnDrawSlipTimeBL = new JButton();
    JButton btnDrawSlipTractiveBR = new JButton();
    JButton btnDrawSlipTimeBR = new JButton();
    JButton btnDrawSlipTractiveLatFL = new JButton();
    JButton btnDrawSlipTractiveLatFR = new JButton();
    JButton btnDrawSlipTractiveLatBL = new JButton();
    JButton btnDrawSlipTractiveLatBR = new JButton();
    public JComboBox comSamples = new JComboBox();
    JLabel jLabel25 = new JLabel();
    JLabel jLabel26 = new JLabel();
    JLabel jLabel27 = new JLabel();
    JCheckBox checkNew = new JCheckBox();
    public void btnBeginSim_actionPerformed(ActionEvent e) {
        Calendar curTime = Calendar.getInstance();

        if ((curTime.getTimeInMillis() - prevTime.getTimeInMillis()) > 1000) {
            SimVals.resetStateVars();
            updateVals();
            if (SimulateList2.running)
                SimulateList2.running = false;
            SimVals.running = false;
            sim = new SimulationCore();
            SimVals.running = true;
            sim.start();
            prevTime = curTime;
        }
    }

    public void BtnStopSim_actionPerformed(ActionEvent e) {
        //   SimulationCore.running=false;
        SimulateList2.running = false;
        SimVals.running = false;
    }

    /**
     * returns x max and min in [0], min max y in [1]
     * @param list ArrayList
     * @return PointDouble[]
     */
    public PointDouble[] getListMaxMin(ArrayList list) {
        PointDouble temp;
        PointDouble val[] = new PointDouble[2];

        val[0] = new PointDouble( +9999999, -9999999);
        val[1] = new PointDouble( +9999999, -9999999);

        for (int i = 0; i < list.size(); i++) {
            temp = (PointDouble) list.get(i);
            if (temp.x == -9999999)
                continue;
            //return min and max of X
            if (temp.x < val[0].x)
                val[0].x = temp.x;
            if (temp.x > val[0].y)
                val[0].y = temp.x;

            //return min and max of y
            if (temp.y < val[1].x)
                val[1].x = temp.y;
            if (temp.y > val[1].y)
                val[1].y = temp.y;

        }

        return val;
    }

    public void jButton2_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listXY);
        PointDouble point[] = getListMaxMin(SimVals.listXY);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the X-Y path";

        d.info.xLabel = "X=" + point[0].y;
        d.info.yLabel = "Y=" + point[1].y;
//        if (!jCheckDotDraw.isSelected())
//            d.info.enableDotDraw = false;
        d.setSize(400, 400);
        d.setVisible(true);

    }

    public void jButton1_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listXTime);
        PointDouble point[] = getListMaxMin(SimVals.listXTime);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the X-Y path";

        d.info.xLabel = "Time=" + point[0].y;
        d.info.yLabel = "X=" + point[1].y;
//        if (!jCheckDotDraw.isSelected())
//            d.info.enableDotDraw = false;
        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void BtnResetDraw_actionPerformed(ActionEvent e) {
        SimVals.resetLists();
    }

    public void checkEnableABS_actionPerformed(ActionEvent e) {
        SimVals.enableABS = checkEnableABS.isSelected();
    }

    public void checkEnableOnline_actionPerformed(ActionEvent e) {
        SimVals.enableOnline = checkEnableOnline.isSelected();
    }

    public void btnDrawYTime_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listYTime);
        PointDouble point[] = getListMaxMin(SimVals.listYTime);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the X-Time path ( suspension)";

        d.info.xLabel = "Time=" + point[0].y;
        d.info.yLabel = "Y=" + point[1].y;
//        if (!jCheckDotDraw.isSelected())
//            d.info.enableDotDraw = false;
        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawXZ_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listXZ);
        PointDouble point[] = getListMaxMin(SimVals.listXZ);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the X-Z path ( movement)";

        d.info.xLabel = "X=" + point[0].y;
        d.info.yLabel = "Z=" + point[1].y;
//        if (!jCheckDotDraw.isSelected())
//            d.info.enableDotDraw = false;
        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTimeFL_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTimeFL);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTimeFL);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the Time Slip relation for the left front wheel";

        d.info.xLabel = "Time=" + point[0].y;
        d.info.yLabel = "Slip=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTimeFR_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTimeFR);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTimeFR);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the Time Slip relation for the right front wheel";

        d.info.xLabel = "Time=" + point[0].y;
        d.info.yLabel = "Slip=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTimeBL_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTimeBL);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTimeBL);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the Time Slip relation for the back left wheel";

        d.info.xLabel = "Time=" + point[0].y;
        d.info.yLabel = "Slip=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTimeBR_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTimeBR);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTimeBR);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the Time Slip relation for the back right wheel";

        d.info.xLabel = "Time=" + point[0].y;
        d.info.yLabel = "Slip=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveFL_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveFL);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveFL);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the tractive force Vs Slip relation for the front left wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "Longitudinal Tractive force=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveFR_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveFR);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveFR);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the tractive force Vs Slip relation for the front right wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "Longitudinal Tractive force=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveBL_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveBL);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveBL);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the tractive force Vs Slip relation for the back left wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "Longitudinal Tractive force=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveBR_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveBR);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveBR);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the tractive force Vs Slip relation for the back right wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "Longitudinal Tractive force=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveLatFL_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveLatFL);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveLatFL);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the lateral tractive force Vs Slip relation for the front left wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "LateralForce=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveLatFR_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveLatFR);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveLatFR);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the lateral tractive force Vs Slip relation for the front right wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "LateralForce=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveLatBL_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveLatBL);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveLatBL);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the lateral tractive force Vs Slip relation for the back left wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "LateralForce=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

    public void btnDrawSlipTractiveLatBR_actionPerformed(ActionEvent e) {
        Diagrams d = new Diagrams(SimVals.listSlipTractiveLatBR);
        PointDouble point[] = getListMaxMin(SimVals.listSlipTractiveLatBR);
        d.info.maxY = point[1].x;
        d.info.minY = point[1].y;
        d.info.minX = point[0].x;
        d.info.maxX = point[0].y;

        d.info.centerLabel = "(" + point[0].x + "," + point[1].x + ")";
        d.info.description =
                "this diagram shows the lateral tractive force Vs Slip relation for the back right wheel";

        d.info.xLabel = "Slip=" + point[0].y;
        d.info.yLabel = "LateralForce=" + point[1].y;

        d.setSize(400, 400);
        d.setVisible(true);
    }

  /*  public void comSamples_actionPerformed(ActionEvent e) {
        /*
        try{
            String choice=(String)comSamples.getSelectedItem();

           System.out.println(choice);
     //      Samples.selectSample((String) comSamples.getSelectedItem());
    // Samples.selectSample("sample1");
         }
         catch(Exception ex){
             System.out.println("selection error");
         }
JComboBox cb = (JComboBox)e.getSource();
String petName = (String)comSamples.getSelectedItem();
        System.out.println(petName);
    }
*/
    public void btnAdvanceInput_actionPerformed(ActionEvent e) {
        AdvancedInput input=new AdvancedInput();
        input.setVisible(true);
    }

    public void txtTBBL_actionPerformed(ActionEvent e) {

    }

    public void checkNew_actionPerformed(ActionEvent e) {
        SimVals.enableNew=checkNew.isSelected();
    }
}


class BrakingUI_checkNew_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_checkNew_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checkNew_actionPerformed(e);
    }
}


class BrakingUI_txtTBBL_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_txtTBBL_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.txtTBBL_actionPerformed(e);
    }
}


class BrakingUI_comSamples_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_comSamples_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        String choice=(String)adaptee.comSamples.getSelectedItem();

              System.out.println(choice);
              Samples.selectSample(choice);
       // Samples.selectSample("sample1");

            //   adaptee.comSamples_actionPerformed(e);
    }
}

/*
class BrakingUI_comSamples_itemAdapter implements ItemListener {
    private BrakingUI adaptee;
    BrakingUI_comSamples_itemAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.comSamples_itemStateChanged(e);
    }
}
*/

class BrakingUI_btnDrawSlipTractiveLatBR_actionAdapter implements
        ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveLatBR_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveLatBR_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTractiveLatBL_actionAdapter implements
        ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveLatBL_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveLatBL_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTractiveLatFR_actionAdapter implements
        ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveLatFR_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveLatFR_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTractiveLatFL_actionAdapter implements
        ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveLatFL_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveLatFL_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTractiveBR_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveBR_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveBR_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTractiveBL_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveBL_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveBL_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTractiveFR_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveFR_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveFR_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTractiveFL_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTractiveFL_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTractiveFL_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTimeBR_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTimeBR_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTimeBR_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTimeBL_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTimeBL_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTimeBL_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTimeFR_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTimeFR_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTimeFR_actionPerformed(e);
    }
}


class BrakingUI_btnDrawSlipTimeFL_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawSlipTimeFL_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawSlipTimeFL_actionPerformed(e);
    }
}


class BrakingUI_btnDrawXZ_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawXZ_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawXZ_actionPerformed(e);
    }
}


class BrakingUI_btnDrawYTime_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnDrawYTime_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDrawYTime_actionPerformed(e);
    }
}


class BrakingUI_checkEnableOnline_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_checkEnableOnline_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checkEnableOnline_actionPerformed(e);
    }
}


class BrakingUI_checkEnableABS_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_checkEnableABS_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checkEnableABS_actionPerformed(e);
    }
}


class BrakingUI_BtnResetDraw_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_BtnResetDraw_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.BtnResetDraw_actionPerformed(e);
    }
}


class BrakingUI_jButton1_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_jButton1_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}


class BrakingUI_jButton2_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_jButton2_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.jButton2_actionPerformed(e);
    }
}


class BrakingUI_BtnStopSim_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_BtnStopSim_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.BtnStopSim_actionPerformed(e);
    }
}


class BrakingUI_btnBeginSim_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_btnBeginSim_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.btnBeginSim_actionPerformed(e);
    }
}


class BrakingUI_BtnReset_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_BtnReset_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.BtnReset_actionPerformed(e);
    }
}


class BrakingUI_jTextField3_actionAdapter implements ActionListener {
    private BrakingUI adaptee;
    BrakingUI_jTextField3_actionAdapter(BrakingUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jTextField3_actionPerformed(e);
    }
}
