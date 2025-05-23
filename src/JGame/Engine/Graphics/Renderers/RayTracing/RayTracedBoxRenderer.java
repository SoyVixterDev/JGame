package JGame.Engine.Graphics.Renderers.RayTracing;

import JGame.Engine.Structures.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class RayTracedBoxRenderer extends RayTracingRenderer
{
    public static List<RayTracedBoxRenderer> allRayTracedBoxes = new ArrayList<>();

    public Vector3D halfSize = new Vector3D(0.5f, 0.5f, 0.5f);
    @Override
    protected void Initialize()
    {
        allRayTracedBoxes.add(this);
    }

    @Override
    protected void OnDestroy()
    {
        allRayTracedBoxes.remove(this);
    }
}
