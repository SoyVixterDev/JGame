package JGame.Engine.Physics.Constraints;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.ForceGenerators.ElasticForceGenerator;
import JGame.Engine.Physics.General.ForceRegistration;
import JGame.Engine.Physics.General.Physics;
import JGame.Engine.Structures.Vector3D;

/**
 * Constraint that creates an Elastic connection between two particles
 */
public class ElasticConstraint extends JComponent
{
    private final ElasticForceGenerator forceGeneratorAB = new ElasticForceGenerator(this);
    private final ElasticForceGenerator forceGeneratorBA = new ElasticForceGenerator(this);

    private ForceRegistration forceRegistrationAB;
    private ForceRegistration forceRegistrationBA;


    /**
     * The spring constant applied to the force
     */
    public float springConstant = 1.0f;
    /**
     * The rest length for the connection. The force will be zero if the length of the connection equals to this
     */
    public float restLength = 1.0f;

    /**
     * Determines the type of elastic behavior
     */
    public ElasticConstraintType elasticType = ElasticConstraintType.Spring;

    /**
     * Sets the two particles that should be connected to each other
     * @param a
     * The first particle
     * @param connectionOffsetA
     * The position of the connection point for the object A, in local space
     * @param b
     * The second particle
     * @param connectionOffsetB
     * The position of the connection point for the object B, in local space
     */
    public void SetConnection(Rigidbody a, Vector3D connectionOffsetA, Rigidbody b, Vector3D connectionOffsetB)
    {
        if(forceRegistrationAB != null) Physics.UnregisterForce(forceRegistrationAB);

        forceGeneratorAB.other = b;
        forceGeneratorAB.connectionPoint = connectionOffsetA;
        forceGeneratorAB.connectionPointOther = connectionOffsetB;

        forceRegistrationAB = a.AddForce(forceGeneratorAB);

        if(forceRegistrationBA != null) Physics.UnregisterForce(forceRegistrationBA);

        forceGeneratorBA.other = a;
        forceGeneratorBA.connectionPoint = connectionOffsetB;
        forceGeneratorBA.connectionPointOther = connectionOffsetA;
        forceRegistrationBA = b.AddForce(forceGeneratorBA);
    }
    /**
     * Sets the two Rigidbodies that should be connected to each other, with connection points at the origin
     * @param a
     * The first particle
     * @param b
     * The second particle
     */
    public void SetConnection(Rigidbody a, Rigidbody b)
    {
        SetConnection(a, Vector3D.Zero, b, Vector3D.Zero);
    }


    public enum ElasticConstraintType
    {
        /**
         * Applies both pull and push forces
         */
        Spring,
        /**
         * Only applies pull forces
         */
        Bungee
    }
}
