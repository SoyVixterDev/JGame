package JGame.Engine.Physics.Constraints;

import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Physics.Collision.Contacts.Contact;

/**
 * Constraint that links two objects using a cable, creating a contact if they stray too far
 */
public class CableConstraint extends LinkConstraint
{


    /**
     * The maximum length the cable can extend
     */
    public float maxLength;
    /**
     * The restitution constant (bounciness) of the cable
     */
    public float restitution;



    @Override
    public int FillContact(Contact[] contacts, int limit)
    {
        float length = CurrentLength();


        if(length < maxLength)
            return 0;

        contacts[0].rigidbodies[0] = rigidbodies[0];
        contacts[0].rigidbodies[1] = rigidbodies[1];

        contacts[0].contactNormal = rigidbodies[1].Transform().GetGlobalPosition().Subtract(rigidbodies[0].Transform().GetGlobalPosition()).Normalized();
        contacts[0].penetration = length - maxLength;
        contacts[0].restitution = restitution;

        return 1;
    }
}
