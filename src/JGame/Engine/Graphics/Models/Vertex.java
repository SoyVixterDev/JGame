package JGame.Engine.Graphics.Models;

import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;

/**
 * Represents a single vertex from a 3D mesh
 */
public class Vertex
{
    public final Vector3D position;
    public final ColorRGBA color;
    public final Vector2D  uvCoordinates;
    public Vector3D normalVector;

    public Vertex(Vector3D localPosition)
    {
        this(localPosition, new Vector2D(localPosition.x, localPosition.y), Vector3D.One, ColorRGBA.White);
    }
    public Vertex(Vector3D localPosition, Vector2D uvCoordinates)
    {
        this(localPosition, uvCoordinates, Vector3D.One, ColorRGBA.White);
    }
    public Vertex(Vector3D localPosition, Vector2D uvCoordinates, ColorRGBA color)
    {
        this(localPosition, uvCoordinates, Vector3D.One, color);
    }
    public Vertex(Vector3D localPosition, Vector3D normalVector, ColorRGBA color)
    {
        this(localPosition, new Vector2D(localPosition.x, localPosition.y), normalVector, color);
    }
    public Vertex(Vector3D localPosition, Vector2D uvCoordinates, Vector3D normalVector, ColorRGBA color)
    {
        this.position = localPosition;
        this.normalVector = normalVector;
        this.color = color;
        this.uvCoordinates = uvCoordinates;
    }
}