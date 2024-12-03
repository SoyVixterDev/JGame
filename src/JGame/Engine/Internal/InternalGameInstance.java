package JGame.Engine.Internal;
import JGame.Engine.Basic.BaseObject;
import Project.JGameInstance;

/**
 * Internal Base Class for Game Instance, used to handle starting the main game instance class
 */
public abstract class InternalGameInstance extends BaseObject
{
    public final static JGameInstance Instance = new JGameInstance();
}
