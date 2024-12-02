package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Structures.Vector3D;

public class WirecubeRenderer extends WireshapeRenderer
{
    private Vector3D halfSize = Vector3D.One.Scale(0.5f);
    private Vector3D centerOffset =  Vector3D.Zero;

    @Override
    protected float[] GetVertices()
    {
        return new float[]
        {
                // Back face
                -halfSize.x + centerOffset.x, -halfSize.y + centerOffset.y, -halfSize.z + centerOffset.z,
                halfSize.x + centerOffset.x, -halfSize.y + centerOffset.y, -halfSize.z + centerOffset.z,
                halfSize.x + centerOffset.x,  halfSize.y + centerOffset.y, -halfSize.z + centerOffset.z,
                -halfSize.x + centerOffset.x,  halfSize.y + centerOffset.y, -halfSize.z + centerOffset.z,

                // Front face
                -halfSize.x + centerOffset.x, -halfSize.y + centerOffset.y,  halfSize.z + centerOffset.z,
                halfSize.x + centerOffset.x, -halfSize.y + centerOffset.y,  halfSize.z + centerOffset.z,
                halfSize.x + centerOffset.x,  halfSize.y + centerOffset.y,  halfSize.z + centerOffset.z,
                -halfSize.x + centerOffset.x,  halfSize.y + centerOffset.y,  halfSize.z + centerOffset.z
        };
    }

    public void SetCenter(Vector3D centerOffset)
    {
        this.centerOffset = centerOffset;
        UpdateVertices();
    }

    public void SetHalfSize(Vector3D size)
    {
        this.halfSize = size;
        UpdateVertices();
    }

    @Override
    protected int[] GetEdges()
    {
        return new int[]
        {
            0, 1, 1, 2, 2, 3, 3, 0,
            4, 5, 5, 6, 6, 7, 7, 4,
            0, 4, 1, 5, 2, 6, 3, 7,

            0, 2,
            5, 7,
            0, 5,
            1, 6,
            2, 7,
            3, 4
        };
    }
}
