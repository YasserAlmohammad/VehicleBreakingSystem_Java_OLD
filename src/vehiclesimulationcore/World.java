
package vehiclesimulationcore;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.geometry.Sphere;

import java.awt.Font;
//import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.picking.behaviors.PickRotateBehavior;

import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ta.aseloader.*;
import java.io.File;
//temp import
import java.util.LinkedList;
import javax.media.j3d.Material;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *<p>
 * World is the class that holds the VirtualUniverse and takes care of collaborating
 * all other geometry classes with independence of the program UI.
 * <p>
 * for now we set members as static to enable access from anywhere without referencing
 * besides it's easier, the simulation requires no more than one world
 */
public class World {
        /**
         * canvas must be set to a content pane from outside this class
         */
    public Canvas3D canvas = null;
    SimpleUniverse universe = null;
    public static BranchGroup scene = null;

    /**
     * two geomerties, one optimized with no textures and no transcparancy, the other with it's
     * full options, how ever both are good models from a complexity point of view
     */
    static VehicleGeom vehicleGeom = VehiclesCoords.createVehicleGeom(0);
    static VehicleGeom vehicleGeomOptimized = VehiclesCoords.createVehicleGeom(1); //code 1 for optimized

    static double worldRadius = 500; //meters
    static BoundingSphere DBS = new BoundingSphere(new Point3d(), worldRadius);
    //Default Bounding Sphere

    //Terrain objects
    public static TransformGroup gridTerrain = Terrain.generateGrid(500, 2);
    public static TransformGroup terrain1 = Terrain.generateTerrain1();
    public static TransformGroup terrain1NoTexture = Terrain.
            generateTerrain1NoTextures();

    /**
     * enable-disable features, access is done using enable/disable/switch methods
     * for accurate manupluation of the scene components
     */
    private static boolean enableGrid = false;
    private static boolean enableTexture = true;
    private static boolean enableTrees = false;
    private static boolean enableTerrain = true;
    private static boolean enableLight1 = true;
    private static boolean enableLight2 = true;
    private static boolean enableLight3 = false;
    private static boolean enableVehicleOptimizedModel = false;

    /**
     * separate branch groups to enable removal of components at runtime without the need to
     * rebuild the whole scene
     */
    static BranchGroup gridBG = new BranchGroup();
    static BranchGroup terrainBG = new BranchGroup();
    static BranchGroup terrainNoTextureBG = new BranchGroup();
    static BranchGroup vehicleBG = new BranchGroup();
    static BranchGroup vehicleBGOptimized = new BranchGroup();
    static BranchGroup treesBG = new BranchGroup();
    static BranchGroup terrain1NoTexturesBG = new BranchGroup();

    /**
     * three directional lights
     */
    static DirectionalLight l_dl1 = null;
    static DirectionalLight l_dl2 = null;
    static DirectionalLight l_dl3 = null;

    /**
     * during construction we create the canvas, the universe, create the scence graph and compile
     * it.
     *<p>
     * creation of single components of the scene graph is done once, and put as static members
     * for now.
     * <p>
     * each branch group is compiled alone
     */
    public World() {
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        scene = new BranchGroup();
        //load vehicle
        //load road
        //load terrain
        createSceneGraph();
        //  scene.addChild(gridTerrain);

        scene.compile();
        universe.addBranchGraph(scene);
    }

    /**
     *set capabilities for the scene graph to allow removal and adding objects at runtime
     *this is set to enable switching the viewing cababitlies without recompiling, which might yields a
     *little performance to switch speed.
     *<p>
     * single objects are added under separate brabch groups to enable detaching from the main
     * scene graph, to enable the switching, and seprate compiling at the end.     *
     */
    BranchGroup createSceneGraph() {

        scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        Camera.setViewPlatformTransformGroup(universe.getViewingPlatform().
                                             getViewPlatformTransform()); //set camera

        //back ground
        Background bg = new Background(0, 0, 0);
        bg.setApplicationBounds(DBS);
        scene.addChild(bg);

        //light1
        l_dl1 = new DirectionalLight(new Color3f(1, 1, 1),
                                     new Vector3f(0, -1, 0));
        l_dl1.setInfluencingBounds(DBS);
        scene.addChild(l_dl1);
        l_dl1.setCapability(DirectionalLight.ALLOW_STATE_WRITE); //allow turn on-off

        //light2
        l_dl2 = new DirectionalLight(new Color3f(1, 1, 1),
                                     new Vector3f(1, 0.1f, -1));
        l_dl2.setInfluencingBounds(DBS);
        scene.addChild(l_dl2);
        l_dl2.setCapability(DirectionalLight.ALLOW_STATE_WRITE);

        //light3 not lit by default
        l_dl3 = new DirectionalLight(new Color3f(1, 1, 1),
                                     new Vector3f(1, 0.1f, 1));
        l_dl3.setInfluencingBounds(DBS);
        scene.addChild(l_dl3);
        l_dl3.setCapability(DirectionalLight.ALLOW_STATE_WRITE);
        l_dl3.setEnable(false);

        //grid
        gridBG.setCapability(BranchGroup.ALLOW_DETACH);
        gridBG.addChild(gridTerrain);
    //    scene.addChild(gridBG);

        //terrain
        terrain1NoTexturesBG.setCapability(BranchGroup.ALLOW_DETACH);
        terrainBG.setCapability(BranchGroup.ALLOW_DETACH);
        terrainBG.addChild(terrain1);
        terrain1NoTexturesBG.addChild(terrain1NoTexture);
        scene.addChild(terrainBG);
   //  scene.addChild(terrain1NoTexturesBG);

        //vehicle
        vehicleBGOptimized.setCapability(BranchGroup.ALLOW_DETACH);
        vehicleBG.setCapability(BranchGroup.ALLOW_DETACH);
        vehicleBGOptimized.addChild(vehicleGeomOptimized.vehicleTG);
        vehicleBG.addChild(vehicleGeom.vehicleTG);
//        System.out.println("Hai ana houn ya ..");
//        System.out.println(vehicleGeom.transform.toString());
        vehicleGeom.bindCamera(true);

        scene.addChild(vehicleBG); //default
        return scene;
    }

    /**
     * empty for the moment
     */
    public static void updateWorld() {

    }

    /**
     * use this to update the world when adding or removing the grid from the scene graph
     * from external classes<p>
     * this method switches between adding or removing the Grid
     */
    public static void switchGrid() {
        enableGrid = !enableGrid;
        if (enableGrid) {
            scene.addChild(gridBG);
        } else {
            gridBG.detach();
        }
    }


    /**
     * use this to update the world when adding or removing the terrain from the scene graph
     * from external classes<p>
     * this method switches between adding or removing the terrain
     */
    public static void switchTerrain() {
        enableTerrain = !enableTerrain;
        if (enableTerrain) {
            if (enableTexture)
                scene.addChild(terrainBG);
            else
                scene.addChild(terrain1NoTexturesBG);
        } else {
            if (enableTexture)
                terrainBG.detach();
            else
                terrain1NoTexturesBG.detach();
        }
    }

    /**
     * enabling and disabling the texture is a little bit different, since textures are loaded by the
     * loader we need to reload the relating objects and free old ones, how ever since we did
     * the whole capabilty thing to speed up switching, so we load two objects for each file
     * one with textures and another without or with less details for fast performace switch
     * since speed is far more concern than memory.
     *
     *so for the vehicle the second object is one without textures and with less level of detail
     */
    public static void switchTexture() {
        enableTexture = !enableTexture;
        if (enableTexture) {
            scene.addChild(terrainBG);
            terrain1NoTexturesBG.detach();
        } else {
            terrainBG.detach();
            scene.addChild(terrain1NoTexturesBG);
        }
    }

    /**
     * switch the vehicle between two modes: full options and one with less level of details
     * here we provide two models for the vehicle and it's wheels, one with transparent windows
     * two sided materails, acceptable wheels.<p>
     * the other model optimizes the wheels, no transparancy, no two sided materials, so when
     * performance degrades switching to such a model will be helpful
     */

    public static void switchVehicle() {
        enableVehicleOptimizedModel = !enableVehicleOptimizedModel;
        if (enableVehicleOptimizedModel) {
            vehicleBG.detach();
            scene.addChild(vehicleBGOptimized);
            vehicleGeomOptimized.bindCamera(true);
        } else {
            vehicleBGOptimized.detach();
            scene.addChild(vehicleBG);
            vehicleGeom.bindCamera(true);
        }
    }

    /**
     * the current selected vehicle geomerty to get viewed depending on the switch choosen
     * between the full-optimized models
     * @return VehicleGeom
     */
    public static VehicleGeom getCurVehicle() {
        if (enableVehicleOptimizedModel)
            return vehicleGeomOptimized;
        else
            return vehicleGeom;
    }

    /**
     * switch light1: directional light from a bove
     */

    public static void switchLight1() {
        enableLight1 = !enableLight1;
        if (enableLight1)
            l_dl1.setEnable(true);
        else
            l_dl1.setEnable(false);
    }

    /**
     * switch light2: directional light, coming from the side headinf towarsd negative z
     */

    public static void switchLight2() {
        enableLight2 = !enableLight2;
        if (enableLight2)
            l_dl2.setEnable(true);
        else
            l_dl2.setEnable(false);
    }

    /**
     * switch light3: directional light,coming from the side headinf towarsd positive z
     */

    public static void switchLight3() {
        enableLight3 = !enableLight3;
        if (enableLight3)
            l_dl3.setEnable(true);
        else
            l_dl3.setEnable(false);
    }


}
