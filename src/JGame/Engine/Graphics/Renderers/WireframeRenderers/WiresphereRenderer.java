package JGame.Engine.Graphics.Renderers.WireframeRenderers;

import JGame.Engine.Structures.Vector3D;

public class WiresphereRenderer extends WireshapeRenderer
{
    private float radius = 0.5f;
    private int slices = 16;
    private int stacks = 16;
    private Vector3D center = Vector3D.Zero;

    @Override
    protected float[] GetVertices()
    {
        int numVertices = (slices + 1) * (stacks + 1);
        float[] vertices = new float[numVertices * 3];
        int index = 0;

        for (int i = 0; i <= stacks; i++)
        {
            float phi = (float) (Math.PI * i / stacks);
            for (int j = 0; j <= slices; j++)
            {
                float theta = (float) (2 * Math.PI * j / slices);
                float x = radius * (float) Math.sin(phi) * (float) Math.cos(theta) + center.x;
                float y = radius * (float) Math.sin(phi) * (float) Math.sin(theta) + center.y;
                float z = radius * (float) Math.cos(phi) + center.z;

                vertices[index++] = x;
                vertices[index++] = y;
                vertices[index++] = z;
            }
        }

        return vertices;
    }

    @Override
    protected int[] GetEdges()
    {
        int numEdges = 2 * slices * stacks + slices;
        int[] edges = new int[numEdges * 2];
        int index = 0;

        for (int i = 0; i < stacks; i++)
        {
            for (int j = 0; j < slices; j++)
            {
                int first = i * (slices + 1) + j;
                int second = (i + 1) * (slices + 1) + j;
                edges[index++] = first;
                edges[index++] = second;
            }
        }

        for (int i = 0; i <= stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int first = i * (slices + 1) + j;
                int second = i * (slices + 1) + (j + 1) % slices;
                edges[index++] = first;
                edges[index++] = second;
            }
        }

        return edges;
    }

    public void SetRadius(float radius)
    {
        this.radius = radius;
        UpdateVertices();
    }

    public void SetSlices(int slices)
    {
        this.slices = slices;
        UpdateVertices();
    }

    public void SetStacks(int stacks)
    {
        this.stacks = stacks;
        UpdateVertices();
    }

    public void SetCenter(Vector3D center)
    {
        this.center = center;
        UpdateVertices();
    }
}
