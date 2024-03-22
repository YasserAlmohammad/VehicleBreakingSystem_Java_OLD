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
 * TODO: Add timecounter when facenormals are calculate and write to file
 */

package ta.aseloader;

/* 
 * When a .ASE file
 * have been read in, each polygon set with the same texture is set up as
 * one stand-alone facedata list
 */
/**
 * Class that holds a single object with only one texture. 
 */
public class ObjectFaceData
{
	//Stored like this:
	//Note that it's only the faces that have frame data, thus only
	//faces/vertices may be animated
	//frame, face, vertex index, vertexpos3f and vertexnormal3f
	protected float[][][][] m_face;
	//face, vindex, texcoord2f
	protected float[][][] m_facetexcoord;
	//face, vertex index, color3f
	protected float[][][] m_facecolor;
	//frame, face, normal3f
	protected float[][][] m_facenormal;

	protected int m_faces = 0;
	protected int m_frames = 0;
	protected String m_texturefile = null;

	//Is the object textured?
	protected boolean m_textured;

	/**
	 * Constructor allocates memeory for all frames' faces, and store the
	 * texture file name this model will be assigned.
	 * 
	 * @param p_frames Amount of frames the object consists of
	 * @param p_faces How many faces the object will consist of
	 * @param p_texturefile Texture file name
	 */
	public ObjectFaceData(int p_frames, int p_faces, String p_texturefile)
	{
		m_frames = p_frames;
		m_faces = p_faces;
		m_face = new float[p_frames][p_faces][3][6];
		m_facetexcoord = new float[p_faces][3][2];
		m_facecolor = new float[p_faces][3][3];
		m_facenormal = new float[p_frames][p_faces][3];

		//Set default color to 1, 1, 1
		for (int i = 0; i < p_faces; i++)
		{
			for (int v = 0; v < 3; v++)
			{
				m_facecolor[i][v][0] = 1;
				m_facecolor[i][v][1] = 1;
				m_facecolor[i][v][2] = 1;
			}
		}

		if (p_texturefile != null)
		{
			m_texturefile = p_texturefile;
			m_textured = true;
		}
		else
		{
			m_textured = false;
		}
	}

	/**
	 * Stores a vertex data for a given face index
	 * 
	 * @param p_frame Frame to store vertex data for
	 * @param p_faceindex What face to save to
	 * @param p_vertindex What vertex index of the frame to save to
	 * @param p_x Vertex x coordinate
	 * @param p_y Vertex y coordinate
	 * @param p_z Vertex z coordinate
	 */
	public void setFaceVertex(int p_frame, int p_faceindex, int p_vertindex, float p_x, float p_y, float p_z)
	{
		m_face[p_frame][p_faceindex][p_vertindex][0] = p_x;
		m_face[p_frame][p_faceindex][p_vertindex][1] = p_y;
		m_face[p_frame][p_faceindex][p_vertindex][2] = p_z;
	}

	/**
	 * Stores texture coordinate data
	 * 
	 * @param p_faceindex Face index
	 * @param p_vertindex Face vertex index
	 * @param p_u U coordinate
	 * @param p_v V coordinate
	 */
	public void setFaceVertexTexCoord(int p_faceindex, int p_vertindex, float p_u, float p_v)
	{
		m_facetexcoord[p_faceindex][p_vertindex][0] = p_u;
		m_facetexcoord[p_faceindex][p_vertindex][1] = p_v;
	}

	/**
	 * Store a face's color data
	 * 
	 * @param p_faceindex Face index
	 * @param p_vertindex Face vertex index
	 * @param p_r Red color component, float 0-1
	 * @param p_g Green color component, float 0-1
	 * @param p_b Blue color component, float 0-1
	 */
	public void setFaceVertexColor(int p_faceindex, int p_vertindex, float p_r, float p_g, float p_b)
	{
		m_facecolor[p_faceindex][p_vertindex][0] = p_r;
		m_facecolor[p_faceindex][p_vertindex][1] = p_g;
		m_facecolor[p_faceindex][p_vertindex][2] = p_b;
	}

	/**
	 * Store texture name for this object
	 * @param p_filename
	 */
	public void setTextureFileName(String p_filename)
	{
		m_texturefile = p_filename;
		
		if (m_texturefile != null)
		{
			m_textured = true;
		}
		else
		{
			m_textured = false;
		}
	}

	/**
	 * Calculates all normals, for both faces and vertices.
	 * 
	 * @param p_curved Set to true if the object should be shaded curve-like
	 */
	public void calcAllNormals(boolean p_curved)
	{
		calcFaceNormals();
		calcVertexNormals(p_curved);
	}

	/**
	 * Calculates all normals, for both faces and vertices. This
	 * function makes the object appear flat-shaded.
	 *
	 */
	public void calcAllNormalsFlat()
	{
		calcFaceNormals();
		calcVertexNormalsFlat();
	}

	/**
	 * Calculates only the face normals. If vertex normals are
	 * not interesting, this function is for you.
	 *
	 */
	public void calcFaceNormals()
	{
		for (int frame = 0; frame < m_frames; frame++)
		{
			//TODO: remove debug
			//	System.out.println("Calculating face normals for frame #" + frame);

			for (int f = 0; f < m_faces; f++)
			{
				//Store coordinates in easier-to-understand variables
				float l_v0_x = m_face[frame][f][0][0];
				float l_v0_y = m_face[frame][f][0][1];
				float l_v0_z = m_face[frame][f][0][2];

				float l_v1_x = m_face[frame][f][1][0];
				float l_v1_y = m_face[frame][f][1][1];
				float l_v1_z = m_face[frame][f][1][2];

				float l_v2_x = m_face[frame][f][2][0];
				float l_v2_y = m_face[frame][f][2][1];
				float l_v2_z = m_face[frame][f][2][2];

				//Get vectors we'll use to calculate the cross-product
				float l_edge0_x = l_v1_x - l_v0_x;
				float l_edge0_y = l_v1_y - l_v0_y;
				float l_edge0_z = l_v1_z - l_v0_z;

				float l_edge1_x = l_v2_x - l_v0_x;
				float l_edge1_y = l_v2_y - l_v0_y;
				float l_edge1_z = l_v2_z - l_v0_z;

				//Calculate the cross product
				float l_cross_x = l_edge0_y * l_edge1_z - l_edge0_z * l_edge1_y;
				float l_cross_y = l_edge0_z * l_edge1_x - l_edge0_x * l_edge1_z;
				float l_cross_z = l_edge0_x * l_edge1_y - l_edge0_y * l_edge1_x;

				float l_cross_length = (float)Math.sqrt(l_cross_x * l_cross_x + l_cross_y * l_cross_y + l_cross_z * l_cross_z);

				//Normalize cross (normal)
				if (l_cross_length > 0)
				{
					l_cross_x = l_cross_x / l_cross_length;
					l_cross_y = l_cross_y / l_cross_length;
					l_cross_z = l_cross_z / l_cross_length;
				}

				//Store normal in class normal variable
				m_facenormal[frame][f][0] = l_cross_x;
				m_facenormal[frame][f][1] = l_cross_y;
				m_facenormal[frame][f][2] = l_cross_z;
			}
		}
	}

	/*
	 * Set up vertex normals so the object will appear flat-shaded when lit
	 */
	private void calcVertexNormalsFlat()
	{
		for (int frame = 0; frame < m_frames; frame++)
		{
			//TODO: remove debug
			//	System.out.println("Calculating flat vertex normals for frame #" + frame);

			for (int f = 0; f < m_faces; f++)
			{
				for (int v = 0; v < 3; v++)
				{
					m_face[frame][f][v][3] = m_facenormal[frame][f][0];
					m_face[frame][f][v][4] = m_facenormal[frame][f][1];
					m_face[frame][f][v][5] = m_facenormal[frame][f][2];
				}
			}
		}
	}

	/*
	 * Calculates vertex normal for all frames vertices
	 */
	private void calcVertexNormals(boolean p_curved)
	{
		for (int frame = 0; frame < m_frames; frame++)
		{
			//TODO: remove debug
			//	System.out.println("Calculating vertex normals for frame #" + frame +" Curved: "+p_curved);

			for (int f = 0; f < m_faces; f++)
			{
				for (int v = 0; v < 3; v++)
				{
					//Search for faces that share this vertex (neighbour faces)
					//We need to get a normal for this vertex if the model is
					//supposed to receive lighting.
					int l_nnum = 0;
					float l_temp_x = 0;
					float l_temp_y = 0;
					float l_temp_z = 0;
					for (int nf = 0; nf < m_faces; nf++)
					{
						for (int nv = 0; nv < 3; nv++)
						{
							if (m_face[frame][nf][nv][0] == m_face[frame][f][v][0] && m_face[frame][nf][nv][1] == m_face[frame][f][v][1] && m_face[frame][nf][nv][2] == m_face[frame][f][v][2])
							{
								//If we have objects with many curved shapes, we will count in all neighbouring faces.
								//On i.e. houses this might not be a good idea, because you might get weird shadows
								//around windows or carved in stuff.
								if (!p_curved)
								{
									//Make sure the normals do not make out an x-degree angle or more...
									float l_dot = m_facenormal[frame][nf][0] * m_facenormal[frame][f][0] + m_facenormal[frame][nf][1] * m_facenormal[frame][f][1] + m_facenormal[frame][nf][2] * m_facenormal[frame][f][2];

									//Skip if angle is too sharp
									if (l_dot <= 0.3f)
									{
										continue;
									}
								}

								l_nnum++;

								//NEW: Pre-calculated face normals make the code here a lot nicer
								l_temp_x += m_facenormal[frame][nf][0];
								l_temp_y += m_facenormal[frame][nf][1];
								l_temp_z += m_facenormal[frame][nf][2];
							}
						}
					}

					//Set normal, an average from all the neighbouring faces normal
					if (l_nnum > 0)
					{
						m_face[frame][f][v][3] = l_temp_x / (float)l_nnum;
						m_face[frame][f][v][4] = l_temp_y / (float)l_nnum;
						m_face[frame][f][v][5] = l_temp_z / (float)l_nnum;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return The texture file name attached to this object.
	 */
	public String getTextureFileName()
	{
		return m_texturefile;
	}

	/**
	 * 
	 * @return Number of faces this object consists of
	 */
	public int getFaceCount()
	{
		return m_faces;
	}

	/**
	 * Returns all faces' position data
	 * @return float[frame][face (0-faces)][vertex (0-2)][vertex coordinate (x=0, z=2) AND vertex normal (nx = 3, nz = 5)]
	 */
	public float[][][][] getFaceData()
	{
		return m_face;
	}

	/**
	 * Returns texture coordinates for the faces
	 * @return float[face (0-faces)][vertex (0-2)][texcoords (u=0, v=1)]
	 */
	public float[][][] getFaceTexCoords()
	{
		return m_facetexcoord;
	}

	/**
	* Returns color components for the faces
	* @return float[face (0-faces)][vertex (0-2)][colors (r=0, g=1, b=2)]
	*/
	public float[][][] getFaceColors()
	{
		return m_facecolor;
	}

	/**
	 * Returns the face normal data
	 * @return float[face (0-faces)][vertex (0-2)][normal vector, 3f]
	 */
	public float[][][] getFaceNormals()
	{
		return m_facenormal;
	}

	/**
	 * Returns number of frames.
	 * @return Number of frames the object consists of
	 */
	public int getFrameCount()
	{
		return m_frames;
	}
}
