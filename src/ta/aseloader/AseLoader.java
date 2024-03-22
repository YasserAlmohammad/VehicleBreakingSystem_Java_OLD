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
 
 /*
  * TODO: 	-add timer and time each model frame's loadtime and write to a file
  */

package ta.aseloader;

import java.io.*;
import java.util.*;

/*
 * Vertex class. Not much more to say about it. It holds texture coordinates as well,
 * in case the vertex should be a "tvert" (really just a fancy word for a texture coordinate
 * I think... They call them that in the .ASE files.
 */
class AseVertex
{
	float m_pos[] = new float[3];
	float m_texcoord[] = new float[2];
}

/*
 * Face class. Support for normals, but not used yet.
 */
class AseFace
{
	int m_vertex[] = new int[3];

	int m_submatindex = -1;
	boolean m_submaterialized;
}

/*
 * The most important class. Or close to.
 * Holds a geometric object. It's vertices,
 * faces, tverts, tfaces, material index etc
 */
class AseGeomObject
{
	int m_count_vertex = 0;
	int m_count_face = 0;
	int m_count_tvert = 0;
	int m_count_tface = 0;
	int m_material = -1;

	int m_used_submats = 0;

	//Only run a frame column for vertices, as that's the only thing we'll animate...
	AseVertex m_vertex[][];
	AseFace m_face[];
	AseVertex m_tvertex[];
	AseFace m_tface[];

	int m_frames = 0;
	int m_frame_current = 0;

	/*
	 * Sets the amount of frames we want
	 */
	public void setFrames(int p_frames)
	{
		m_frames = p_frames;
		AseLoader.out("Setting frames: " + m_frames);
	}

	/*
	 * Sets current frame we're storing vertex data to
	 */
	public void setCurrentFrame(int p_frame)
	{
		m_frame_current = p_frame;
	}

	/*
	 * Sets the vertex count and allocates memory
	 * for 'p_count' vertices 
	 */
	public void setVertexCount(int p_count)
	{
		if (m_frame_current > 0)
		{
			return;
		}

		if (m_frames <= 0)
		{
			m_frames = 1;
		}

		AseLoader.out("Frames: " + m_frames);

		m_count_vertex = p_count;

		m_vertex = new AseVertex[m_frames][m_count_vertex];

		for (int k = 0; k < m_frames; k++)
		{
			for (int i = 0; i < m_count_vertex; i++)
			{
				m_vertex[k][i] = new AseVertex();
			}
		}
	}

	/*
	 * Same as above, but for faces.
	 */
	public void setFaceCount(int p_count)
	{
		m_count_face = p_count;

		m_face = new AseFace[m_count_face];

		for (int i = 0; i < m_count_face; i++)
		{
			m_face[i] = new AseFace();

			m_face[i].m_submatindex = -1;
			m_face[i].m_submaterialized = false;
		}
	}
	/*
	* Same as above, but for tfaces.
	*/
	public void setTFaceCount(int p_count)
	{
		m_count_tface = p_count;

		m_tface = new AseFace[m_count_tface];

		for (int i = 0; i < m_count_tface; i++)
		{
			m_tface[i] = new AseFace();
		}
	}
	/*
	 * Also same as above, but this time, it's for
	 * tvertices!
	 */
	public void setTVertexCount(int p_count)
	{
		m_count_tvert = p_count;

		m_tvertex = new AseVertex[m_count_tvert];

		for (int i = 0; i < m_count_tvert; i++)
		{
			m_tvertex[i] = new AseVertex();
		}
	}
	/*
	 * Stores the material index for this object
	 */
	public void setMaterial(int p_index)
	{
		m_material = p_index;
	}
	/*
	 * Stores vertex data for vertex 'p_index'
	 */
	public void setVertex(int p_index, float p_x, float p_y, float p_z)
	{
		m_vertex[m_frame_current][p_index].m_pos[0] = p_x;
		m_vertex[m_frame_current][p_index].m_pos[1] = p_y;
		m_vertex[m_frame_current][p_index].m_pos[2] = p_z;
	}
	/*
	 * See above.
	 */
	public void setFace(int p_index, int p_vert1, int p_vert2, int p_vert3)
	{
		m_face[p_index].m_vertex[0] = p_vert1;
		m_face[p_index].m_vertex[1] = p_vert2;
		m_face[p_index].m_vertex[2] = p_vert3;
	}
	/*
	 * See above.
	 */
	public void setTFace(int p_index, int p_tvert1, int p_tvert2, int p_tvert3)
	{
		m_tface[p_index].m_vertex[0] = p_tvert1;
		m_tface[p_index].m_vertex[1] = p_tvert2;
		m_tface[p_index].m_vertex[2] = p_tvert3;
	}
	/*
	 * See above
	 */
	public void setTVertex(int p_index, float p_u, float p_v, float p_w)
	{
		m_tvertex[p_index].m_pos[0] = p_u;
		m_tvertex[p_index].m_pos[1] = p_v;
		//The 'w' dimension of the texture coordinate is not likely to be used
		//since we're mostly using 2D maps
		m_tvertex[p_index].m_pos[2] = p_w;
	}
	/*
	 * Sets the submaterial index for a face, and flags as
	 * "submaterialized"
	 */
	public void setFaceSubmatIndex(int p_face, int p_index)
	{
		if (p_index >= 0)
		{

			m_face[p_face].m_submatindex = p_index;
			m_face[p_face].m_submaterialized = true;

			//Go through all faces and see if this face is the only one to use this submaterial.
			for (int i = 0; i < m_count_face; i++)
			{
				if (i != p_face && m_face[i].m_submaterialized && m_face[i].m_submatindex == p_index)
				{
					if (m_face[i].m_submatindex == p_index)
					{
						//Found one... stop searching
						return;
					}
				}
			}

			//No other faces use this texture index. So, we have a new one!
			m_used_submats++;
			AseLoader.out("Submats used: " + m_used_submats);
		}
		else
		{
			m_face[p_face].m_submaterialized = false;
		}
	}

	/*
	 * The following methods return obvious values.
	 */
	public int getSubmatCount()
	{
		return m_used_submats;
	}
	public int getVertexCount()
	{
		return m_count_vertex;
	}
	public int getFaceCount()
	{
		return m_count_face;
	}
	public int getTVertexCount()
	{
		return m_count_tvert;
	}
	public int getTFaceCount()
	{
		return m_count_tface;
	}
	public int getMaterialIndex()
	{
		return m_material;
	}

	/*
		* Creates a face data list based on the info for this geometric object.
		* Stores texture name in face data list as well as texture coordinates. 
		* Face data lists are easy to use, though not optimal for speed or memory, 
		* considering they only use triangle lists to store the data.
		*/
	public ObjectFaceData getFaceList(String p_texturefile, float p_scale, AseMaterial p_mat)
	{
		AseLoader.out("Sending face list, faces: " + m_count_face);
		ObjectFaceData l_facelist = new ObjectFaceData(m_frames, m_count_face, p_texturefile);

		for (int f = 0; f < m_frames; f++)
		{
			for (int i = 0; i < m_count_face; i++)
			{
				//Set vertex data for this face
				l_facelist.setFaceVertex(f, i, 0, m_vertex[f][m_face[i].m_vertex[0]].m_pos[0] * p_scale, m_vertex[f][m_face[i].m_vertex[0]].m_pos[1] * p_scale, m_vertex[f][m_face[i].m_vertex[0]].m_pos[2] * p_scale);
				l_facelist.setFaceVertex(f, i, 1, m_vertex[f][m_face[i].m_vertex[1]].m_pos[0] * p_scale, m_vertex[f][m_face[i].m_vertex[1]].m_pos[1] * p_scale, m_vertex[f][m_face[i].m_vertex[1]].m_pos[2] * p_scale);
				l_facelist.setFaceVertex(f, i, 2, m_vertex[f][m_face[i].m_vertex[2]].m_pos[0] * p_scale, m_vertex[f][m_face[i].m_vertex[2]].m_pos[1] * p_scale, m_vertex[f][m_face[i].m_vertex[2]].m_pos[2] * p_scale);

				if (p_texturefile != null)
				{
					//Set texture coordinates for this face, 2D
					l_facelist.setFaceVertexTexCoord(i, 0, m_tvertex[m_tface[i].m_vertex[0]].m_pos[0], m_tvertex[m_tface[i].m_vertex[0]].m_pos[1]);
					l_facelist.setFaceVertexTexCoord(i, 1, m_tvertex[m_tface[i].m_vertex[1]].m_pos[0], m_tvertex[m_tface[i].m_vertex[1]].m_pos[1]);
					l_facelist.setFaceVertexTexCoord(i, 2, m_tvertex[m_tface[i].m_vertex[2]].m_pos[0], m_tvertex[m_tface[i].m_vertex[2]].m_pos[1]);
				}

				if (p_mat != null)
				{
					//Store colors for this face
					l_facelist.setFaceVertexColor(i, 0, p_mat.getDiffuse()[0], p_mat.getDiffuse()[1], p_mat.getDiffuse()[2]);
					l_facelist.setFaceVertexColor(i, 1, p_mat.getDiffuse()[0], p_mat.getDiffuse()[1], p_mat.getDiffuse()[2]);
					l_facelist.setFaceVertexColor(i, 2, p_mat.getDiffuse()[0], p_mat.getDiffuse()[1], p_mat.getDiffuse()[2]);

					//	System.out.println("DEBUG: "+" R: " +p_mat.getDiffuse()[0]+" G: "+ p_mat.getDiffuse()[1]+" B: "+ p_mat.getDiffuse()[2]);
				}
			}
		}

		return l_facelist;
	}
}

/*
 * Basically the same as taAseMaterial, it's just used for
 * sub-materials,  materials in the material :)
 */
class AseSubMaterial extends AseMaterial
{
	public AseSubMaterial()
	{
		m_diffuse[0] = 1;
		m_diffuse[1] = 1;
		m_diffuse[2] = 1;
	}
}

/*
 * Material class. Holds texture name and diffuse colors.
 */
class AseMaterial
{
	private String m_filename = "";
	AseSubMaterial m_submat[];
	private int m_subs = 0;

	protected float m_diffuse[] = new float[3];

	public AseMaterial()
	{
		m_diffuse[0] = 1;
		m_diffuse[1] = 1;
		m_diffuse[2] = 1;
		m_subs = 0;
	}

	public void setSubMaterials(int p_subcount)
	{
		m_submat = new AseSubMaterial[p_subcount];

		for (int i = 0; i < p_subcount; i++)
		{
			m_submat[i] = new AseSubMaterial();
		}

		m_subs = p_subcount;
	}

	public void setFileName(String p_path)
	{
		m_filename = p_path;
	}

	public void setDiffuse(float p_r, float p_g, float p_b)
	{
		m_diffuse[0] = p_r;
		m_diffuse[1] = p_g;
		m_diffuse[2] = p_b;
	}

	public boolean hasSubMats()
	{
		if (m_subs > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public float[] getDiffuse()
	{
		return m_diffuse;
	}

	public int getSubMatCount()
	{
		return m_subs;
	}

	public String getFileName()
	{
		return m_filename;
	}

	public boolean hasTexture()
	{
		if (m_filename.length() > 0)
		{
			return true;
		}

		return false;
	}

	public AseSubMaterial getSubMat(int p_index)
	{
		if (p_index >= m_subs)
		{
			return null;
		}

		return m_submat[p_index];
	}
}

/**
 * 
 * Reads and converts .ASE files to a format which can be
 * used in Java3D
 */
public class AseLoader
{
	//It is more than important that these values match the ones in J3DObject
	public final static int SHADE_NORMAL = 0;
	public final static int SHADE_FLAT = 1;
	public final static int SHADE_CURVED = 2;
	//Add-on
	public final static int SHADE_NOTLIT = 3;

	private final static int MODE_MAT_LIST = 6;
	private final static int MODE_GEOMOBJECT = 7;

	private int m_mode_main = 0;

	private AseMaterial m_matlist[];
	private int m_count_material = 0;
	private int m_current_material = 0;

	private int m_count_submaterial = 0;
	private int m_current_submaterial = 0;

	private AseGeomObject m_geomobject_list[];
	//	taAseGeomObject m_geomobject_current;
	private int m_count_object = 0;
	private int m_current_object = -1;

	private ObjectFaceData m_object_facedata[];

	private String m_path;

	private int m_frame_current = 0;
	private int m_frame_count = 0;

	/**
	 * Constructor. Loads in the passed model file, which must be .ASE.
	 * 
	 * @param p_path The model path, not including the filename!
	 * @param p_filename The filename, not including path
	 */
	public AseLoader(String p_path, String p_filename)
	{
		String[] l_filename = { p_filename };
		loadFrames(p_path, l_filename);
	}

	/**
	 * Constructor. Loads in the passed model file(s). This constructor
	 * must be used if the model is supposed to be key-frame animated.
	 * 
	 * @param p_path The model path, not including filename
	 * @param p_filename A list of filenames representing each frame.
	 * Each frame must have the same amount of faces/vertices, and it is
	 * only the vertices which will be "animatable."
	 */
	public AseLoader(String p_path, String[] p_filename)
	{
		loadFrames(p_path, p_filename);
	}

	/*
	 * Gets a filename from caller and
	 * opens that file for reading. Reads the file
	 * line-by-line and passes each line to a
	 * process-function for handling. Reads in several
	 * frames.
	*/
	private void loadFrames(String p_path, String p_filename[])
	{
		m_path = p_path;
		File l_file = new File(p_path + File.separator + p_filename[0] + ".ase");
		FileReader l_fr;
		BufferedReader l_br;
		String l_line = "";

		//Make sure it is a valid filename
		if (!l_file.exists())
		{
			err("File does not exist: " + l_file.getAbsolutePath());
			return;
		}

		m_count_object = getNumberOfGeomObjects(l_file);

		//Allocate memory for the object list
		//createFaceDataList();
		createGeomObjectList(m_count_object);

		int k = 0;
		m_frame_count = p_filename.length;
		for (k = 0; k < m_frame_count; k++)
		{
			m_current_object = -1;
			m_frame_current = k;
			m_mode_main = 0;
			
			//TODO: remove debug
	//		System.out.println("Reading frame #"+k);

			try
			{
				//Need to reset in-string, or else only the first
				//file will be read
				l_line = "";
				l_file = new File(p_path + File.separator + p_filename[m_frame_current] + ".ase");

				//Make sure it is a valid filename
				if (!l_file.exists())
				{
					err("File does not exist: " + l_file.getAbsolutePath());
					m_frame_count = 0;
					return;
				}

				l_fr = new FileReader(l_file);
				l_br = new BufferedReader(l_fr);

				//Perform a few checks to make sure the file has the same data as the previous one
				if (m_count_object != getNumberOfGeomObjects(l_file))
				{
					err("Different object count in file " + p_filename[m_frame_current]);
					m_frame_count = 0;
					return;
				}

				//Check if the file is a .ASE file
				if (!isASE(l_file))
				{
					err("Missing or invalid .ASE header in file: " + p_filename[m_frame_current]);
					return;
				}

				out("Starting to read file");
				while (l_line != null)
				{
					l_line = l_br.readLine();

					if (l_line != null)
					{
						//Use relevant processing function depending on 'mode'
						if (m_mode_main == MODE_MAT_LIST)
						{
							processLineMaterial(l_line);
						}
						else if (m_mode_main == MODE_GEOMOBJECT)
						{
							processLineGeomObject(l_line);
						}

						//Check the current line the the general 
						processLineGeneral(l_line);
					}
					else
					{
						//End of file... Store the last object read, in case it hasn't been already.
						storeCurrentObject();
					}
				}
				l_br.close();
				l_fr.close();
				out("Done reading file");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		out("No more frames. Total frame count is " + m_frame_count);
	}

	/*
	 * Stores the recently parsed object into the object array
	 * Obsolete! Storing data directly into object list from now on.
	 */
	private void storeCurrentObject()
	{
	}

	/*
	 * Allocates memory for as many geometric objects located in the file
	 */
	private void createGeomObjectList(int p_count)
	{
		m_geomobject_list = new AseGeomObject[p_count];

		for (int i = 0; i < p_count; i++)
		{
			m_geomobject_list[i] = new AseGeomObject();
		}
	}

	/*
	* Checks for .ASE header
	*/
	private boolean isASE(File p_file)
	{
		FileReader l_fr;
		BufferedReader l_br;
		String l_line = "";

		int l_objects = 0;

		try
		{
			l_fr = new FileReader(p_file);
			l_br = new BufferedReader(l_fr);

			while (l_line != null)
			{
				l_line = l_br.readLine();

				if (l_line != null)
				{
					l_line = l_line.toUpperCase();
					if (l_line.indexOf("*3DSMAX_ASCIIEXPORT") >= 0 && l_line.indexOf("200") >= 0)
					{
						return true;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	/*
	 * Reads through the file before we start collecting data, simply to
	 * know how much memory we must set away for the objects.
	 */
	private int getNumberOfGeomObjects(File p_file)
	{
		FileReader l_fr;
		BufferedReader l_br;
		String l_line = "";

		int l_objects = 0;

		try
		{
			l_fr = new FileReader(p_file);
			l_br = new BufferedReader(l_fr);

			while (l_line != null)
			{
				l_line = l_br.readLine();

				if (l_line != null)
				{
					if (l_line.indexOf("*GEOMOBJECT") >= 0)
					{
						l_objects++;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		out("Found " + l_objects + " Geometric Objects in file");

		return l_objects;
	}

	/*
	 * Processes the current line just read in from the .ASE file.
	 * Passes the job over to other processing functions as it figures out
	 * what kind of data it is trying to handle.
	 */
	private void processLineGeneral(String p_line)
	{
		StringTokenizer l_tokenizer = new StringTokenizer(p_line, " \t");
		String l_curtoken = "";

		while (l_tokenizer.hasMoreTokens())
		{
			l_curtoken = l_tokenizer.nextToken();

			//Found the start of a material list...
			if (l_curtoken.equalsIgnoreCase("*material_list"))
			{
				m_mode_main = MODE_MAT_LIST;
				out("Setting main mode: MODE_MAT_LIST");
			}
			//Found a new geometric object. Set mode and store the previously parsed
			//geometric object (if any).
			else if (l_curtoken.equalsIgnoreCase("*geomobject"))
			{
				m_mode_main = MODE_GEOMOBJECT;
				storeCurrentObject();
				m_current_object++;
				m_geomobject_list[m_current_object].setFrames(m_frame_count);
				m_geomobject_list[m_current_object].setCurrentFrame(m_frame_current);
				out("Setting main mode: MODE_GEOMOBJECT");
			}
			//Found an object we haven't added support for yet.
			else if (l_curtoken.equalsIgnoreCase("*shapeobject") || l_curtoken.equalsIgnoreCase("*lightobject") || l_curtoken.equalsIgnoreCase("*cameraobject") || l_curtoken.equalsIgnoreCase("*helperobject"))
			{
				err("Not supported object: " + l_curtoken);
				m_mode_main = 0;
			}
		}
	}

	/*
	 * Handles material data when in material list mode. Stores texture filename in a material list.
	 */
	private void processLineMaterial(String p_line)
	{
		StringTokenizer l_tokenizer = new StringTokenizer(p_line, " \t");
		String l_curtoken = "";

		while (l_tokenizer.hasMoreTokens())
		{
			l_curtoken = l_tokenizer.nextToken();

			if (l_curtoken.equalsIgnoreCase("*material_count"))
			{
				//Get the count of materials in the file, and create a list
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						m_count_material = Integer.parseInt(l_curtoken);

						m_matlist = new AseMaterial[m_count_material];

						for (int i = 0; i < m_count_material; i++)
						{
							m_matlist[i] = new AseMaterial();
						}

						out("Materials: " + m_count_material);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*material"))
			{
				//Get material we're about to read info on
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						m_current_material = Integer.parseInt(l_curtoken);
						out("Material #: " + m_current_material);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				//Set the sub material to -1, so we'll know if the 
				//value is >= 0 we have subs in this material
				m_current_submaterial = -1;
				m_count_submaterial = -1;

				//	m_material_stage++;
			}
			//Number of submaterials for the current material
			if (l_curtoken.equalsIgnoreCase("*NUMSUBMTLS"))
			{
				//Get the count of materials in the file, and create a list
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						m_count_submaterial = Integer.parseInt(l_curtoken);

						m_matlist[m_current_material].setSubMaterials(m_count_submaterial);

						out("Submaterials: " + m_count_submaterial);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*submaterial"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						m_current_submaterial = Integer.parseInt(l_curtoken);
						out("Submaterial #: " + m_current_submaterial);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*material_diffuse"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					float l_r = 1;
					float l_g = 1;
					float l_b = 1;

					try
					{
						l_curtoken = l_tokenizer.nextToken();
						l_r = Float.parseFloat(l_curtoken);

						l_curtoken = l_tokenizer.nextToken();
						l_g = Float.parseFloat(l_curtoken);

						l_curtoken = l_tokenizer.nextToken();
						l_b = Float.parseFloat(l_curtoken);

						if (m_current_submaterial >= 0 && m_current_material >= 0)
						{
							//		out("DEBUG: colors for submat #" +m_current_submaterial +": R:"+l_r +" G:"+l_g +" B:"+l_b);

							m_matlist[m_current_material].m_submat[m_current_submaterial].setDiffuse(l_r, l_g, l_b);
						}
						else if (m_current_material >= 0)
						{
							//		out("DEBUG: colors for material #" +m_current_material +": R:"+l_r +" G:"+l_g +" B:"+l_b);

							m_matlist[m_current_material].setDiffuse(l_r, l_g, l_b);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*bitmap"))
			{
				//Get material texture file, convert to relative path and store in material list
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						//Get rid of "'s in the string. 
						StringTokenizer l_temptokenizer = new StringTokenizer(l_curtoken, "\"");
						
						if (l_temptokenizer.countTokens() > 0)
						{
							l_curtoken = l_temptokenizer.nextToken();
	
							if (l_curtoken.lastIndexOf("\\") != -1)
							{
								l_curtoken = l_curtoken.substring(l_curtoken.lastIndexOf("\\") + 1, l_curtoken.length());
							}
							l_curtoken = m_path + "\\" + l_curtoken;
	
							//If we're currently handling a submaterial, store the texture as a submaterial...
							if (m_current_submaterial >= 0)
							{
								m_matlist[m_current_material].m_submat[m_current_submaterial].setFileName(l_curtoken);
	
								out("Submaterial texture file: " + m_matlist[m_current_material].m_submat[m_current_submaterial].getFileName());
							}
							else
							{
								m_matlist[m_current_material].setFileName(l_curtoken);
	
								out("Material texture file: " + m_matlist[m_current_material].getFileName());
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*
	* Handles geomtric object data. Stores vertices, faces, textured faces and texture coordinates, as
	* well as links the object to a material. Pretty long function, though I think the code is pretty easy
	* to follow...
	*/
	private void processLineGeomObject(String p_line)
	{
		StringTokenizer l_tokenizer = new StringTokenizer(p_line, " \t:");
		String l_curtoken = "";

		while (l_tokenizer.hasMoreTokens())
		{
			l_curtoken = l_tokenizer.nextToken();

			if (l_curtoken.equalsIgnoreCase("*mesh_numvertex"))
			{
				//Get number of vertices for current mesh
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						//Compare vertex count against previous frame
						if (m_frame_current > 0 && m_geomobject_list[m_current_object].getVertexCount() != Integer.parseInt(l_curtoken))
						{
							err("Vertex count differs from previous frame for object #" + m_current_object + " Frame: " + m_frame_current);
							return;
						}

						m_geomobject_list[m_current_object].setVertexCount(Integer.parseInt(l_curtoken));
						out("Vertices: " + m_geomobject_list[m_current_object].getVertexCount());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*mesh_numfaces"))
			{
				//Get number of faces for current mesh
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						//Compare face count against previous frame
						if (m_frame_current > 0 && m_geomobject_list[m_current_object].getFaceCount() != Integer.parseInt(l_curtoken))
						{
							err("Face count differs from previous frame for object #" + m_current_object + " Frame: " + m_frame_current);
							return;
						}

						m_geomobject_list[m_current_object].setFaceCount(Integer.parseInt(l_curtoken));
						out("Faces: " + m_geomobject_list[m_current_object].getFaceCount());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*mesh_numtvfaces"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						//Compare tface count against previous frame
						if (m_frame_current > 0 && m_geomobject_list[m_current_object].getTFaceCount() != Integer.parseInt(l_curtoken))
						{
							err("TFace count differs from previous frame for object #" + m_current_object + " Frame: " + m_frame_current);
							return;
						}

						m_geomobject_list[m_current_object].setTFaceCount(Integer.parseInt(l_curtoken));
						out("Textured Faces: " + m_geomobject_list[m_current_object].getTFaceCount());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*mesh_numtvertex"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						//Compare tvert count against previous frame
						if (m_frame_current > 0 && m_geomobject_list[m_current_object].getTVertexCount() != Integer.parseInt(l_curtoken))
						{
							err("TVert count differs from previous frame for object #" + m_current_object + " Frame: " + m_frame_current);
							return;
						}

						m_geomobject_list[m_current_object].setTVertexCount(Integer.parseInt(l_curtoken));
						out("TVerts: " + m_geomobject_list[m_current_object].getTVertexCount());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*mesh_vertexnormal"))
			{
			}
			else if (l_curtoken.equalsIgnoreCase("*mesh_vertex"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					int l_curvertex = 0;
					float l_x;
					float l_y;
					float l_z;

					try
					{
						l_curtoken = l_tokenizer.nextToken();
						l_curvertex = Integer.parseInt(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_x = Float.parseFloat(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_y = Float.parseFloat(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_z = Float.parseFloat(l_curtoken);

						m_geomobject_list[m_current_object].setVertex(l_curvertex, l_x, l_y, l_z);
						//HACK: Pushing the x-y-z order around a bit because of MAX' unusual (?) axis-notification
						//m_geomobject_list[m_current_object].setVertex(l_curvertex, l_y, l_z, l_x);
						//			out("Vertex #" + l_curvertex + ": " + l_x + "," + l_y + "," + l_z);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			//Texture coordinate vertex. The .ASE file holds a 3 dimensional
			//coordinate set, though I think two of them will do for most
			//of us.
			else if (l_curtoken.equalsIgnoreCase("*mesh_tvert"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					int l_curvertex = 0;
					float l_u;
					float l_v;
					float l_w;

					try
					{
						l_curtoken = l_tokenizer.nextToken();
						l_curvertex = Integer.parseInt(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_u = Float.parseFloat(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_v = Float.parseFloat(l_curtoken);
						//W-dimension is not likely to be used
						l_curtoken = l_tokenizer.nextToken();
						l_w = Float.parseFloat(l_curtoken);

						m_geomobject_list[m_current_object].setTVertex(l_curvertex, l_u, l_v, l_w);
						//			out("TVertex #" + l_curvertex + ": " + l_u + "," + l_v + "," + l_w);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (l_curtoken.equalsIgnoreCase("*mesh_facenormal"))
			{
			}
			//Reads in and stores a face. The faces have been set up somewhat different from the other
			//stuff we read in, that's why we have the tokenizer to read into nothing now and then. Check
			//out the .ASE file and you'll understand.
			else if (l_curtoken.equalsIgnoreCase("*mesh_face"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					int l_curface = 0;
					int l_v1;
					int l_v2;
					int l_v3;
					int l_submatindex = -1;

					try
					{
						l_curtoken = l_tokenizer.nextToken();
						l_curface = Integer.parseInt(l_curtoken);
						l_tokenizer.nextToken(); //Read in a token, between each vertex index there is a letter identifier we don't need
						l_curtoken = l_tokenizer.nextToken();
						l_v1 = Integer.parseInt(l_curtoken);
						l_tokenizer.nextToken();
						l_curtoken = l_tokenizer.nextToken();
						l_v2 = Integer.parseInt(l_curtoken);
						l_tokenizer.nextToken();
						l_curtoken = l_tokenizer.nextToken();
						l_v3 = Integer.parseInt(l_curtoken);

						//Read past garbage we don't need.
						/*		l_curtoken = l_tokenizer.nextToken();
								l_curtoken = l_tokenizer.nextToken();
								l_curtoken = l_tokenizer.nextToken();
								l_curtoken = l_tokenizer.nextToken();
								l_curtoken = l_tokenizer.nextToken();
								l_curtoken = l_tokenizer.nextToken();
								l_curtoken = l_tokenizer.nextToken();
								l_curtoken = l_tokenizer.nextToken();
						
								//Just to be on the safe side. However, I think all faces are stored with this value.
								m_geomobject_list[m_current_object].setFaceSubmatIndex(l_curface, l_submatindex);
						
								//Ah. This should be identifying the materal index (sub material) for this face
								l_curtoken = l_tokenizer.nextToken();*/

						//Just to be on the safe side. However, I think all faces are stored with this value.
						m_geomobject_list[m_current_object].setFaceSubmatIndex(l_curface, l_submatindex);

						//Find columb containing submaterial index for face
						while (l_tokenizer.hasMoreTokens())
						{
							l_curtoken = l_tokenizer.nextToken();

							if (l_curtoken.equalsIgnoreCase("*mesh_mtlid"))
							{
								break;
							}
						}

						//Store submaterial index
						if (l_curtoken.equalsIgnoreCase("*mesh_mtlid"))
						{
							l_curtoken = l_tokenizer.nextToken();

							try
							{
								l_submatindex = Integer.parseInt(l_curtoken);
							}
							catch (Exception e)
							{
								err("Failed to convert to int: " + l_curtoken);
							}

							//	out("Submatindex for face #" +l_curface +": " +l_submatindex);
							m_geomobject_list[m_current_object].setFaceSubmatIndex(l_curface, l_submatindex);
						}
						else
						{
							err("No submaterial index for face #" + l_curface);
						}

						m_geomobject_list[m_current_object].setFace(l_curface, l_v1, l_v2, l_v3);
						//			out("Face #" + l_curface + ": " + l_v1 + "," + l_v2 + "," + l_v3);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			//Don't really know why they just couldn't store the texture coordinate data
			//with the vertices or the faces, but they made their own textured faces.
			//Things may go bad if there are a different number of textured faces and
			//actual faces on one object!
			else if (l_curtoken.equalsIgnoreCase("*mesh_tface"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					int l_curface = 0;
					int l_tv1;
					int l_tv2;
					int l_tv3;

					try
					{
						l_curtoken = l_tokenizer.nextToken();
						l_curface = Integer.parseInt(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_tv1 = Integer.parseInt(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_tv2 = Integer.parseInt(l_curtoken);
						l_curtoken = l_tokenizer.nextToken();
						l_tv3 = Integer.parseInt(l_curtoken);

						m_geomobject_list[m_current_object].setTFace(l_curface, l_tv1, l_tv2, l_tv3);
						//		out("TFace #" + l_curface + ": " + l_tv1 + "," + l_tv2 + "," + l_tv3);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			//Found animation key frame-stuff. Tell user that it is not supported as of now
			else if (l_curtoken.equalsIgnoreCase("*tm_animation"))
			{
				err("Key-frame animation is not supported");
			}
			//Get what index of the material this object uses
			else if (l_curtoken.equalsIgnoreCase("*material_ref"))
			{
				if (l_tokenizer.hasMoreTokens())
				{
					l_curtoken = l_tokenizer.nextToken();

					try
					{
						//Make sure there actually IS a material with this index
						if (Integer.parseInt(l_curtoken) < m_count_material)
						{
							if (m_matlist[Integer.parseInt(l_curtoken)].hasSubMats())
							{
								m_geomobject_list[m_current_object].setMaterial(Integer.parseInt(l_curtoken));
								out("Material #" + m_geomobject_list[m_current_object].getMaterialIndex() + " has submaterials");
							}
							//No need for this, now we have default colors on all  materials
							/*		else if (!m_matlist[Integer.parseInt(l_curtoken)].hasTexture())
									{
										m_geomobject_current.setMaterial(-1);
									}*/
							else
							{
								m_geomobject_list[m_current_object].setMaterial(Integer.parseInt(l_curtoken));

								out("Material #" + m_geomobject_list[m_current_object].getMaterialIndex() + ": " + m_matlist[m_geomobject_list[m_current_object].getMaterialIndex()].getFileName());
							}
						}
						else
						{
							m_geomobject_list[m_current_object].setMaterial(-1);
							out("No such material index: " + Integer.parseInt(l_curtoken));
						}

					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	protected static void out(String p_str)
	{
		//	System.out.println("ASELoader: " + p_str);
	}

	protected static void err(String p_str)
	{
		System.err.println("ASELoader ERROR: " + p_str);
	}

	/**
	 * Wrapper function. Loads a model to support lighting with curved shading.
	 * @see #getJ3DObject(float p_scale, boolean p_skipunmaterialized, boolean p_support_light, boolean p_random_colors, int p_shademode)
	 *  
	 * @param p_scale Model scale
	 * @param p_skipunmaterialized Pass true if unmaterialized faces are unwanted
	 * @param p_support_light Set to true if model is supposed to support Java3D's lighting
	 * @return J3DObject for use in Java3D
	 */
	public J3DObject getJ3DObject(float p_scale, boolean p_skipunmaterialized, boolean p_support_light)
	{
		return getJ3DObject(p_scale, p_skipunmaterialized, p_support_light, false, SHADE_CURVED);
	}

	/**
	 * Wrapper function. Loads a model to support lighting with normal shading (SHADE_NORMAL)
	 * @see #getJ3DObject(float p_scale, boolean p_skipunmaterialized, boolean p_support_light, boolean p_random_colors, int p_shademode)
	 * 
	 * @param p_scale Model scale
	 * @return J3DObject for use in Java3D
	 */
	public J3DObject getJ3DObject(float p_scale)
	{
		return getJ3DObject(p_scale, false, false, false, SHADE_NORMAL);
	}

	/**
	 * Wrapper function.
	 * @see #getJ3DObject(float p_scale, boolean p_skipunmaterialized, boolean p_support_light, boolean p_random_colors, int p_shademode)
	 * 
	 * @param p_scale Model scale
	 * @param p_shademode Shademode
	 * @return J3DObject for use in Java3D
	 */
	public J3DObject getJ3DObject(float p_scale, int p_shademode)
	{
		if (p_shademode == SHADE_NORMAL || p_shademode == SHADE_FLAT || p_shademode == SHADE_CURVED)
		{
			return getJ3DObject(p_scale, true, true, false, p_shademode);
		}
		else
		{
			return getJ3DObject(p_scale, true, false, false, 0);
		}
	}

	/**
	 * Wrapper function. 
	 * @see #getJ3DObject(float p_scale, boolean p_skipunmaterialized, boolean p_support_light, boolean p_random_colors, int p_shademode)
	 * 
	 * @param p_scale Model scale
	 * @param p_skipunmaterialized Set to true if faces with no assigned material should be ignored
	 * @param p_shademode Shademode
	 * @return J3DObject for use in Java3D
	 */
	public J3DObject getJ3DObject(float p_scale, boolean p_skipunmaterialized, int p_shademode)
	{
		if (p_shademode == SHADE_NORMAL || p_shademode == SHADE_FLAT || p_shademode == SHADE_CURVED)
		{
			return getJ3DObject(p_scale, p_skipunmaterialized, true, false, p_shademode);
		}
		else
		{
			return getJ3DObject(p_scale, p_skipunmaterialized, false, false, 0);
		}
	}

	/**
	 * Returns the loaded model in an object which will be directly used in Java3D.
	 * 
	 * @param p_scale Model scale
	 * @param p_skipunmaterialized Set to true if faces without a material should be ignored
	 * @param p_support_light Set to true if the model should support Java3D lighting
	 * @param p_random_colors Set to true will result in a model with random colors. Each material will
	 * be assigned a random color.
	 * @param p_shademode Four options: 
	 * SHADE_NORMAL: Faces with sharp angle between them will get a "hard" edge. All other edges will be soft
	 * SHADE_CURVED: All shared edges will be soft
	 * SHADE_FLAT: All faces will appear flat
	 * SHADE_NOTLIT: The model will not support lighting.
	 * @return The object ready to be added to Java3D's branchgraph, a J3DObject
	 */
	public J3DObject getJ3DObject(float p_scale, boolean p_skipunmaterialized, boolean p_support_light, boolean p_random_colors, int p_shademode)
	{
		AseGeomObject l_source;
		int l_objects_loaded = 0;
		ObjectFaceData l_facedata_list[] = null;
		int l_object_count = 0;
		float l_scale = p_scale;
		boolean l_skipuntextured = p_skipunmaterialized;
		boolean l_support_light = p_support_light;
		boolean l_random_colors = p_random_colors;
		int l_shademode = p_shademode;
		J3DObject l_j3dobject = null;

		if (m_frame_count <= 0)
		{
			err("No frames!");
			return null;
		}

		//We need to figure out how many separate objects we're going to store
		//in the j3dobject... And we store all faces with the same submaterial
		//index in one geometric object as a stand-alone object, thus we might
		//get to store more objects than we found in the .ASE file.
		for (int frame = 0; frame < m_frame_count; frame++)
		{
			out("---------FRAME #" + frame + "---------");
			for (int go = 0; go < m_count_object; go++)
			{
				int l_submats = 0;

				//Get the potential number of submaterials for current object
				if (m_geomobject_list[go].getMaterialIndex() >= 0)
				{
					if (m_matlist[m_geomobject_list[go].getMaterialIndex()].getSubMatCount() > 0)
					{
						l_submats = m_geomobject_list[go].getSubmatCount(); //m_matlist[m_geomobject_list[go].getMaterialIndex()].getSubMatCount();
					}
				}

				//Submaterialized objects are split up into as many objects as they have submaterials.
				if (l_submats > 0)
				{
					int l_cur_submat = 0;
					out("Number of submats: " + l_submats);
					//Only count in the ones that have a texture...
					while (l_cur_submat < l_submats)
					{
						//Only count in those objects that actually have a real submaterial reference
						if (l_cur_submat < l_submats) //m_matlist[m_geomobject_list[go].getMaterialIndex()].getSubMatCount())
						{
							//Removing this part, as the "untextured" part will from now on mean
							//"unmaterialized"
							/*
							//If we are to skip untextured objects, check to see if it has a valid material attached.
							if (l_skipuntextured)
							{
								if (m_matlist[m_geomobject_list[go].getMaterialIndex()].hasSubMats() && l_cur_submat < m_matlist[m_geomobject_list[go].getMaterialIndex()].getSubMatCount() && m_matlist[m_geomobject_list[go].getMaterialIndex()].getSubMat(l_cur_submat).hasTexture())
								{
									out("Counting in textured subobject: " + l_cur_submat);
									l_object_count++;
								}
								else
								{
									out("Skipping untextured subobject " + l_cur_submat);
								}
							}
							else*/ {
								l_object_count++;
							}
						}
						else
						{
							out("Skipping subobject " + l_cur_submat);
						}

						l_cur_submat++;
					}
				}
				else
				{
					//From now on "untextured" is "unmaterialized", so the next portion will be 
					//modified a little bit

					//If we want to skip untextured objects, only add to counter if it has a valid
					//material attached to it.
					if (l_skipuntextured)
					{
						if (m_geomobject_list[go].getMaterialIndex() >= 0)
						{
							out("Counting in materialized object: " + go);
							l_object_count++;
						}
						else
						{
							out("Skipping unmaterialized object: " + go);
						}
					}
					else
					{
						l_object_count++;
					}
				}
			}

			//Set up the J3DObject, and make it handle as many objects as we have here...
			//Only if we're at frame 0!
			if (frame == 0)
			{
				out("Creating " + l_object_count);
				l_j3dobject = new J3DObject(l_object_count, l_support_light, l_random_colors, l_shademode);
			}

			//Go through list of objects and load them into the j3dobject.
			for (int geomobj = 0; geomobj < m_count_object; geomobj++)
			{
				//Get object from geometric object list
				l_source = m_geomobject_list[geomobj];

				int l_submatcount = 0;

				if (l_source.getMaterialIndex() >= 0)
				{
					l_submatcount = m_matlist[l_source.getMaterialIndex()].getSubMatCount();
				}
				else
				{
					//No material, so skip object if skipflag is set.
					if (l_skipuntextured)
					{
						out("Not storing unmaterialized object: " + geomobj);
						continue;
					}
				}

				//If the object does not use submaterials, just store the whole object at once with one texture.
				if (l_submatcount <= 0)
				{
					//HACK: Only add to j3dobject when doing the last frame...
					if (frame == m_frame_count - 1)
					{
						out("Object #" + geomobj + " will be only one object, no submaterials");
						if (l_source.getMaterialIndex() < 0)
						{
							out("Object #" + geomobj + " has no material");
							//Skip untextured objects if flagged to do so
							if (!l_skipuntextured)
							{
								l_j3dobject.addObjectFaceData(l_source.getFaceList(null, l_scale, null));
							}
						}
						else
						{
							//Objects are saved differently if they have no texture...
							if (m_matlist[l_source.getMaterialIndex()].hasTexture())
							{
								l_j3dobject.addObjectFaceData(l_source.getFaceList(m_matlist[l_source.getMaterialIndex()].getFileName(), l_scale, m_matlist[l_source.getMaterialIndex()]));
							}
							else
							{
								//Skipping untextured objects if flagged to do so
								//From now on, we're only skipping unmaterialized objects, not untextured
								//	if (!l_skipuntextured)
								{
									l_j3dobject.addObjectFaceData(l_source.getFaceList(null, l_scale, m_matlist[l_source.getMaterialIndex()]));
								}
							}
						}
					}
					continue;
				}

				//NOTE 	that the object count is no longer the amount of all potensial objects,
				//		but the amount of objects we'll create from a submaterialized object.
				l_object_count = l_source.getSubmatCount();

				out("Number of objects to potentially be created from object #" + geomobj + ": " + l_object_count);

				String l_texturefilename = "";

				//Create a list of facedata objects, for as many as we _might_ need.
				//Only create it for the FIRST frame, or else we'll delete the
				//previous frames... And yes, this IS an ugly hack.
				if (frame == 0)
				{
					l_facedata_list = new ObjectFaceData[l_object_count];
				}

				//We will make one object for each submaterial... May sound weird, but I
				//don't know of any way to put two textures into the same Java3D geometry-
				//array. We miss OpenGL...
				for (int i = 0; i < l_object_count; i++)
				{
					int l_facecount = 0;
					//Need to find how many faces that use current submatindex
					for (int face = 0; face < l_source.getFaceCount(); face++)
					{
						if (l_source.m_face[face].m_submaterialized && l_source.m_face[face].m_submatindex == i)
						{
							l_facecount++;
						}
					}

					//We have have found enough data to create a new object for this sub-material
					//Handle textured submaterials separately
					if (i < l_submatcount && m_matlist[l_source.getMaterialIndex()].getSubMat(i).hasTexture())
					{
						l_texturefilename = m_matlist[l_source.getMaterialIndex()].getSubMat(i).getFileName();
						AseLoader.out("Found " + l_facecount + " using submat " + i + ": " + l_texturefilename);

						//Crap. Only for frame 0
						if (frame == 0)
						{
							l_facedata_list[i] = new ObjectFaceData(m_frame_count, l_facecount, l_texturefilename);
						}

						//Search through all object faces, but only store those with faces that holds the current
						//submaterial.
						int l_facedata_faceindex = 0;
						for (int f = 0; f < l_source.getFaceCount(); f++)
						{
							if (l_source.m_face[f].m_submaterialized && l_source.m_face[f].m_submatindex == i)
							{
								//	out("Writing face #" +f +" with submatindex "+p_source.m_face[f].m_submatindex);
								//Set vertex data for this face
								l_facedata_list[i].setFaceVertex(frame, l_facedata_faceindex, 0, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[0]].m_pos[0] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[0]].m_pos[1] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[0]].m_pos[2] * l_scale);
								l_facedata_list[i].setFaceVertex(frame, l_facedata_faceindex, 1, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[1]].m_pos[0] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[1]].m_pos[1] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[1]].m_pos[2] * l_scale);
								l_facedata_list[i].setFaceVertex(frame, l_facedata_faceindex, 2, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[2]].m_pos[0] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[2]].m_pos[1] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[2]].m_pos[2] * l_scale);

								//Store texture coordinates for this face
								l_facedata_list[i].setFaceVertexTexCoord(l_facedata_faceindex, 0, l_source.m_tvertex[l_source.m_tface[f].m_vertex[0]].m_pos[0], l_source.m_tvertex[l_source.m_tface[f].m_vertex[0]].m_pos[1]);
								l_facedata_list[i].setFaceVertexTexCoord(l_facedata_faceindex, 1, l_source.m_tvertex[l_source.m_tface[f].m_vertex[1]].m_pos[0], l_source.m_tvertex[l_source.m_tface[f].m_vertex[1]].m_pos[1]);
								l_facedata_list[i].setFaceVertexTexCoord(l_facedata_faceindex, 2, l_source.m_tvertex[l_source.m_tface[f].m_vertex[2]].m_pos[0], l_source.m_tvertex[l_source.m_tface[f].m_vertex[2]].m_pos[1]);

								//Store colors for this face
								l_facedata_list[i].setFaceVertexColor(l_facedata_faceindex, 0, m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[0], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[1], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[2]);
								l_facedata_list[i].setFaceVertexColor(l_facedata_faceindex, 1, m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[0], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[1], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[2]);
								l_facedata_list[i].setFaceVertexColor(l_facedata_faceindex, 2, m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[0], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[1], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[2]);

								//Increase current face index for this _facedatalist_
								l_facedata_faceindex++;
							}
						}

						//Not all objects with a submaterial are stored. This one is,
						//and we need to know how many objects we're going to pass to the
						//J3DObject.
						l_objects_loaded++;
					}
					else
					{
						//Only skipping unmaterialized objects, not untextured.
						//Flagged to skip untextured stuff, so skip it
						/*		if (l_skipuntextured)
								{
									continue;
								}
						*/
						AseLoader.out("Found " + l_facecount + " using submat " + i + ": No texture");

						//Again... Only create object at frame 0
						if (frame == 0)
						{
							l_facedata_list[i] = new ObjectFaceData(m_frame_count, l_facecount, null);
						}

						//Search through all object faces, but only store those with faces that holds the current
						//submaterial.
						int l_facedata_faceindex = 0;
						for (int f = 0; f < l_source.getFaceCount(); f++)
						{
							if (l_source.m_face[f].m_submaterialized && l_source.m_face[f].m_submatindex == i)
							{
								//	out("Writing face #" +f +" with submatindex "+p_source.m_face[f].m_submatindex);
								//Set vertex data for this face
								l_facedata_list[i].setFaceVertex(frame, l_facedata_faceindex, 0, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[0]].m_pos[0] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[0]].m_pos[1] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[0]].m_pos[2] * l_scale);
								l_facedata_list[i].setFaceVertex(frame, l_facedata_faceindex, 1, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[1]].m_pos[0] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[1]].m_pos[1] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[1]].m_pos[2] * l_scale);
								l_facedata_list[i].setFaceVertex(frame, l_facedata_faceindex, 2, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[2]].m_pos[0] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[2]].m_pos[1] * l_scale, l_source.m_vertex[frame][l_source.m_face[f].m_vertex[2]].m_pos[2] * l_scale);

								//Store colors for this face
								//Make sure the submaterial index is valid.
								if (m_matlist[l_source.getMaterialIndex()].getSubMatCount() > i)
								{
									try
									{
										l_facedata_list[i].setFaceVertexColor(l_facedata_faceindex, 0, m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[0], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[1], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[2]);
										l_facedata_list[i].setFaceVertexColor(l_facedata_faceindex, 1, m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[0], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[1], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[2]);
										l_facedata_list[i].setFaceVertexColor(l_facedata_faceindex, 2, m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[0], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[1], m_matlist[l_source.getMaterialIndex()].getSubMat(i).getDiffuse()[2]);
									}
									catch (Exception e)
									{
										err("Sub: " + i + " Max subs for material: " + m_matlist[l_source.getMaterialIndex()].getSubMatCount());
									}
								}
								//Increase current face index for this _facedatalist_
								l_facedata_faceindex++;
							}
						}
					}

					//	out("SubMat#"+l_source.getMaterialIndex() + " R:"+l_facedata_list[i].getFaceColors()[0][0][0]+" G: " +l_facedata_list[i].getFaceColors()[0][0][1]+" B: " +l_facedata_list[i].getFaceColors()[0][0][2]);

					//HACK: Only add when doing the last frame...
					if (frame == m_frame_count - 1)
					{
						out("Adding object " + i);
						l_j3dobject.addObjectFaceData(l_facedata_list[i]);
					}
				}
			}
		}

		return l_j3dobject;
	}

	/**
	 * 
	 * @return The number of frames the model consists of.
	 */
	public int getFrameCount()
	{
		return m_frame_count;
	}
	
	/**
	 * Returns the current version number.
	 * @return Version as a string
	 */
	public static String getVersion()
	{
		return "4.62";
	}
}
