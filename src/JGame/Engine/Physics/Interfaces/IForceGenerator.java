package JGame.Engine.Physics.Interfaces;

import JGame.Engine.Physics.Bodies.Rigidbody;

public interface IForceGenerator
{
    /**
     * Should handle updating the forces for the body, using the provided time step
     * @param body
     * The body to be affected
     */
    void UpdateForce(Rigidbody body);

    /**
     * Should return true if the generator is active
     * @return
     * True if the generator is active and should update the body's forces.
     */
    boolean isActive();
}

