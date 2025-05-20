package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Structures.Vector3D;

public class WiresphereRenderer extends WireshapeRenderer
{
    private float radius = 0.5f;
    private int segments = 32;
    private Vector3D center = Vector3D.Zero;

    @Override
    protected float[] GetVertices()
    {
        float[] vertices = new float[segments * 2 * 3];
        int index = 0;

        for (int i = 0; i < segments; i++)
        {
            float angle = (float) (2 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle) + center.x;
            float y = radius * (float) Math.sin(angle) + center.y;
            float z = center.z;

            vertices[index++] = x;
            vertices[index++] = y;
            vertices[index++] = z;
        }

        for (int i = 0; i < segments; i++)
        {
            float angle = (float) (2 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle) + center.x;
            float y = center.y;
            float z = radius * (float) Math.sin(angle) + center.z;

            vertices[index++] = x;
            vertices[index++] = y;
            vertices[index++] = z;
        }

        return vertices;
    }

    @Override
    protected int[] GetEdges()
    {
        int[] edges = new int[segments * 2 * 2];
        int index = 0;

        for (int i = 0; i < segments; i++)
        {
            int next = (i + 1) % segments;
            edges[index++] = i;
            edges[index++] = next;
        }

        for (int i = 0; i < segments; i++)
        {
            int next = (i + 1) % segments + segments;
            edges[index++] = i + segments;
            edges[index++] = next;
        }

        return edges;
    }

    public void SetRadius(float radius)
    {
        this.radius = radius;
        UpdateVertices();
    }

    public void SetSegments(int segments)
    {
        this.segments = segments;
        UpdateVertices();
    }

    public void SetCenter(Vector3D center)
    {
        this.center = center;
        UpdateVertices();
    }
}
