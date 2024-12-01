package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;

public class WireplaneRenderer extends WireshapeRenderer
{
    private Vector2D size = Vector2D.One;
    private Vector3D centerOffset =  Vector3D.Zero;

    @Override
    protected float[] GetVertices()
    {
        Vector2D size = this.size.Scale(0.5f);

        return new float[]
        {
                // Bottom face
                size.x + centerOffset.x, centerOffset.y, size.y + centerOffset.z,
                size.x + centerOffset.x, centerOffset.y, -size.y + centerOffset.z,
                -size.x + centerOffset.x, centerOffset.y, -size.y + centerOffset.z,
                -size.x + centerOffset.x, centerOffset.y, size.y + centerOffset.z,
        };
    }

    public void SetCenter(Vector3D centerOffset)
    {
        this.centerOffset = centerOffset;
        UpdateVertices();
    }

    public void SetSize(Vector2D size)
    {
        this.size = size;
        UpdateVertices();
    }

    @Override
    protected int[] GetEdges()
    {
        return new int[]
        {
            0, 1, 1, 2, 2, 3, 3, 0,
            0, 2
        };
    }
}
