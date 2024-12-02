package JGame.Engine.Graphics.Lighting;

import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Textures.ShadowMap;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Utilities.CameraMatrixUtilities;


public class DirectionalLight extends Light
{

    @Override
    public void InitializeShadowmap()
    {
        shadowMap = new ShadowMap(Settings.Shadowmap.RESOLUTION_DIRECTIONAL);
    }

    @Override
    public void UpdateLightValues(Shader shader, int index)
    {
        super.UpdateLightValues(shader, index);

        String lightIndex = "lights[" + index + "]";

        shader.SetUniformProperty(lightIndex + ".forward", transform().Forward(), true);
        shader.SetUniformProperty(lightIndex + ".type", LIGHT_TYPE_DIRECTIONAL, true);
    }

    @Override
    protected Matrix4x4[] CalculateViewMatrices()
    {
        return new Matrix4x4[]{CameraMatrixUtilities.LookAt(transform().Backward().Scale(20f), transform().Forward(), transform().Up())};
    }

    @Override
    protected Matrix4x4 CalculateProjectionMatrix()
    {
        return CameraMatrixUtilities.OrthographicProjection
        (
                -Settings.Shadowmap.DIRECTIONAL_HALF_SIZE,
                Settings.Shadowmap.DIRECTIONAL_HALF_SIZE,
                -Settings.Shadowmap.DIRECTIONAL_HALF_SIZE,
                Settings.Shadowmap.DIRECTIONAL_HALF_SIZE,

                Settings.Shadowmap.DIRECTIONAL_NEAR_PLANE,
                Settings.Shadowmap.DIRECTIONAL_FAR_PLANE
        );
    }

}
