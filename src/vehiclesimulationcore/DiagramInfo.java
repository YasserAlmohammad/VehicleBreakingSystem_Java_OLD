package vehiclesimulationcore;


import javax.swing.JFrame;
import java.awt.Dimension;
/**
 *
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * to hold information about the diagram, like labels, Min, Max vals...
 * you can set the fields or use thier defualt values.
 */
public class DiagramInfo{
    public String xLabel;//xLabel: String valuse to lable the Horizontal axis
    public String yLabel;//yLabel: String valuse to lable the Vertical axis
    public String description;  //description of the Diagram , drawn below the axis
    public String centerLabel; //like (0,0)

    public double minY,maxY; //min,max value of the original drawn points, used to
    public double minX,maxX; //min,max value of the original drawn points, used to
    //scale into the screen coordinates, minX=0, maxX=list.Size()
    private int x1,y1,x2,y2; //onFrame coordinates for rectangle corners
    public int textMargin; //margin height to draw the text into
    private int xAxisLen,yAxisLen; //constants for better calculations
    //coordinates are controlled
    public boolean enableDotDraw;
    public DiagramInfo(){
        init();
    }

    public DiagramInfo(JFrame frame){
        init();
        setFrame(frame);

    }

    /**
     * window coords of the target to draw within
     * @param x1 int
     * @param y1 int
     * @param x2 int
     * @param y2 int
     */
    public void setCoordinates(int x1,int y1, int x2, int y2){
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
        xAxisLen=x2-x1-2*textMargin;
        yAxisLen=y2-y1-2*textMargin;
    }

    /**
     *  just to set the dimensions on the specified frame window using another way
     * @param frame JFrame
     */
    public void setFrame(JFrame frame){
        Dimension dim=frame.getSize();
        setCoordinates(0,10,dim.width,dim.height);
    }

    /**
     * sets default values for all fields
     */
    public void init(){
        xLabel="X";
        yLabel="Y";
        centerLabel="(0,0)";
        description="this diagram shows the relation between X and Y";
        minY=0;
        maxY=100;
        minX=0;
        maxX=100;
        textMargin=40;
        setCoordinates(0,0,150,150);
        enableDotDraw=true;
    }

    /**
     * accessors
     * @return int
     */
    public int getXAxisLen(){
                return xAxisLen;
    }
    public int getYAxisLen(){
        return yAxisLen;
    }
    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

}
