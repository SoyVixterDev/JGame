package JGame.Engine.Physics.Collision.Contacts;

import JGame.Engine.Physics.Bodies.Rigidbody;

import java.util.List;

/**
 * Holds a potential contact between two bodies
 */
public class PotentialContact
{
    public final Rigidbody[] rigidbodies = new Rigidbody[2];

    public PotentialContact(Rigidbody a, Rigidbody b)
    {
        rigidbodies[0] = a;
        rigidbodies[1] = b;
    }

    /**
     * Gets the contacts between both rigidbodies
     * @param limit
     * The contact limit count
     * @return
     * The list of contacts between both rigidbodies, empty if none are found
     */
    public List<Contact> GetContacts(int limit)
    {
        return rigidbodies[0].GetContacts(rigidbodies[1], limit);
    }
}
