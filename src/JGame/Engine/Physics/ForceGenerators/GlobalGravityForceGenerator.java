package JGame.Engine.Physics.ForceGenerators;

import JGame.Engine.Basic.BaseEngineClass;
import JGame.Engine.Physics.General.Physics;
import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Settings;

public class GlobalGravityForceGenerator implements IForceGenerator
{
    public static final GlobalGravityForceGenerator Instance;

    static
    {
        Instance = new GlobalGravityForceGenerator();
    }

    @Override
    public void UpdateForce(Rigidbody body)
    {
        if(body.useGravity)
            body.AddForce(Physics.gravityDirection.Scale(Settings.Physics.gravityForce * body.gravityScale * body.GetMass()), Rigidbody.ForceType.Force);
    }

    @Override
    public boolean isActive()
    {
        return Physics.useGlobalGravity;
    }
}
