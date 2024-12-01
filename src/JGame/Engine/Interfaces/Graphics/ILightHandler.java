package JGame.Engine.Interfaces.Graphics;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Graphics.Lighting.AmbientLight;
import JGame.Engine.Graphics.Lighting.DirectionalLight;
import JGame.Engine.Graphics.Lighting.Light;
import JGame.Engine.Graphics.Lighting.PointLight;
import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Graphics.Renderers.MeshRenderer;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Vector3D;
import org.lwjgl.opengl.GL46;

import static JGame.Engine.Settings.Lighting.MAX_LIGHTS;

/**
 * Interface used for lighting handling, add to your renderer class if you want to add support for interfacing with lighting
 */
public interface ILightHandler
{
    /**
     * Implement this function into your implemented class with a variable:
     * Light[] currentLights = new Light[MAX_LIGHTS];
     *
     * This function should return that variable
     */
    Light[] GetLights();
    /**
     * Tries to add the light to the renderer's current lights
     * @param light
     * The light to try to add
     * @return
     * True if it could add, false if not
     */
    default boolean TryAddToCurrentLights(Light light)
    {
        Vector3D currentPos = ((JComponent) this).Transform().GetGlobalPosition();

        //Not even attempt to load the light if it's too far
        if(light instanceof PointLight pointLight)
        {
            if(!pointLight.IsInRange(currentPos))
            {
                return false;
            }
        }


        int indexToReplace = 0;
        float fartherDist = Float.MAX_VALUE;

        for (int i = 0; i < MAX_LIGHTS; i++)
        {
            if(GetLights()[i] == light)
            {
                return false;
            }

            if(GetLights()[i] == null)
            {
                indexToReplace = i;
                break;
            }

            float dist = 0;

            //Only take into account the distance if the light isn't directional or ambient, as they are omnipresent and should always have priority
            if(!(GetLights()[i] instanceof DirectionalLight || GetLights()[i] instanceof AmbientLight))
            {
                dist = Vector3D.DistanceSquared(GetLights()[i].Transform().GetGlobalPosition(), currentPos);
            }

            if (fartherDist < dist)
            {
                fartherDist = dist;
                indexToReplace = i;
            }
        }

        float newLightDist = 0;

        //Only take into account the distance if the light isn't directional or ambient, as they are omnipresent and should always have priority
        if(!(light instanceof DirectionalLight || light instanceof AmbientLight))
        {
           newLightDist = Vector3D.DistanceSquared(light.Transform().GetGlobalPosition(), currentPos);
        }
        if (newLightDist >= fartherDist)
        {
            return false;
        }

        GetLights()[indexToReplace] = light;
        GetLights()[indexToReplace].shadowMap.BindToShader(((MeshRenderer)this).material.shader, indexToReplace);

        return true;
    }

    /**
     * Removes a light that if it's too far
     */
    private void RemoveFarawayLights()
    {
        Vector3D currentPos = ((JComponent) this).Transform().GetGlobalPosition();

        for(int i = 0; i < MAX_LIGHTS; i++)
        {
            if(GetLights()[i] instanceof PointLight pointLight)
            {
                if(!pointLight.IsInRange(currentPos))
                {
                    GetLights()[i] = null;
                }
            }
        }

    }

    /**
     * Should be called in the render method of your renderer class, before any render operations
     * @param shader
     * The shader to which apply lighting
     */
    default void UpdateLightData(Shader shader)
    {
        RemoveFarawayLights();

        if(!shader.GetLit())
            return;

        for(int i = 0; i < MAX_LIGHTS; i++)
        {
            if(GetLights()[i] != null && GetLights()[i].IsAvailable())
            {
                GetLights()[i].UpdateLightValues(shader, i);
            }
            else
                DisableLight(shader, i);
        }
    }

    default void DisableLight(Shader shader, int index)
    {
        String lightIndex = "lights[" + index + "]";
        shader.SetUniformProperty(lightIndex + ".type", -1, true);
    }

    default void BindLightShadowmaps()
    {
        for(int i = 0; i < MAX_LIGHTS; i++)
        {
            if(GetLights()[i] == null)
                continue;
            GetLights()[i].shadowMap.Bind(GL46.GL_TEXTURE18 + i);
        }
    }
    default void UnbindLightShadowmaps()
    {
        for(Light light : GetLights())
        {
            if(light == null)
                continue;
            light.shadowMap.Unbind();
        }
    }
}

