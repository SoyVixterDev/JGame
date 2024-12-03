package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.EventSystem.Event1P;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Settings;
import JGame.Engine.Structures.ColorRGBA;

import java.util.List;

/**
 * Base Class for colliders, all colliders should extend from this class
 */
public abstract class Collider extends JComponent
{
    protected Rigidbody rigidbody;
    protected WireshapeRenderer colliderRenderer;

    private final Event1P<Boolean> OnDebugView = new Event1P<>()
    {
        @Override
        protected void OnInvoke(Boolean param)
        {
            colliderRenderer.SetActive(param);
        }
    };

    @Override
    protected final void Initialize()
    {
        rigidbody = object().GetComponentInParent(Rigidbody.class);

        colliderRenderer = CreateWireframe();
        colliderRenderer.SetColor(ColorRGBA.Green);

        colliderRenderer.SetActive(Settings.Debug.GetDebugView());

        Settings.Debug.changeDebugViewEvent.Subscribe(OnDebugView);
    }

    @Override
    protected void OnDestroy()
    {
        Settings.Debug.changeDebugViewEvent.Unsubscribe(OnDebugView);
    }

    @Override
    protected final void OnEnable()
    {
        if(rigidbody != null)
            rigidbody.AddCollider(this);
    }

    @Override
    protected final void OnDisable()
    {
        if(rigidbody != null)
            rigidbody.RemoveCollider(this);
    }

    /**
     * Gets contacts between this collider and another
     * @param other
     * The other collider
     * @return
     * A list of the contacts between colliders, empty if no contacts were found
     */
    public final List<Contact> GetContacts(Collider other)
    {
        if(other instanceof BoxCollider boxCollider)
        {
            return GetContacts(boxCollider);
        }
        else if(other instanceof SphereCollider sphereCollider)
        {
            return GetContacts(sphereCollider);
        }
        else
        {
            throw new IllegalArgumentException("Invalid Collider type!, add the collider to the GetContacts function in the Collider base class! Type: " + other.getClass().getName());
        }
    }


    /**
     * Should add a wireshape renderer of the matching shape to the object and return it
     * @return
     * The wireshape renderer
     */
    protected abstract WireshapeRenderer CreateWireframe();
    /**
     * Gets the bounding volume that encapsulates this collider
     * @return
     * The bounding volume that encapsulates this collider
     */
    public abstract BoundingVolume GetBoundingVolume();

    public abstract List<Contact> GetContacts(BoxCollider boxCollider);
    public abstract List<Contact> GetContacts(SphereCollider sphereCollider);
}
