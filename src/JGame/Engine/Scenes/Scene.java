package JGame.Engine.Scenes;

import JGame.Engine.Basic.JGameObject;

/**
 * Abstract class used to handle scenes, you can create an init scene function and define the specifics of the scene,
 * then use StartScene to load it
 */
public abstract class Scene
{
    public void StartScene()
    {
        ResetScene();
        InitScene();
    }

    protected abstract void InitScene();

    private void ResetScene()
    {
        JGameObject.DestroyAll();
    }
}
