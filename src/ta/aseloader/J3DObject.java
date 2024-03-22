/*
 * Trond Abusdal 2006
 * 
 * ASE Loader 4.62
 * 
 * Feel free to use for any purpose. No fees, no advertisement :)
 * However, this code is in no way guaranteed to be fast, stable,
 * bug-free or even correctly written. Nevertheless I have tried to
 * make this a useful piece of code for (hobby) J3D programmers.
 * 
 * Questions? Suggestions? Corrections? Anything?
 * Contact me:
 * trondabusdal@hotmail.com
 */

package ta.aseloader;

import java.io.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.image.*;


/*
 * Class keeping control of which textures we've loaded and which needs to
 * be loaded. So we won't load the same texture more than once.
 */
class TextureList
{
	static Hashtable m_texturelist = new Hashtable();

	/*
	 * Returns a texture-referende if it has been loaded
	 */
	public static Texture getTexture(String p_texturename)
	{
		//Check to see if the texture has been loaded before
		for (int i = 0; i < m_texturelist.size(); i++)
		{
			if (m_texturelist.containsKey(p_texturename))
			{
				return (Texture)m_texturelist.get(p_texturename);
			}
		}

		return null;
	}

	/*
	 * Checks to see if the requested texture has been previously
	 * loaded.
	 */
	public static boolean hasTexture(String p_texturename)
	{
		if (m_texturelist.containsKey(p_texturename))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/*
	 * Stores a texture in the hashtable
	 */
	public static void storeTexture(Texture p_tex, String p_texturename)
	{
		m_texturelist.put(p_texturename, p_tex);
	}
}

/**
 *
 * This class is a "bridge" between the file loader and Java3D.
 * This class has a function so it can be added to the branch graph
 * in Java3D, it is capable of checking a line collision
 * with the faces in the model and it can be used to animate
 * a model fairly easy.
 */
public class J3DObject
{
	private TriangleArray m_trianglelist[];
	//	LineArray m_normallist[];
	private String m_texturefilename;
	private Appearance m_app = null;

	private ObjectFaceData m_facedata[];

	private float m_interpolated_face[][][][];
	private float m_interpolated_facenormal[][][];
	private float m_current_face[][][][];
	private float m_current_facenormal[][][];

	private TransformGroup m_tg = new TransformGroup();

	private int m_objectcount = 0;
	private int m_currentobject = 0;
	private int m_frames;
	private int m_currentframe = 0;

	private float m_pos_x = 0;
	private float m_pos_y = 0;
	private float m_pos_z = 0;

	private boolean m_support_light;
	private boolean m_random_colors;
	private int m_shademode;

	private final static int SHADE_NORMAL = 0;
	private final static int SHADE_FLAT = 1;
	private final static int SHADE_CURVED = 2;

	public final static int ROT_XYZ = 0;
	public final static int ROT_XZY = 1;
	public final static int ROT_YZX = 2;
	public final static int ROT_YXZ = 3;
	public final static int ROT_ZXY = 4;
	public final static int ROT_ZYX = 5;

	private Transform3D m_t3d_rotx = new Transform3D();
	private Transform3D m_t3d_roty = new Transform3D();
	private Transform3D m_t3d_rotz = new Transform3D();
	private Transform3D m_t3d = new Transform3D();

	private float m_rotx = 0;
	private float m_roty = 0;
	private float m_rotz = 0;

	private int m_rotorder = 0;

	private int m_animations = 0;
	private int m_animation[][] = null;

	//The following variables are for collision testing/"reporting"
	private final static int MAX_COLLISIONS = 256;
	private Point3f m_isp[] = new Point3f[MAX_COLLISIONS];
	private Vector3f m_isp_normal[] = new Vector3f[MAX_COLLISIONS];
	private int m_isp_face[] = new int[MAX_COLLISIONS];
	private int m_isp_object[] = new int[MAX_COLLISIONS];
	private int m_collisions = 0;
	private int m_col_near = 0;
	private int m_col_far = 0;

	/*
	 * Constructor. Allocates memory for the passed number of objects.
	 * If someone tries to add more objects than there has been
	 * pass to the constructor, the program will crash.
	 */
	protected J3DObject(int p_objectcount, boolean p_support_light, boolean p_random_colors, int p_shademode)
	{
		m_facedata = new ObjectFaceData[p_objectcount];
		m_trianglelist = new TriangleArray[p_objectcount];
		//	m_normallist = new LineArray[p_objectcount];

		m_objectcount = p_objectcount;
		m_support_light = p_support_light;
		m_random_colors = p_random_colors;

		m_shademode = p_shademode;

		//Set up memory for temporary vertex-array and normal array
		m_current_face = new float[p_objectcount][][][];
		m_current_facenormal = new float[p_objectcount][][];
		m_interpolated_face = new float[p_objectcount][][][];
		m_interpolated_facenormal = new float[p_objectcount][][];

		initCollisionData();
	}

	/*
	 * Adds object to the class' TransformGroup. The transformgroup will then
	 * be able to use directly in Java3D when retrieved from the getObject()
	 * method. Initial vertex data is read from frame #0
	 */
	protected void addObjectFaceData(ObjectFaceData p_facelist)
	{

		m_facedata[m_currentobject] = p_facelist;
		//		m_facedata_original[m_currentobject] = new taObjectFaceData(p_facelist.getFrameCount(), p_facelist.getFaceCount(), null);

		if (p_facelist == null)
		{
			return;
		}
		
		if (m_currentobject >= m_objectcount)
		{
			err("Too many objects: "+m_currentobject);
			return;
		}

		//Objects without faces need to be filtered out. And yes, this is an
		//ugly hack to do so.
		//TODO: this should be handled in the .ASE loader!
		if (m_facedata[m_currentobject].getFaceCount() <= 0)
		{
			out("No faces for this object");
			//Bad, bad, bad, baaaad hack
			m_objectcount--;
			return;
		}

		//Need to set up vertex normal data for object
		if (m_support_light)
		{
			if (m_shademode == SHADE_CURVED)
			{
				m_facedata[m_currentobject].calcAllNormals(true);
			}
			else if (m_shademode == SHADE_FLAT)
			{
				m_facedata[m_currentobject].calcAllNormalsFlat();
			}
			else
			{
				m_facedata[m_currentobject].calcAllNormals(false);
			}
		}
		else
		{
			m_facedata[m_currentobject].calcFaceNormals();
		}

		//Set up a triangle array with 3*times as many vertices as we have faces
		out("VertexCount: " + (p_facelist.getFaceCount() * 3));

		m_trianglelist[m_currentobject] = new TriangleArray(p_facelist.getFaceCount() * 3, TriangleArray.COORDINATES | TriangleArray.TEXTURE_COORDINATE_2 /*| TriangleArray.COLOR_3*/ | TriangleArray.NORMALS);
		m_trianglelist[m_currentobject].setCapability(TriangleArray.ALLOW_COORDINATE_WRITE);
		m_trianglelist[m_currentobject].setCapability(TriangleArray.ALLOW_NORMAL_WRITE);

		//		m_normallist[m_currentobject] = new LineArray(p_facelist.getFaceCount() * 2, LineArray.COORDINATES);
		//		m_normallist[m_currentobject].setCapability(LineArray.ALLOW_COORDINATE_WRITE);

		float l_red = (float)Math.random();
		float l_green = (float)Math.random();
		float l_blue = (float)Math.random();
		
		ColoringAttributes l_color = new ColoringAttributes();

		//Read face data into the triangle list
		int l_curvertex = 0;
		for (int f = 0; f < p_facelist.getFaceCount(); f++)
		{
			for (int v = 0; v < 3; v++)
			{
				//Store vertex normal. It's stored as the last 3 entries in the
				//vertex coordinate array. Ugly. I know. Live with it.
				if (m_support_light)
				{
					m_trianglelist[m_currentobject].setNormal(l_curvertex, new Vector3f(p_facelist.getFaceData()[0][f][v][3], p_facelist.getFaceData()[0][f][v][4], p_facelist.getFaceData()[0][f][v][5]));
				}

				//Store standard vertex/face data
				m_trianglelist[m_currentobject].setCoordinate(l_curvertex, new Point3f(p_facelist.getFaceData()[0][f][v][0], p_facelist.getFaceData()[0][f][v][1], p_facelist.getFaceData()[0][f][v][2]));
				m_trianglelist[m_currentobject].setTextureCoordinate(0, l_curvertex, new TexCoord2f(p_facelist.getFaceTexCoords()[f][v][0], p_facelist.getFaceTexCoords()[f][v][1]));

				//Use random colors if flagged to do so, overriding the material colors
				if (m_random_colors)
				{
					//HACK: To support color change by user
				//	m_trianglelist[m_currentobject].setColor(l_curvertex, new Color3f(l_rand_r, l_rand_g, l_rand_b));
					l_color.setColor(l_red, l_green, l_blue);
				}
				else
				{
					//HACK: To support color change by user
				//	m_trianglelist[m_currentobject].setColor(l_curvertex, new Color3f(p_facelist.getFaceColors()[f][v][0], p_facelist.getFaceColors()[f][v][1], p_facelist.getFaceColors()[f][v][2]));
					l_red = p_facelist.getFaceColors()[f][v][0];
					l_green = p_facelist.getFaceColors()[f][v][1];
					l_blue = p_facelist.getFaceColors()[f][v][2];
					l_color.setColor(l_red, l_green, l_blue);
				}
				l_curvertex++;
			}
		}

		//Attach texture to object, if any
		m_texturefilename = p_facelist.m_texturefile;

		Appearance l_app = null;

		//Load and set texture if the object is textured and we're not supposed to have random colors
		if (p_facelist.m_textured && !m_random_colors)
		{
			l_app = new Appearance();
			l_app.setTexture(getTexture(m_texturefilename));

			TextureAttributes l_texatt = new TextureAttributes();
			l_texatt.setTextureMode(TextureAttributes.MODULATE);
			l_app.setTextureAttributes(l_texatt);
		}

		//Check to see if we should support lighting
		if (m_support_light)
		{
			if (l_app == null)
			{
				l_app = new Appearance();
			}

			Material l_mat = new Material();

			l_mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
			l_mat.setSpecularColor(new Color3f(0, 0, 0)); //.7f, 0.7f, 0.7f));
			l_mat.setAmbientColor(new Color3f(0, 0, 0));
			l_mat.setDiffuseColor(new Color3f(l_red, l_green, l_blue));
			
			l_app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

			l_app.setMaterial(l_mat);
		}
		
		if (l_app == null)
		{
			l_app = new Appearance();
			
			l_app.setColoringAttributes(l_color);
		}

		//Add object to transformgroup!
	//	if (l_app != null)
		{
			Shape3D l_s3d = new Shape3D(m_trianglelist[m_currentobject], l_app);
			l_s3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			l_s3d.setAppearanceOverrideEnable(true);
			m_tg.addChild(l_s3d);
		}
	/*	else
		{
			Shape3D l_s3d = new Shape3D(m_trianglelist[m_currentobject]);
			l_s3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			l_s3d.setAppearanceOverrideEnable(true);
			m_tg.addChild(l_s3d);
		}*/

		m_tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		//Allocate memory for vertex/normal data
		m_current_face[m_currentobject] = new float[p_facelist.getFaceCount()][3][6];
		m_current_facenormal[m_currentobject] = new float[p_facelist.getFaceCount()][3];
		m_interpolated_face[m_currentobject] = new float[p_facelist.getFaceCount()][3][6];
		m_interpolated_facenormal[m_currentobject] = new float[p_facelist.getFaceCount()][3];

		//Need to store model data in the face-array used for collision testing
		fillFaceData(m_currentobject, p_facelist.getFaceCount());

		//Keep track of where we are in the facedata-array
		m_currentobject++;

		//May be a bad way to get frame count...
		m_frames = p_facelist.getFrameCount();
	}

	/*
	 * Fill facedata-array used for collision detection
	 */
	private void fillFaceData(int p_object, int p_faces)
	{
		for (int f = 0; f < p_faces; f++)
		{
			m_current_facenormal[p_object][f][0] = m_interpolated_facenormal[p_object][f][0] = m_facedata[p_object].getFaceNormals()[0][f][0];
			m_current_facenormal[p_object][f][1] = m_interpolated_facenormal[p_object][f][1] = m_facedata[p_object].getFaceNormals()[0][f][1];
			m_current_facenormal[p_object][f][2] = m_interpolated_facenormal[p_object][f][2] = m_facedata[p_object].getFaceNormals()[0][f][2];

			for (int v = 0; v < 3; v++)
			{
				m_current_face[p_object][f][v][0] = m_interpolated_face[p_object][f][v][0] = m_facedata[p_object].getFaceData()[0][f][v][0];
				m_current_face[p_object][f][v][1] = m_interpolated_face[p_object][f][v][1] = m_facedata[p_object].getFaceData()[0][f][v][1];
				m_current_face[p_object][f][v][2] = m_interpolated_face[p_object][f][v][2] = m_facedata[p_object].getFaceData()[0][f][v][2];
				m_current_face[p_object][f][v][3] = m_interpolated_face[p_object][f][v][3] = m_facedata[p_object].getFaceData()[0][f][v][3];
				m_current_face[p_object][f][v][4] = m_interpolated_face[p_object][f][v][4] = m_facedata[p_object].getFaceData()[0][f][v][4];
				m_current_face[p_object][f][v][5] = m_interpolated_face[p_object][f][v][5] = m_facedata[p_object].getFaceData()[0][f][v][5];
			}
		}
	}

	/*
	* Get unrotated interpolated face data
	*/
	private void setUnrotatedFaceData()
	{
		for (int obj = 0; obj < m_objectcount; obj++)
		{
			for (int f = 0; f < m_facedata[obj].getFaceCount(); f++)
			{
				m_current_facenormal[obj][f][0] = m_interpolated_facenormal[obj][f][0];
				m_current_facenormal[obj][f][1] = m_interpolated_facenormal[obj][f][1];
				m_current_facenormal[obj][f][2] = m_interpolated_facenormal[obj][f][2];

				for (int v = 0; v < 3; v++)
				{
					m_current_face[obj][f][v][0] = m_interpolated_face[obj][f][v][0];
					m_current_face[obj][f][v][1] = m_interpolated_face[obj][f][v][1];
					m_current_face[obj][f][v][2] = m_interpolated_face[obj][f][v][2];
					m_current_face[obj][f][v][3] = m_interpolated_face[obj][f][v][3];
					m_current_face[obj][f][v][4] = m_interpolated_face[obj][f][v][4];
					m_current_face[obj][f][v][5] = m_interpolated_face[obj][f][v][5];
				}
			}
		}
	}
	
	/**
	 * Set new appearance for entire model.
	 * 
	 * @param p_app The J3D appearance object to be applied
	 */
	public void setAppearance(Appearance p_app)
	{
		Enumeration l_objlist = m_tg.getAllChildren();
		
		while (l_objlist.hasMoreElements())
		{
			Shape3D l_s3d = (Shape3D)l_objlist.nextElement();
			
			l_s3d.setAppearance(p_app);
		}
	}

	/**
	 * Set the number of animations this model should consist of.
	 * 
	 * @param p_animations Amount of animation sequences. Must be above 0
	 */
	public void setAnimationSequences(int p_animations)
	{
		if (p_animations <= 0)
		{
			err("Invalid animation count: " + p_animations);
			return;
		}

		m_animations = p_animations;

		m_animation = new int[m_animations][3];
	}

	/**
	 * Defines the animation frames an animation sequence consists of. The number of
	 * valid animations must be set before this function is called.
	 * 
	 * @param p_anim Animation index to set frames for
	 * @param p_startframe Animation start frame
	 * @param p_lastframe Animation last frame
	 * 
	 * @see #setAnimationSequences(int p_animations)
	 */
	public void setAnimationFrames(int p_anim, int p_startframe, int p_lastframe)
	{
		if (p_anim >= m_animations)
		{
			err("Invalid animation index: " + p_anim);
			return;
		}

		if (p_startframe < 0 || p_startframe >= m_frames)
		{
			err("Invalid startframe: " + p_startframe);
			return;
		}

		if (p_lastframe < 0 || p_lastframe >= m_frames)
		{
			err("Invalid lastframe: " + p_startframe);
			return;
		}

		m_animation[p_anim][0] = p_startframe;
		m_animation[p_anim][1] = p_lastframe;
		m_animation[p_anim][2] = p_lastframe - p_startframe;
	}

	/**
	 * Will choose a frame in a previously defined animation sequence based
	 * on the percentage value passed.
	 * 
	 * @param p_anim Animation sequence index
	 * @param p_percentage Animation progress in percent
	 * 
	 * @see #setAnimationFrames(int p_anim, int p_startframe, int p_lastframe)
	 */
	public void setAnimationProgress(int p_anim, float p_percentage)
	{
		if (p_anim >= m_animations)
		{
			err("Invalid animation index: " + p_anim);
			return;
		}

		//Obviously, we'll be fine using 0-100%
		p_percentage %= 100;

		if (p_percentage < 0)
		{
			p_percentage += 100;
		}

		//A variable to make the code more readable. Holds the number of
		//frames this animation consists of
		int l_frames = m_animation[p_anim][2];

		//Figure out the start position, in a float value. The integer part
		//will be used to get the source frame, and the "float part" will
		//be used as the interpolation value.
		float l_startpos = ((float)l_frames * (p_percentage / 100)) + m_animation[p_anim][0];

		//Make sure we won't go into other animation sequences...
		//The second entry in the animation array tells us the
		//las frame of the animation
		if (l_startpos > m_animation[p_anim][1])
		{
			l_startpos -= m_animation[p_anim][1];
		}

		//Get the source frame and the target frame
		int l_sourceframe = (int)l_startpos;
		int l_targetframe = l_sourceframe + 1;

		//If the targetframe should go over animation bounds, set the target frame
		//to be the first in the animation
		if (l_targetframe > m_animation[p_anim][1])
		{
			l_targetframe = m_animation[p_anim][0];
		}

		//Chances are fairly high that the frame must be interpolated, so we'll
		//get the interpolation value here. It's just the non-integer part
		//of the startpos
		float l_ip = l_startpos % 1; // - l_sourceframe;

		//Debug
		//System.out.println("percentage: "+p_percentage+" frames: "+l_frames+" startpos: " + l_startpos + " sourceframe: " + l_sourceframe + " targetframe: " + l_targetframe + " ip: " + l_ip);

		//Finally, set the interpolated frame based on the calculations above.
		setInterpolatedFrame(l_sourceframe, l_targetframe, l_ip);
	}

	/**
	 * Set next frame from current frameindex.
	 *
	 */
	public void setFrameNext()
	{
		if (m_currentframe + 1 >= m_frames)
		{
			setFrame(0);
		}
		else
		{
			setFrame(m_currentframe + 1);
		}
	}
	
	/**
	 * Set previous frame from current frameindex.
	 *
	 */
	public void setFramePrev()
	{
		if (m_currentframe - 1 < 0)
		{
			setFrame(m_frames-1);
		}
		else
		{
			setFrame(m_currentframe - 1);
		}
	}

	/**
	 * Set a frame, no interpolation
	 * 
	 * @param p_frame Wanted frame index
	 */
	public void setFrame(int p_frame)
	{

		setInterpolatedFrame(p_frame, p_frame, 1);
	}

	/**
	 * Set a frame, or an interpolated frame.
	 * 
	 * @param p_frame_source The index of the base frame to interpolate from
	 * 
	 * @param p_frame_target The index of the target frame
	 * 
	 * @param p_ip The interpolation value, should vary from 0 to 1, where numbers in between
	 * will result in an interpolated frame.
	 */
	public void setInterpolatedFrame(int p_frame_source, int p_frame_target, float p_ip)
	{
		int l_newframe = 0;

		//Check for valid frame entries
		if (p_frame_source >= m_frames || p_frame_target >= m_frames)
		{
			return;
		}
		else if (p_frame_source < 0 || p_frame_target < 0)
		{
			return;
		}
		else
		{
			//Set frame variable, so we'll know what frame to test collision etc against.
			l_newframe = p_frame_source;
		}

		Point3f l_pointlist[] = null;
		Vector3f l_normallist[] = null;
		//	Point3f l_facenormals[] = null;

		//Temporary interpolation variables used both for normals
		//and vertices
		float l_lerp_x = 0;
		float l_lerp_y = 0;
		float l_lerp_z = 0;

		//Read frame face data into the triangle list
		int o = 0;
		for (o = 0; o < m_objectcount; o++)
		{
			int l_curvertex = 0;

			int l_curnormal = 0;

			//Set memory size needed for list of coordinates and vertex normals
			l_pointlist = new Point3f[m_facedata[o].getFaceCount() * 3];
			//	l_facenormals = new Point3f[m_facedata[o].getFaceCount() * 2];
			if (m_support_light)
			{
				l_normallist = new Vector3f[m_facedata[o].getFaceCount() * 3];
			}

			for (int f = 0; f < m_facedata[o].getFaceCount(); f++)
			{
				l_lerp_x = (m_facedata[o].getFaceNormals()[p_frame_target][f][0] - m_facedata[o].getFaceNormals()[p_frame_source][f][0]) * p_ip;
				l_lerp_y = (m_facedata[o].getFaceNormals()[p_frame_target][f][1] - m_facedata[o].getFaceNormals()[p_frame_source][f][1]) * p_ip;
				l_lerp_z = (m_facedata[o].getFaceNormals()[p_frame_target][f][2] - m_facedata[o].getFaceNormals()[p_frame_source][f][2]) * p_ip;

				m_current_facenormal[o][f][0] = m_interpolated_facenormal[o][f][0] = m_facedata[o].getFaceNormals()[p_frame_source][f][0] + l_lerp_x;
				m_current_facenormal[o][f][1] = m_interpolated_facenormal[o][f][1] = m_facedata[o].getFaceNormals()[p_frame_source][f][1] + l_lerp_y;
				m_current_facenormal[o][f][2] = m_interpolated_facenormal[o][f][2] = m_facedata[o].getFaceNormals()[p_frame_source][f][2] + l_lerp_z;

				/*			float l_facenormal_length = (float)Math.sqrt(m_current_facenormal[o][f][0] * m_current_facenormal[o][f][0] + m_current_facenormal[o][f][1] * m_current_facenormal[o][f][1] + m_current_facenormal[o][f][2] * m_current_facenormal[o][f][2]);
				
							m_current_facenormal[o][f][0] = m_interpolated_facenormal[o][f][0] = m_current_facenormal[o][f][0] / l_facenormal_length;
							m_current_facenormal[o][f][1] = m_interpolated_facenormal[o][f][1] = m_current_facenormal[o][f][1] / l_facenormal_length;
							m_current_facenormal[o][f][2] = m_interpolated_facenormal[o][f][2] = m_current_facenormal[o][f][2] / l_facenormal_length;
				*/
				for (int v = 0; v < 3; v++)
				{
					//Store vertex data in the arrays we set aside memory for
					if (m_support_light)
					{
						l_lerp_x = (m_facedata[o].getFaceData()[p_frame_target][f][v][3] - m_facedata[o].getFaceData()[p_frame_source][f][v][3]) * p_ip;
						l_lerp_y = (m_facedata[o].getFaceData()[p_frame_target][f][v][4] - m_facedata[o].getFaceData()[p_frame_source][f][v][4]) * p_ip;
						l_lerp_z = (m_facedata[o].getFaceData()[p_frame_target][f][v][5] - m_facedata[o].getFaceData()[p_frame_source][f][v][5]) * p_ip;

						//This data need to be stored in the actual modeldata as well, for collision testing
						m_current_face[o][f][v][3] = m_interpolated_face[o][f][v][3] = m_facedata[o].m_face[p_frame_source][f][v][3] + l_lerp_x;
						m_current_face[o][f][v][4] = m_interpolated_face[o][f][v][4] = m_facedata[o].m_face[p_frame_source][f][v][4] + l_lerp_y;
						m_current_face[o][f][v][5] = m_interpolated_face[o][f][v][5] = m_facedata[o].m_face[p_frame_source][f][v][5] + l_lerp_z;

						//	l_normallist[l_curvertex] = new Vector3f(m_interpolated_face[o][f][v][3], m_interpolated_face[o][f][v][4], m_interpolated_face[o][f][v][5]);
						l_normallist[l_curvertex] = new Vector3f(m_current_face[o][f][v][3], m_current_face[o][f][v][4], m_current_face[o][f][v][5]);
					}

					l_lerp_x = (m_facedata[o].getFaceData()[p_frame_target][f][v][0] - m_facedata[o].getFaceData()[p_frame_source][f][v][0]) * p_ip;
					l_lerp_y = (m_facedata[o].getFaceData()[p_frame_target][f][v][1] - m_facedata[o].getFaceData()[p_frame_source][f][v][1]) * p_ip;
					l_lerp_z = (m_facedata[o].getFaceData()[p_frame_target][f][v][2] - m_facedata[o].getFaceData()[p_frame_source][f][v][2]) * p_ip;

					//This data need to be stored in the actual modeldata as well, for collision testing
					m_current_face[o][f][v][0] = m_interpolated_face[o][f][v][0] = m_facedata[o].m_face[p_frame_source][f][v][0] + l_lerp_x;
					m_current_face[o][f][v][1] = m_interpolated_face[o][f][v][1] = m_facedata[o].m_face[p_frame_source][f][v][1] + l_lerp_y;
					m_current_face[o][f][v][2] = m_interpolated_face[o][f][v][2] = m_facedata[o].m_face[p_frame_source][f][v][2] + l_lerp_z;

					//					l_pointlist[l_curvertex] = new Point3f(m_interpolated_face[o][f][v][0], m_interpolated_face[o][f][v][1], m_interpolated_face[o][f][v][2]);
					l_pointlist[l_curvertex] = new Point3f(m_current_face[o][f][v][0], m_current_face[o][f][v][1], m_current_face[o][f][v][2]);

					l_curvertex++;
				}

				/*		l_facenormals[l_curnormal] = new Point3f((m_current_face[o][f][0][0] + m_current_face[o][f][1][0] + m_current_face[o][f][2][0]) / 3, (m_current_face[o][f][0][1] + m_current_face[o][f][1][1] + m_current_face[o][f][2][1]) / 3, (m_current_face[o][f][0][2] + m_current_face[o][f][1][2] + m_current_face[o][f][2][2]) / 3);
						l_curnormal++;
				
						float l_length = 0.1f;
				
						l_facenormals[l_curnormal] = new Point3f(((m_current_face[o][f][0][0] + m_current_face[o][f][1][0] + m_current_face[o][f][2][0]) / 3) + (m_current_facenormal[o][f][0] * l_length), ((m_current_face[o][f][0][1] + m_current_face[o][f][1][1] + m_current_face[o][f][2][1]) / 3) + (m_current_facenormal[o][f][1] * l_length), ((m_current_face[o][f][0][2] + m_current_face[o][f][1][2] + m_current_face[o][f][2][2]) / 3) + (m_current_facenormal[o][f][2] * l_length));
						l_curnormal++;*/
			}

			//Copy vertex data into the trianglelist in two calls instead of faces*3 calls! :)
			//This is the 'secret' behind smooth animation...
			if (m_support_light)
			{
				m_trianglelist[o].setNormals(0, l_normallist);
			}
			m_trianglelist[o].setCoordinates(0, l_pointlist);

			//	m_normallist[o].setCoordinates(0, l_facenormals);
		}

		m_currentframe = l_newframe;

		//Update rotation data
		if (m_t3d != null)
			setTransform(m_t3d);
	}
	
	/**
	 * Sets a transform for the model, but does not apply it. Should be used if animation
	 * is in use, that way the transform won't be applied twice to the model. If you use the
	 * setTransform() function, you will multiply the model with the passed transform,
	 * which is not necessary if you are doing animation, since the animation function
	 * calls the setTransform() function as well. This function only sets a transform
	 * to use whenever an animation/setframe function is called.
	 * 
	 * @param p_t3d Transform to use for model
	 * 
	 * @see #setTransform(Transform3D p_t3d)
	 */
	public void useTransform(Transform3D p_t3d)
	{
		m_t3d = p_t3d;
	}

	/**
	 * Multiplies the model data with the passed transform. This function is required to use
	 * if collision-detection is wanted.
	 * 
	 * @param p_t3d The transform you would use on your geometry to make it rotate, move etc.
	 */
	public void setTransform(Transform3D p_t3d)
	{
		Matrix4f l_tmat = new Matrix4f();

		m_t3d = p_t3d;
		m_t3d.get(l_tmat);

		//	System.out.println(l_tmat.toString());
		//	System.out.println(l_tmat.m03);

		float l_tempx = 0;
		float l_tempy = 0;
		float l_tempz = 0;

		//These need to be reset since they make bad things happen
		//to the collision detection if this method is used to
		//set the correct transform.
		m_pos_x = 0;
		m_pos_y = 0;
		m_pos_z = 0;

		setUnrotatedFaceData();

		for (int o = 0; o < m_objectcount; o++)
		{
			for (int f = 0; f < m_facedata[o].getFaceCount(); f++)
			{
				//Transform facenormal
				l_tempx = m_current_facenormal[o][f][0] * l_tmat.m00 + m_current_facenormal[o][f][1] * l_tmat.m01 + m_current_facenormal[o][f][2] * l_tmat.m02;
				l_tempy = m_current_facenormal[o][f][0] * l_tmat.m10 + m_current_facenormal[o][f][1] * l_tmat.m11 + m_current_facenormal[o][f][2] * l_tmat.m12;
				l_tempz = m_current_facenormal[o][f][0] * l_tmat.m20 + m_current_facenormal[o][f][1] * l_tmat.m21 + m_current_facenormal[o][f][2] * l_tmat.m22;

				m_current_facenormal[o][f][0] = l_tempx;
				m_current_facenormal[o][f][1] = l_tempy;
				m_current_facenormal[o][f][2] = l_tempz;

				for (int v = 0; v < 3; v++)
				{
					//Transform face vertex
					l_tempx = m_current_face[o][f][v][0] * l_tmat.m00 + m_current_face[o][f][v][1] * l_tmat.m01 + m_current_face[o][f][v][2] * l_tmat.m02;
					l_tempy = m_current_face[o][f][v][0] * l_tmat.m10 + m_current_face[o][f][v][1] * l_tmat.m11 + m_current_face[o][f][v][2] * l_tmat.m12;
					l_tempz = m_current_face[o][f][v][0] * l_tmat.m20 + m_current_face[o][f][v][1] * l_tmat.m21 + m_current_face[o][f][v][2] * l_tmat.m22;

					m_current_face[o][f][v][0] = l_tempx + l_tmat.m03;
					m_current_face[o][f][v][1] = l_tempy + l_tmat.m13;
					m_current_face[o][f][v][2] = l_tempz + l_tmat.m23;

					//Transform vertex normal
					if (m_support_light)
					{
						l_tempx = m_current_face[o][f][v][3] * l_tmat.m00 + m_current_face[o][f][v][4] * l_tmat.m01 + m_current_face[o][f][v][5] * l_tmat.m02;
						l_tempy = m_current_face[o][f][v][3] * l_tmat.m10 + m_current_face[o][f][v][4] * l_tmat.m11 + m_current_face[o][f][v][5] * l_tmat.m12;
						l_tempz = m_current_face[o][f][v][3] * l_tmat.m20 + m_current_face[o][f][v][4] * l_tmat.m21 + m_current_face[o][f][v][5] * l_tmat.m22;

						m_current_face[o][f][v][3] = l_tempx;
						m_current_face[o][f][v][4] = l_tempy;
						m_current_face[o][f][v][5] = l_tempz;
					}
				}
			}
		}

		m_tg.setTransform(m_t3d);
	}

	/*
	 * Rotate model around x-axis
	 */
	private void rotateX(float p_rad)
	{
		float l_sinx = (float)Math.sin(p_rad);
		float l_cosx = (float)Math.cos(p_rad);

		for (int o = 0; o < m_objectcount; o++)
		{
			for (int f = 0; f < m_facedata[o].getFaceCount(); f++)
			{
				float l_tempy = 0;
				float l_tempz = 0;

				//---------------------------Rotate x
				l_tempz = m_current_facenormal[o][f][2] * l_cosx - m_current_facenormal[o][f][1] * l_sinx;
				l_tempy = m_current_facenormal[o][f][1] * l_cosx + m_current_facenormal[o][f][2] * l_sinx;

				m_current_facenormal[o][f][2] = l_tempz;
				m_current_facenormal[o][f][1] = l_tempy;

				for (int v = 0; v < 3; v++)
				{
					//Rotate face vertex
					l_tempz = m_current_face[o][f][v][2] * l_cosx - m_current_face[o][f][v][1] * l_sinx;
					l_tempy = m_current_face[o][f][v][1] * l_cosx + m_current_face[o][f][v][2] * l_sinx;

					m_current_face[o][f][v][2] = l_tempz;
					m_current_face[o][f][v][1] = l_tempy;

					//Rotate vertex normal
					l_tempz = m_current_face[o][f][v][5] * l_cosx - m_current_face[o][f][v][4] * l_sinx;
					l_tempy = m_current_face[o][f][v][4] * l_cosx + m_current_face[o][f][v][5] * l_sinx;

					m_current_face[o][f][v][5] = l_tempz;
					m_current_face[o][f][v][4] = l_tempy;
				}
			}
		}
	}

	/*
	 * Rotate model around y-axis
	 */
	private void rotateY(float p_rad)
	{
		float l_siny = (float)Math.sin(p_rad);
		float l_cosy = (float)Math.cos(p_rad);

		for (int o = 0; o < m_objectcount; o++)
		{
			for (int f = 0; f < m_facedata[o].getFaceCount(); f++)
			{
				float l_tempx = 0;
				float l_tempz = 0;

				//---------------------------Rotate y
				l_tempz = m_current_facenormal[o][f][2] * l_cosy - m_current_facenormal[o][f][0] * l_siny;
				l_tempx = m_current_facenormal[o][f][0] * l_cosy + m_current_facenormal[o][f][2] * l_siny;

				m_current_facenormal[o][f][2] = l_tempz;
				m_current_facenormal[o][f][0] = l_tempx;

				for (int v = 0; v < 3; v++)
				{
					//Rotate face vertex
					l_tempz = m_current_face[o][f][v][2] * l_cosy - m_current_face[o][f][v][0] * l_siny;
					l_tempx = m_current_face[o][f][v][0] * l_cosy + m_current_face[o][f][v][2] * l_siny;

					m_current_face[o][f][v][2] = l_tempz;
					m_current_face[o][f][v][0] = l_tempx;

					//Rotate vertex normal
					l_tempz = m_current_face[o][f][v][5] * l_cosy - m_current_face[o][f][v][3] * l_siny;
					l_tempx = m_current_face[o][f][v][3] * l_cosy + m_current_face[o][f][v][5] * l_siny;

					m_current_face[o][f][v][5] = l_tempz;
					m_current_face[o][f][v][3] = l_tempx;
				}
			}
		}
	}

	/*
	 * Rotate around z-axis
	 */
	private void rotateZ(float p_rad)
	{
		float l_sinz = (float)Math.sin(p_rad);
		float l_cosz = (float)Math.cos(p_rad);

		for (int o = 0; o < m_objectcount; o++)
		{
			for (int f = 0; f < m_facedata[o].getFaceCount(); f++)
			{
				float l_tempy = 0;
				float l_tempx = 0;

				//---------------------------Rotate z
				l_tempx = m_current_facenormal[o][f][0] * l_cosz - m_current_facenormal[o][f][1] * l_sinz;
				l_tempy = m_current_facenormal[o][f][1] * l_cosz + m_current_facenormal[o][f][0] * l_sinz;

				m_current_facenormal[o][f][0] = l_tempx;
				m_current_facenormal[o][f][1] = l_tempy;

				for (int v = 0; v < 3; v++)
				{
					//Rotate face vertex
					l_tempx = m_current_face[o][f][v][0] * l_cosz - m_current_face[o][f][v][1] * l_sinz;
					l_tempy = m_current_face[o][f][v][1] * l_cosz + m_current_face[o][f][v][0] * l_sinz;

					m_current_face[o][f][v][0] = l_tempx;
					m_current_face[o][f][v][1] = l_tempy;

					//Rotate vertex normal
					l_tempx = m_current_face[o][f][v][3] * l_cosz - m_current_face[o][f][v][4] * l_sinz;
					l_tempy = m_current_face[o][f][v][4] * l_cosz + m_current_face[o][f][v][3] * l_sinz;

					m_current_face[o][f][v][3] = l_tempx;
					m_current_face[o][f][v][4] = l_tempy;
				}
			}
		}
	}

	/*
	 * Reset rotation
	 */
	private void resetRotation()
	{
		//Rotate the visual model using J3D's API functions
		m_t3d_rotx.setIdentity();
		m_t3d_roty.setIdentity();
		m_t3d_rotz.setIdentity();
		m_t3d.setIdentity();

		//Need to have the unrotated face data
		setUnrotatedFaceData();
	}

	/**
	 * 
	 * @see #setTransform(Transform3D p_t3d)
	 * 
	 * @deprecated
	 * 
	 * To set rotation/position set up a Transform3D and use the .setTransform(Transform3D p_t3d)
	 * function to apply the transform.
	 */
	public void setRotation(int p_rotation_order, float p_rad_x, float p_rad_y, float p_rad_z)
	{
		//Store these values as class-variables so we can access them when changing frames
		m_rotorder = p_rotation_order;
		m_rotx = p_rad_x;
		m_roty = p_rad_y;
		m_rotz = p_rad_z;

		resetRotation();

		m_t3d_rotz.rotZ(p_rad_z);
		m_t3d_roty.rotY(p_rad_y);
		m_t3d_rotx.rotX(p_rad_x);

		//Handles the different rotation possibilities.
		//This only rotates the model data, so collision
		//detection will be functional.
		switch (p_rotation_order)
		{
			case ROT_XYZ :
				{
					rotateX(p_rad_x);
					rotateY(p_rad_y);
					rotateZ(p_rad_z);

					m_t3d.mul(m_t3d_rotz);
					m_t3d.mul(m_t3d_roty);
					m_t3d.mul(m_t3d_rotx);
					break;
				}
			case ROT_XZY :
				{
					rotateX(p_rad_x);
					rotateZ(p_rad_z);
					rotateY(p_rad_y);

					m_t3d.mul(m_t3d_roty);
					m_t3d.mul(m_t3d_rotz);
					m_t3d.mul(m_t3d_rotx);
					break;
				}
			case ROT_YZX :
				{
					rotateY(p_rad_y);
					rotateZ(p_rad_z);
					rotateX(p_rad_x);

					m_t3d.mul(m_t3d_rotx);
					m_t3d.mul(m_t3d_rotz);
					m_t3d.mul(m_t3d_roty);
					break;
				}
			case ROT_YXZ :
				{
					rotateY(p_rad_y);
					rotateX(p_rad_x);
					rotateZ(p_rad_z);

					m_t3d.mul(m_t3d_rotz);
					m_t3d.mul(m_t3d_rotx);
					m_t3d.mul(m_t3d_roty);
					break;
				}
			case ROT_ZXY :
				{
					rotateZ(p_rad_z);
					rotateX(p_rad_x);
					rotateY(p_rad_y);

					m_t3d.mul(m_t3d_roty);
					m_t3d.mul(m_t3d_rotx);
					m_t3d.mul(m_t3d_rotz);
					break;
				}
			case ROT_ZYX :
				{
					rotateZ(p_rad_z);
					rotateY(p_rad_y);
					rotateX(p_rad_x);

					m_t3d.mul(m_t3d_rotx);
					m_t3d.mul(m_t3d_roty);
					m_t3d.mul(m_t3d_rotz);
					break;
				}
			default :
				return;
		}

		m_t3d.setTranslation(new Vector3f(m_pos_x, m_pos_y, m_pos_z));
		m_tg.setTransform(m_t3d);
	}

	/**
	 * 
	 * @see #setTransform(Transform3D p_t3d)
	 * 
	 * @deprecated
	 * To set object position, use the setTransform function.
	 * 
	 */
	public void setPosition(float p_x, float p_y, float p_z)
	{
		m_pos_x = p_x;
		m_pos_y = p_y;
		m_pos_z = p_z;

		m_t3d.setTranslation(new Vector3f(m_pos_x, m_pos_y, m_pos_z));
		m_tg.setTransform(m_t3d);
	}

	/**
	 * 
	 * @return The model's TransformGroup, which can be used directly in Java3D to add the model
	 * to the branch graph.
	 */
	public TransformGroup getObject()
	{
		return m_tg;
	}

	/**
	 * 
	 * @return Returns the number of frames this model consists of.
	 */
	public int getFrameCount()
	{
		return m_frames;
	}

	/**
	 * Returns the current frame. If the current frame is an interpolated frame, it returns the
	 * frame index of the first source frame passed to the setInterpolatedFrame() function.
	 * 
	 * @return Current frame index. If the frame is an interpolated frame, the last passed "source frame"
	 * is returned.
	 */
	public int getFrame()
	{
		return m_currentframe;
	}

	/*
	 * Loads and returns a texture from a file (p_texturename)
	 */
	private Texture getTexture(String p_texturename)
	{
		out("Loading texture '" + p_texturename + "'");
		p_texturename = p_texturename.substring(0, p_texturename.length() - 4) + ".jpg";

		//Make sure the texture file exists
		File l_f = new File(p_texturename);
		if (!l_f.exists())
		{
			err("Bad texture name: " + p_texturename);
			return null;
		}

		if (TextureList.hasTexture(p_texturename))
		{
			//	err("Texture found in hash: " + p_texturename);
			return TextureList.getTexture(p_texturename);
		}
		else
		{
			//	err("Loading texture: " + p_texturename);
			TextureList.storeTexture(new TextureLoader(p_texturename, null).getTexture(), p_texturename);
			return TextureList.getTexture(p_texturename);
		}
	}

	/*
	 * Returns the normal vector for a given face at the current frame
	 */
	private Vector3f getNormal(int p_object, int p_face)
	{
		return new Vector3f(m_current_facenormal[p_object][p_face][0], m_current_facenormal[p_object][p_face][1], m_current_facenormal[p_object][p_face][2]);
	}

	/*
	 * Returns the distance to origo from the plane a polygon (p_face) lies on
	 */
	private float getDistanceToOrigo(int p_object, int p_face, float p_nx, float p_ny, float p_nz)
	{
		//Get distance to origo. 
		//Plane equation: Ax + Bz +Cz + D = 0
		//A, B and C represents the normal X, Y and Z, while
		//D is the unit length to origo.
		//A vector dottet with another vector = 0 which means there is
		//a 90 degree angle between them, alas - we get the x, y, z
		//values if we put it all into the plane equation.
		//The vector we'll dot with can be any
		//point on the plane, so one of the face corners will do.

		//Get the vector formed by one of the face sides
		float l_x = m_pos_x + m_current_face[p_object][p_face][2][0];
		float l_y = m_pos_y + m_current_face[p_object][p_face][2][1];
		float l_z = m_pos_z + m_current_face[p_object][p_face][2][2];

		//Inserting data into the plane equation to get the distance
		float l_dist = - (p_nx * l_x) - (p_ny * l_y) - (p_nz * l_z);

		//	System.out.println("Distance from face #" +p_face+" to origo: "+l_dist);

		return l_dist;
	}

	/*
	 * Finds the intersection point between a line and a plane.
	 * Called when we figured out the line segment ends passed to the collision test
	 * did not fall on the same side of the plane.
	 */
	private Point3f getIntersectionPoint(float p_nx, float p_ny, float p_nz, float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2, float p_distfromstart)
	{
		float l_x = 0;
		float l_y = 0;
		float l_z = 0;

		float l_vec_x = p_x2 - p_x1;
		float l_vec_y = p_y2 - p_y1;
		float l_vec_z = p_z2 - p_z1;

		float l_length = (float)Math.sqrt((l_vec_x * l_vec_x) + (l_vec_y * l_vec_y) + (l_vec_z * l_vec_z));

		//Normalize the direction vector
		l_vec_x = l_vec_x / l_length;
		l_vec_y = l_vec_y / l_length;
		l_vec_z = l_vec_z / l_length;

		//The math:
		//nx*xt + ny*yt + nz*zt + dist = 0;
		//t(nx*x + ny*y + nz*z) = -dist;
		//t = -dist/(nx*x + ny*y + nz*z);
		
		//Split the division into two parts so we can check for bad denominator (== 0)
		float numerator = -p_distfromstart;
		//Denominator is the dot product between the normal vector of the face and the direction vector
		float denominator = p_nx * l_vec_x + p_ny * l_vec_y + p_nz * l_vec_z;

		float l_disttoplane = 0;
		if (denominator != 0)
		{
			l_disttoplane = numerator / denominator;
		}
		else
		{
			return null;
		}

		return new Point3f(p_x1 + l_vec_x * l_disttoplane, p_y1 + l_vec_y * l_disttoplane, p_z1 + l_vec_z * l_disttoplane);
	}

	/*
	 * The collision data memory allocated at the
	 * object creation, so we won't have to create
	 * so many points and vectors "run-time"
	 */
	private void initCollisionData()
	{
		for (int i = 0; i < MAX_COLLISIONS; i++)
		{
			m_isp[i] = new Point3f(0, 0, 0);
			m_isp_normal[i] = new Vector3f(0, 0, 0);
		}
	}

	/**
	* 
	* @return The the nearest collision point, or null if there were no collisions the last time
	* lineCollision was called.
	* 
	* @see #lineCollision(float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2)
	*/
	public Point3f getCollisionPointNear()
	{
		if (m_collisions <= 0)
		{
			return null;
		}

		return m_isp[m_col_near];
	}

	/**
	* 
	* @return The the farthest collision point, or null if there were no collisions the last time
	* lineCollision was called.
	* 
	* @see #lineCollision(float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2)
	*/
	public Point3f getCollisionPointFar()
	{
		if (m_collisions <= 0)
		{
			return null;
		}

		return m_isp[m_col_far];
	}

	/**
	* 
	* @return The normal for the nearest collision point, or null if there were no collisions the last time
	* lineCollision was called.
	* 
	* @see #lineCollision(float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2)
	*/
	public Vector3f getCollisionNormalNear()
	{
		if (m_collisions <= 0)
		{
			return null;
		}

		return m_isp_normal[m_col_near];
	}

	/**
	 * 
	 * @return The normal for the farthest collision point, or null if there were no collisions the last time
	 * lineCollision was called.
	 * 
	 * @see #lineCollision(float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2)
	 */
	public Vector3f getCollisionNormalFar()
	{
		if (m_collisions <= 0)
		{
			return null;
		}

		return m_isp_normal[m_col_far];
	}

	/**
	 * 
	 * @return An array of the collision points. If there were no collision the last time lineCollision was called,
	 * null is returned. Use the returned value from lineCollision to traverse through the array.
	 * 
	 * @see #lineCollision(float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2)
	 */
	public Point3f[] getCollisionPoints()
	{
		if (m_collisions > 0)
		{
			return m_isp;
		}
		return null;
	}

	/**
	 * 
	 * @return An array of the normals for each collision point. If there were no collisions, null is returned.
	 * Use the returned value from lineCollision to traverse through the array.
	 * 
	 * @see #lineCollision(float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2)
	 */
	public Vector3f[] getCollisionNormals()
	{
		if (m_collisions > 0)
		{
			return m_isp_normal;
		}
		return null;
	}

	/**
	 * Checks to see whether or not the passed line segment crosses one the triangles the model
	 * consists of. Please note that this function will only work correctly if: The model has been
	 * transformed using this class' setTransform() function, or if the model is untransformed.
	 * 
	 * @param p_x1 Line start x coordinate
	 * @param p_y1 Line start y coordinate
	 * @param p_z1 Line start z coordinate
	 * @param p_x2 Line end x coordinate
	 * @param p_y2 Line end y coordinate
	 * @param p_z2 Line end z coordinate
	 * @return -1 if error, otherwise it will return the number of collisions
	 */
	public int lineCollision(float p_x1, float p_y1, float p_z1, float p_x2, float p_y2, float p_z2)
	{
		float l_dist1 = 0;
		float l_dist2 = 0;
		float l_polydist = 0;
		Vector3f l_polynormal;
		float l_polynormalx = 0;
		float l_polynormaly = 0;
		float l_polynormalz = 0;

		float l_dist_near = 999999999;
		float l_dist_far = 0;
		int l_intersections = m_collisions = 0;

		for (int i = 0; i < m_objectcount; i++)
		{
			if (m_facedata[i] == null)
			{
				continue;
			}

			//Test if the vector crosses the plane of this face
			for (int face = 0; face < m_facedata[i].getFaceCount(); face++)
			{
				//First test: 	Check to see if the endpoints of the vector is on opposite
				//				sides of the plane the polygon lies on.
				//l_polynormal = getNormal(i, face); //new Vector3f(m_current_facenormal[i][face][0], m_current_facenormal[i][face][1], m_current_facenormal[i][face][2]); //getNormal(i, face);
				l_polynormalx = m_current_facenormal[i][face][0];
				l_polynormaly = m_current_facenormal[i][face][1];
				l_polynormalz = m_current_facenormal[i][face][2];
				l_polydist = getDistanceToOrigo(i, face, l_polynormalx, l_polynormaly, l_polynormalz);

				//Calculate distances from endpoints to plane
				l_dist1 = p_x1 * l_polynormalx + p_y1 * l_polynormaly + p_z1 * l_polynormalz + l_polydist;
				l_dist2 = p_x2 * l_polynormalx + p_y2 * l_polynormaly + p_z2 * l_polynormalz + l_polydist;

				//If both distances are either negative or positive, they are on the same side
				if (l_dist1 * l_dist2 >= 0)
				{
					//Product of both distances are positive means that the points are on the same side.
					//No collision... Keep checking
					continue;
				}
				else
				{
					//Not on the same side! Calculate intersection point.
					Point3f l_intersectionpoint = getIntersectionPoint(l_polynormalx, l_polynormaly, l_polynormalz, p_x1, p_y1, p_z1, p_x2, p_y2, p_z2, l_dist1);
					if (l_intersectionpoint == null)
					{
						return -1;
					}

					//All that remains now is to check whether the intersection point was inside the
					//face bounds.

					//The way we'll do that check is like this:
					//We create three imaginary lines to each of the face's edges,
					//which will result in 3 imaginary triangles. We'll then check the
					//angle for each of the new triangle from the intersection point.
					//If the sum of the three angles is ~360, we will assume the inter-
					//section point is inside the face.

					float l_tmpv1x;
					float l_tmpv1y;
					float l_tmpv1z;
					float l_tmpv1length;

					float l_tmpv2x;
					float l_tmpv2y;
					float l_tmpv2z;
					float l_tmpv2length;

					float l_angle = 0;

					//TODO: Make faster! Get rid of 'acos' and, if possible, the sqrt's!
					for (int c = 0; c < 3; c++)
					{
						l_tmpv1x = (m_pos_x + m_current_face[i][face][c][0]) - l_intersectionpoint.x;
						l_tmpv1y = (m_pos_y + m_current_face[i][face][c][1]) - l_intersectionpoint.y;
						l_tmpv1z = (m_pos_z + m_current_face[i][face][c][2]) - l_intersectionpoint.z;

						l_tmpv1length = (float)Math.sqrt(l_tmpv1x * l_tmpv1x + l_tmpv1y * l_tmpv1y + l_tmpv1z * l_tmpv1z);

						l_tmpv2x = (m_pos_x + m_current_face[i][face][(c + 1) % 3][0]) - l_intersectionpoint.x;
						l_tmpv2y = (m_pos_y + m_current_face[i][face][(c + 1) % 3][1]) - l_intersectionpoint.y;
						l_tmpv2z = (m_pos_z + m_current_face[i][face][(c + 1) % 3][2]) - l_intersectionpoint.z;

						l_tmpv2length = (float)Math.sqrt(l_tmpv2x * l_tmpv2x + l_tmpv2y * l_tmpv2y + l_tmpv2z * l_tmpv2z);
						
						l_angle += (float)Math.acos((l_tmpv1x * l_tmpv2x + l_tmpv1y * l_tmpv2y + l_tmpv1z * l_tmpv2z) / (l_tmpv1length * l_tmpv2length));
					}

					//Count in a little less than 2PI because of floating point precision...
					if (l_angle >= Math.PI * 2 * 0.9999)
					{
						//Store all collision data in an array
						m_isp[l_intersections].x = l_intersectionpoint.x;
						m_isp[l_intersections].y = l_intersectionpoint.y;
						m_isp[l_intersections].z = l_intersectionpoint.z;
						m_isp_normal[l_intersections].x = l_polynormalx;
						m_isp_normal[l_intersections].y = l_polynormaly;
						m_isp_normal[l_intersections].z = l_polynormalz;
						
						//Also store which face... Will come in handy when we want
						//to get the interpolated normal!
						m_isp_object[l_intersections] = i;
						m_isp_face[l_intersections] = face;

						float l_dist_from_start = l_intersectionpoint.x * l_intersectionpoint.x + l_intersectionpoint.y * l_intersectionpoint.y + l_intersectionpoint.z * l_intersectionpoint.z;

						//Store farthest and nearest collision points
						if (l_dist_from_start < l_dist_near)
						{
							l_dist_near = l_dist_from_start;
							m_col_near = l_intersections;
						}

						if (l_dist_from_start > l_dist_far)
						{
							l_dist_far = l_dist_from_start;
							m_col_far = l_intersections;
						}

						l_intersections++;

						if (l_intersections >= MAX_COLLISIONS)
						{
							m_collisions = l_intersections;
							err("Too many intersections: " + l_intersections);
							return l_intersections;
						}

						//	return true;
					}

					continue;
				}
			}
		}

		//This is nice to have as a classvariable
		m_collisions = l_intersections;

		return l_intersections;
	}
	
	/**
	 * Casts a line straight down and up from the point passed, in p_length in both directions.
	 * Stores the collisions points as the lineCollision()-function does. This function will be
	 * better to use for surface following than the above, because this function is
	 * a lot faster. 
	 * 
	 * Please note that this function will only work correctly if: The model has been
	 * transformed using this class' setTransform() function, or if the model is untransformed.
	 * 
	 * @param p_x1 X Coordinate of point to test for height collision
	 * @param p_y1 Y Coordinate
	 * @param p_z1 Z Coordinate
	 * @param p_depth Collision test length in both directions
	 * @return -1 if error, otherwise it will return the number of collisions
	 */
	public int lineCollisionY(float p_x1, float p_y1, float p_z1, float p_depth)
	{
		float p_y2 = p_y1 - p_depth;
		p_y1 = p_y1 + p_depth;
		float p_x2 = p_x1;
		float p_z2 = p_z1;
		float l_dist1 = 0;
		float l_dist2 = 0;
		float l_polydist = 0;
		Vector3f l_polynormal;
		float l_polynormalx = 0;
		float l_polynormaly = 0;
		float l_polynormalz = 0;

		float l_dist_near = 999999999;
		float l_dist_far = 0;
		int l_intersections = m_collisions = 0;

		for (int i = 0; i < m_objectcount; i++)
		{
			if (m_facedata[i] == null)
			{
				continue;
			}

			//Test if the vector crosses the plane of this face
			for (int face = 0; face < m_facedata[i].getFaceCount(); face++)
			{
				//Skip polygons we know for sure we're not hovering over.
				//Find bounding box for polygon
				float l_maxx = m_current_face[i][face][0][0];
				float l_minx = m_current_face[i][face][0][0];
				float l_maxz = m_current_face[i][face][0][2];
				float l_minz = m_current_face[i][face][0][2];
							
				//Get min values
				if (l_minx > m_current_face[i][face][1][0])
				{
					l_minx = m_current_face[i][face][1][0];
				}
				if (l_minz > m_current_face[i][face][1][2])
				{
					l_minz = m_current_face[i][face][1][2];
				}
				if (l_minx > m_current_face[i][face][2][0])
				{
					l_minx = m_current_face[i][face][2][0];
				}
				if (l_minz > m_current_face[i][face][2][2])
				{
					l_minz = m_current_face[i][face][2][2];
				}
				
				//Get max values
				if (l_maxz < m_current_face[i][face][2][2])
				{
					l_maxz = m_current_face[i][face][2][2];
				}
				if (l_maxx < m_current_face[i][face][2][0])
				{
					l_maxx = m_current_face[i][face][2][0];
				}
				if (l_maxz < m_current_face[i][face][1][2])
				{
					l_maxz = m_current_face[i][face][1][2];
				}
				if (l_maxx < m_current_face[i][face][1][0])
				{
					l_maxx = m_current_face[i][face][1][0];
				}
				
				//Here is the actual magic, ignoring the
				//faces not close to being hit by us!
				if (!(p_x1 >= l_minx
						&& p_x1 < l_maxx
						&& p_z1 >= l_minz
						&& p_z1 < l_maxz))
				{
					continue;
				}
				
				//First test: 	Check to see if the endpoints of the vector is on opposite
				//				sides of the plane the polygon lies on.
				//l_polynormal = getNormal(i, face); //new Vector3f(m_current_facenormal[i][face][0], m_current_facenormal[i][face][1], m_current_facenormal[i][face][2]); //getNormal(i, face);
				l_polynormalx = m_current_facenormal[i][face][0];
				l_polynormaly = m_current_facenormal[i][face][1];
				l_polynormalz = m_current_facenormal[i][face][2];
				l_polydist = getDistanceToOrigo(i, face, l_polynormalx, l_polynormaly, l_polynormalz);

				//Calculate distances from endpoints to plane
				l_dist1 = p_x1 * l_polynormalx + p_y1 * l_polynormaly + p_z1 * l_polynormalz + l_polydist;
				l_dist2 = p_x2 * l_polynormalx + p_y2 * l_polynormaly + p_z2 * l_polynormalz + l_polydist;

				//If both distances are either negative or positive, they are on the same side
				if (l_dist1 * l_dist2 >= 0)
				{
					//Product of both distances are positive means that the points are on the same side.
					//No collision... Keep checking
					continue;
				}
				else
				{
					//Not on the same side! Calculate intersection point.
					Point3f l_intersectionpoint = getIntersectionPoint(l_polynormalx, l_polynormaly, l_polynormalz, p_x1, p_y1, p_z1, p_x2, p_y2, p_z2, l_dist1);
					if (l_intersectionpoint == null)
					{
						return -1;
					}

					//All that remains now is to check whether the intersection point was inside the
					//face bounds.

					//The way we'll do that check is like this:
					//We create three imaginary lines to each of the face's edges,
					//which will result in 3 imaginary triangles. We'll then check the
					//angle for each of the new triangle from the intersection point.
					//If the sum of the three angles is ~360, we will assume the inter-
					//section point is inside the face.

					float l_tmpv1x;
					float l_tmpv1y;
					float l_tmpv1z;
					float l_tmpv1length;

					float l_tmpv2x;
					float l_tmpv2y;
					float l_tmpv2z;
					float l_tmpv2length;

					float l_angle = 0;

					//TODO: Make faster! Get rid of 'acos' and, if possible, the sqrt's!
					for (int c = 0; c < 3; c++)
					{
						l_tmpv1x = (m_pos_x + m_current_face[i][face][c][0]) - l_intersectionpoint.x;
						l_tmpv1y = (m_pos_y + m_current_face[i][face][c][1]) - l_intersectionpoint.y;
						l_tmpv1z = (m_pos_z + m_current_face[i][face][c][2]) - l_intersectionpoint.z;

						l_tmpv1length = (float)Math.sqrt(l_tmpv1x * l_tmpv1x + l_tmpv1y * l_tmpv1y + l_tmpv1z * l_tmpv1z);

						l_tmpv2x = (m_pos_x + m_current_face[i][face][(c + 1) % 3][0]) - l_intersectionpoint.x;
						l_tmpv2y = (m_pos_y + m_current_face[i][face][(c + 1) % 3][1]) - l_intersectionpoint.y;
						l_tmpv2z = (m_pos_z + m_current_face[i][face][(c + 1) % 3][2]) - l_intersectionpoint.z;

						l_tmpv2length = (float)Math.sqrt(l_tmpv2x * l_tmpv2x + l_tmpv2y * l_tmpv2y + l_tmpv2z * l_tmpv2z);
						
						l_angle += (float)Math.acos((l_tmpv1x * l_tmpv2x + l_tmpv1y * l_tmpv2y + l_tmpv1z * l_tmpv2z) / (l_tmpv1length * l_tmpv2length));
					}

					//Count in a little less than 2PI because of floating point precision...
					if (l_angle >= Math.PI * 2 * 0.9999)
					{
						//Store all collision data in an array
						m_isp[l_intersections].x = l_intersectionpoint.x;
						m_isp[l_intersections].y = l_intersectionpoint.y;
						m_isp[l_intersections].z = l_intersectionpoint.z;
						m_isp_normal[l_intersections].x = l_polynormalx;
						m_isp_normal[l_intersections].y = l_polynormaly;
						m_isp_normal[l_intersections].z = l_polynormalz;
						
						//Also store which face... Will come in handy when we want
						//to get the interpolated normal!
						m_isp_object[l_intersections] = i;
						m_isp_face[l_intersections] = face;

						float l_dist_from_start = l_intersectionpoint.x * l_intersectionpoint.x + l_intersectionpoint.y * l_intersectionpoint.y + l_intersectionpoint.z * l_intersectionpoint.z;

						//Store farthest and nearest collision points
						if (l_dist_from_start < l_dist_near)
						{
							l_dist_near = l_dist_from_start;
							m_col_near = l_intersections;
						}

						if (l_dist_from_start > l_dist_far)
						{
							l_dist_far = l_dist_from_start;
							m_col_far = l_intersections;
						}

						l_intersections++;

						if (l_intersections >= MAX_COLLISIONS)
						{
							m_collisions = l_intersections;
							err("Too many intersections: " + l_intersections);
							return l_intersections;
						}

						//	return true;
					}

					continue;
				}
			}
		}

		//This is nice to have as a classvariable
		m_collisions = l_intersections;

		return l_intersections;
	}
	
	/**
	 * Returns an ObjectFaceData array containing the model's
	 * facedata. Each entry in the array is one part of the model.
	 * The model is split up in different objects by texture, so
	 * each object will have only one texture.
	 * 
	 * @return An array of facedata objects
	 */
	public ObjectFaceData[] getObjectFaceData()
	{
		return m_facedata;
	}

	/**
	 * Exports all frames to a simple textfile format on disk.
	 * 
	 * @param p_filename File to store model data in
	 */
	public void export(String p_filename)
	{
		File l_file = new File(p_filename);

		try
		{
			FileWriter l_fw = new FileWriter(l_file);

			//Write all objects to file
			l_fw.write("objects:\n" + m_objectcount + "\n");
			l_fw.write("frames:\n" + m_frames + "\n");
			for (int o = 0; o < m_objectcount; o++)
			{
				l_fw.write("object:\n" + o + "\n");

				//First we'll write the texture
				if (m_facedata[o].m_textured)
				{
					String l_tex = m_facedata[o].getTextureFileName();
					l_tex = l_tex.substring(l_tex.lastIndexOf("\\") + 1, l_tex.length() - 4);
					l_fw.write("tex:\n" + l_tex);
					l_fw.write("\n");
				}
				//Write material color. Since all faces in one object uses the same color,
				//it should be enough to store for the first frame, first face, first vertex...
				l_fw.write("color:\n");
				l_fw.write("#" + m_facedata[o].getFaceColors()[0][0][0]);
				l_fw.write("#" + m_facedata[o].getFaceColors()[0][0][1]);
				l_fw.write("#" + m_facedata[o].getFaceColors()[0][0][2]);
				l_fw.write("\n");

				l_fw.write("faces:\n" + m_facedata[o].getFaceCount());
				l_fw.write("\n");

				//Write framedata
				for (int frame = 0; frame < m_frames; frame++)
				{
					l_fw.write("frame:\n" + Integer.toString(frame));
					l_fw.write("\n");
					l_fw.write("facedata:\n");

					for (int f = 0; f < m_facedata[o].getFaceCount(); f++)
					{
						l_fw.write("face:\n");
						l_fw.write("facenormal:\n");
						l_fw.write("#" + m_facedata[o].getFaceNormals()[frame][f][0]);
						l_fw.write("#" + m_facedata[o].getFaceNormals()[frame][f][1]);
						l_fw.write("#" + m_facedata[o].getFaceNormals()[frame][f][2]);
						l_fw.write("\n");
						for (int v = 0; v < 3; v++)
						{
							l_fw.write("vertex:\n");
							l_fw.write("#" + m_facedata[o].getFaceData()[frame][f][v][0]);
							l_fw.write("#" + m_facedata[o].getFaceData()[frame][f][v][1]);
							l_fw.write("#" + m_facedata[o].getFaceData()[frame][f][v][2]);
							l_fw.write("\n");
							l_fw.write("vertexnormal:\n");
							l_fw.write("#" + m_facedata[o].getFaceData()[frame][f][v][3]);
							l_fw.write("#" + m_facedata[o].getFaceData()[frame][f][v][4]);
							l_fw.write("#" + m_facedata[o].getFaceData()[frame][f][v][5]);
							l_fw.write("\n");
						}
					}
				}
			}

			l_fw.flush();
			l_fw.close();
		}
		catch (Exception e)
		{
			err("Error when exporting to file: " + p_filename);
		}
	}

	/*
	 * Prints out a standard message...
	 * Got a little annoying, so commented out the
	 * print. Use for debugging
	 */
	protected static void out(String p_str)
	{
		//	System.out.println("J3DObject: " + p_str);
	}

	/*
	 * Prints out an error message.
	 */
	protected static void err(String p_str)
	{
		System.err.println("J3DObject ERROR: " + p_str);
	}
}
