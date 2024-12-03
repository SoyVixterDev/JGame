package JGame.Engine.Physics.Constraints;

import JGame.Engine.Physics.Collision.Contacts.Contact;
import JGame.Engine.Structures.Vector3D;

public class RodConstraint extends LinkConstraint
{


    /**
     * The length of the rod
     */
    public float length;

    @Override
    public int FillContact(Contact[] contacts, int limit)
    {
        float currentLength = CurrentLength();

        if(currentLength == length)
           return 0;

        contacts[0].rigidbodies[0] = rigidbodies[0];
        contacts[0].rigidbodies[1] = rigidbodies[1];

        Vector3D normal = rigidbodies[1].transform().GetGlobalPosition().Subtract(rigidbodies[0].transform().GetGlobalPosition()).Normalized();

//        if(currentLength > length)
//        {
//            contacts[0].contactNormal = normal;
//            contacts[0].penetration = currentLength - length;
//        }
//        else
//        {
//            contacts[0].contactNormal = normal.Negate();
//            contacts[0].penetration = length - currentLength;
//        }
//
//        contacts[0].restitution = 0;

        return 1;
    }
}
