package JGame.Engine.Physics.Collision.Contacts;


import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.General.Physics;
import JGame.Engine.Structures.Vector3D;


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
     *  The point in space where the contact occurred
     */
    public final Vector3D contactPoint = Vector3D.Zero;
    /**
     * Normal of contact, from the perspective of the first object
     */
    public final Vector3D contactNormal;
    /**
     * The depth of penetration at the contact
     */
    public final float penetration = 0;

    Contact(Rigidbody A, Rigidbody B)
    {
        this(A, B, Physics.RestitutionResolution.Average);
    }

    Contact(Rigidbody A, Rigidbody B, Physics.RestitutionResolution resolution)
    {
        rigidbodies[0] = A;
        rigidbodies[1] = B;

        contactNormal = Vector3D.Subtract(B.transform().GetGlobalPosition(), A.transform().GetGlobalPosition()).Normalized();

        switch (resolution)
        {
            case Min -> restitution = Math.min(A.restitution, B.restitution);
            case Max -> restitution = Math.max(A.restitution, B.restitution);
            case Average -> restitution = (A.restitution + B.restitution) / 2;
            case Multiply -> restitution = A.restitution * B.restitution;
            default -> restitution = 0.0f;
        }

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

