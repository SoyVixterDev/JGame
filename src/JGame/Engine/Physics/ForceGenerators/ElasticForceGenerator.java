package JGame.Engine.Physics.ForceGenerators;

import JGame.Engine.Basic.BaseEngineClass;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Constraints.ElasticConstraint;
import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Structures.Vector3D;

public class ElasticForceGenerator implements IForceGenerator
{
    /**
     * The elastic constraint holding this force generator
     */
    private final ElasticConstraint elasticConstraint;

    /**
     * The position from where the elastic force originates
     */
    public Rigidbody other;

    public Vector3D connectionPoint;
    public Vector3D connectionPointOther;

    public ElasticForceGenerator(ElasticConstraint elasticConstraint)
    {
        this.elasticConstraint = elasticConstraint;
    }

    @Override
    public void UpdateForce(Rigidbody body)
    {
        if(other == null)
            return;

        Vector3D worldConnectionPoint = body.Transform().LocalToWorldSpace(connectionPoint);
        Vector3D worldConnectionPointOther = other.Transform().LocalToWorldSpace(connectionPointOther);

        Vector3D force = Vector3D.Subtract(worldConnectionPoint, worldConnectionPointOther);

        float magnitude = force.Magnitude();

        if(elasticConstraint.elasticType == ElasticConstraint.ElasticConstraintType.Spring)
        {
            if(magnitude <= elasticConstraint.restLength)
                return;
        }

        magnitude = Math.abs(magnitude - elasticConstraint.restLength);
        magnitude *= elasticConstraint.springConstant;

        force = force.Normalized().Scale(-magnitude);

        body.AddForceAtPoint(force, worldConnectionPoint, Rigidbody.ForceType.Force);
    }

    @Override
    public boolean isActive()
    {
        return elasticConstraint.IsAvailable();
    }
}
