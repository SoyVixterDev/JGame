package JGame.Engine.Basic;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;

/**
 * JGameObject that can be instantiated in the world, you can add your own JComponents,
 * and it has access to its transform.
 */
public class JGameObject extends BaseEngineClass
{
    public final static ArrayList<JGameObject> allObjects = new ArrayList<>();

    private Transform transform = new Transform(this);
    ArrayList<JComponent> JComponents = new ArrayList<>();

    JGameObject()
    {
        super();
        OnEnable();
    }

    //----- Engine Callbacks -----

    /**
     * Destroys the object, its JComponents and recursively does the same for all its children
     */
    @Override
    public final void Destroy()
    {
        for(Transform child : transform.GetChildren())
        {
            child.Object().Destroy();
        }

        JComponent[] comps = new JComponent[JComponents.size()];

        comps = JComponents.toArray(comps);

        for (JComponent comp : comps)
        {
            comp.Destroy();
        }

        JComponents.clear();

        transform.Destroy();
        transform = null;

        super.Destroy();
    }

    //------ Other Functions -------

    /**
     * Creates a JGameObject at Position Zero and no rotation
     * @param components
     * The components to add after instantiation
     * @return
     * The instantiated object
     */
    @SafeVarargs
    public static JGameObject Create(Class<? extends JComponent>... components)
    {
        return Create(Vector3D.Zero, Quaternion.Identity, Vector3D.One, components);
    }

    /**
     * Creates a JGameObject at position with no rotation
     * @param position
     * Position where to instantiate the object
     * @param components
     * The components to add after instantiation
     * @return
     * The instantiated object
     */
    @SafeVarargs
    public static JGameObject Create(Vector3D position, Class<? extends JComponent>... components)
    {
        return Create(position, Quaternion.Identity, components);
    }


    /**
     * Creates a JGameObject with the selected parameters
     * @param position
     * Position where to instantiate the object
     * @param rotation
     * Rotation to apply to the instantiated object
     * @param components
     * The components to add after instantiation
     * @return
     * The instantiated object
     */
    @SafeVarargs
    public static JGameObject Create(Vector3D position, Quaternion rotation, Class<? extends JComponent>... components)
    {
        return Create(position, rotation, Vector3D.One, components);
    }

    /**
     * Creates a JGameObject with the selected parameters
     * @param position
     * Position where to instantiate the object
     * @param rotation
     * Rotation to apply to the instantiated object
     * @param scale
     * Scale to apply to the instantiated object
     * @param components
     * The components to add after instantiation
     * @return
     * The instantiated object
     */
    @SafeVarargs
    public static JGameObject Create(Vector3D position, Quaternion rotation, Vector3D scale, Class<? extends JComponent>... components)
    {
        return Create(position, rotation, scale, null, components);
    }

    /**
     * Creates a JGameObject with the selected parameters
     * @param position
     * Position where to instantiate the object
     * @param rotation
     * Rotation to apply to the instantiated object
     * @param scale
     * Scale to apply to the instantiated object
     * @param parent
     * The parent of the object
     * @param components
     * The components to add after instantiation
     * @return
     * The instantiated object
     */
    @SafeVarargs
    public static JGameObject Create(Vector3D position, Quaternion rotation, Vector3D scale, Transform parent, Class<? extends JComponent>... components)
    {
        JGameObject newObject = new JGameObject();
        allObjects.add(newObject);

        newObject.Transform().SetGlobalPosition(position);
        newObject.Transform().SetGlobalRotation(rotation);
        newObject.Transform().SetGlobalScale(scale);
        newObject.Transform().SetParent(parent);

        for(Class<? extends JComponent> _class : components)
        {
            newObject.AddComponent(_class);
        }

        return newObject;
    }
    /**
     * Adds components to the JGameObject
     * @param types
     * The types of the components to be added
     * @return
     * The array of components that had been added to the Object
     */
    @SafeVarargs
    public final JComponent[] AddComponents(Class<JComponent>... types)
    {
        JComponent[] components = new JComponent[types.length];
        int i = 0;

        for(Class<JComponent> type : types)
        {
            components[i++] = AddComponent(type);
        }

        return components;
    }
    /**
     * Adds a component to the JGameObject of type
     * @param type
     * The type of the component to be added
     * @return
     * The component that's been added to the Object
     * @param <C>
     * The type of the component
     */
    public <C extends JComponent> C AddComponent(Class<C> type)
    {
        C comp = JComponent.CreateComponent(type, this);
        JComponents.add(comp);
        return comp;
    }

    //------ Getter Functions ------

    public Transform Transform()
    {
        return transform;
    }
    /**
     * Gets the first JComponent of Type associated with the object
     * @param type
     * The type of the JComponent to get
     * @return
     * The component or null if it didn't any
     * @param <C>
     * The type of the JComponent
     */
    public <C extends JComponent> C GetComponent(Class<C> type)
    {
        return type.cast(JComponents.stream().filter(type::isInstance).findFirst().orElse(null));
    }

    /**
     * Gets the first JComponent of type associated with the object or one of its parents
     * @param type
     * The type of the JComponent to get
     * @return
     * The component or null if it didn't any
     * @param <C>
     * The type of the JComponent
     */
    public <C extends JComponent> C GetComponentInParent(Class<C> type)
    {
        Transform current = transform;
        do
        {
            C comp = current.Object().GetComponent(type);

            if(comp != null)
                return comp;

            current = transform.GetParent();
        } while(current != null);

        return null;
    }
    /**
     * Gets all the JComponents of Type associated to this object as an array list
     * @param type
     * The type of the JComponents to get
     * @return
     * The array list containing all Components of Type or null if it didn't find any
     * @param <C>
     * The type of the JComponents
     */
    public <C extends JComponent> ArrayList<C> GetComponents(Class<C> type)
    {
        ArrayList<C> arrayList = new ArrayList<>((Collection<? extends C>) JComponents.stream().filter(type::isInstance));
        return arrayList.isEmpty() ? null : arrayList;
    }
    /**
     * Gets the first component of type associated with the object or one of its parents
     * @param type
     * The type of the JComponents to get
     * @return
     * The array list containing all Components of Type or null if it didn't find any
     * @param <C>
     * The type of the JComponents
     */
    public <C extends JComponent> ArrayList<C> GetComponentsInParent(Class<C> type)
    {
        Transform current = transform;
        ArrayList<C> allcomps = new ArrayList<>();
        do
        {
            ArrayList<C> comps = current.Object().GetComponents(type);
            allcomps.addAll(comps);

            current = transform.GetParent();
        } while(current != null);

        return allcomps.isEmpty() ? null : allcomps;
    }
    /**
     * Called when closing the application, destroys all objects
     */
    public static void Terminate()
    {
        for(JGameObject object : allObjects)
            object.Destroy();
    }

    @Override
    protected final boolean CalculateAvailability()
    {
        return GetActive() && (transform.GetParent() == null || transform.GetParent().IsAvailable());
    }
}
