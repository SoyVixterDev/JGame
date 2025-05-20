package JGame.Engine.Basic;

import JGame.Engine.Internal.Logger;
import Project.JGameInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * The base class for most instanceable classes used by the engine. Handles memory management, reference deletion and provides the
 * primitive versions of the callbacks like Update, PhysicsUpdate, Start, OnEnable, OnDisable, etc
 */
public abstract class BaseObject
{
    private boolean enabled = true;
    private boolean available = true;
    public static final ArrayList<BaseObject> allBaseObjects = new ArrayList<>();

    public static <T extends BaseObject> T CreateInstance(Class<T> clazz)
    {
        try
        {
            Constructor<T> baseConstructor = clazz.getConstructor();
            baseConstructor.setAccessible(true);

            T instance = baseConstructor.newInstance();

            instance._internalInitialize();
            instance.OnEnable();

            return instance;
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException("Failed to create instance for " + clazz.getName() + ". Ensure it extends BaseObject and has a default constructor.", e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create instance for " + clazz.getName() + ". Error:", e);
        }
    }

    /**
     * Don't use the constructor to instantiate BaseObjects, use BaseObject.CreateInstance(type) instead!
     * Don't Override this constructor, for initialization logic use the Initialize function!
     */
    protected BaseObject()
    {
        allBaseObjects.add(this);
    }

    /**
     * Internal call for initialization of the object
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

    /**
     * Destroys the object by deleting all references to it.
     * The base version of this function should always be called.
     * Add super.Destroy() in the end if the function is overridden.
     */
    public void Destroy()
    {
        SetActive(false);
        OnDestroy();
        ReferenceNullifierHandler(this);

        allBaseObjects.remove(this);
    }

    /**
     * Changes the Active state of the object
     * @param value
     * The new state
     */
    public final void SetActive(boolean value)
    {
        if(value == enabled)
            return;

        enabled = value;

        UpdateAvailability();
    }

    public final boolean GetActive()
    {
        return enabled;
    }

    /**
     * Internal function to update the available variable
     */
    private void UpdateAvailability()
    {
        boolean newAvailability = CalculateAvailability();

        if(newAvailability != available)
        {
            if(newAvailability)
            {
                OnEnable();
            }
            else
            {
                OnDisable();
            }
        }

        available = newAvailability;


        if(this instanceof JGameObject object)
        {
            for(JComponent comp : object.JComponents)
            {
                ((BaseObject)comp).UpdateAvailability();
            }
            for(Transform child : object.transform().GetChildren())
            {
                if(child.object() != null)
                    ((BaseObject)child.object()).UpdateAvailability();
            }
        }
    }

    /**
     * Function used to calculate the availability for the object
     * @return
     * Should return true if the conditions of the object dictate it is available to be used by code
     */
    protected boolean CalculateAvailability()
    {
        return enabled;
    }

    /**
     * Returns true if the object is available to be used by code, for example if it is active.
     * Override this function with custom code if necessary
     * @return
     * True if the object is available
     */
    public final boolean IsAvailable()
    {
        return available;
    }

    /**
     * Handles nullifying all the references to the target engine class
     * @param target
     * The BaseObject to be deleted from all references
     */
    protected static void ReferenceNullifierHandler(BaseObject target)
    {
        for(BaseObject baseObject : allBaseObjects)
        {
            baseObject.NullifyReferencesTo(target);
        }

        JGameInstance.Instance.NullifyReferencesTo(target);
    }
    /**
     * Uses reflection to set all references to the target object in this class instance to null,
     * including fields that are arrays containing references to the target object.
     * @param target The object to nullify.
     */
    protected final void NullifyReferencesTo(BaseObject target)
    {
        Class<?> currentClass = this.getClass();

        while (currentClass != null)
        {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields)
            {
                if (Modifier.isFinal(field.getModifiers()))
                    continue;

                field.setAccessible(true);
                try
                {
                    Object fieldValue = field.get(this);

                    if (BaseObject.class.isAssignableFrom(field.getType()))
                    {
                        if (fieldValue == target)
                        {
                            field.set(this, null);
                        }
                    }
                    else if (field.getType().isArray() && BaseObject.class.isAssignableFrom(field.getType().getComponentType()))
                    {
                        BaseObject[] array = (BaseObject[]) fieldValue;
                        if (array != null)
                        {
                            for (int i = 0; i < array.length; i++)
                            {
                                if (array[i] == target)
                                {
                                    array[i] = null;
                                }
                            }
                        }
                    }
                }
                catch (IllegalAccessException e)
                {
                    Logger.DebugStackTraceError("An error occurred while nullifying references to: " + target, e);
                }
            }

            currentClass = currentClass.getSuperclass();
        }
    }

}
