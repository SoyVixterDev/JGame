package JGame.Engine.Physics.ForceGenerators;

import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Structures.Vector3D;

public class GlobalLinearDragForceGenerator implements IForceGenerator
{
    public static final GlobalLinearDragForceGenerator Instance;

    static
    {
        Instance = new GlobalLinearDragForceGenerator();
    }

    @Override
    public void UpdateForce(Rigidbody body)
    {
        if(body.linearVelocity.equals(Vector3D.Zero)) return;

        Vector3D dragForce = body.linearVelocity;

        float dragCoefficient = dragForce.Magnitude();

        dragCoefficient =
                body.linearDragCoefficients.dragCoefficient * dragCoefficient +
                body.linearDragCoefficients.squaredDragCoefficient * dragCoefficient * dragCoefficient;

        dragForce = dragForce.Normalized().Scale(-dragCoefficient);

        body.AddForce(dragForce, Rigidbody.ForceType.Impulse);
    }

    @Override
    public boolean isActive()
    {
        return true;
    }
}
