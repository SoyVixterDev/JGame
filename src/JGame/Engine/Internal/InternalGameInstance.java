package JGame.Engine.Internal;
import JGame.Engine.Basic.BaseEngineClass;
import Project.JGameInstance;

/**
 * Internal Base Class for Game Instance, used to handle starting the main game instance class
 */
public abstract class InternalGameInstance
{
    public final static JGameInstance Instance = new JGameInstance();

    /**
     * Handles calling the initialization function in the game instance
     */
    public final void _internalInitialize()
    {
        Initialize();
    }

    /**
     * Internal call for early update of the object, handles calling start
     */
    public final void _internalEarlyUpdate()
    {
        if(!started)
        {
            Start();
            started = true;
        }
        EarlyUpdate();
    }
    /**
     * Internal call for physics update of the object
     */
    public final void _internalPhysicsUpdate()
    {
        PhysicsUpdate();
    }
    /**
     * Internal call for update of the object, handles calling start and update accordingly
     */
    public final void _internalUpdate()
    {
        Update();
    }
    /**
     * Internal call for late update of the object
     */
    public final void _internalLateUpdate()
    {
        LateUpdate();
    }

    private boolean started = false;

    /**
     * Called right after the object is being created
     */
    protected void Initialize() { }

    /**
     * Called before the first update
     */
    protected void Start() { }
    /**
     * Called at the beginning of every frame
     */
    protected void EarlyUpdate() { }
    /**
     * Called on every frame
     */
    protected void Update() { }
    /**
     * Called at the end of every frame
     */
    protected void LateUpdate() { }
    /**
     * Called a fixed number of times per second defined by Physics.physicsUpdateInterval
     */
    protected void PhysicsUpdate() { }

    /**
     * Called when the object is Destroyed
     */
    protected void OnDestroy() { }

    /**
     * Called right after the object is created or enabled
     */
    protected void OnEnable() { }

    /**
     * Called when disabling or destroying an object
     */
    protected void OnDisable() { }

}
