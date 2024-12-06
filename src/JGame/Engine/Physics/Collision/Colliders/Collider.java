package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.EventSystem.Event1P;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Physics.Collision.Helper.BoundingVolumeHelper;
import JGame.Engine.Physics.Collision.Helper.CollisionHelper;
import JGame.Engine.Settings;
import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Vector3D;

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
            if(colliderRenderer == null && param)
                CreateRenderer();

            if(colliderRenderer != null && !param)
                colliderRenderer.Destroy();
        }
    };

    public Rigidbody GetRigidbody()
    {
        return rigidbody;
    }

    /**
     * Center of the box, in local space
     */
    protected Vector3D center = Vector3D.Zero;
    /**
     * Gets the center of the collider in local space
     * @return
     * The center of the collider in local space
     */
    public Vector3D GetCenter()
    {
        return center;
    }
    /**
     * Gets the center of the collider in world space
     * @return
     * The center of the collider in world space
     */
    public Vector3D GetCenterWorld()
    {
        return transform().LocalToWorldSpace(center);
    }
    public void SetCenter(Vector3D center)
    {
        this.center = center;
    }

    private void CreateRenderer()
    {
        colliderRenderer = CreateWireframe();
        colliderRenderer.SetColor(ColorRGBA.Green);

        colliderRenderer.SetActive(Settings.Debug.GetDebugView());
    }

    @Override
    protected final void Initialize()
    {
        rigidbody = object().GetComponentInParent(Rigidbody.class);

        if(Settings.Debug.GetDebugBVH())
            CreateRenderer();

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
     * Checks if this collider overlaps with another
     * @param other
     * The other collider
     * @return
     * True if the colliders are overlapping
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    public final boolean Overlaps(Collider other)
    {
        try
        {
            try
            {
                var method = CollisionHelper.class.getMethod("Overlaps", this.getClass(), other.getClass());
                return (Boolean) method.invoke(null, this, other);
            }
            catch(NoSuchMethodException e1)
            {
                var method = BoundingVolumeHelper.class.getMethod("Overlaps", other.getClass(), this.getClass());
                return (Boolean) method.invoke(null, other, this);
            }
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Invalid Collider type! Add the appropriate GetContacts method to this class. Type: " + other.getClass().getName(), e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while getting contacts with collider type: " + other.getClass().getName(), e);
        }
    }

    /**
     * Gets contacts between this collider and another
     * @param other
     * The other collider
     * @param limit
     * The limit count of contacts to get
     * @return
     * A list of the contacts between colliders, empty if no contacts were found
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    public final Contact GetContact(Collider other, int limit)
    {
        try
        {
            try
            {
                var method = CollisionHelper.class.getMethod("GetContact", this.getClass(), other.getClass(), int.class);
                return (Contact) method.invoke(null, this, other, limit);
            }
            catch(NoSuchMethodException e1)
            {
                var method = CollisionHelper.class.getMethod("GetContact", other.getClass(), this.getClass(), int.class);
                return (Contact) method.invoke(null, other, this, limit);
            }
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Invalid Collider type! Add the appropriate GetContacts method to this class. Type: " + other.getClass().getName(), e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while getting contacts with collider type: " + other.getClass().getName(), e);
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

    /**
     * Returns true if the point is inside the collider
     * @param point
     * The point
     * @return
     * True if the point is inside the collider
     */
    public abstract boolean CheckPoint(Vector3D point);

    /**
     * Gets a contact between the point and the collider
     * @param point
     * The point
     * @param source
     * The source of the contact
     * @return
     * A contact between the point and collider
     */
    public abstract Contact GetContactPoint(Vector3D point, Rigidbody source);
}
