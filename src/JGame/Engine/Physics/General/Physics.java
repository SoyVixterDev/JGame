package JGame.Engine.Physics.General;

import JGame.Engine.Basic.BaseObject;
import JGame.Engine.Internal.InternalGameInstance;
import JGame.Engine.Internal.Time;
import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Settings;
import JGame.Engine.Structures.Vector3D;

import java.util.ArrayList;

public class Physics
{
    public static final ArrayList<PhysicsObject> physicsObjects = new ArrayList<>();

    public static final ArrayList<ForceRegistration> forceRegistrations = new ArrayList<>();

    public static boolean useGlobalGravity = true;

    public static Vector3D gravityDirection = Vector3D.Down;

    private static double physicsTimer = 0.0f;
    /**
     * Runs the update for physics behaviors
     */
    public static void UpdatePhysics()
    {
        physicsTimer += Time.DeltaTime();

        //Limit the physics timer to at most perform 6 updates per frame (loss of precision when under 10fps)
        physicsTimer = Math.min(physicsTimer, Settings.Physics.physicsUpdateInterval * 6);

        while (physicsTimer >= Settings.Physics.physicsUpdateInterval)
        {
            Time.UpdatePhysicsTime();

            UpdateForces();

            InternalGameInstance.Instance._internalPhysicsUpdate();

            // Run physics update on all objects
            for(BaseObject baseObj : new ArrayList<>(BaseObject.allBaseObjects))
            {
                if(baseObj != null && baseObj.IsAvailable())
                {
                    baseObj._internalPhysicsUpdate();
                }
            }

            physicsTimer -= Settings.Physics.physicsUpdateInterval;
        }
    }

    /**
     * Updates the accumulated forces for every registered force-object pair
     */
    private static void UpdateForces()
    {
        for(ForceRegistration reg : new ArrayList<>(forceRegistrations))
        {
            //If either the registration itself, the generator or rigidbody are null, then delete the registration
            if(reg == null || reg.Generator() == null || reg.Rigidbody() == null)
            {
                UnregisterForce(reg);
                continue;
            }

            if(reg.Generator().isActive() && reg.Rigidbody().IsAvailable())
            {
                reg.Generator().UpdateForce(reg.Rigidbody());
            }
        }
    }
    /**
     * Registers a Force - Object pair
     * @param forceGenerator
     * The force generator
     * @param rigidbody
     * The rigidbody affected by the force
     * @return
     * Returns the generated force registration
     */
    public static ForceRegistration RegisterForce(IForceGenerator forceGenerator, Rigidbody rigidbody)
    {
        ForceRegistration forceRegistration = new ForceRegistration(forceGenerator, rigidbody);

        RegisterForce(forceRegistration);

        return forceRegistration;
    }


    /**
     * Registers a Force - Object pair
     * @param forceRegistration
     * The pair to register
     */
    public static void RegisterForce(ForceRegistration forceRegistration)
    {
        forceRegistrations.add(forceRegistration);
    }

    /**
     * Unregisters a Force - Object pair
     * @param forceRegistration
     * The pair to register
     */
    public static void UnregisterForce(ForceRegistration forceRegistration)
    {
        forceRegistrations.remove(forceRegistration);
    }
    /**
     * Clears all Force - Object pair registers
     */
    public static void ClearForceRegisters()
    {
        forceRegistrations.clear();
    }


    public enum RestitutionResolution
    {
        Min,
        Max,
        Average,
        Multiply
    }


    public static class Constraints
    {
        /**
         * Constraint for the specified axis, true = locked
         */
        public boolean x, y, z = false;

        public Vector3D AsVector()
        {
            return new Vector3D(x ? 0.0f : 1.0f, y ? 0.0f : 1.0f,z ? 0.0f : 1.0f);
        }
    }
    public static class DragCoefficients
    {
        public float dragCoefficient, squaredDragCoefficient;

        public DragCoefficients(float dragCoefficient, float squaredDragCoefficient)
        {
            this.dragCoefficient = dragCoefficient;
            this.squaredDragCoefficient = squaredDragCoefficient;
        }

        public DragCoefficients()
        {
            dragCoefficient = 0.001f;
            squaredDragCoefficient = 0.0001f;
        }
    }
}
