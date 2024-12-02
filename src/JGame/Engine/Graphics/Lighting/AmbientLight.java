package JGame.Engine.Graphics.Lighting;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Structures.Matrix4x4;

import javax.naming.InitialContext;

public class AmbientLight extends Light
{


    @Override
    protected void Initialize()
    {
        super.Initialize();
        emitShadows = false;
    }

    @Override
    public void UpdateLightValues(Shader shader, int index)
    {
        super.UpdateLightValues(shader, index);
        String lightIndex = "lights[" + index + "]";

        shader.SetUniformProperty(lightIndex + ".type", LIGHT_TYPE_AMBIENT, true);
    }

    @Override
    public void RenderShadowMap()
    {
    }

    @Override
    protected Matrix4x4 CalculateProjectionMatrix()
    {
        return Matrix4x4.Identity();
    }

    @Override
    protected Matrix4x4[] CalculateViewMatrices()
    {
        return new Matrix4x4[]{Matrix4x4.Identity()};
    }
}
