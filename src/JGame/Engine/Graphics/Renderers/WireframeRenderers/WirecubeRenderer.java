package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Lighting.DirectionalLight;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Vector3D;

public class WirecubeRenderer extends WireshapeRenderer
{
    private Vector3D size = Vector3D.One;
    private Vector3D centerOffset =  Vector3D.Zero;

    @Override
    protected float[] GetVertices()
    {
        Vector3D size = this.size.Scale(0.5f);

        return new float[]
        {
                // Back face
                -size.x + centerOffset.x, -size.y + centerOffset.y, -size.z + centerOffset.z,
                size.x + centerOffset.x, -size.y + centerOffset.y, -size.z + centerOffset.z,
                size.x + centerOffset.x,  size.y + centerOffset.y, -size.z + centerOffset.z,
                -size.x + centerOffset.x,  size.y + centerOffset.y, -size.z + centerOffset.z,

                // Front face
                -size.x + centerOffset.x, -size.y + centerOffset.y,  size.z + centerOffset.z,
                size.x + centerOffset.x, -size.y + centerOffset.y,  size.z + centerOffset.z,
                size.x + centerOffset.x,  size.y + centerOffset.y,  size.z + centerOffset.z,
                -size.x + centerOffset.x,  size.y + centerOffset.y,  size.z + centerOffset.z
        };
    }

    public void SetCenter(Vector3D centerOffset)
    {
        this.centerOffset = centerOffset;
        UpdateVertices();
    }

    public void SetSize(Vector3D size)
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
