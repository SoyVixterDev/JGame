package JGame.Engine.Basic;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * JGameObject that can be instantiated in the world, you can add your own JComponents,
 * and it has access to its transform.
 */
public class JGameObject extends BaseObject
{
    public final static ArrayList<JGameObject> allObjects = new ArrayList<>();

    public String name;
    private Transform transform = new Transform(this);
    ArrayList<JComponent> JComponents = new ArrayList<>();

    //----- Debug Callbacks -----

    /**
     * Destroys the object, its JComponents and recursively does the same for all its children
     */
    @Override
    public final void Destroy()
    {
        if(transform == null)
            return;

        for(Transform child : transform.GetChildren())
        {
            child.object().Destroy();
        }

        JComponent[] comps = new JComponent[JComponents.size()];

        comps = JComponents.toArray(comps);

        for (JComponent comp : comps)
        {
            comp.Destroy();
        }

        JComponents.clear();

        super.Destroy();

        transform.Destroy();
        transform = null;

        allObjects.remove(this);
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
    public static JGameObject Create(String name, Class<? extends JComponent>... components)
    {
        return Create(name, Vector3D.Zero, Quaternion.Identity, Vector3D.One, components);
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
    public static JGameObject Create(String name, Vector3D position, Class<? extends JComponent>... components)
    {
        return Create(name, position, Quaternion.Identity, components);
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
    public static JGameObject Create(String name, Vector3D position, Quaternion rotation, Class<? extends JComponent>... components)
    {
        return Create(name, position, rotation, Vector3D.One, components);
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
    public static JGameObject Create(String name, Vector3D position, Quaternion rotation, Vector3D scale, Class<? extends JComponent>... components)
    {
        return Create(name, position, rotation, scale, null, components);
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
    public static JGameObject Create(String name, Vector3D position, Quaternion rotation, Vector3D scale, Transform parent, Class<? extends JComponent>... components)
    {
        JGameObject newObject = BaseObject.CreateInstance(JGameObject.class);
        allObjects.add(newObject);

        newObject.name = name;
        newObject.transform().SetGlobalPosition(position);
        newObject.transform().SetGlobalRotation(rotation);
        newObject.transform().SetGlobalScale(scale);
        newObject.transform().SetParent(parent);

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

    public Transform transform()
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
            C comp = current.object().GetComponent(type);

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
            ArrayList<C> comps = current.object().GetComponents(type);
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

        for(JGameObject object : new ArrayList<>(allObjects))
        {
            object.Destroy();
        }
    }

    @Override
    protected final boolean CalculateAvailability()
    {
        return GetActive() && (transform.GetParent() == null || transform.GetParent().IsAvailable());
    }

    @Override
    public String toString()
    {
        return name + " (" + super.toString() + ")";
    }
}
