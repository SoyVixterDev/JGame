package JGame.Engine.Physics.ForceGenerators;

import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Structures.Vector3D;

public class GlobalAngularDragForceGenerator implements IForceGenerator
{
    public static final GlobalAngularDragForceGenerator Instance;

    static
    {
        Instance = new GlobalAngularDragForceGenerator();
    }

    @Override
    public void UpdateForce(Rigidbody body)
    {
        if(body.GetAngularVelocity().IsZero()) return;

        Vector3D dragForce = body.GetAngularVelocity();

        float dragCoefficient = dragForce.Magnitude();

        dragCoefficient =
                body.angularDragCoefficients.dragCoefficient * dragCoefficient +
                body.angularDragCoefficients.squaredDragCoefficient * dragCoefficient * dragCoefficient;

        dragForce = dragForce.Normalized().Scale(-dragCoefficient);

        body.AddTorque(dragForce, Rigidbody.ForceType.Force);
    }

    @Override
    public boolean isActive()
    {
        return true;
    }
}
