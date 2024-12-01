package JGame.Engine.Graphics.Lighting;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Utilities.CameraMatrixUtilities;

public class SpotLight extends PointLight
{
    /**
     * Value determining the size of the inner cone of the Spotlight, equal to Cosine(cone angle)
     */
    private float innerCone = 0.81915204f;
    /**
     * Value determining the size of the outer cone of the Spotlight, equal to Cosine(cone angle)
     */
    private float outerCone = 0.70710678f;

    @Override
    public void Initialize()
    {
        super.Initialize();
        minShadowBias = 0.00005f;
        maxShadowBias = 0.00025f;

        OverrideShader = new Shader("/Shaders/Internal/DepthOnly/depthOnlyVertShader.glsl", "/Shaders/Internal/DepthOnly/depthOnlyFragShader.glsl");
    }

    @Override
    public void UpdateLightValues(Shader shader, int index)
    {
        super.UpdateLightValues(shader, index);
        String lightIndex = "lights[" + index + "]";

        shader.SetUniformProperty(lightIndex + ".forward", Transform().Forward(), true);
        shader.SetUniformProperty(lightIndex + ".outerCone", outerCone, true);
        shader.SetUniformProperty(lightIndex + ".innerCone", innerCone, true);
        shader.SetUniformProperty(lightIndex + ".type", LIGHT_TYPE_SPOT, true);
    }

    /**
     * Sets the value for the spotlight cones
     * @param innerCone
     * Angle between the center and the extreme of the inner cone, in radians
     * @param outerCone
     * Angle between the center and the extreme of the outer cone, in radians
     */
    public void SetSpotlightCone(float innerCone, float outerCone)
    {
        this.innerCone = (float)Math.cos(innerCone);
        this.outerCone = (float)Math.cos(outerCone);
    }

    @Override
    protected Matrix4x4 CalculateProjectionMatrix()
    {
        return CameraMatrixUtilities.PerspectiveProjection(90f, 1, range, Settings.Shadowmap.SPOTLIGHT_NEAR_PLANE);
    }

    @Override
    protected Matrix4x4[] CalculateViewMatrices()
    {
        return new Matrix4x4[]{CameraMatrixUtilities.LookAt(Transform().GetGlobalPosition(), Transform().Backward(), Transform().Up())};
    }
}
