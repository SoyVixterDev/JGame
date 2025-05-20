package JGame.Engine.Physics.Bodies;

import JGame.Engine.EventSystem.Event;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Internal.Time;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Colliders.Collider;
import JGame.Engine.Physics.Collision.Contact.Contact;
import JGame.Engine.Physics.Collision.Detection.BroadCollisionDetection;
import JGame.Engine.Physics.ForceGenerators.GlobalAngularDragForceGenerator;
import JGame.Engine.Physics.ForceGenerators.GlobalGravityForceGenerator;
import JGame.Engine.Physics.ForceGenerators.GlobalLinearDragForceGenerator;
import JGame.Engine.Physics.General.ForceRegistration;
import JGame.Engine.Physics.General.Physics;
import JGame.Engine.Physics.General.PhysicsObject;
import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Physics.Raycast.RaycastContact;
import JGame.Engine.Structures.Matrix3x3;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;
import org.lwjgl.system.linux.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a body in a physics simulation
 */
public class Rigidbody extends PhysicsObject
{
    //------Enumerators------
    public enum ForceType
    {
        /**
         * Adds an instantaneous change in velocity
         */
        Impulse,
        /**
         * Adds a force to be accumulated for the next physics update
         */
        Force
    }

    public enum BodyType
    {
        /**
         * Object is affected by external forces in the simulation
         */
        Dynamic,
        /**
         * Object is only affected by explicit changes in velocity
         */
        Kinematic,
        /**
         * Object is not affected by any changes in velocity or acceleration
         */
        Static
    }

    //------Variables------
    protected final List<Collider> colliders = new ArrayList<>();

    protected float inverseMass = 1.0f;
    protected float mass = 1.0f;

    private Matrix3x3 inverseInertiaTensor = new Matrix3x3();

    private final Matrix3x3 worldSpaceInverseInertiaTensor = new Matrix3x3();

    private BodyType bodyType = BodyType.Dynamic;

    public boolean useGravity = true;
    public float gravityScale = 1.0f;

    public String tag = "Default";

    /**
     * Defines how much this body bounces in collisions, 0 means no bounciness, 1 means no energy is lost in the collision
     */
    public float restitution = 0.6f;
    /**
     * Defines how this body's restitution blends with another during collision
     */
    public MathUtilities.BlendingMode restitutionBlendingMode = MathUtilities.BlendingMode.Average;
    /**
     * Defines how much friction this body has
     */
    public float friction = 0.0f;
    /**
     * Defines how this body's friction blends with another during collision
     */
    public MathUtilities.BlendingMode frictionBlendingMode = MathUtilities.BlendingMode.Average;
    /**
     * Defines the drag coefficients of the object in terms of linear movement
     */
    public Physics.DragCoefficients linearDragCoefficients = new Physics.DragCoefficients(0.1f, 0.02f);
    /**
     * Defines the drag coefficients of the object in terms of rotation
     */
    public Physics.DragCoefficients angularDragCoefficients = new Physics.DragCoefficients(0.2f, 0.05f);

    /**
     * Defines the constrained axes in terms of linear movement
     */
    public Physics.Constraints movementConstraints = new Physics.Constraints();
    /**
     * Defines the constrained axes in terms of rotation
     */
    public Physics.Constraints rotationConstraints = new Physics.Constraints();

    //-Velocities & Accelerations-

    protected Vector3D linearVelocity = Vector3D.Zero;
    protected Vector3D linearAcceleration = Vector3D.Zero;

    protected Vector3D angularVelocity = Vector3D.Zero;
    protected Vector3D angularAcceleration = Vector3D.Zero;

    //-Accumulators-

    /**
     * Accumulator variable for forces to be applied in the next physics update
     */
    protected Vector3D forceAccum = Vector3D.Zero;
    private Vector3D torqueAccum = Vector3D.Zero;

    //-Events-

    private final Event updateWorldSpaceInertiaTensor = new Event()
    {
        @Override
        protected void OnInvoke()
        {
            UpdateWorldInertiaTensor();
        }
    };
    private final Event updateInBVHTree = new Event()
    {
        @Override
        protected void OnInvoke()
        {
            UpdateInHierarchy();
        }
    };

    //------Debug Callbacks------

    @Override
    protected void Initialize()
    {
        RegisterConstantForces();

        for(Collider collider : object().GetComponentsInChildren(Collider.class))
        {
            collider.SetRigidbody(this);
            AddCollider(collider, true);
        }

        CalculateInverseInertiaTensor();
    }

    @Override
    protected void OnEnable()
    {
        super.OnEnable();
        transform().OnChangeRotation.Subscribe(updateWorldSpaceInertiaTensor);
        transform().OnChangeTransformation.Subscribe(updateInBVHTree);

        BroadCollisionDetection.BVHTree.Insert(this);
    }

    @Override
    protected void OnDisable()
    {
        super.OnDisable();
        transform().OnChangeRotation.Unsubscribe(updateWorldSpaceInertiaTensor);
        transform().OnChangeTransformation.Unsubscribe(updateInBVHTree);

        BroadCollisionDetection.BVHTree.Remove(this);
    }

    @Override
    protected void OnDestroy()
    {
        colliders.clear();
    }

    @Override
    public void PhysicsUpdate()
    {
        if (bodyType == BodyType.Static)
        {
            return;
        }

        Integrate();
    }
    //------Integration & Interpolation------

    /**
     * Calculates integration for the physics components
     */
    protected void Integrate()
    {
        LinearIntegration();
        AngularIntegration();

        ClearAccumulators();
    }

    /**
     * Handles Linear Integration
     */
    protected void LinearIntegration()
    {
        linearAcceleration = forceAccum.Scale(inverseMass);

        //V = Vo + a * t, t = PhysicsDeltaTime
        linearVelocity = linearVelocity.AddScaledVector(linearAcceleration,
                (float) Time.PhysicsDeltaTime() * Time.timeScale);

        //d = do + V * t, t = PhysicsDeltaTime
        Vector3D deltaPos = linearVelocity.Multiply(movementConstraints.AsVector()).Scale((float)  Time.PhysicsDeltaTime() * Time.timeScale);


        transform().PositionAdd(deltaPos);
    }

    /**
     * Handles Angular Integration
     */
    protected void AngularIntegration()
    {
        angularAcceleration = worldSpaceInverseInertiaTensor.Multiply(torqueAccum);

        //V = Vo + a * t, t = PhysicsDeltaTime
        angularVelocity = angularVelocity.AddScaledVector(angularAcceleration,
                (float)  Time.PhysicsDeltaTime() * Time.timeScale);

        //q = qo + V * t, t = PhysicsDeltaTime
        Vector3D delta = angularVelocity.Multiply(rotationConstraints.AsVector()).Scale((float)  Time.PhysicsDeltaTime() * Time.timeScale);

        transform().RotationAdd(delta);
    }

    /**
     * Resets the accumulators back to zero
     */
    protected void ClearAccumulators()
    {
        forceAccum = Vector3D.Zero;
        torqueAccum = Vector3D.Zero;
    }

    //-----Force Adders------

    /**
     * Adds a force generator to add consistent additive forces
     *
     * @param forceGenerator The force generator to add
     * @return The generated force registration
     */
    public ForceRegistration AddForce(IForceGenerator forceGenerator)
    {
        return Physics.RegisterForce(forceGenerator, this);
    }

    /**
     * Adds a force to the object at the center of mass
     * @param force     The force to be added
     * @param forceType The type of force to add
     */
    public void AddForce(Vector3D force, ForceType forceType)
    {
        if (bodyType == BodyType.Static || inverseMass == 0) return;

        if (forceType == ForceType.Force)
        {
            forceAccum = forceAccum.Add(force);
        }
        else if (forceType == ForceType.Impulse)
        {
            linearVelocity = linearVelocity.Add(force.Scale(inverseMass));
        }
    }

    /**
     * Adds a torque to the object without affecting linear acceleration
     * @param torque
     * The torque to add
     * @param forceType
     * The force type to add
     */
    public void AddTorque(Vector3D torque, ForceType forceType)
    {
        if (bodyType == BodyType.Static || inverseMass == 0) return;

        if (forceType == ForceType.Force)
        {
            torqueAccum = torqueAccum.Add(torque);
        }
        else if (forceType == ForceType.Impulse)
        {
            angularVelocity = angularVelocity.Add(worldSpaceInverseInertiaTensor.Multiply(torque));
        }
    }

    /**
     * Adds a force acting at a point in world space
     * @param force
     * The force to apply
     * @param point
     * The application point, in world space
     * @param forceType
     * The type of force to add
     */
    public void AddForceAtPoint(Vector3D force, Vector3D point, ForceType forceType)
    {
        AddForceAtPoint(force, point, forceType, false);
    }

    /**
     * Adds a force acting at a specific point
     * @param force
     * The force to apply
     * @param point
     * The application point
     * @param forceType
     * The type of force to add
     * @param localCoordinates
     * Is the point relative to the center of mass?
     */
    public void AddForceAtPoint(Vector3D force, Vector3D point, ForceType forceType, boolean localCoordinates)
    {
        if(bodyType == BodyType.Static || inverseMass == 0)
            return;

        if(localCoordinates)
            point = transform().LocalToWorldSpace(point);

        point = point.Subtract(transform().GetGlobalPosition());
        if(forceType == ForceType.Impulse)
        {
            linearVelocity = linearVelocity.Add(force.Scale(inverseMass));
            angularVelocity = angularVelocity.Add(inverseInertiaTensor.Multiply(point.CrossProduct(force)));
        }
        else if(forceType == ForceType.Force)
        {
            forceAccum = forceAccum.Add(force);
            torqueAccum = torqueAccum.Add(point.CrossProduct(force));
        }
    }

    //------Getters/Setters------

    /**
     * Sets the mass of the body
     *
     * @param mass The mass to be set
     */
    public void SetMass(float mass)
    {
        if (mass != 0)
        {
            if (mass < 0) Logger.DebugWarning("Negative mass detected! Expect undefined behavior");

            inverseMass = 1 / mass;
            this.mass = mass;
        }
        else
        {
            Logger.DebugWarning("Mass can't be zero! Skipping mass assignment");
        }

        CalculateInverseInertiaTensor();
    }

    public float GetMass()
    {
        return mass;
    }
    public float GetInverseMass()
    {
        return bodyType == BodyType.Static ? 0 : inverseMass;
    }

    /**
     * Sets the inertia tensor for the body
     * @param inertiaTensor
     * The inertia tensor to set
     */
    public void SetInertiaTensor(Matrix3x3 inertiaTensor)
    {
        inverseInertiaTensor = inertiaTensor.Inverse();
    }

    /**
     * Gets the world space inverse inertia tensor
     */
    public Matrix3x3 GetInverseInertiaTensorWorld()
    {
        return bodyType == BodyType.Static ? Matrix3x3.Zero() : worldSpaceInverseInertiaTensor;
    }

    public Vector3D GetLinearVelocity()
    {
        return linearVelocity;
    }
    public void SetLinearVelocity(Vector3D linearVelocity)
    {
        if(bodyType == BodyType.Static)
            return;

        this.linearVelocity = linearVelocity;
    }
    public Vector3D GetAngularVelocity()
    {
        return angularVelocity;
    }
    public void SetAngularVelocity(Vector3D angularVelocity)
    {
        if(bodyType == BodyType.Static)
            return;

        this.angularVelocity = angularVelocity;
    }

    public Vector3D GetLinearAcceleration()
    {
        return linearAcceleration;
    }
    public Vector3D GetAngularAcceleration() { return angularAcceleration; }


    public void SetBodyType(BodyType bodyType)
    {
        if(this.bodyType == bodyType)
            return;

        this.bodyType = bodyType;

        linearVelocity = Vector3D.Zero;
        angularVelocity = Vector3D.Zero;

        ClearAccumulators();
    }
    public BodyType GetBodyType()
    {
        return bodyType;
    }

    //------Miscellaneous functions------

    /**
     * Calculates the inertia tensor
     */
    public void CalculateInverseInertiaTensor()
    {
        inverseInertiaTensor = new Matrix3x3
        (
            new float[]
            {
                    6*inverseMass, 0, 0,
                    0, 6*inverseMass, 0,
                    0, 0, 6*inverseMass
            }
        );
        UpdateWorldInertiaTensor();
    }

    /**
     * Calculates the current Inertia Tensor in world space
     */
    private void UpdateWorldInertiaTensor()
    {
        float[] transformationValues = transform().GetTransformationMatrix().values;
        float[] inertiaValues = inverseInertiaTensor.values;

        float t4 = transformationValues[0] * inertiaValues[0] +
                transformationValues[1] * inertiaValues[3] +
                transformationValues[2] * inertiaValues[6];
        float t9 = transformationValues[0] * inertiaValues[1] +
                transformationValues[1] * inertiaValues[4] +
                transformationValues[2] * inertiaValues[7];
        float t14 = transformationValues[0] * inertiaValues[2] +
                transformationValues[1] * inertiaValues[5] +
                transformationValues[2] * inertiaValues[8];
        float t28 = transformationValues[4] * inertiaValues[0] +
                transformationValues[5] * inertiaValues[3] +
                transformationValues[6] * inertiaValues[6];
        float t33 = transformationValues[4] * inertiaValues[1] +
                transformationValues[5] * inertiaValues[4] +
                transformationValues[6] * inertiaValues[7];
        float t38 = transformationValues[4] * inertiaValues[2] +
                transformationValues[5] * inertiaValues[5] +
                transformationValues[6] * inertiaValues[8];
        float t52 = transformationValues[8] * inertiaValues[0] +
                transformationValues[9] * inertiaValues[3] +
                transformationValues[10] * inertiaValues[6];
        float t57 = transformationValues[8] * inertiaValues[1] +
                transformationValues[9] * inertiaValues[4] +
                transformationValues[10] * inertiaValues[7];
        float t62 = transformationValues[8] * inertiaValues[2] +
                transformationValues[9] * inertiaValues[5] +
                transformationValues[10] * inertiaValues[8];

        float[] worldInertiaValues = worldSpaceInverseInertiaTensor.values;

        worldInertiaValues[0] = t4 * transformationValues[0] +
                t9 * transformationValues[1] +
                t14 * transformationValues[2];
        worldInertiaValues[1] = t4 * transformationValues[4] +
                t9 * transformationValues[5] +
                t14 * transformationValues[6];
        worldInertiaValues[2] = t4 * transformationValues[8] +
                t9 * transformationValues[9] +
                t14 * transformationValues[10];
        worldInertiaValues[3] = t28 * transformationValues[0] +
                t33 * transformationValues[1] +
                t38 * transformationValues[2];
        worldInertiaValues[4] = t28 * transformationValues[4] +
                t33 * transformationValues[5] +
                t38 * transformationValues[6];
        worldInertiaValues[5] = t28 * transformationValues[8] +
                t33 * transformationValues[9] +
                t38 * transformationValues[10];
        worldInertiaValues[6] = t52 * transformationValues[0] +
                t57 * transformationValues[1] +
                t62 * transformationValues[2];
        worldInertiaValues[7] = t52 * transformationValues[4] +
                t57 * transformationValues[5] +
                t62 * transformationValues[6];
        worldInertiaValues[8] = t52 * transformationValues[8] +
                t57 * transformationValues[9] +
                t62 * transformationValues[10];
    }

    /**
     * Updates this rigidbody in the BVH Tree
     */
    private void UpdateInHierarchy()
    {
        BroadCollisionDetection.BVHTree.UpdateNode(this);
    }

    /**
     * Adds a collider to the list of colliders for this rigidbody
     */
    public void AddCollider(Collider collider)
    {
        AddCollider(collider, false);
    }

    /**
     * Adds a collider to the list of the colliders for this rigidbody
     * @param collider
     * The collider to add
     * @param ignoreUpdate
     * Should it ignore updating in the BVH?
     */
    public void AddCollider(Collider collider, boolean ignoreUpdate)
    {
        colliders.add(collider);
        if(!ignoreUpdate)
            UpdateInHierarchy();
    }

    /**
     * Removes a collider from the list of colliders
     */
    public void RemoveCollider(Collider collider)
    {
        colliders.remove(collider);
        UpdateInHierarchy();
    }

    /**
     * Gets the list of contacts between this Rigidbody and another
     * @param other
     * The other Rigidbody
     * @param limit
     * The limit count of contacts to get
     * @return
     * The list of contacts between the rigid bodies, or empty if none are found
     */
    public List<Contact> GetContacts(Rigidbody other, int limit)
    {
        List<Contact> contacts = new ArrayList<>();

        if(other.colliders.isEmpty() || colliders.isEmpty())
            return contacts;

        for(Collider col : colliders)
        {
            for(Collider otherCol : other.colliders)
            {
                if(limit <= 0)
                    return contacts;

                Contact contact = col.GetContact(otherCol);
                if(contact == null)
                    continue;
                contacts.add(contact);
                limit -= 1;
            }
        }

        return contacts;
    }

    /**
     * Casts a ray
     * @param origin
     * The origin
     * @param direction
     * The direction
     * @param maxDistance
     * The max distance
     * @return
     * The contact, or null if none are found
     */
    public RaycastContact Raycast(Vector3D origin, Vector3D direction,float maxDistance, String... ignoreTags)
    {
        for (String ignoredTag : ignoreTags)
        {
            if (ignoredTag.equals(tag))
            {
                return null;
            }
        }

        float minSquaredDist = Float.MAX_VALUE;
        RaycastContact bestContact = null;

        for(Collider collider : colliders)
        {
            RaycastContact contact = collider.Raycast(origin, direction, maxDistance);

            if(contact == null)
                continue;

            float squaredDist = Vector3D.DistanceSquared(contact.point, origin);

            if(minSquaredDist > squaredDist)
            {
                minSquaredDist = squaredDist;
                bestContact = contact;
            }
        }

        return bestContact;
    }

    /**
     * Gets the bounding volume that encapsulates all colliders assigned to this rigid body
     * @return
     * The bounding volume that encapsulates all colliders assigned to this rigid body
     */
    public BoundingVolume GetBoundingVolume()
    {
        if(colliders.isEmpty())
            return null;

        List<BoundingVolume> boundingVolumes = colliders.stream().map(Collider::GetBoundingVolume).toList();

        return BoundingVolume.GenerateFromBounds(boundingVolumes);
    }

    /**
     * Adds the constant global forces to the object
     */
    protected void RegisterConstantForces()
    {
        //Registers the object to receive global gravity forces
        AddForce(GlobalGravityForceGenerator.Instance);
        //Registers the object to receive global linear drag forces
        AddForce(GlobalLinearDragForceGenerator.Instance);
        //Registers the object to receive global angular drag forces
        AddForce(GlobalAngularDragForceGenerator.Instance);
    }

}
