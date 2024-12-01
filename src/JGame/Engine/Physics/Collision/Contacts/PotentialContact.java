package JGame.Engine.Physics.Collision.Contacts;

import JGame.Engine.Physics.Bodies.Rigidbody;

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
}
