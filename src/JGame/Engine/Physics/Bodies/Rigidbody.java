package JGame.Engine.Physics.Bodies;

import JGame.Engine.EventSystem.Event;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Internal.Time;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Colliders.Collider;
import JGame.Engine.Physics.Collision.Detection.BroadCollisionDetection;
import JGame.Engine.Physics.ForceGenerators.GlobalAngularDragForceGenerator;
import JGame.Engine.Physics.ForceGenerators.GlobalGravityForceGenerator;
import JGame.Engine.Physics.ForceGenerators.GlobalLinearDragForceGenerator;
import JGame.Engine.Physics.General.ForceRegistration;
import JGame.Engine.Physics.General.Physics;
import JGame.Engine.Physics.General.PhysicsObject;
import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Matrix3x3;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;

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

    public BodyType bodyType = BodyType.Dynamic;

    public boolean interpolate = true;
    /**
     * Real position where the object wants to be, used by the interpolation
     */
    protected Vector3D targetPosition;
    /**
     * Real orientation of the object, used by the interpolation
     */
    protected Quaternion targetRotation;

    public boolean useGravity = true;
    public float gravityScale = 1.0f;

    /**
     * Defines how much this body bounces in collisions, 0 means no bounciness, 1 means no energy is lost in the collision
     */
    public float restitution = 0.0f;

    /**
     * Defines the drag coefficients of the object in terms of linear movement
     */
    public Physics.DragCoefficients linearDragCoefficients = new Physics.DragCoefficients(0.00025f, 0.00001f);
    /**
     * Defines the drag coefficients of the object in terms of rotation
     */
    public Physics.DragCoefficients angularDragCoefficients = new Physics.DragCoefficients(0.002f, 0.00001f);

    /**
     * Defines the constrained axes in terms of linear movement
     */
    public Physics.Constraints movementConstraints = new Physics.Constraints();
    /**
     * Defines the constrained axes in terms of rotation
     */
    public Physics.Constraints rotationConstraints = new Physics.Constraints();

    //-Velocities & Accelerations-

    public Vector3D linearVelocity = Vector3D.Zero;
    protected Vector3D linearAcceleration = Vector3D.Zero;

    public Vector3D angularVelocity = Vector3D.Zero;
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

        targetPosition = transform().GetGlobalPosition();
        targetRotation = transform().GetGlobalRotation();

        CalculateInverseInertiaTensor();
    }

    @Override
    protected void OnEnable()
    {
        super.OnEnable();
        transform().OnChangeRotation.Subscribe(updateWorldSpaceInertiaTensor);
        transform().OnChangePosition.Subscribe(updateInBVHTree);

        BroadCollisionDetection.BVHTree.Insert(this);
    }

    @Override
    protected void OnDisable()
    {
        super.OnDisable();
        transform().OnChangeRotation.Unsubscribe(updateWorldSpaceInertiaTensor);
        transform().OnChangePosition.Unsubscribe(updateInBVHTree);

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

    @Override
    public void Update()
    {
        if (interpolate) Interpolate();
    }

    //------Integration & Interpolation------

    /**
     * Calculates integration for the physics components
     */
    protected void Integrate()
    {
        LinearIntegration();
        AngularIntegration();

        if (!interpolate)
        {
            transform().SetGlobalPosition(targetPosition);
            transform().SetGlobalRotation(targetRotation);
        }

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
                (float) Settings.Physics.physicsUpdateInterval * Time.timeScale);

        //d = do + V * t, t = PhysicsDeltaTime
        targetPosition = targetPosition.AddScaledVector(linearVelocity.Multiply(movementConstraints.AsVector()),
                (float)  Settings.Physics.physicsUpdateInterval * Time.timeScale);
    }

    /**
     * Handles Angular Integration
     */
    protected void AngularIntegration()
    {
        angularAcceleration = worldSpaceInverseInertiaTensor.Multiply(torqueAccum);

        //V = Vo + a * t, t = PhysicsDeltaTime
        angularVelocity = angularVelocity.AddScaledVector(angularAcceleration,
                (float)  Settings.Physics.physicsUpdateInterval * Time.timeScale);

        //q = qo + V * t, t = PhysicsDeltaTime
        targetRotation = targetRotation.AddScaledVector(angularVelocity.Multiply(rotationConstraints.AsVector()),
                (float)  Settings.Physics.physicsUpdateInterval * Time.timeScale);
    }

    /**
     * Interpolates between the current position and the target position calculated during the last physics update
     */
    protected void Interpolate()
    {
        float interpolationFactor = (float) (Time.DeltaTime() / Settings.Physics.physicsUpdateInterval);
        interpolationFactor = Math.min(interpolationFactor, 1.0f);

        Vector3D newPos = Vector3D.Lerp(transform().GetGlobalPosition(), targetPosition, interpolationFactor);
        transform().SetGlobalPosition(newPos);

        Quaternion newRotation = Quaternion.Lerp(transform().GetGlobalRotation(), targetRotation, interpolationFactor);
        transform().SetGlobalRotation(newRotation);
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
     *
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
            torqueAccum = forceAccum.Add(torque);
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

        point = point.Subtract(targetPosition);

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
    }

    public float GetMass()
    {
        return mass;
    }
    public float GetInverseMass()
    {
        return inverseMass;
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

    public Vector3D GetLinearAcceleration()
    {
        return linearAcceleration;
    }
    public Vector3D GetAngularAcceleration() { return angularAcceleration; }


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
        colliders.add(collider);
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
     * Gets the bounding volume that encapsulates all colliders assigned to this rigid body
     * @return
     * The bounding volume that encapsulates all colliders assigned to this rigid body
     */
    public BoundingVolume GetBoundingVolume()
    {
        if(!colliders.isEmpty())
        {
            List<BoundingVolume> boundingVolumes = colliders.stream().map(Collider::GetBoundingVolume).toList();

            return BoundingVolume.GenerateFromBounds(boundingVolumes);
        }

        return null;
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
