package JGame.Engine.Physics.Collision.Contacts;


import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;


/**
 * Structure representing a contact between particles
 */
public class Contact
{
    /**
     * Particles involved in the contact
     */
    final public Rigidbody[] rigidbodies = new Rigidbody[] { null, null };
    /**
     * The resulting coefficient of restitution of the contact
     */
    public final float restitution;
    /**
     * The resulting coefficient of friction of the contact
     */
    public final float friction;
    /**
     *  The point in space where the contact occurred
     */
    public final Vector3D contactPoint;
    /**
     * Normal of contact, from the perspective of the first object
     */
    public final Vector3D contactNormal;
    /**
     * The depth of penetration at the contact
     */
    public final float penetration;

    public Contact(Rigidbody A, Rigidbody B, Vector3D contactPoint, Vector3D contactNormal, float penetration)
    {
        rigidbodies[0] = A;
        rigidbodies[1] = B;

        this.contactNormal = contactNormal;
        this.contactPoint = contactPoint;
        this.penetration = penetration;

        restitution = MathUtilities.Blend(A.restitution, B.restitution, A.restitutionBlendingMode);
        friction = MathUtilities.Blend(A.friction, B.friction, A.restitutionBlendingMode);
    }

    /**
     * Handles calling all the required resolving functions
     * @param duration
     * The timeframe to resolve (usually physicsDeltaTime)
     */
    public void Resolve(float duration)
    {
        ResolveVelocity(duration);
        ResolveInterpenetration(duration);
    }

    /**
     * Gets the Contact Velocity for this contact
     * @return
     * The separating velocity for this contact
     */
    public final float CalculateContactVelocity()
    {
        Vector3D relativeVelocity = rigidbodies[0].linearVelocity;

        if(rigidbodies[1] != null)
            relativeVelocity = relativeVelocity.Subtract(rigidbodies[1].linearVelocity);

        return relativeVelocity.DotProduct(contactNormal);
    }

    /**
     * Resolves for Interpenetration between the particles
     */
    public void ResolveInterpenetration(float duration)
    {
        if(penetration <= 0)
            return;

        float totalInverseMass = rigidbodies[0].GetInverseMass();

        if(rigidbodies[1] != null)
            totalInverseMass += rigidbodies[1].GetInverseMass();

        if(totalInverseMass <= 0)
            return;

        Vector3D movementPerIMass = contactNormal.Scale(-penetration/totalInverseMass);

        rigidbodies[0].transform().SetGlobalPosition
                (
                        rigidbodies[0].transform().GetGlobalPosition().Add(movementPerIMass.Scale(rigidbodies[0].GetInverseMass()))
                );

        if(rigidbodies[1] != null)
            rigidbodies[1].transform().SetGlobalPosition
                    (
                            rigidbodies[1].transform().GetGlobalPosition().Subtract(movementPerIMass.Scale(rigidbodies[0].GetInverseMass()))
                    );
    }

    /**
     * Resolves for Velocity in both particles of the contact
     * @param duration
     * The duration to resolve (usually physicsDeltaTime)
     */
    public void ResolveVelocity(float duration)
    {
        float contactVelocity = CalculateContactVelocity();

        //If the particles are already separating or at rest don't resolve
        if(contactVelocity > 0)
            return;

        float separatingVelocity = -contactVelocity * restitution;


        Vector3D velocityByAcc = rigidbodies[0].GetLinearAcceleration();
        if(rigidbodies[1] != null)
            velocityByAcc = velocityByAcc.Subtract(rigidbodies[1].GetLinearAcceleration());

        float separatingVelocityByAcc = velocityByAcc.DotProduct(contactNormal) * duration;

        if(separatingVelocityByAcc < 0)
        {
            separatingVelocity += restitution * separatingVelocityByAcc;

            separatingVelocity = Math.max(separatingVelocity, 0);
        }

        float deltaVel = separatingVelocity - contactVelocity;

        float totalInverseMass = rigidbodies[0].GetInverseMass();

        if(rigidbodies[1] != null)
            totalInverseMass += rigidbodies[1].GetInverseMass();

        if(totalInverseMass <= 0)
            return;

        float impulse = deltaVel/totalInverseMass;

        Vector3D impulsePerIMass = contactNormal.Scale(impulse);

        rigidbodies[0].AddForce(impulsePerIMass, Rigidbody.ForceType.Impulse);

        if(rigidbodies[1] != null)
            rigidbodies[1].AddForce(impulsePerIMass.Negate(), Rigidbody.ForceType.Impulse);
    }

}

