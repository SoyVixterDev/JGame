package JGame.Engine.Physics.Collision.Colliders;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Class for colliders, all colliders should extend from this class
 */
public abstract class Collider extends JComponent
{
    private Rigidbody rigidbody;

    @Override
    protected void Start()
    {
        rigidbody = Object().GetComponentInParent(Rigidbody.class);
    }

    @Override
    protected void OnEnable()
    {
        if(rigidbody != null)
            rigidbody.AddCollider(this);
    }

    @Override
    protected void OnDisable()
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
        return new ArrayList<>();
    }

    /**
     * Gets the bounding volume that encapsulates this collider
     * @return
     * The bounding volume that encapsulates this collider
     */
    public abstract BoundingVolume GetBoundingVolume();
}
