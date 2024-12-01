package JGame.Engine.Physics.Constraints;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Vector3D;

/**
 * Base class for Constraints that link two particles together
 */
public abstract class LinkConstraint extends JComponent
{

    /**
     * Pair of bodies to be linked
     */
    protected Rigidbody[] rigidbodies = new Rigidbody[2];

    /**
     * Gets the current length of the link
     * @return
     * The length of the link
     */
    protected final float CurrentLength()
    {
        return Vector3D.Distance(rigidbodies[0].Transform().GetGlobalPosition(), rigidbodies[1].Transform().GetGlobalPosition());
    }

    /**
     * Fills the given contact structure with the contact needed to ensure the constraint is maintained.
     * @param contacts
     * The contacts to be filled
     * @param limit
     * The maximum number of contacts to write to
     * @return
     * Returns the amount of contacts that have been written to
     */
    public abstract int FillContact(Contact[] contacts, int limit);

    /**
     * Fills the given contact structure with the contact needed to ensure the constraint is maintained.
     * @param contact
     * The contact to be filled
     * @return
     * Returns the amount of contacts that have been written to
     */
    public final int FillContact(Contact contact)
    {
        return FillContact(new Contact[]{contact}, 1);
    }
}
