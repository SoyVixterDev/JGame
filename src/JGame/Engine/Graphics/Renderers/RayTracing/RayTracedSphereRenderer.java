package JGame.Engine.Graphics.Renderers.RayTracing;

import java.util.ArrayList;
import java.util.List;

public class RayTracedSphereRenderer extends RayTracingRenderer
{
    public static List<RayTracedSphereRenderer> allRayTracedSpheres = new ArrayList<>();

    public float radius = 0.5f;

    @Override
    protected void Initialize()
    {
        allRayTracedSpheres.add(this);
    }

    /**
     * Used to delete the renderer from the renderers list for the pipeline,
     * if you want to override the function add super.Destroy() last!
     */
    @Override
    protected void OnDestroy()
    {
        allRayTracedSpheres.remove(this);
    }
}
