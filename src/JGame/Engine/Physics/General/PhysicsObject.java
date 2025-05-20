package JGame.Engine.Physics.General;

import JGame.Engine.Basic.JComponent;
import JGame.Engine.Basic.JGameObject;

public abstract class PhysicsObject extends JComponent
{
    @Override
    protected void OnEnable()
    {
        Physics.physicsObjects.add(this);
    }

    @Override
    protected void OnDisable()
    {
        Physics.physicsObjects.remove(this);
    }
}
