package JGame.Engine.Basic;

import JGame.Engine.Graphics.Renderers.BillboardRenderer;
import JGame.Engine.Graphics.Textures.Texture;
import JGame.Engine.EventSystem.Event1P;
import JGame.Engine.Settings;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Base class for the game JComponents, extend from this class to create your own custom behavior for objects,
 * you can add JComponents to JGameObjects using JGameObject.AddComponent(),
 * if a component is attached to a JGameObject then the different callback functions such as Start, Update and OnDestroy
 * will be called accordingly.
 */
public abstract class JComponent extends BaseEngineClass
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

    private void InitializeComponent(JGameObject object)
    {
        this.object = object;
        this.transform = object.transform();
        Texture image = GetIcon();
        if(image != null)
        {
            iconRenderer = this.object().AddComponent(BillboardRenderer.class);
            iconRenderer.SetImage(image);
            iconRenderer.SetActive(Settings.Engine.GetDebugView());
        }
        else
        {
            iconRenderer = null;
        }

        allJComponents.add(this);

        Settings.Engine.changeDebugViewEvent.Subscribe(debugViewCallback);
    }

    @Override
    public final void Destroy()
    {
        Settings.Engine.changeDebugViewEvent.Unsubscribe(debugViewCallback);
        object.JComponents.remove(this);
        super.Destroy();
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
}
