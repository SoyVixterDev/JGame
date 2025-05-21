package JGame.Engine.Graphics.Misc;

import JGame.Engine.Structures.ColorRGB;
import JGame.Engine.Structures.ColorRGBA;

/**
 * Class holding material information for Ray Tracing objects
 */
public class RayTracingMaterial
{
    /**
     * The tint of the material
     */
    public ColorRGBA color = ColorRGBA.White;
    /**
     * The color for specular reflections when using specularity
     */
    public ColorRGB specularColor = ColorRGB.White;

    /**
     * Controls the reflectiveness of the object, 0 smoothness is completely matte, and 1 is completely reflective
     */
    public float smoothness = 0.0f;
    /**
     * Controls how much light the object emits
     */
    public float emissivity = 0.0f;
    /**
     * Controls the specular probability of the object, a specularity level of 1 indicates that 100% of the rays will
     * bounce specularly, and a specularity level indicates that 100% of the rays will bounce diffusely.
     */
    public float specularity = 1.0f;

    public RayTracingMaterial(ColorRGBA color, ColorRGBA specularColor, float smoothness, float emissivity, float specularity)
    {
        this.color = color;
        this.specularColor = specularColor;
        this.smoothness = smoothness;
        this.emissivity = emissivity;
        this.specularity = specularity;
    }

    public RayTracingMaterial() { }

    public static RayTracingMaterial Default()
    {
        return new RayTracingMaterial();
    }
}
