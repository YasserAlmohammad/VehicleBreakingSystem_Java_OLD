package vehiclesimulationcore;
import javax.media.j3d.TransformGroup;
import ta.aseloader.*;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.mnstarfire.loaders3d.Inspector3DS;
import com.mnstarfire.loaders3d.Loader3DS;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <hr><p>
 * Loaders class, joins multiple loader functionality, to load objects of specific
 * interest for the simulation, and to enable using this class at runtime to load
 * different models
 * <p>
 * class has staic methos for the loading operations, all functions are unified
 * to return a transform group
 *<p>
 * Currently:
 *   <p>   - .ASE loader written by  Trond Abusda 2006 trondabusdal@hotmail.com <br>
 *   <p>   -.3ds 3DSLoader:Copyright (c) 2000,2001, 2003 Starfire Research - All Rights Reserved.
 *   <p>   -.Obj Loader also the object loader coming with the Java3d package
 *
 * <p>
 */
public class Loaders {
    public Loaders() {
    }

    /**
     * loads an ASE model into a transform group, easy to use
     *
     * @param fileDir String
     * @param fileName String
     * @return TransformGroup
     */
    static TransformGroup loadASE(String fileDir,String fileName){
        AseLoader loader=new AseLoader(fileDir,fileName);
        J3DObject bodyModel= loader.getJ3DObject(1.0f, AseLoader.SHADE_CURVED);
        return bodyModel.getObject();
    }

    /**
     * load 3ds file with all available options
     *
     * @param filePath String
     * @return TransformGroup
     */
    static TransformGroup load3DS(String filePath){
        Inspector3DS loader = new Inspector3DS(filePath); // constructor

        loader.parseIt(); // process the file
        return loader.getModel();
    }

    /**
     * few options are considered when loading, including enabling/disabling textures
     *
     * @param filePath String
     * @param texture boolean
     * @return TransformGroup
     */

    static TransformGroup load3DS(String filePath,boolean texture) {
        TransformGroup model=new TransformGroup();
        try{
            Loader3DS loader = new Loader3DS(); // constructor
            if (!texture)
                loader.noTextures();
            model.addChild(loader.load(filePath).getSceneGroup());
        }
        catch(Exception ex){
        }

        return model;
    }


    /**
     * the object file loader
     *
     * @param filePath String
     * @return Scene
     */
    static Scene loadObj(String filePath){
        ObjectFile f = new ObjectFile();
        try{
            return f.load(filePath);
        }
        catch(Exception ex){

        }
        return null;
    }


}
