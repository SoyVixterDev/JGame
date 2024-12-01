package JGame.Engine.Utilities;

import JGame.Engine.Graphics.Models.Triangle;
import JGame.Engine.Graphics.Models.Vertex;

/**
 * Utilities class used for graphics calculations and OpenGL data sending
 */
public class GraphicsUtilities
{
    /**
     * Converts a set of vertices into a float array of its Colors
     * @param vertices
     * The vertices to use in the array
     * @return
     * A float array containing all the components of the colors
     */
    public static float[] VertexColorsAsFloatArray(Vertex[] vertices)
    {
        float[] colorData = new float[vertices.length * 4];

        for(int i = 0; i < vertices.length; i++)
        {
            colorData[i * 4] = vertices[i].color.r;
            colorData[i * 4 + 1] = vertices[i].color.g;
            colorData[i * 4 + 2] = vertices[i].color.b;
            colorData[i * 4 + 3] = vertices[i].color.a;
        }
        return colorData;
    }
    /**
     * Converts a set of vertices into a float array of its positions
     * @param vertices
     * The vertices to use in the array
     * @return
     * A float array containing all the positions of the vertices
     */
    public static float[] VertexPositionsAsFloatArray(Vertex[] vertices)
    {
        float[] positionData = new float[vertices.length * 3];

        for(int i = 0; i < vertices.length; i++)
        {
            positionData[i * 3] = vertices[i].position.x;
            positionData[i * 3 + 1] = vertices[i].position.y;
            positionData[i * 3 + 2] = vertices[i].position.z;
        }
        return positionData;
    }
    /**
     *
     */
    public static float[] VertexUVCoordinatesAsFloatArray(Vertex[] vertices)
    {
        float[] coordData = new float[vertices.length * 2];

        for(int i = 0; i < vertices.length; i++)
        {
            coordData[i * 2] = vertices[i].uvCoordinates.x;
            coordData[i * 2 + 1] =  1 - vertices[i].uvCoordinates.y;
        }

        return coordData;
    }

    public static float[] VertexNormalsAsFloatArray(Vertex[] vertices)
    {
        float[] normalsData = new float[vertices.length * 3];

        for(int i = 0; i < vertices.length; i++)
        {
            normalsData[i * 3] = vertices[i].normalVector.x;
            normalsData[i * 3 + 1] =  vertices[i].normalVector.y;
            normalsData[i * 3 + 2] =  vertices[i].normalVector.z;
        }

        return normalsData;
    }

    /**
     * Converts a set of triangles into an integer array of all indices
     * @param tris
     * The triangles to use for the array
     * @return
     * The integer array of all indices in the triangles
     */
    public static int[] TriangleIndicesAsIntArray(Triangle[] tris)
    {
        int[] indicesData = new int[tris.length * 3];
        for(int i = 0; i < tris.length; i++)
        {
            indicesData[i * 3] = tris[i].vertIndices[0];
            indicesData[i * 3 + 1] = tris[i].vertIndices[1];
            indicesData[i * 3 + 2] = tris[i].vertIndices[2];
        }
        return indicesData;
    }
}
