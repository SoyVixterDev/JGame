package JGame.Engine.Graphics.Models;

/**
 * Wrapper class for a triangle consisting of 3 vertex indices
 */
public class Triangle
{
    public final int[] vertIndices = new int[3];

    public Triangle(int vert1, int vert2, int vert3)
    {
        vertIndices[0] = vert1;
        vertIndices[1] = vert2;
        vertIndices[2] = vert3;
    }
}