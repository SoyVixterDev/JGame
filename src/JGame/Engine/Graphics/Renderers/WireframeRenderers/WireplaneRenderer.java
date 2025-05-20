package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;

public class WireplaneRenderer extends WireshapeRenderer
{
    private Vector2D halfSize = Vector2D.One.Scale(0.5f);
    private Vector3D centerOffset = Vector3D.Zero;

    @Override
    protected float[] GetVertices()
    {
        return new float[]
        {
                // Bottom face
                halfSize.x + centerOffset.x, centerOffset.y, halfSize.y + centerOffset.z,
                halfSize.x + centerOffset.x, centerOffset.y, -halfSize.y + centerOffset.z,
                -halfSize.x + centerOffset.x, centerOffset.y, -halfSize.y + centerOffset.z,
                -halfSize.x + centerOffset.x, centerOffset.y, halfSize.y + centerOffset.z,
        };
    }

    public void SetCenter(Vector3D centerOffset)
    {
        this.centerOffset = centerOffset;
        UpdateVertices();
    }

    public void SetHalfSize(Vector2D halfSize)
    {
        this.halfSize = halfSize;
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
