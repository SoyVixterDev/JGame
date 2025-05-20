package JGame.Engine.Graphics.Misc;

import JGame.Engine.Structures.ColorRGB;
import JGame.Engine.Structures.ColorRGBA;

/**
 * Class holding material information for Ray Tracing objects
 */
public class RayTracingMaterial
{
    public ColorRGBA color = ColorRGBA.White;
    public float smoothness = 0.0f;
    public float emissivity = 0.0f;

    public RayTracingMaterial(ColorRGBA color, float smoothness)
    {
        this.color = color;
        this.smoothness = smoothness;
    }

    public static RayTracingMaterial Default()
    {
        return new RayTracingMaterial(ColorRGBA.White, 0.0f);
    }
}
