package JGame.Engine.Basic;

import JGame.Engine.EventSystem.EventHandler;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Class representing an object's transform, containing information about position, rotation, scale and hierarchy
 */
public class Transform extends BaseEngineClass
{
    private static final Transform worldParent = new Transform(null);

    private final JGameObject object;
    private final ArrayList<Transform> children = new ArrayList<>();
    private Transform parent;

    private Vector3D localPosition = Vector3D.Zero;
    private Vector3D globalPosition = Vector3D.Zero;

    private Quaternion localRotation = Quaternion.Identity;
    private Quaternion globalRotation = Quaternion.Identity;

    private Vector3D localScale = Vector3D.One;
    private Vector3D globalScale = Vector3D.One;

    private Matrix4x4 transformationMatrix = Matrix4x4.Identity();

    public final EventHandler OnChangeRotation = new EventHandler();
    public final EventHandler OnChangePosition = new EventHandler();
    public final EventHandler OnChangeScale = new EventHandler();


    Transform(JGameObject object)
    {
        super();
        this.object = object;

        if(object != null)
            SetParent(worldParent);
    }

    //----- Callbacks -----

    //----- Getter Functions -----
    public Vector3D GetLocalScale()
    {
        return localScale;
    }
    public Vector3D GetGlobalScale()
    {
        return globalScale;
    }

    public Vector3D GetLocalPosition()
    {
        return localPosition;
    }
    public Quaternion GetLocalRotation()
    {
        return localRotation;
    }
    public Vector3D GetGlobalPosition()
    {
        return globalPosition;
    }
    public Quaternion GetGlobalRotation()
    {
        return globalRotation;
    }
    public Transform GetParent()
    {
        return parent == worldParent ? null : parent;
    }
    public Transform GetChild(int index)
    {
        return children.get(index);
    }
    public ArrayList<Transform> GetChildren()
    {
        return children;
    }
    public Matrix4x4 GetTransformationMatrix()
    {
        return transformationMatrix;
    }
    public JGameObject Object()
    {
        return object;
    }


    //------ Setter Functions ------
    public void SetLocalScale(Vector3D newScale)
    {
        if(newScale == null)
        {
            Logger.DebugError("Can't assign null scale!");
            return;
        }

        localScale = newScale;
        UpdateGlobalScale();
        UpdateLocalScale();
    }
    public void SetGlobalScale(Vector3D newScale)
    {
        if(newScale == null)
        {
            Logger.DebugError("Can't assign null scale!");
            return;
        }

        globalScale = newScale;
        UpdateLocalScale();
        UpdateGlobalScale();
    }
    public void SetLocalPosition(Vector3D newPosition)
    {
        if(newPosition == null)
        {
            Logger.DebugError("Can't assign null position!");
            return;
        }

        localPosition = newPosition;
        UpdateGlobalPosition();
        UpdateLocalPosition();
    }
    public void SetGlobalPosition(Vector3D newPosition)
    {
        if(newPosition == null)
        {
            Logger.DebugError("Can't assign null position!");
            return;
        }

        globalPosition = newPosition;
        UpdateLocalPosition();
        UpdateGlobalPosition();
    }

    public void SetLocalRotation(Quaternion newRotation)
    {
        if(newRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }

        localRotation = newRotation.Normalized();
        UpdateGlobalRotation();
        UpdateLocalRotation();
    }
    public void SetGlobalRotation(Quaternion newRotation)
    {
        if(newRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }

        globalRotation = newRotation.Normalized();
        UpdateLocalRotation();
        UpdateGlobalRotation();
    }

    public void SetLocalRotationEuler(Vector3D newEulerRotation)
    {
        if(newEulerRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }

        SetLocalRotation(Quaternion.EulerToQuaternion(newEulerRotation));
    }
    public void SetGlobalRotationEuler(Vector3D newEulerRotation)
    {
        if(newEulerRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }

        SetGlobalRotation(Quaternion.EulerToQuaternion(newEulerRotation));
    }

    //-------Update Functions-------

    private void UpdateTransformationMatrix()
    {
        transformationMatrix = Matrix4x4.Transformation(globalPosition, globalScale, globalRotation);
    }

    /**
     * Updates the Global scale in itself and the children based on the local scale and parent globalScale
     */
    private void UpdateGlobalScale()
    {
        globalScale = Vector3D.Multiply(localScale, parent.globalScale);
        for(Transform child : children)
        {
            if(child != null)
                child.UpdateGlobalScale();
        }

        OnChangeScale.Invoke();
        UpdateTransformationMatrix();
    }

    /**
     * Updates the Local scale on itself and the children based on the global scale and the parent's global scale
     */
    private void UpdateLocalScale()
    {
        localScale = Vector3D.Divide(globalScale, parent.globalScale);
        for(Transform child : children)
        {
            if(child != null)
                child.UpdateGlobalScale();
        }

        OnChangeScale.Invoke();
        UpdateTransformationMatrix();
    }

    /**
     * Updates the Global position on itself and the children based on the local position and parent's global position
     */
    private void UpdateGlobalPosition()
    {
        globalPosition = Vector3D.Add
        (
            localPosition.Rotate(parent.globalRotation),
            parent.globalPosition
        );

        for(Transform child : children)
        {
            if(child != null)
                child.UpdateGlobalPosition();
        }


        OnChangePosition.Invoke();
        UpdateTransformationMatrix();
    }

    /**
     * Updates the local position on itself and the children based on the global position and parent's global position
     */
    private void UpdateLocalPosition()
    {
        localPosition = Vector3D.Subtract(globalPosition, parent.globalPosition).Rotate(parent.globalRotation.Inverse());
        for(Transform child : children)
        {
            if(child != null)
                child.UpdateGlobalPosition();
        }

        OnChangePosition.Invoke();
        UpdateTransformationMatrix();
    }

    /**
     * Updates the global rotation on itself and the children based on the local rotation and parent's global rotation
     */
    private void UpdateGlobalRotation()
    {
        globalRotation = Quaternion.Multiply(parent.globalRotation, localRotation).Normalized();

        UpdateGlobalPosition();

        for(Transform child : children)
        {
            if(child != null)
                child.UpdateGlobalRotation();
        }

        OnChangeRotation.Invoke();
        UpdateTransformationMatrix();
    }

    /**
     * Updates the local rotation on itself and the children based on the global rotation and the parent's global rotation
     */
    private void UpdateLocalRotation()
    {
        localRotation = Quaternion.Divide(globalRotation, parent.globalRotation).Normalized();
        UpdateGlobalPosition();

        for(Transform child : children)
        {
            if(child != null)
                child.UpdateGlobalRotation();
        }

        OnChangeRotation.Invoke();
        UpdateTransformationMatrix();
    }

    //-----Miscellaneous Functions-----

    /**
     * Rotates the transform along an absolute axis
     * @param axis
     * The axis to rotate along
     * @param rotation
     * The amount in radians to rotate
     */
    public void RotateAxis(Vector3D axis, float rotation)
    {
        SetLocalRotation(globalRotation.RotateAxis(axis, rotation));
    }

    /**
     * Sets a new parent to the object
     * @param parent
     * The new parent to set
     */
    public void SetParent(Transform parent)
    {
        if(parent == null)
            parent = worldParent;

        if(this.parent != null)
            this.parent.children.remove(this);

        this.parent = parent;

        UpdateLocalRotation();
        UpdateLocalPosition();
        UpdateLocalScale();

        if(this.parent != null)
            this.parent.children.add(this);
    }

    /**
     * Transforms a point from local to world space
     * @param point
     * The point to transform, in local space
     * @return
     * The point transformed into world space
     */
    public Vector3D LocalToWorldSpace(Vector3D point)
    {
        return transformationMatrix.Multiply(point);
    }

    //------Direction functions------

    /**
     * Returns a Vector representing the relative Up Vector for this object
     * @return
     * Vector representing the relative Up Vector for this object
     */
    public Vector3D Up()
    {
        return Vector3D.Up.Rotate(globalRotation);
    }
    /**
     * Returns a Vector representing the relative Down Vector for this object
     * @return
     * Vector representing the relative Down Vector for this object
     */
    public Vector3D Down()
    {
        return Vector3D.Down.Rotate(globalRotation);
    }
    /**
     * Returns a Vector representing the relative Forward Vector for this object
     * @return
     * Vector representing the relative Forward Vector for this object
     */
    public Vector3D Forward()
    {
        return Vector3D.Forward.Rotate(globalRotation);
    }
    /**
     * Returns a Vector representing the relative Backward Vector for this object
     * @return
     * Vector representing the relative Backward Vector for this object
     */
    public Vector3D Backward()
    {
        return Vector3D.Backward.Rotate(globalRotation);
    }
    /**
     * Returns a Vector representing the relative Right Vector for this object
     * @return
     * Vector representing the relative Right Vector for this object
     */
    public Vector3D Right()
    {
        return Vector3D.Right.Rotate(globalRotation);
    }
    /**
     * Returns a Vector representing the relative Left Vector for this object
     * @return
     * Vector representing the relative Left Vector for this object
     */
    public Vector3D Left()
    {
        return Vector3D.Left.Rotate(globalRotation);
    }

}
