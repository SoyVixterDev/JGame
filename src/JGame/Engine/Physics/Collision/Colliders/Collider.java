package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.EventSystem.Event1P;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WireshapeRenderer;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contact.Contact;
import JGame.Engine.Physics.Collision.Helper.CollisionHelper;
import JGame.Engine.Physics.Raycast.RaycastContact;
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

    /**
     * Sets the rigidbody to which this collider is attached to, this function shouldn't be usually used
     * @param rigidbody
     * The rigidbody to set
     */
    public void SetRigidbody(Rigidbody rigidbody)
    {
        this.rigidbody = rigidbody;
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

        if(Settings.Debug.GetDebugView())
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
            rigidbody.  AddCollider(this);
    }

    @Override
    protected final void OnDisable()
    {
        if(rigidbody != null)
            rigidbody.RemoveCollider(this);
    }

    /**
     * Gets and returns a contact from a raycast
     * @param origin
     * The origin of the ray
     * @param direction
     * The direction of the ray
     * @param maxDistance
     * The maximum distance
     * @return
     * The contact, or null if it doesn't find any
     */
    public abstract RaycastContact Raycast(Vector3D origin, Vector3D direction, float maxDistance);

    /**
     * Checks if this collider overlaps with another
     * @param other
     * The other collider
     * @return
     * True if the colliders are overlapping
     */
    public final boolean Overlaps(Collider other)
    {
        // Handle cases where `this` is a BoxCollider
        if (this instanceof BoxCollider box)
        {
            if (other instanceof BoxCollider boxB)
            {
                return CollisionHelper.Overlaps(box, boxB);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.Overlaps(box, sphere);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.Overlaps(box, capsule);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.Overlaps(box, cylinder);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.Overlaps(box, plane);
            }
        }

        // Handle cases where `this` is a SphereCollider
        else if (this instanceof SphereCollider sphere)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.Overlaps(box, sphere);
            }
            else if (other instanceof SphereCollider sphereB)
            {
                return CollisionHelper.Overlaps(sphere, sphereB);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.Overlaps(sphere, capsule);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.Overlaps(sphere, cylinder);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.Overlaps(sphere, plane);
            }
        }

        // Handle cases where `this` is a CapsuleCollider
        else if (this instanceof CapsuleCollider capsule)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.Overlaps(box, capsule);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.Overlaps(sphere, capsule);
            }
            else if (other instanceof CapsuleCollider capsuleB)
            {
                return CollisionHelper.Overlaps(capsule, capsuleB);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.Overlaps(cylinder, capsule);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.Overlaps(capsule, plane);
            }
        }

        // Handle cases where `this` is a CylinderCollider
        else if (this instanceof CylinderCollider cylinder)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.Overlaps(box, cylinder);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.Overlaps(sphere, cylinder);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.Overlaps(cylinder, capsule);
            }
            else if (other instanceof CylinderCollider cylinderB)
            {
                return CollisionHelper.Overlaps(cylinder, cylinderB);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.Overlaps(cylinder, plane);
            }
        }

        // Handle cases where `this` is a PlaneCollider
        else if (this instanceof PlaneCollider plane)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.Overlaps(box, plane);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.Overlaps(sphere, plane);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.Overlaps(capsule, plane);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.Overlaps(cylinder, plane);
            }
            else if (other instanceof PlaneCollider planeB)
            {
                return CollisionHelper.Overlaps(plane, planeB);
            }
        }

        throw new IllegalArgumentException(
                "Invalid Collider type combination! Collider types: "
                        + this.getClass().getName() + " and "
                        + other.getClass().getName()
        );
    }


    /**
     * Gets contacts between this collider and another
     * @param other
     * The other collider
     * @return
     * A contact between colliders, null if no contacts were found
     */
    public final Contact GetContact(Collider other)
    {
        // Handle cases where `this` is a BoxCollider
        if (this instanceof BoxCollider box)
        {
            if (other instanceof BoxCollider boxB)
            {
                return CollisionHelper.GetContact(box, boxB);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.GetContact(box, sphere);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.GetContact(box, capsule);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.GetContact(box, cylinder);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.GetContact(box, plane);
            }
        }

        // Handle cases where `this` is a SphereCollider
        else if (this instanceof SphereCollider sphere)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.GetContact(box, sphere);
            }
            else if (other instanceof SphereCollider sphereB)
            {
                return CollisionHelper.GetContact(sphere, sphereB);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.GetContact(sphere, capsule);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.GetContact(sphere, cylinder);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.GetContact(sphere, plane);
            }
        }

        // Handle cases where `this` is a CapsuleCollider
        else if (this instanceof CapsuleCollider capsule)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.GetContact(box, capsule);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.GetContact(sphere, capsule);
            }
            else if (other instanceof CapsuleCollider capsuleB)
            {
                return CollisionHelper.GetContact(capsule, capsuleB);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.GetContact(cylinder, capsule);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.GetContact(capsule, plane);
            }
        }

        // Handle cases where `this` is a CylinderCollider
        else if (this instanceof CylinderCollider cylinder)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.GetContact(box, cylinder);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.GetContact(sphere, cylinder);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.GetContact(cylinder, capsule);
            }
            else if (other instanceof CylinderCollider cylinderB)
            {
                return CollisionHelper.GetContact(cylinder, cylinderB);
            }
            else if (other instanceof PlaneCollider plane)
            {
                return CollisionHelper.GetContact(cylinder, plane);
            }
        }

        // Handle cases where `this` is a PlaneCollider
        else if (this instanceof PlaneCollider plane)
        {
            if (other instanceof BoxCollider box)
            {
                return CollisionHelper.GetContact(box, plane);
            }
            else if (other instanceof SphereCollider sphere)
            {
                return CollisionHelper.GetContact(sphere, plane);
            }
            else if (other instanceof CapsuleCollider capsule)
            {
                return CollisionHelper.GetContact(capsule, plane);
            }
            else if (other instanceof CylinderCollider cylinder)
            {
                return CollisionHelper.GetContact(cylinder, plane);
            }
            else if (other instanceof PlaneCollider planeB)
            {
                return CollisionHelper.GetContact(plane, planeB);
            }
        }

        throw new IllegalArgumentException(
                "Invalid Collider type combination! Collider types: "
                        + this.getClass().getName() + " and "
                        + other.getClass().getName()
        );
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
