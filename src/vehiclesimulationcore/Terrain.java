 package vehiclesimulationcore;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;

import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;
import javax.vecmath.Color3f;
import javax.media.j3d.Billboard;
import javax.media.j3d.Node;
import javax.media.j3d.BranchGroup;
import java.util.Enumeration;
import com.sun.j3d.loaders.Scene;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *<p>
 * Terrain class holds multiple runtime created worlds, environments, for more realistic
 * simulation.
 *<p>
 * one terrain is loaded on demand from model file, and others are programatically generated
 * the Grid terrain is a line cross grid with 1 meter distance between each line
 * <p>
 * currently terrain is a land, with few sample roads generated in 3dmax
 * <p>
 * terrain without textures is also provided for possible performance issues on some machines
 * <p>
 * all methods are static, since the sole role of this class is the generation of such objects
 */
public class Terrain {
    public Terrain() {
    }

    /**
     * grid for the emulation of movement
     * counts should be even values, distance between lines is specified
     * steps defines how many vertical or horiontal lines exists
     *
     * @param steps int         number of lines(horizontal or vertical)
     * @param distance int      distance between two grid lines
     * @return TransformGroup   put it all in one transform group
     */
    public static TransformGroup generateGrid(int steps/*even*/,int distance){
        TransformGroup grid=new TransformGroup();


        //mult 4: line=2 vertixes, double the lines for horiontal and vertical lines
        LineArray lines = new LineArray(steps*4/*vertixes*/,LineArray.COORDINATES | LineArray.COLOR_3);

        double hX=steps*distance/2; //max distance in one direction from origin
        double nextStep=0;
        final double yValue=0.0; //the difference between radius and WheelY (wheel center)
        Point3d p1=new Point3d();
        Point3d p2=new Point3d();
        Color3f lineColor=new Color3f(1,1,1); //white grid
        for(int i=0;i<steps*4;i+=8){
            ///////////////////////////the horiontal line
            //positive z direction
            p1.set(hX,yValue,nextStep*distance);
            p2.set(-hX,yValue,nextStep*distance);
            lines.setCoordinate(i, p1);
            lines.setCoordinate(i + 1, p2);
           //negative z direction
            p1.set(hX,yValue,-nextStep*distance);
            p2.set(-hX,yValue,-nextStep*distance);
            lines.setCoordinate(i+2, p1);
            lines.setCoordinate(i + 3, p2);

            //color the lines
            lines.setColor(i, lineColor);
            lines.setColor(i + 1,lineColor);
            lines.setColor(i+2, lineColor);
            lines.setColor(i + 3,lineColor);

            //////////////////////////the vertical line
            //positive x direction
            p1.set(nextStep*distance,yValue,hX);
            p2.set(nextStep*distance,yValue,-hX);
            lines.setCoordinate(i+4, p1);
            lines.setCoordinate(i + 5, p2);
           //negative x direction
            p1.set(-nextStep*distance,yValue,hX);
            p2.set(-nextStep*distance,yValue,-hX);
            lines.setCoordinate(i+6, p1);
            lines.setCoordinate(i + 7, p2);

            //color the lines
            lines.setColor(i+4, lineColor);
            lines.setColor(i + 5,lineColor);
            lines.setColor(i+6, lineColor);
            lines.setColor(i + 7,lineColor);

            ++nextStep;
        }

        grid.addChild(new Shape3D(lines));

        return grid;
    }

    /**
     * load worlds into scene:
     * terrain1 is a sample land scene with few sample streets textured proberly for a good
     * visualiation.
     * <p>
     * later if possible the world will be spheral and some conversion function will be inserted
     * to adapt without changing previous code, for now the land is simple horizonatl and
     * large enough
     */

    public static TransformGroup generateTerrain1(){
        TransformGroup terrain=Loaders.load3DS("models\\terrain1.3DS");
        return terrain;
    }

    /**
     * for performance considerations the same previous terrain is loaded but without texures
     */
    public static TransformGroup generateTerrain1NoTextures() {
        //we know first child is a branch group
        return Loaders.load3DS("models\\terrain1.3DS",false);
    }


    /**
     * one transform group to hold all transform groups of all threes
     * each tree consists of a stim which is a solid object, can be used in collision
     * and branches which are one or more transparent textures and connected to a billboard
     * a branch has two TGs one for translation and another to apply the billboard onto(rotation)
     *<p>
     * the stim is a solid geomerty and thus requires no billborading, that's why
     * they exist in a separate file
     *<p>
     * this method is loader specific: here we use the Loader3DS from starfire, which uses this as we saw:
     * we get TransformGroup by the method Loader3DS.getModel(), this TG contains one BranchGroup which
     * consists of a TG for each single object in the file, each TG has a child of Shape3D
     *
     * @return TransformGroup
     */

    public static TransformGroup generateTrees(){
        TransformGroup trees=new TransformGroup();
        // a tree every 25 meters
        Transform3D stimTransform=new Transform3D();
        Transform3D branchTransform=new Transform3D();
        Vector3d stimPos=new Vector3d(0,0,0);
        Vector3d branchPos=new Vector3d(0,0,0);
        for(int i=0;i<20;i++){
            TransformGroup treeTrans = new TransformGroup();
            TransformGroup stim=Loaders.load3DS("models\\stim.3ds");
            TransformGroup treeRot=Loaders.load3DS("models\\tree.3DS");

            treeRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

            stimPos.set(250 - i * 25, 0, -6);
            branchPos.set(250 - i * 25, 2.55, -6);

            branchTransform.setTranslation(branchPos);
            treeTrans.setTransform(branchTransform);
            stimTransform.setTranslation(stimPos);
            stim.setTransform(stimTransform);

            Billboard bill = new Billboard(treeRot);
            bill.setSchedulingBounds(World.DBS);
            trees.addChild(bill);

            treeTrans.addChild(treeRot);
            trees.addChild(stim);
            trees.addChild(treeTrans);
        }

        for(int i=0;i<20;i++){
          TransformGroup treeTrans = new TransformGroup();
          TransformGroup stim=Loaders.load3DS("models\\stim.3ds");
          TransformGroup treeRot=Loaders.load3DS("models\\tree.3DS");

          treeRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

          stimPos.set(250 - i * 25, 0, 28);
          branchPos.set(250 - i * 25, 2.55, 28);

          branchTransform.setTranslation(branchPos);
          treeTrans.setTransform(branchTransform);
          stimTransform.setTranslation(stimPos);
          stim.setTransform(stimTransform);

          Billboard bill = new Billboard(treeRot);
          bill.setSchedulingBounds(World.DBS);
          trees.addChild(bill);

          treeTrans.addChild(treeRot);
          trees.addChild(stim);
          trees.addChild(treeTrans);
      }


        return trees;
    }
    /*
    public static TransformGroup generateTrees(){
        TransformGroup trees=new TransformGroup();
        //create billboards for the trees
        TransformGroup stims=Loaders.load3DS("models\\stims.3ds");
        TransformGroup treesBody=Loaders.load3DS("models\\trees.3DS");
        //each tree object in trees must be billboared alone

        BranchGroup mainBranch = (BranchGroup) treesBody.getChild(0); //1 child
        int numTrees = mainBranch.numChildren();

        for (int i = 0; i < numTrees; i++) {
            BranchGroup tree = (BranchGroup) mainBranch.getChild(i);
            TransformGroup treeRot = (TransformGroup) tree.getChild(0);//numChildren = 1
            Transform3D trans=new Transform3D();
            treeRot.getTransform(trans);
            //because we gonna lose the position trans due billboarding then we read it intp trans
            //and set it back intp treeTrans
            TransformGroup treeTrans = new TransformGroup();
            treeTrans.setTransform(trans);
            treeRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            tree.removeChild(0);

            Billboard bill = new Billboard(treeRot);
            bill.setSchedulingBounds(World.DBS);
            trees.addChild(bill);
            treeTrans.addChild(treeRot);
            trees.addChild(treeTrans);
        }
        //stims are solid geoms
        trees.addChild(stims);
        return trees;
    } */
}
