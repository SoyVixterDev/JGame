package JGame.Engine.Basic;

import JGame.Engine.Graphics.Renderers.BillboardRenderer;
import JGame.Engine.Graphics.Textures.Texture;
import JGame.Engine.EventSystem.Event1P;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Base class for the game JComponents, extend from this class to create your own custom behavior for objects,
 * you can add JComponents to JGameObjects using JGameObject.AddComponent(),
 * if a component is attached to a JGameObject then the different callback functions such as Start, Update and OnDestroy
 * will be called accordingly.
 */
public abstract class JComponent extends BaseObject
{
    public static final ArrayList<JComponent> allJComponents = new ArrayList<>();

    private JGameObject object;
    private Transform transform;
    private BillboardRenderer iconRenderer;

    private final Event1P<Boolean> debugViewCallback = new Event1P<>()
    {
        @Override
        protected void OnInvoke(Boolean param)
        {
            if(iconRenderer != null)
                iconRenderer.SetActive(param);
        }
    };

    /**
     * Factory method to create a JComponent and bind it to a JGameObject.
     * @param type The type of JComponent to instantiate.
     * @param object The JGameObject to which the JComponent is attached.
     * @param <T> The type of the JComponent.
     * @return The instantiated JComponent.
     */
    public static <T extends JComponent> T CreateComponent(Class<T> type, JGameObject object)
    {
        try
        {
            // Use reflection to create a new instance
            Constructor<T> constructor = type.getConstructor();
            constructor.setAccessible(true);

            T comp =  constructor.newInstance();
            ((JComponent)comp).InitializeComponent(object);

            comp._internalInitialize();
            comp.OnEnable();

            return comp;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create component for " + type.getName() + ". Ensure it extends JComponent and has a valid constructor.", e);
        }
    }

    /**
     * Creates a duplicate of the given JComponent and binds it to the specified JGameObject.
     * @param original
     * The original JComponent to duplicate.
     * @param object
     * The JGameObject to which the duplicate is attached.
     * @param <T>
     * The type of the JComponent.
     * @return The duplicated JComponent.
     */
    public static <T extends JComponent> T DuplicateComponent(T original, JGameObject object)
    {
        try
        {
            // Create a new instance of the same type as the original
            Class<T> type = (Class<T>) original.getClass();
            Constructor<T> constructor = type.getConstructor();
            constructor.setAccessible(true);

            T duplicate = constructor.newInstance();
            ((JComponent)duplicate).InitializeComponent(object);

            duplicate._internalInitialize();
            duplicate.OnEnable();


            Class<?> currentClass = type;
            while (currentClass != null)
            {
                Field[] fields = currentClass.getDeclaredFields();
                for (Field field : fields)
                {
                    int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers))
                    {
                        continue;
                    }

                    field.setAccessible(true);
                    try
                    {
                        Object fieldValue = field.get(original);
                        field.set(duplicate, fieldValue);
                    }
                    catch (IllegalAccessException e)
                    {
                        Logger.DebugStackTraceError("Failed to copy field: " + field.getName(), e);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            return duplicate;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to duplicate component of type " + original.getClass().getName(), e);
        }
    }


    private void InitializeComponent(JGameObject object)
    {
        this.object = object;
        this.transform = object.transform();
        Texture image = GetIcon();
        if(image != null)
        {
            iconRenderer = this.object().AddComponent(BillboardRenderer.class);
            iconRenderer.SetImage(image);
            iconRenderer.SetActive(Settings.Debug.GetDebugView());
        }
        else
        {
            iconRenderer = null;
        }

        allJComponents.add(this);

        Settings.Debug.changeDebugViewEvent.Subscribe(debugViewCallback);
    }

    @Override
    public final void Destroy()
    {
        Settings.Debug.changeDebugViewEvent.Unsubscribe(debugViewCallback);
        object.JComponents.remove(this);
        super.Destroy();
        allJComponents.remove(this);
        object = null;
    }

    /**
     * Should return the icon for the component, visible when using the debug view is active. Override with a function that returns the desired icon texture
     * @return
     * The texture that will be used for the Component's Icon
     */
    protected Texture GetIcon()
    {
        return null;
    }

    /**
     * Gets the first component in the scene that is of type
     * @param type
     * The type of the component to find
     * @return
     * The component, or null if it wasn't found
     * @param <C>
     * The type of the component
     */
    public static <C extends JComponent> C FindComponent(Class<C> type)
    {
        return type.cast(allJComponents.stream().filter(type::isInstance).findFirst().orElse(null));
    }
    /**
     * Gets the first component in the scene that is of type
     * @param type
     * The type of the component to find
     * @return
     * The component, or null if it wasn't found
     * @param <C>
     * The type of the component
     */
    public static <C extends JComponent> C FindComponents(Class<C> type)
    {
        return type.cast(allJComponents.stream().filter(type::isInstance));
    }

    @Override
    public final boolean CalculateAvailability()
    {
        return GetActive() && object.IsAvailable();
    }

    public JGameObject object()
    {
        return object;
    }
    public Transform transform()
    {
        return transform;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + " (" + object.toString() + ")";
    }
}
