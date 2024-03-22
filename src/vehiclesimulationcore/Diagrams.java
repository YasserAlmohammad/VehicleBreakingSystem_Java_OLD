package vehiclesimulationcore;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import dynamics.*;
import java.util.HashSet;

/**
 *
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * to enable drawing lists of values as a diagram over a frame surface
 * <p>
 * we may put further drawing related functions inside this class
 */

class DrawingTools{

        /**
         * passing a surface to draw onto, a list containg values as Y axis, their order as X axis
         * the diagram info(lables,dims...) we draw the diagram.
         * <p>
         * diagram can be drawn once into buffered image then drawn back - optional many - to graphics
         *
         * array list of PointDouble values
         * @param g Graphics
         * @param list ArrayList  of type PointDouble
         * @param info DiagramInfo
         */
        static public void drawDiagram(Graphics g, ArrayList list, DiagramInfo info){
        //draw background color, black
        //axis, labels...
        g.setColor(Color.black);
        g.fillRect(info.getX1(),info.getY1(),info.getX2(),info.getY2());
        g.setColor(Color.green);//line color
        //draw axis
        g.drawLine(info.getX1()+info.textMargin, info.getY1()+info.textMargin, info.getX1()+info.textMargin, info.getY2()-info.textMargin);
        g.drawLine(info.getX2()-info.textMargin,info.getY2()-info.textMargin,info.getX1()+info.textMargin,info.getY2()-info.textMargin);
        //draw labels
        g.setColor(Color.white);
        g.drawString(info.centerLabel,info.getX1()+5,info.getY2()-info.textMargin);
        g.drawString(info.yLabel,info.getX1()+10,info.getY1()+info.textMargin);
        g.drawString(info.xLabel,info.getX2()-info.textMargin-30,info.getY2()-info.textMargin);
        g.drawString(info.description,info.getX1()+info.textMargin, info.getY2()-info.textMargin/2);



        double percent=0;
        double length=list.size();
        int x=0; //current point coordinates
        int y=0;
        PointDouble p=new PointDouble(0,0); //value inside a list element

        //////////////////////////draw Grid///////////////////////
        int tenth=info.getYAxisLen()/10;
        for(int i=0;i<10;i++){
            g.setColor(Color.darkGray);
            g.drawLine(info.getX1()+info.textMargin,info.getY1()+tenth*i+info.textMargin,info.getX2()-info.textMargin,info.getY1()+tenth*i+info.textMargin);
            g.setColor(Color.white);
        }
        tenth=info.getXAxisLen()/10;
        for(int i=1;i<=10;i++){
            g.setColor(Color.darkGray);
            g.drawLine(info.getX1() + tenth * i + info.textMargin,info.getY1() + info.textMargin,
                       info.getX1() + tenth * i + info.textMargin,info.getY2() - info.textMargin);
            g.setColor(Color.white);
        }
        //////////////////////////////////////////////////////
        int cluster=0;

        if(list.size()==0)
            return;
        if(SimVals.listCompsitionCount==0)
            return;

        p=(PointDouble)list.get(0);
        percent = (p.x - info.minX) / (info.maxX - info.minX);
        x = (int) (info.textMargin + percent * info.getXAxisLen());

        percent = (p.y - info.minY) / (info.maxY - info.minY);
        y = (int) ( + info.textMargin + percent * info.getYAxisLen());

        int prevX=x;
        int prevY=y;
        int vecNum=0;

        for(int i=1;i<length;i++){
                //draw one point

                p=(PointDouble)list.get(i);

                if (p.x == -9999999) {
                    g.setColor(new Color((int)((Math.random())*200+50), (int)((Math.random())*200+50),(int)(((Math.random())*200+50))));
                 ++i;
                 if(i>=length)
                     return;
                 p=(PointDouble)list.get(i);

                 percent=(p.x-info.minX)/(info.maxX-info.minX);
                x=(int)(info.textMargin+percent*info.getXAxisLen());

                percent=(p.y-info.minY)/(info.maxY-info.minY);

                y=(int)(+info.textMargin+percent*info.getYAxisLen());

                prevX=x;
                prevY=y;


                 continue;
                }



                percent=(p.x-info.minX)/(info.maxX-info.minX);
                x=(int)(info.textMargin+percent*info.getXAxisLen());

                percent=(p.y-info.minY)/(info.maxY-info.minY);

                y=(int)(+info.textMargin+percent*info.getYAxisLen());
           //     System.out.println((((int)i)%((int)SimVals.listCompsitionCount)));

//              if(drawDots)

                    g.drawLine(prevX,prevY,x,y);

           //     System.out.println("p.x,p.y="+p.x+"  "+p.y);
           //     System.out.println("x,y="+x+"  "+y);

                prevX=x;
                prevY=y;
        }

    }



}

/**
 *
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * we use it just to create a separte drawing window to be used by the previous class(DrawingTools)
 * <p>
 * before calling this frame to become visible you should set the list and DiagramInfo fields
 * then call it to become visible and it calles the drawing tools to draw on itself
 */
public class Diagrams extends JFrame {
    BorderLayout borderLayout1 = new BorderLayout();

    /**
     * this field must be set before drawing is done
     */
    public ArrayList list;

    /**
     * set this field too before drawing
     */
    public DiagramInfo info;

    public Diagrams(ArrayList list) {
        try {
            jbInit();
            this.list=list;
            info=new DiagramInfo(this);



        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        getContentPane().setLayout(borderLayout1);
        this.addWindowStateListener(new Diagrams_this_windowStateAdapter(this));
    }

    public void paint(Graphics g){
        info.setFrame(this); //due to possible size changing
        DrawingTools.drawDiagram(g,list,info);
        System.out.println("change");
    }

    public void this_windowStateChanged(WindowEvent e) {
        this.validate();
    }
}


class Diagrams_this_windowStateAdapter implements WindowStateListener {
    private Diagrams adaptee;
    Diagrams_this_windowStateAdapter(Diagrams adaptee) {
        this.adaptee = adaptee;
    }

    public void windowStateChanged(WindowEvent e) {
        adaptee.this_windowStateChanged(e);
    }
}
