package JGame.Engine.Graphics.Renderers;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Graphics.Misc.Camera;
import JGame.Engine.Structures.Vector3D;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

/**
 * Base class for Renderers, used by other specialized renderers
 */
public abstract class Renderer extends JComponent
{
    public static final ArrayList<Renderer> allRenderers = new ArrayList<>();
    protected static boolean isBackfaceCullingEnabled = false;
    public boolean opaque = true;

    @Override
    protected void Initialize()
    {
        allRenderers.add(this);
    }

    /**
     * Used to delete the renderer from the renderers list for the pipeline,
     * if you want to override the function add super.Destroy() last!
     */
    @Override
    protected void OnDestroy()
    {
        allRenderers.remove(this);
    }

    protected float GetDistanceSquaredFromCamera()
    {
        return Vector3D.DistanceSquared(transform().GetGlobalPosition(), Camera.Main.transform().GetGlobalPosition());
    }

    /**
     * Renders all Transparents
     */
    public static void RenderTransparents()
    {
        List<Renderer> transparentRenderers = new ArrayList<>(allRenderers.stream().filter(renderer -> renderer.IsAvailable() && !renderer.opaque).toList());

        transparentRenderers.sort((r1, r2) ->
        {
            float depth1 = r1.GetDistanceSquaredFromCamera();
            float depth2 = r2.GetDistanceSquaredFromCamera();
            return Float.compare(depth2, depth1);
        });

        glDepthMask(false);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for(Renderer renderer : transparentRenderers)
        {
            if(!renderer.IsAvailable())
                continue;

            renderer.Render();
        }
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    /**
     * Renders all Opaques
     */
    public static void RenderOpaques()
    {
        for(Renderer renderer : allRenderers)
        {
            if(!renderer.IsAvailable() || !renderer.opaque)
                continue;

            renderer.Render();
        }
    }


    /**
     * Called every frame in all renderers, your render logic should go here
     */
    abstract protected void Render();
}
