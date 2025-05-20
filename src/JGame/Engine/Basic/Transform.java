package JGame.Engine.Basic;

import JGame.Engine.EventSystem.EventHandler;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Structures.Matrix4x4;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;

import java.util.ArrayList;

/**
 * Class representing an object's transform, containing information about position, rotation, scale and hierarchy
 */
public class Transform extends BaseObject
{
    private static final Transform worldParent = BaseObject.CreateInstance(Transform.class);

    private JGameObject object;
    private final ArrayList<Transform> children = new ArrayList<>();
    private Transform parent;

    private Vector3D localPosition = Vector3D.Zero;
    private Vector3D globalPosition = Vector3D.Zero;

    private Quaternion localRotation = Quaternion.Identity;
    private Quaternion globalRotation = Quaternion.Identity;

    private Vector3D localScale = Vector3D.One;
    private Vector3D globalScale = Vector3D.One;

    private Matrix4x4 transformationMatrix = Matrix4x4.Identity();

    /**
     * Invoked when the rotation is changed or updated in the transform
     */
    public final EventHandler OnChangeRotation = new EventHandler();
    /**
     * Invoked when the position is changed or updated in the transform
     */
    public final EventHandler OnChangePosition = new EventHandler();
    /**
     * Invoked when the scale is changed or updated in the transform
     */
    public final EventHandler OnChangeScale = new EventHandler();
    /**
     * Invoked when the transformation matrix is changed or updated in the transform
     */
    public final EventHandler OnChangeTransformation = new EventHandler();

    /**
     * Sets the object to which this transform is attached to
     * @param object
     * The object
     */
    protected void SetGameObject(JGameObject object)
    {
        this.object = object;
    }


    //----- Callbacks -----
    @Override
    protected void Initialize()
    {
        if(this != worldParent)
            SetParent(worldParent);
    }

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
    public JGameObject object()
    {
        return object;
    }
    //------ Adder Functions ------

    /**
     * Adds a vector to the global position
     * @param delta
     * The vector to add
     */
    public void PositionAdd(Vector3D delta)
    {
        SetGlobalPosition(GetGlobalPosition().Add(delta));
    }

    /**
     * Adds a rotation-vector to the global rotation
     * @param delta
     * The rotation vector
     */
    public void RotationAdd(Vector3D delta)
    {
        SetGlobalRotation(GetGlobalRotation().Add(delta));
    }

    //------ Setter Functions ------

    /**
     * Resets a transform to the origin
     */
    public void ResetTransform()
    {
        SetParent(null);
        SetGlobalPosition(Vector3D.Zero);
        SetGlobalRotation(Quaternion.Identity);
        SetGlobalScale(Vector3D.One);
    }

    public void SetLocalScale(Vector3D newScale)
    {
        if(newScale == null)
        {
            Logger.DebugError("Can't assign null scale!");
            return;
        }

        if(newScale.equals(localScale))
            return;

        localScale = newScale;
        UpdateGlobalScale();
        UpdateTransformationMatrix();
    }
    public void SetGlobalScale(Vector3D newScale)
    {
        if(newScale == null)
        {
            Logger.DebugError("Can't assign null scale!");
            return;
        }

        if(newScale.equals(globalScale))
            return;

        globalScale = newScale;
        UpdateLocalScale();
        UpdateTransformationMatrix();
    }
    public void SetLocalPosition(Vector3D newPosition)
    {
        if(newPosition == null)
        {
            Logger.DebugError("Can't assign null position!");
            return;
        }

        if(newPosition.equals(localPosition))
            return;

        localPosition = newPosition;
        UpdateGlobalPosition();
        UpdateTransformationMatrix();
    }
    public void SetGlobalPosition(Vector3D newPosition)
    {
        if(newPosition == null)
        {
            Logger.DebugError("Can't assign null position!");
            return;
        }

        if(globalPosition.equals(newPosition))
            return;

        globalPosition = newPosition;
        UpdateLocalPosition();
        UpdateTransformationMatrix();
    }

    public void SetLocalRotation(Quaternion newRotation)
    {
        if(newRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }
        newRotation = newRotation.Normalized();

        if(localRotation.equals(newRotation))
            return;

        localRotation = newRotation;
        UpdateGlobalRotation();
        UpdateTransformationMatrix();
    }
    public void SetGlobalRotation(Quaternion newRotation)
    {
        if(newRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }
        newRotation = newRotation.Normalized();

        if(globalRotation.equals(newRotation))
            return;

        globalRotation = newRotation;
        UpdateLocalRotation();
        UpdateTransformationMatrix();
    }

    /**
     * Sets the local rotation in euler angles, in degrees
     * @param newEulerRotation
     * The new rotation in degrees
     */
    public void SetLocalRotationEuler(Vector3D newEulerRotation)
    {
        SetLocalRotationEuler(newEulerRotation, false);
    }
    /**
     * Sets the local rotation in euler angles
     * @param newEulerRotation
     * The new rotation
     * @param inRadians
     * Is the new rotation in radians?
     */
    public void SetLocalRotationEuler(Vector3D newEulerRotation, boolean inRadians)
    {
        if(newEulerRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }

        SetLocalRotation(Quaternion.EulerToQuaternion(newEulerRotation, inRadians));
    }

    /**
     * Sets the Global rotation in euler angles, in degrees
     * @param newEulerRotation
     * The new rotation in degrees
     */
    public void SetGlobalRotationEuler(Vector3D newEulerRotation)
    {
        SetGlobalRotationEuler(newEulerRotation, false);
    }
    /**
     * Sets the Global rotation in euler angles
     * @param newEulerRotation
     * The new rotation
     * @param inRadians
     * Is the new rotation in radians?
     */
    public void SetGlobalRotationEuler(Vector3D newEulerRotation, boolean inRadians)
    {
        if(newEulerRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }

        SetGlobalRotation(Quaternion.EulerToQuaternion(newEulerRotation, inRadians));
    }

    public void SetGlobalPositionAndRotation(Vector3D newPosition, Quaternion newRotation)
    {
        if(newRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }
        if(newPosition == null)
        {
            Logger.DebugError("Can't assign null position!");
            return;
        }
        newRotation = newRotation.Normalized();

        if(!globalRotation.equals(newRotation))
        {
            globalRotation = newRotation;
            UpdateLocalRotation();
        }

        if(!globalPosition.equals(newPosition))
        {
            globalPosition = newPosition;
            UpdateLocalPosition();
        }

        UpdateTransformationMatrix();
    }

    public void SetLocalPositionAndRotation(Vector3D newPosition, Quaternion newRotation)
    {
        if(newRotation == null)
        {
            Logger.DebugError("Can't assign null rotation!");
            return;
        }
        if(newPosition == null)
        {
            Logger.DebugError("Can't assign null position!");
            return;
        }
        newRotation = newRotation.Normalized();

        if(!localRotation.equals(newRotation))
        {
            localRotation = newRotation;
            UpdateGlobalRotation();
        }

        if(!localPosition.equals(newPosition))
        {
            localPosition = newPosition;
            UpdateGlobalPosition();
        }

        UpdateTransformationMatrix();
    }

    //-------Update Functions-------

    private void UpdateTransformationMatrix()
    {
        transformationMatrix = Matrix4x4.Transformation(globalPosition, globalScale, globalRotation);
        OnChangeTransformation.Invoke();
    }

    private void UpdateAllGlobalComponents()
    {
        globalScale = Vector3D.Multiply(localScale, parent.globalScale);
        globalPosition = Vector3D.Add(localPosition.Rotate(parent.globalRotation), parent.globalPosition);
        globalRotation = Quaternion.Multiply(parent.globalRotation, localRotation);

        UpdateTransformationMatrix();

        OnChangePosition.Invoke();
        OnChangeRotation.Invoke();
        OnChangeScale.Invoke();

        for(Transform child : children)
        {
            child.UpdateAllGlobalComponents();
        }
    }

    private void UpdateAllLocalComponents()
    {
        localPosition = Vector3D.Subtract(globalPosition, parent.globalPosition).Rotate(parent.globalRotation.Inverse());
        localRotation = Quaternion.Divide(globalRotation, parent.globalRotation);
        localScale = Vector3D.Divide(globalScale, parent.globalScale);

        UpdateTransformationMatrix();

        OnChangePosition.Invoke();
        OnChangeRotation.Invoke();
        OnChangeScale.Invoke();

        for(Transform child : children)
        {
            child.UpdateAllGlobalComponents();
        }
    }

    private void UpdateGlobalPosition()
    {
        Vector3D scaledPosition = Vector3D.Multiply(localPosition, parent.globalScale);
        Vector3D newPosition = Vector3D.Add(scaledPosition.Rotate(parent.globalRotation), parent.globalPosition);

        if (globalPosition.equals(newPosition))
            return;

        globalPosition = newPosition;
        OnChangePosition.Invoke();

        for (Transform child : children)
        {
            child.UpdateGlobalPosition();
            child.UpdateTransformationMatrix();
        }
    }

    private void UpdateLocalPosition()
    {
        Vector3D scaledParentPosition = Vector3D.Multiply(parent.globalPosition, parent.globalScale);
        Vector3D unscaledGlobalPosition = Vector3D.Subtract(globalPosition, scaledParentPosition);

        Vector3D newPosition = unscaledGlobalPosition.Rotate(parent.globalRotation.Inverse());

        if(localPosition.equals(newPosition))
            return;

        localPosition = newPosition;
        OnChangePosition.Invoke();

        for (Transform child : children)
        {
            child.UpdateGlobalPosition();
            child.UpdateTransformationMatrix();
        }
    }

    private void UpdateGlobalRotation()
    {
        Quaternion newRotation = Quaternion.Multiply(parent.globalRotation, localRotation);

        if(globalRotation.equals(newRotation))
            return;

        globalRotation = newRotation;
        UpdateGlobalPosition();
        OnChangeRotation.Invoke();

        for (Transform child : children)
        {
            child.UpdateGlobalRotation();
            child.UpdateTransformationMatrix();
        }
    }

    private void UpdateLocalRotation()
    {
        Quaternion newRotation = Quaternion.Divide(globalRotation, parent.globalRotation);

        if(localRotation.equals(newRotation))
            return;

        localRotation = newRotation;
        UpdateGlobalPosition();
        OnChangeRotation.Invoke();

        for (Transform child : children)
        {
            child.UpdateGlobalRotation();
            child.UpdateTransformationMatrix();
        }
    }

    private void UpdateGlobalScale()
    {
        Vector3D newScale = Vector3D.Multiply(localScale, parent.globalScale);

        if(globalScale.equals(newScale))
            return;

        globalScale = newScale;
        OnChangeScale.Invoke();

        for (Transform child : children)
        {
            child.UpdateGlobalScale();
            child.UpdateGlobalPosition();
            child.UpdateTransformationMatrix();
        }
    }

    private void UpdateLocalScale()
    {
        Vector3D newScale = Vector3D.Divide(globalScale, parent.globalScale);

        if(localScale.equals(newScale))
            return;

        localScale = newScale;
        OnChangeScale.Invoke();

        for (Transform child : children)
        {
            child.UpdateGlobalScale();
            child.UpdateGlobalPosition();
            child.UpdateTransformationMatrix();
        }
    }

    //-----Miscellaneous Functions-----

    /**
     * Rotates the transform along an absolute axis
     * @param axis
     * The axis to rotate along
     * @param rotation
     * The amount in degrees to rotate
     */
    public void RotateAxis(Vector3D axis, float rotation)
    {
        RotateAxis(axis, rotation, false);
    }
    /**
     * Rotates the transform along an absolute axis
     * @param axis
     * The axis to rotate along
     * @param rotation
     * The amount to rotate
     * @param radians
     * Use radians or degrees?
     */
    public void RotateAxis(Vector3D axis, float rotation, boolean radians)
    {
        SetLocalRotation(globalRotation.RotateAxis(axis, rotation * (radians ? 1 : MathUtilities.TO_RADIANS)));
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

        if(this.parent == parent)
            return;

        if(this.parent != null)
            this.parent.children.remove(this);

        this.parent = parent;
        this.parent.children.add(this);

        localPosition = Vector3D.Subtract(globalPosition, parent.globalPosition).Rotate(parent.globalRotation.Inverse());
        localRotation = Quaternion.Divide(globalRotation, parent.globalRotation);
        localScale = Vector3D.Divide(globalScale, parent.globalScale);

        globalScale = Vector3D.Multiply(localScale, parent.globalScale);
        globalPosition = Vector3D.Add(localPosition.Rotate(parent.globalRotation), parent.globalPosition);
        globalRotation = Quaternion.Multiply(parent.globalRotation, localRotation);

        UpdateTransformationMatrix();

        OnChangePosition.Invoke();
        OnChangeRotation.Invoke();
        OnChangeScale.Invoke();

        for(Transform child : children)
        {
            child.UpdateAllGlobalComponents();
        }
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
    public Vector3D WorldToLocalSpace(Vector3D point)
    {
        return transformationMatrix.Inverse().Multiply(point);
    }

    //------Direction functions------

    /**
     * Returns the relative Up Vector for this object.
     */
    public Vector3D Up()
    {
        return new Vector3D(
                transformationMatrix.values[1],
                transformationMatrix.values[5],
                transformationMatrix.values[9]
        ).Normalized();
    }

    /**
     * Returns the relative Down Vector for this object.
     */
    public Vector3D Down()
    {
        return Up().Negate();
    }

    /**
     * Returns the relative Forward Vector for this object.
     */
    public Vector3D Forward()
    {
        return new Vector3D(
                transformationMatrix.values[2],
                transformationMatrix.values[6],
                transformationMatrix.values[10]
        ).Normalized();
    }

    /**
     * Returns the relative Backward Vector for this object.
     */
    public Vector3D Backward()
    {
        return Forward().Negate();
    }

    /**
     * Returns the relative Right Vector for this object.
     */
    public Vector3D Right()
    {
        return new Vector3D(
                transformationMatrix.values[0],
                transformationMatrix.values[4],
                transformationMatrix.values[8]
        ).Normalized();
    }

    /**
     * Returns the relative Left Vector for this object.
     */
    public Vector3D Left()
    {
        return Right().Negate();
    }

    /**
     * Gets the X,Y or Z axis from an integer index
     * @param index
     * Which axis to get, in order
     * @return
     * The selected axis
     */
    public Vector3D GetAxis(int index)
    {
        if(index == 0) return Right();
        if(index == 1) return Up();
        if(index == 2) return Forward();
        throw new IllegalArgumentException("Invalid axis index! Use 0, 1, and 2");
    }
}
