package JGame.Engine.Physics.Collision.Contact;


import JGame.Engine.Internal.Logger;
import JGame.Engine.Internal.Time;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Matrix3x3;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;
import org.lwjgl.system.linux.Stat;

import java.util.Arrays;
import java.util.Vector;


/**
 * Structure representing a contact between particles
 */
public class Contact
{
    /**
     * Particles involved in the contact
     */
    final public Rigidbody[] bodies = new Rigidbody[] { null, null };
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
    public float penetration;
    /**
     * Stores the contact Velocity
     */
    public Vector3D contactVelocity;
    /**
     * Stores the relative positions of the bodies within the contact
     */
    public final Vector3D[] relativeContactPosition = new Vector3D[2];
    /**
     * Stores the desired delta velocity
     */
    public float desiredDeltaVelocity;

    /**
     * Stores a matrix to convert from contact to world space
     */
    public final Matrix3x3 contactToWorldMatrix;
    /**
     * Stores a matrix to convert from world to contact space (contactToWorldMatrix's Transpose)
     */
    public final Matrix3x3 worldToContactMatrix;


    public Contact(Rigidbody A, Rigidbody B, Vector3D contactPoint, Vector3D contactNormal, float penetration)
    {
        bodies[0] = A;
        bodies[1] = B;

        if(bodies[0] == null || bodies[1] == null)
            throw new IllegalArgumentException("A contact can't exist between two null bodies!");

        this.contactNormal = contactNormal;
        this.contactPoint = contactPoint;
        this.penetration = penetration;

        contactToWorldMatrix = CalculateContactBasis();
        worldToContactMatrix = contactToWorldMatrix.Transpose();

        restitution = MathUtilities.Blend(bodies[0].restitution, bodies[1].restitution, bodies[0].restitutionBlendingMode);
        friction = MathUtilities.Blend(bodies[0].friction, bodies[1].friction, bodies[0].frictionBlendingMode);
    }

    /**
     * Calculates internal values and checks if contact is valid to be resolved
     * @return
     * True if the contact is valid to be resolved, false otherwise
     */
    public boolean CalculateInternal()
    {
        //Skip invalid contacts, I guess this could only happen if a rigidbody gets deleted mid-resolution?
        if(bodies[0] == null || bodies[1] == null)
            return false;

        //Skip static-static contacts
        if(bodies[0].GetBodyType() == Rigidbody.BodyType.Static && bodies[1].GetBodyType() == Rigidbody.BodyType.Static)
            return false;

        UpdateRelativePositions();

        contactVelocity = CalculateContactVelocity(0).Subtract(CalculateContactVelocity(1));

        CalculateDesiredDeltaVelocity();
        return true;
    }

    void UpdateRelativePositions()
    {
        relativeContactPosition[0] = contactPoint.Subtract(bodies[0].transform().GetGlobalPosition());
        relativeContactPosition[1] = contactPoint.Subtract(bodies[1].transform().GetGlobalPosition());
    }

    /**
     * Applies the angular and linear velocity changes to the provided bodies
     * @return
     * Returns both pairs of linear and angular delta velocities
     */
    Vector3D[][] ApplyVelocityChange()
    {
        Matrix3x3 inverseInertiaTensorA = bodies[0].GetInverseInertiaTensorWorld();
        Matrix3x3 inverseInertiaTensorB = bodies[1].GetInverseInertiaTensorWorld();

        Vector3D impulseContact = (friction == 0.0f) ?
                CalculateFrictionlessImpulse(inverseInertiaTensorA, inverseInertiaTensorB) :
                CalculateFrictionlessImpulse(inverseInertiaTensorA, inverseInertiaTensorB);

        Vector3D impulseWorld = contactToWorldMatrix.Multiply(impulseContact);

        Vector3D[] linearChange = new Vector3D[]{ Vector3D.Zero, Vector3D.Zero };
        Vector3D[] angularChange = new Vector3D[]{ Vector3D.Zero, Vector3D.Zero };

        // Apply changes to Body A
        if (bodies[0].GetBodyType() != Rigidbody.BodyType.Static)
        {
            linearChange[0] = impulseWorld.Scale(bodies[0].GetInverseMass());
            Vector3D impulsiveTorque = relativeContactPosition[0].CrossProduct(impulseWorld).Multiply(bodies[0].transform().GetGlobalScale());
            angularChange[0] = inverseInertiaTensorA.Multiply(impulsiveTorque);

            bodies[0].SetLinearVelocity(bodies[0].GetLinearVelocity().Add(linearChange[0]));
            bodies[0].SetAngularVelocity(bodies[0].GetAngularVelocity().Add(angularChange[0]));
        }

        // Apply changes to Body B
        if (bodies[1].GetBodyType() != Rigidbody.BodyType.Static)
        {
            linearChange[1] = impulseWorld.Scale(-bodies[1].GetInverseMass());
            Vector3D impulsiveTorque = impulseWorld.CrossProduct(relativeContactPosition[1]).Multiply(bodies[1].transform().GetGlobalScale());
            angularChange[1] = inverseInertiaTensorB.Multiply(impulsiveTorque);

            bodies[1].SetLinearVelocity(bodies[1].GetLinearVelocity().Add(linearChange[1]));
            bodies[1].SetAngularVelocity(bodies[1].GetAngularVelocity().Add(angularChange[1]));
        }

        return new Vector3D[][] { linearChange, angularChange };
    }


    /**
     * Solves interpenetration in the contact by applying a small linear and angular correction
     * @return
     * Returns both pairs of changes for both bodies
     */
    Vector3D[][] ApplyPositionChange()
    {
        float angularLimit = 0.1f;

        float[] angularMove = new float[]{0, 0};
        float[] linearMove = new float[]{0, 0};

        float totalInertia = 0;
        float[] angularInertia = new float[] {0, 0};
        float[] linearInertia = new float[] {0, 0};

        Vector3D[] angularChange = new Vector3D[]{Vector3D.Zero, Vector3D.Zero};
        Vector3D[] linearChange = new Vector3D[]{Vector3D.Zero, Vector3D.Zero};

        for(int i = 0; i < 2; i++)
        {
            if (bodies[i] == null || bodies[i].GetBodyType() == Rigidbody.BodyType.Static)
                continue;

            Matrix3x3 invInertiaTensor = bodies[i].GetInverseInertiaTensorWorld();
            Vector3D scaledContactPosition = relativeContactPosition[i].Multiply(bodies[i].transform().GetGlobalScale());

            Vector3D angularInertiaWorld = scaledContactPosition.CrossProduct(contactNormal);
            angularInertiaWorld = invInertiaTensor.Multiply(angularInertiaWorld);
            angularInertiaWorld = angularInertiaWorld.CrossProduct(scaledContactPosition);
            angularInertia[i] = angularInertiaWorld.DotProduct(contactNormal);

            linearInertia[i] = bodies[i].GetInverseMass();
            totalInertia += linearInertia[i] + angularInertia[i];
        }

        for(int i = 0; i < 2; i++) if (bodies[i] != null && bodies[i].GetBodyType() != Rigidbody.BodyType.Static)
        {
            float sign = contactNormal.DotProduct(relativeContactPosition[i]) > 0 ? -1 : 1;

            angularMove[i] = sign * penetration * (angularInertia[i] / totalInertia);
            linearMove[i] = sign * penetration * (linearInertia[i] / totalInertia);

            Vector3D projection = relativeContactPosition[i].AddScaledVector(contactNormal, -relativeContactPosition[i].DotProduct(contactNormal));

            float maxMagnitude = angularLimit * projection.Magnitude();
            if (Math.abs(angularMove[i]) > maxMagnitude)
            {
                float totalMovement = angularMove[i] + linearMove[i];
                angularMove[i] = Math.signum(angularMove[i]) * maxMagnitude;
                linearMove[i] = totalMovement - angularMove[i];
            }

            if (angularMove[i] != 0)
            {
                Vector3D targetAngularDirection = relativeContactPosition[i].CrossProduct(contactNormal);
                Matrix3x3 invInertiaTensor = bodies[i].GetInverseInertiaTensorWorld();

                angularChange[i] = invInertiaTensor.Multiply(targetAngularDirection).Scale(angularMove[i] / angularInertia[i]);
            }

            linearChange[i] = contactNormal.Scale(linearMove[i]);

            bodies[i].transform().SetGlobalPositionAndRotation(
                    bodies[i].transform().GetGlobalPosition().Add(linearChange[i]),
                    bodies[i].transform().GetGlobalRotation().Add(angularChange[i])
            );
        }

        return new Vector3D[][] { linearChange, angularChange };
    }
    /**
     * Calculates and returns the change in velocity required to solve the contact
     */
    void CalculateDesiredDeltaVelocity()
    {
        float velocityLimit = 0.01f;
        float duration = (float) Time.PhysicsDeltaTime();

        float velocityFromAcc = 0;

        velocityFromAcc -= bodies[0].GetLinearAcceleration().Scale(duration).DotProduct(contactNormal);
        velocityFromAcc += bodies[1].GetLinearAcceleration().Scale(duration).DotProduct(contactNormal);

        float thisRestitution = restitution;
        if(Math.abs(contactVelocity.x) < velocityLimit)
        {
            thisRestitution = 0.0f;
        }

        desiredDeltaVelocity = -contactVelocity.x - thisRestitution * (contactVelocity.x - velocityFromAcc);
    }

    /**
     * Calculates velocity for a body in contact space
     * @param bodyIdx
     * The index of the body to calculate
     * @return
     * The contact velocity
     */
    Vector3D CalculateContactVelocity(int bodyIdx)
    {
        Rigidbody body = bodies[bodyIdx];

        float duration = (float) Time.PhysicsDeltaTime();

        Vector3D vel = body.GetAngularVelocity().CrossProduct(relativeContactPosition[bodyIdx]);
        vel = vel.Add(body.GetLinearVelocity());

        Vector3D contactVel = worldToContactMatrix.Multiply(vel);

        Vector3D accVel = body.GetLinearAcceleration().Scale(duration);
        accVel = worldToContactMatrix.Multiply(accVel);

        return new Vector3D(contactVel.x, contactVel.y + accVel.y, contactVel.z + accVel.z);
    }

    Vector3D CalculateFrictionImpulse(Matrix3x3 inverseInertiaTensorA, Matrix3x3 inverseInertiaTensorB)
    {
        //TO-DO
        return null;
    }

    /**
     * Calculates and returns the impulse without using friction
     */
    Vector3D CalculateFrictionlessImpulse(Matrix3x3 inverseInertiaTensorA, Matrix3x3 inverseInertiaTensorB)
    {
        Vector3D deltaVelWorldA = relativeContactPosition[0].CrossProduct(contactNormal);
        deltaVelWorldA = inverseInertiaTensorA.Multiply(deltaVelWorldA);
        deltaVelWorldA = deltaVelWorldA.CrossProduct(relativeContactPosition[0]);

        float deltaVelocity = deltaVelWorldA.DotProduct(contactNormal);
        deltaVelocity += bodies[0].GetInverseMass();

        Vector3D deltaVelWorldB = relativeContactPosition[1].CrossProduct(contactNormal);
        deltaVelWorldB = inverseInertiaTensorB.Multiply(deltaVelWorldB);
        deltaVelWorldB = deltaVelWorldB.CrossProduct(relativeContactPosition[1]);

        deltaVelocity += deltaVelWorldB.DotProduct(contactNormal);
        deltaVelocity += bodies[1].GetInverseMass();

        return new Vector3D(desiredDeltaVelocity / deltaVelocity, 0, 0);
    }

    /**
     * Calculates and returns a 3x3 matrix used to convert in and out of contact space
     */
    private Matrix3x3 CalculateContactBasis()
    {
        Vector3D[] contactTangent = new Vector3D[2];
        if (Math.abs(contactNormal.x) > Math.abs(contactNormal.y))
        {
            float s = 1.0f / (float) Math.sqrt(contactNormal.z * contactNormal.z + contactNormal.x * contactNormal.x);
            contactTangent[0] = new Vector3D(contactNormal.z * s, 0, -contactNormal.x * s);
        }
        else
        {
            float s = 1.0f / (float) Math.sqrt(contactNormal.z * contactNormal.z + contactNormal.y * contactNormal.y);
            contactTangent[0] = new Vector3D(0, -contactNormal.z * s, contactNormal.y * s);
        }

        contactTangent[1] = contactNormal.CrossProduct(contactTangent[0]).Normalized();

        return new Matrix3x3(contactNormal, contactTangent[0], contactTangent[1]);
    }
    @Override
    public String toString()
    {
        return "Contact{" + "bodies=" + Arrays.toString(bodies) + ", restitution=" + restitution + ", friction=" + friction + ", contactPoint=" + contactPoint + ", contactNormal=" + contactNormal + ", penetration=" + penetration + ", contactVelocity=" + contactVelocity + ", relativeContactPosition=" + Arrays.toString(relativeContactPosition) + ", desiredDeltaVelocity=" + desiredDeltaVelocity + ", contactToWorldMatrix=" + contactToWorldMatrix + ", worldToContactMatrix=" + worldToContactMatrix + '}';
    }
}

