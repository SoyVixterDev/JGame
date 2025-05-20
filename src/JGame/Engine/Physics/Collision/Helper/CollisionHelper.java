package JGame.Engine.Physics.Collision.Helper;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Physics.Collision.Colliders.*;
import JGame.Engine.Physics.Collision.Contact.Contact;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.MathUtilities;
import org.lwjgl.system.windows.DISPLAY_DEVICE;

import java.util.*;

public class CollisionHelper
{
    /**
     * Calculates the point of closest approach between two line segments
     * @param pointA
     * Starting point of segment A
     * @param directionA
     * Direction of segment A
     * @param sizeA
     * Length of segment A
     * @param pointB
     * Starting point of segment B
     * @param directionB
     * Direction of segment B
     * @param sizeB
     * Length of segment B
     * @param useA
     * Should the algorithm use A's midpoint? Otherwise, use B's midpoint
     * @return
     * The closest point between both segments
     */
    public static Vector3D GetClosestPoint(Vector3D pointA, Vector3D directionA, float sizeA,
                                           Vector3D pointB, Vector3D directionB, float sizeB,
                                           boolean useA)
    {
        float squaredMagnitudeA = directionA.SquaredMagnitude();
        float squaredMagnitudeB = directionB.SquaredMagnitude();
        float dotProductAB = directionB.DotProduct(directionA);

        float denominator = squaredMagnitudeA * squaredMagnitudeB - dotProductAB * dotProductAB;

        if(Math.abs(denominator) < 1e-6)
            return useA ? pointA : pointB;

        Vector3D toStart = pointA.Subtract(pointB);
        float dotProductStartA = pointA.DotProduct(toStart);
        float dotProductStartB = pointB.DotProduct(toStart);

        float paramEdgeA = (dotProductAB * dotProductStartB - squaredMagnitudeB * dotProductStartA) / denominator;
        float paramEdgeB = (squaredMagnitudeA * dotProductStartB - dotProductAB * dotProductStartA) / denominator;

        if(paramEdgeA > sizeA ||
            paramEdgeA < -sizeA ||
            paramEdgeB > sizeB ||
            paramEdgeB < -sizeB)
        {
            return useA ? pointA : pointB;
        }
        else
        {
            Vector3D closestA = pointA.Add(directionA.Scale(paramEdgeA));
            Vector3D closestB = pointB.Add(directionB.Scale(paramEdgeB));

            return closestA.Scale(0.5F).Add(closestB.Scale(0.5f));
        }

    }
    /**
     *  Returns a Point-Face contact from another collider and a plane
     */
    public static Contact PointFaceContact(Collider collider, PlaneCollider plane, Vector3D T, Vector3D normal, float penetration)
    {
        if(normal.DotProduct(T) > 1e-6)
            normal = normal.Negate();

        Vector2D vector2D = plane.GetScaledHalfSize();

        float vertexX = vector2D.x;
        float vertexZ = vector2D.y;

        if(plane.transform().Right().DotProduct(normal) < 0) vertexX = -vector2D.x;
        if(plane.transform().Forward().DotProduct(normal) < 0) vertexZ = -vector2D.y;

        Vector3D vector = new Vector3D(vertexX, 0, vertexZ);

        return new Contact(collider.GetRigidbody(), plane.GetRigidbody(), plane.transform().LocalToWorldSpace(vector), normal, penetration);
    }
    /**
     *  Returns a Point-Face contact from another collider and a box
     */
    public static Contact PointFaceContact(Collider collider, BoxCollider box, Vector3D T, Vector3D normal, float penetration)
    {
        // Ensure normal direction is correct
        if (normal.DotProduct(T) <  1e-6)
            normal = normal.Negate();

        Vector3D scaledHalfSize = box.GetScaledHalfSize();
        Vector3D vertex = new Vector3D(
                normal.DotProduct(box.transform().Right()) < 0 ? -scaledHalfSize.x : scaledHalfSize.x,
                normal.DotProduct(box.transform().Up()) < 0 ? -scaledHalfSize.y : scaledHalfSize.y,
                normal.DotProduct(box.transform().Forward()) < 0 ? -scaledHalfSize.z : scaledHalfSize.z
        );

        vertex = box.transform().LocalToWorldSpace(vertex);

        return new Contact(collider.GetRigidbody(), box.GetRigidbody(), vertex, normal, penetration);
    }

    /**
     * Gets the penetration depth between a box and a plane
     * @param box
     * The box
     * @param plane
     * The plane
     * @param axis
     * The axis
     * @param T
     * The relative vector
     * @return
     * The penetration depth
     */
    public static float GetPenetrationDepth(BoxCollider box, PlaneCollider plane, Vector3D axis, Vector3D T)
    {
        float projectionA = ProjectOntoAxis(box, axis);
        float projectionB = ProjectOntoAxis(plane, axis);
        float distance = Math.abs(T.DotProduct(axis));

        return (projectionA + projectionB) - distance;
    }

    /**
     * Gets the penetration depth between two planes along an axis
     * @param planeA
     * The first plane
     * @param planeB
     * The second plane
     * @param axis
     * The axis
     * @param T
     * The relative vector
     * @return
     * The penetration depth
     */
    public static float GetPenetrationDepth(PlaneCollider planeA, PlaneCollider planeB, Vector3D axis, Vector3D T)
    {
        float projectionA = ProjectOntoAxis(planeA, axis);
        float projectionB = ProjectOntoAxis(planeB, axis);
        float distance = Math.abs(T.DotProduct(axis));

        return (projectionA + projectionB) - distance;
    }

    /**
     * Gets the penetration depth between two boxes along an axis
     * @param boxA
     * The first Box
     * @param boxB
     * The second Box
     * @param axis
     * The axis
     * @param T
     * The relative vector
     * @return
     * The penetration depth
     */
    public static float GetPenetrationDepth(BoxCollider boxA, BoxCollider boxB, Vector3D axis, Vector3D T)
    {
        float projectionA = ProjectOntoAxis(boxA, axis);
        float projectionB = ProjectOntoAxis(boxB, axis);
        float distance = Math.abs(T.DotProduct(axis));

        return (projectionA + projectionB) - distance;
    }

    /**
     * Checks if a box and a plane overlap in an axis
     * @param box
     * The box
     * @param plane
     * The plane
     * @param axis
     * The axis to test
     * @param T
     * The displacement between the box and plane's centers
     * @return
     * True if there's an overlap in the axis
     */
    private static boolean CheckAxisOverlap(BoxCollider box, PlaneCollider plane, Vector3D axis, Vector3D T)
    {
        return GetPenetrationDepth(box, plane, axis, T) >= 0;
    }
    /**
     * Checks if two planes overlap in an axis
     * @param planeA
     * Plane A
     * @param planeB
     * Plane B
     * @param axis
     * The axis to test
     * @param T
     * The displacement between the planes centers
     * @return
     * True if there's an overlap in the axis
     */
    private static boolean CheckAxisOverlap(PlaneCollider planeA, PlaneCollider planeB, Vector3D axis, Vector3D T)
    {
        return GetPenetrationDepth(planeA, planeB, axis, T) >= 0;
    }
    /**
     * Checks if two boxes overlap in an axis
     * @param boxA
     * Box A
     * @param boxB
     * Box B
     * @param axis
     * The axis to test
     * @param T
     * The displacement between the boxes centers
     * @return
     * True if there's an overlap in the axis
     */
    private static boolean CheckAxisOverlap(BoxCollider boxA, BoxCollider boxB, Vector3D axis, Vector3D T)
    {
        return GetPenetrationDepth(boxA, boxB, axis, T) >= 0;
    }
    /**
     * Projects a plane into an axis
     * @param plane
     * The plane
     * @param axis
     * The axis
     * @return
     * The value of the projected plane
     */
    private static float ProjectOntoAxis(PlaneCollider plane, Vector3D axis)
    {
        Vector2D halfSize = plane.GetScaledHalfSize();

        return Math.abs(plane.transform().Right().DotProduct(axis)) * halfSize.x +
                Math.abs(plane.transform().Forward().DotProduct(axis)) * halfSize.y;
    }
    /**
     * Projects a box into an axis
     * @param box
     * The box
     * @param axis
     * The axis
     * @return
     * The value of the projected box
     */
    private static float ProjectOntoAxis(BoxCollider box, Vector3D axis)
    {
        Vector3D halfSize = box.GetScaledHalfSize();

        return Math.abs(box.transform().Right().DotProduct(axis)) * halfSize.x +
                Math.abs(box.transform().Up().DotProduct(axis)) * halfSize.y +
                Math.abs(box.transform().Forward().DotProduct(axis)) * halfSize.z;
    }

    //---- Overlap Checking ----

    /**
     * Checks if two boxes overlap
     * @param boxA
     * The first box collider
     * @param boxB
     * The second box collider
     * @return
     * True if the boxes overlap
     */
    public static boolean Overlaps(BoxCollider boxA, BoxCollider boxB)
    {
        List<Vector3D> candidateAxes = new ArrayList<>();

        Vector3D[] axesA = {
                boxA.transform().Right(),
                boxA.transform().Up(),
                boxA.transform().Forward()
        };

        Vector3D[] axesB = {
                boxB.transform().Right(),
                boxB.transform().Up(),
                boxB.transform().Forward()
        };

        Collections.addAll(candidateAxes, axesA);
        Collections.addAll(candidateAxes, axesB);

        for (Vector3D axisA : axesA)
        {
            for (Vector3D axisB : axesB)
            {
                Vector3D cross = axisA.CrossProduct(axisB).Normalized();
                if (cross.SquaredMagnitude() > 1e-6)
                {
                    candidateAxes.add(cross);
                }
            }
        }

        Vector3D T = boxA.GetCenterWorld().Subtract(boxB.GetCenterWorld());

        for(Vector3D axis : candidateAxes)
        {
            if(!CheckAxisOverlap(boxA, boxB, axis, T))
                return false;
        }

        return true;
    }

    /**
     * Checks if a box and a sphere overlap
     * @param box
     * The box collider
     * @param sphere
     * The sphere collider
     * @return
     * True if the box and sphere overlap
     */
    public static boolean Overlaps(BoxCollider box, SphereCollider sphere)
    {
        Vector3D sphereCenter = sphere.GetCenterWorld();
        Vector3D boxScaledHalfSize = box.GetScaledHalfSize();
        float sphereRadius = sphere.GetScaledRadius();

        Vector3D localSphereCenter = box.transform().WorldToLocalSpace(sphereCenter);

        if (Math.abs(localSphereCenter.x) > boxScaledHalfSize.x + sphereRadius ||
            Math.abs(localSphereCenter.y) > boxScaledHalfSize.y + sphereRadius ||
            Math.abs(localSphereCenter.z) > boxScaledHalfSize.z + sphereRadius)
        {
            return false;
        }

        Vector3D closestPoint = new Vector3D(
                MathUtilities.Clamp(localSphereCenter.x, -boxScaledHalfSize.x, boxScaledHalfSize.x),
                MathUtilities.Clamp(localSphereCenter.y, -boxScaledHalfSize.y, boxScaledHalfSize.y),
                MathUtilities.Clamp(localSphereCenter.z, -boxScaledHalfSize.z, boxScaledHalfSize.z)
        ).Multiply(box.transform().GetGlobalScale().Normalized());;
        closestPoint = box.transform().LocalToWorldSpace(closestPoint);

        float distanceSquared = Vector3D.DistanceSquared(closestPoint, sphereCenter);

        return (distanceSquared <= sphereRadius * sphereRadius);
    }

    /**
     * Checks if a box and a cylinder overlap
     * @param box
     * The box collider
     * @param cylinder
     * The cylinder collider
     * @return
     * True if the box and cylinder overlap
     */
    public static boolean Overlaps(BoxCollider box, CylinderCollider cylinder)
    {
        return false;
    }

    /**
     * Checks if a box and a capsule overlap
     * @param box
     * The box collider
     * @param capsule
     * The capsule collider
     * @return
     * True if the box and capsule overlap
     */
    public static boolean Overlaps(BoxCollider box, CapsuleCollider capsule)
    {
        return false;
    }
    /**
     * Checks if a box and a plane overlap
     * @param box
     * The box collider
     * @param plane
     * The plane collider
     * @return
     * True if the box and plane overlap
     */
    public static boolean Overlaps(BoxCollider box, PlaneCollider plane)
    {
        List<Vector3D> candidateAxes = new ArrayList<>();

        Vector3D[] axesA = {
                box.transform().Right(),
                box.transform().Up(),
                box.transform().Forward()
        };

        Vector3D[] axesB = {
                plane.transform().Right(),
                plane.transform().Forward()
        };

        Collections.addAll(candidateAxes, axesA);
        Collections.addAll(candidateAxes, axesB);

        for (Vector3D axisA : axesA)
        {
            for (Vector3D axisB : axesB)
            {
                Vector3D cross = axisA.CrossProduct(axisB).Normalized();
                if (cross.SquaredMagnitude() > 1e-6)
                {
                    candidateAxes.add(cross);
                }
            }
        }

        Vector3D T = box.GetCenterWorld().Subtract(plane.GetCenterWorld());

        for(Vector3D axis : candidateAxes)
        {
            if(!CheckAxisOverlap(box, plane, axis, T))
                return false;
        }

        return true;
    }

    /**
     * Checks if two spheres overlap
     * @param sphereA
     * The first sphere collider
     * @param sphereB
     * The second sphere collider
     * @return
     * True if the spheres overlap
     */
    public static boolean Overlaps(SphereCollider sphereA, SphereCollider sphereB)
    {
        float squaredDistance = Vector3D.DistanceSquared(sphereA.GetCenterWorld(), sphereB.GetCenterWorld());
        float addedRadii = (sphereA.GetScaledRadius() + sphereB.GetScaledRadius());

        return squaredDistance <= (addedRadii * addedRadii);
    }

    /**
     * Checks if a sphere and a cylinder overlap
     * @param sphere
     * The sphere collider
     * @param cylinder
     * The cylinder collider
     * @return
     * True if the sphere and cylinder overlap
     */
    public static boolean Overlaps(SphereCollider sphere, CylinderCollider cylinder)
    {
        return false;
    }

    /**
     * Checks if a sphere and a capsule overlap
     * @param sphere
     * The sphere collider
     * @param capsule
     * The capsule collider
     * @return
     * True if the sphere and capsule overlap
     */
    public static boolean Overlaps(SphereCollider sphere, CapsuleCollider capsule)
    {
        return false;
    }
    /**
     * Checks if a sphere and a plane overlap
     * @param sphere
     * The sphere collider
     * @param plane
     * The plane collider
     * @return
     * True if the sphere and plane overlap
     */
    public static boolean Overlaps(SphereCollider sphere, PlaneCollider plane)
    {
        Vector3D sphereCenter = sphere.GetCenterWorld();
        Vector2D planeBoxHalfSize = plane.GetScaledHalfSize();
        float sphereRadius = sphere.GetScaledRadius();

        Vector3D localSphereCenter = plane.transform().WorldToLocalSpace(sphereCenter);

        if (Math.abs(localSphereCenter.x) > planeBoxHalfSize.x + sphereRadius ||
            Math.abs(localSphereCenter.y) > 1e-6 + sphereRadius ||
            Math.abs(localSphereCenter.z) > planeBoxHalfSize.y + sphereRadius)
        {
            return false;
        }

        Vector3D closestPoint = new Vector3D(
                MathUtilities.Clamp(localSphereCenter.x, -planeBoxHalfSize.x, planeBoxHalfSize.x),
                0,
                MathUtilities.Clamp(localSphereCenter.z, -planeBoxHalfSize.y, planeBoxHalfSize.y)
        );
        closestPoint = plane.transform().LocalToWorldSpace(closestPoint);

        float distanceSquared = Vector3D.DistanceSquared(closestPoint, sphereCenter);

        return (distanceSquared <= sphereRadius * sphereRadius);
    }

    /**
     * Checks if two cylinders overlap
     * @param cylinderA
     * The first cylinder collider
     * @param cylinderB
     * The second cylinder collider
     * @return
     * True if the cylinders overlap
     */
    public static boolean Overlaps(CylinderCollider cylinderA, CylinderCollider cylinderB)
    {
        return false;
    }

    /**
     * Checks if a cylinder and a capsule overlap
     * @param cylinder
     * The cylinder collider
     * @param capsule
     * The capsule collider
     * @return
     * True if the cylinder and capsule overlap
     */
    public static boolean Overlaps(CylinderCollider cylinder, CapsuleCollider capsule)
    {
        return false;
    }
    /**
     * Checks if a cylinder and a plane overlap
     * @param cylinder
     * The cylinder collider
     * @param plane
     * The plane collider
     * @return
     * True if the cylinder and plane overlap
     */
    public static boolean Overlaps(CylinderCollider cylinder, PlaneCollider plane)
    {
        return false;
    }

    /**
     * Checks if two capsules overlap
     * @param capsuleA
     * The first capsule collider
     * @param capsuleB
     * The second capsule collider
     * @return
     * True if the capsules overlap
     */
    public static boolean Overlaps(CapsuleCollider capsuleA, CapsuleCollider capsuleB)
    {
        return false;
    }
    /**
     * Checks if a capsule and a plane overlap
     * @param capsule
     * The cylinder collider
     * @param plane
     * The plane collider
     * @return
     * True if the capsule and plane overlap
     */
    public static boolean Overlaps(CapsuleCollider capsule, PlaneCollider plane)
    {
        return false;
    }
    /**
     * Checks if two planes overlap
     * @param planeA
     * The first plane collider
     * @param planeB
     * The second plane collider
     * @return
     * True if the plane overlap
     */
    public static boolean Overlaps(PlaneCollider planeA, PlaneCollider planeB)
    {
        List<Vector3D> candidateAxes = new ArrayList<>();

        Vector3D[] axesA = {
                planeA.transform().Right(),
                planeA.transform().Forward()
        };

        Vector3D[] axesB = {
                planeB.transform().Right(),
                planeB.transform().Forward()
        };

        Collections.addAll(candidateAxes, axesA);
        Collections.addAll(candidateAxes, axesB);

        for (Vector3D axisA : axesA)
        {
            for (Vector3D axisB : axesB)
            {
                Vector3D cross = axisA.CrossProduct(axisB);
                if (cross.SquaredMagnitude() > 1e-6)
                {
                    candidateAxes.add(cross.Normalized());
                }
            }
        }

        Vector3D T = planeA.GetCenterWorld().Subtract(planeB.GetCenterWorld());

        for(Vector3D axis : candidateAxes)
        {
            if(!CheckAxisOverlap(planeA, planeB, axis, T))
                return false;
        }

        return true;
    }

//---- Contact Generation ----

    /**
     * Generates contacts between two boxes
     * @param boxA
     * The first box collider
     * @param boxB
     * The second box collider
     * @return
     * A contact between the boxes
     */
    public static Contact GetContact(BoxCollider boxA, BoxCollider boxB)
    {
        List<Vector3D> candidateAxes = new ArrayList<>();

        Vector3D[] axesA = {
                boxA.transform().Right(),
                boxA.transform().Up(),
                boxA.transform().Forward()
        };

        Vector3D[] axesB = {
                boxB.transform().Right(),
                boxB.transform().Up(),
                boxB.transform().Forward()
        };

        Collections.addAll(candidateAxes, axesA);
        Collections.addAll(candidateAxes, axesB);

        for (Vector3D axisA : axesA)
        {
            for (Vector3D axisB : axesB)
            {
                Vector3D cross = axisA.CrossProduct(axisB);
                if (cross.SquaredMagnitude() > 1e-6)
                {
                    candidateAxes.add(cross.Normalized());
                }
            }
        }

        Vector3D T = boxA.GetCenterWorld().Subtract(boxB.GetCenterWorld());

        float minPen = Float.MAX_VALUE;
        int minPenAxisIdx = Integer.MAX_VALUE;
        int minPenSingleAxisIdx = -1;

        for(int i = 0; i < candidateAxes.size(); i++)
        {
            if(i == 6) minPenSingleAxisIdx = minPenAxisIdx;

            Vector3D candidateAxis = candidateAxes.get(i);

            float penetration = GetPenetrationDepth(boxA, boxB, candidateAxis, T);
            if(penetration < 0)
                return null;

            if(penetration < minPen)
            {
                minPen = penetration;
                minPenAxisIdx = i;
            }
        }

        if (minPenAxisIdx == Integer.MAX_VALUE)
            throw new IllegalStateException("No valid collision axis found.");

        if(minPenAxisIdx < 3)
        {
            Vector3D normal = boxA.transform().GetAxis(minPenAxisIdx);
            //Logger.DebugLog("First Box Axis!");
            return PointFaceContact(boxA, boxB, T, normal, minPen);
        }
        else if(minPenAxisIdx < 6)
        {
            Vector3D normal = boxB.transform().GetAxis(minPenAxisIdx - 3);
            //Logger.DebugLog("Second Box Axis!");
            return PointFaceContact(boxB, boxA, T.Negate(), normal, minPen);
        }
        else
        {
            minPenAxisIdx -= 6;
            int axisIndexA = minPenAxisIdx / 3;
            int axisIndexB = minPenAxisIdx % 3;

            Vector3D axisA = boxA.transform().GetAxis(axisIndexA);
            Vector3D axisB = boxB.transform().GetAxis(axisIndexB);
            Vector3D axis = axisA.CrossProduct(axisB).Normalized();

            if(axis.DotProduct(T) < 1e-6) axis = axis.Negate();

            Vector3D halfSizeA = boxA.GetScaledHalfSize();
            Vector3D halfSizeB = boxB.GetScaledHalfSize();

            float[] pointEdgeA = halfSizeA.ToArray();
            float[] pointEdgeB = halfSizeB.ToArray();

            for(int i = 0; i < 3; i++)
            {
                if(axisIndexA == i) pointEdgeA[i] = 0;
                else if(boxA.transform().GetAxis(i).DotProduct(axis) > 0) pointEdgeA[i] = -pointEdgeA[i];

                if(axisIndexB == i) pointEdgeB[i] = 0;
                else if(boxB.transform().GetAxis(i).DotProduct(axis) > 0) pointEdgeB[i] = -pointEdgeB[i];
            }

            Vector3D pointEdgeAVector = new Vector3D(pointEdgeA);
            Vector3D pointEdgeBVector = new Vector3D(pointEdgeB);

            pointEdgeAVector = boxA.transform().LocalToWorldSpace(pointEdgeAVector);
            pointEdgeBVector = boxB.transform().LocalToWorldSpace(pointEdgeBVector);

            float sizeA = axisIndexA == 0 ? halfSizeA.x : (axisIndexA == 1 ? halfSizeA.y : halfSizeA.z);
            float sizeB = axisIndexB == 0 ? halfSizeB.x : (axisIndexB == 1 ? halfSizeB.y : halfSizeB.z);

            Vector3D vertex = GetClosestPoint(pointEdgeAVector, axisA, sizeA,
                                              pointEdgeBVector, axisB, sizeB,
                                         minPenSingleAxisIdx > 2);

            //Logger.DebugLog("Cross Product Axis!");
            return new Contact(boxA.GetRigidbody(), boxB.GetRigidbody(), vertex, axis, minPen);
        }
    }

    /**
     * Generates contacts between a box and a sphere
     * @param box
     * The box collider
     * @param sphere
     * The sphere collider
     * @return
     * A contact between the box and sphere
     */
    public static Contact GetContact(BoxCollider box, SphereCollider sphere)
    {
        Vector3D sphereCenter = sphere.GetCenterWorld();
        Vector3D boxScaledHalfSize = box.GetScaledHalfSize();
        float sphereRadius = sphere.GetScaledRadius();

        Vector3D localSphereCenter = box.transform().WorldToLocalSpace(sphereCenter);

        if (Math.abs(localSphereCenter.x) > boxScaledHalfSize.x + sphereRadius ||
                Math.abs(localSphereCenter.y) > boxScaledHalfSize.y + sphereRadius ||
                Math.abs(localSphereCenter.z) > boxScaledHalfSize.z + sphereRadius)
        {
            return null;
        }

        Vector3D closestPoint = new Vector3D(
                MathUtilities.Clamp(localSphereCenter.x, -boxScaledHalfSize.x, boxScaledHalfSize.x),
                MathUtilities.Clamp(localSphereCenter.y, -boxScaledHalfSize.y, boxScaledHalfSize.y),
                MathUtilities.Clamp(localSphereCenter.z, -boxScaledHalfSize.z, boxScaledHalfSize.z)
        );
        closestPoint = box.transform().LocalToWorldSpace(closestPoint);

        float distanceSquared = Vector3D.DistanceSquared(closestPoint, sphereCenter);

        if(distanceSquared > sphereRadius * sphereRadius) return null;

        Vector3D contactNormal = closestPoint.Subtract(sphereCenter).Normalized();
        float  penetration = sphereRadius - (float) Math.sqrt(distanceSquared);

        return new Contact(box.GetRigidbody(), sphere.GetRigidbody(), closestPoint, contactNormal, penetration);
    }

    /**
     * Generates contacts between a box and a cylinder
     * @param box
     * The box collider
     * @param cylinder
     * The cylinder collider
     * @return
     * A contact between the box and cylinder
     */
    public static Contact GetContact(BoxCollider box, CylinderCollider cylinder)
    {
        return null;
    }

    /**
     * Generates contacts between a box and a plane
     * @param box
     * The box collider
     * @param plane
     * The plane collider
     * @return
     * A contact between the box and plane
     */
    public static Contact GetContact(BoxCollider box, PlaneCollider plane)
    {
        List<Vector3D> candidateAxes = new ArrayList<>();

        Vector3D[] axesBox = {
                box.transform().Right(),
                box.transform().Up(),
                box.transform().Forward()
        };

        Vector3D[] axesPlane = {
                plane.transform().Right(),
                plane.transform().Forward()
        };

        Collections.addAll(candidateAxes, axesBox);
        Collections.addAll(candidateAxes, axesPlane);

        for (Vector3D axisBox : axesBox)
        {
            for (Vector3D axisPlane : axesPlane)
            {
                Vector3D cross = axisBox.CrossProduct(axisPlane);
                if (cross.SquaredMagnitude() > 1e-6)
                {
                    candidateAxes.add(cross.Normalized());
                }
            }
        }

        Vector3D T = box.GetCenterWorld().Subtract(plane.GetCenterWorld());

        float minPen = Float.MAX_VALUE;
        int minPenSingleAxisIdx = 0;
        int minPenAxisIdx = Integer.MAX_VALUE;

        for(int i = 0; i < candidateAxes.size(); i++)
        {
            if(i == 6) minPenSingleAxisIdx = minPenAxisIdx;

            Vector3D candidateAxis = candidateAxes.get(i);

            float penetration = GetPenetrationDepth(box, plane, candidateAxis, T);
            if(penetration < 0)
                return null;

            if(penetration < minPen)
            {
                minPen = penetration;
                minPenAxisIdx = i;
            }
        }

        if (minPenAxisIdx == Integer.MAX_VALUE)
            throw new IllegalStateException("No valid collision axis found.");

        if(minPenAxisIdx < 3)
        {
            Vector3D normal = box.transform().GetAxis(minPenAxisIdx);
            return PointFaceContact(box, plane, T, normal, minPen);
        }
        else if(minPenAxisIdx < 6)
        {
            Vector3D normal = plane.transform().GetAxis(minPenAxisIdx - 3);
            return PointFaceContact(plane, box, T.Negate(), normal, minPen);
        }
        else
        {
            minPenAxisIdx -= 6;
            int axisIndexBox = minPenAxisIdx / 3;
            int axisIndexPlane = minPenAxisIdx % 3;

            Vector3D axisBox = box.transform().GetAxis(axisIndexBox);
            Vector3D axisPlane = plane.transform().GetAxis(axisIndexPlane);
            Vector3D axis = axisBox.CrossProduct(axisPlane).Normalized();

            if(axis.DotProduct(T) > 0) axis = axis.Negate();

            Vector3D halfSizeBox = box.GetScaledHalfSize();
            Vector2D halfSizePlane = plane.GetScaledHalfSize();

            float[] pointEdgeBox = halfSizeBox.ToArray();
            float[] pointEdgePlane = new float[]{halfSizePlane.x, 0, halfSizePlane.y};

            for(int i = 0; i < 3; i++)
            {
                if(axisIndexBox == i) pointEdgeBox[i] = 0;
                else if(box.transform().GetAxis(i).DotProduct(axis) > 0) pointEdgeBox[i] = -pointEdgeBox[i];

                if(axisIndexPlane == i) pointEdgePlane[i] = 0;
                else if(plane.transform().GetAxis(i).DotProduct(axis) > 0) pointEdgePlane[i] = -pointEdgePlane[i];
            }

            Vector3D pointEdgeBoxVector = new Vector3D(pointEdgeBox);
            Vector3D pointEdgePlaneVector = new Vector3D(pointEdgePlane);

            pointEdgeBoxVector = box.transform().LocalToWorldSpace(pointEdgeBoxVector);
            pointEdgePlaneVector = plane.transform().LocalToWorldSpace(pointEdgePlaneVector);

            float sizeA = axisIndexBox == 0 ? halfSizeBox.x : (axisIndexBox == 1 ? halfSizeBox.y : halfSizeBox.z);
            float sizeB = axisIndexPlane == 0 ? halfSizePlane.x : (axisIndexPlane == 1 ? 0 : halfSizePlane.y);

            Vector3D vertex = GetClosestPoint(pointEdgeBoxVector, axisBox, sizeA,
                                              pointEdgePlaneVector, axisPlane, sizeB,
                                         minPenSingleAxisIdx > 2);

            return new Contact(box.GetRigidbody(), plane.GetRigidbody(), vertex, axis, minPen);
        }
    }

    /**
     * Generates contacts between a box and a capsule
     * @param box
     * The box collider
     * @param capsule
     * The capsule collider
     * @return
     * A contact between the box and capsule
     */
    public static Contact GetContact(BoxCollider box, CapsuleCollider capsule)
    {
        return null;
    }

    /**
     * Generates contacts between two spheres
     * @param sphereA
     * The first sphere collider
     * @param sphereB
     * The second sphere collider
     * @return
     * A contact between the spheres
     */
    public static Contact GetContact(SphereCollider sphereA, SphereCollider sphereB)
    {
        Vector3D midLine = sphereA.GetCenterWorld().Subtract(sphereB.GetCenterWorld());

        float distance = midLine.Magnitude();
        float addedRadii = (sphereA.GetScaledRadius() + sphereB.GetScaledRadius());

        if(distance >= addedRadii)
            return null;

        Vector3D contactNormal = midLine.Scale(1.0f/distance);
        Vector3D contactPoint = sphereA.GetCenterWorld().Add(midLine.Scale(0.5f));

        float penetration = addedRadii - distance;

        return new Contact(sphereA.GetRigidbody(), sphereB.GetRigidbody(), contactPoint, contactNormal, penetration);
    }
    /**
     * Generates contacts between a sphere and a cylinder
     * @param sphere
     * The sphere collider
     * @param cylinder
     * The cylinder collider
     * @return
     * A contact between the sphere and cylinder
     */
    public static Contact GetContact(SphereCollider sphere, CylinderCollider cylinder)
    {
        return null;
    }

    /**
     * Generates contacts between a sphere and a capsule
     * @param sphere
     * The sphere collider
     * @param capsule
     * The capsule collider
     * @return
     * A contact between the sphere and capsule
     */
    public static Contact GetContact(SphereCollider sphere, CapsuleCollider capsule)
    {
        return null;
    }

    /**
     * Generates contacts between a sphere and a plane
     * @param sphere
     * The sphere collider
     * @param plane
     * The plane collider
     * @return
     * A contact between the sphere and plane
     */
    public static Contact GetContact(SphereCollider sphere, PlaneCollider plane)
    {
        Vector3D sphereCenter = sphere.GetCenterWorld();
        float sphereRadius = sphere.GetScaledRadius();

        Vector2D planeHalfSize = plane.GetScaledHalfSize();
        Vector3D localSphereCenter = plane.transform().WorldToLocalSpace(sphereCenter);


        if (Math.abs(localSphereCenter.x) > planeHalfSize.x + sphereRadius ||
                Math.abs(localSphereCenter.z) > planeHalfSize.y + sphereRadius)
        {
            return null;
        }

        Vector3D closestPoint = new Vector3D(
                MathUtilities.Clamp(localSphereCenter.x, -planeHalfSize.x, planeHalfSize.x),
                0,
                MathUtilities.Clamp(localSphereCenter.z, -planeHalfSize.y, planeHalfSize.y)
        );

        closestPoint = plane.transform().LocalToWorldSpace(closestPoint);

        float distance = Vector3D.Distance(sphereCenter, closestPoint);

        if (distance > sphereRadius) return null;

        float penetration = sphereRadius - distance;

        return new Contact(plane.GetRigidbody(), sphere.GetRigidbody(), closestPoint, plane.GetNormal(), penetration);
    }

    /**
     * Generates contacts between two cylinders
     * @param cylinderA
     * The first cylinder collider
     * @param cylinderB
     * The second cylinder collider
     * @return
     * A contact between the cylinders
     */
    public static Contact GetContact(CylinderCollider cylinderA, CylinderCollider cylinderB)
    {
        return null;
    }

    /**
     * Generates contacts between a cylinder and a capsule
     * @param cylinder
     * The cylinder collider
     * @param capsule
     * The capsule collider
     * @return
     * A contact between the cylinder and capsule
     */
    public static Contact GetContact(CylinderCollider cylinder, CapsuleCollider capsule)
    {
        return null;
    }

    /**
     * Generates contacts between a cylinder and a plane
     * @param cylinder
     * The cylinder collider
     * @param plane
     * The plane collider
     * @return
     * A contact between the cylinder and plane
     */
    public static Contact GetContact(CylinderCollider cylinder, PlaneCollider plane)
    {
        return null;
    }

    /**
     * Generates contacts between two capsules
     * @param capsuleA
     * The first capsule collider
     * @param capsuleB
     * The second capsule collider
     * @return
     * A contact between the capsules
     */
    public static Contact GetContact(CapsuleCollider capsuleA, CapsuleCollider capsuleB)
    {
        return null;
    }

    /**
     * Generates contacts between a capsule and a plane
     * @param capsule
     * The capsule collider
     * @param plane
     * The plane collider
     * @return
     * A contact between the capsule and plane
     */
    public static Contact GetContact(CapsuleCollider capsule, PlaneCollider plane)
    {
        return null;
    }

    /**
     * Generates contacts between two planes
     * @param planeA
     * The first plane collider
     * @param planeB
     * The second plane collider
     * @return
     * A contact between the planes
     */
    public static Contact GetContact(PlaneCollider planeA, PlaneCollider planeB)
    {
        List<Vector3D> candidateAxes = new ArrayList<>();

        Vector3D[] axesA = {
                planeA.transform().Right(),
                planeA.transform().Forward()
        };

        Vector3D[] axesB = {
                planeB.transform().Right(),
                planeB.transform().Forward()
        };

        Collections.addAll(candidateAxes, axesA);
        Collections.addAll(candidateAxes, axesB);

        for (Vector3D axisA : axesA)
        {
            for (Vector3D axisB : axesB)
            {
                Vector3D cross = axisA.CrossProduct(axisB);
                if (!cross.IsNaN() && cross.SquaredMagnitude() > 1e-6)
                {
                    candidateAxes.add(cross.Normalized());
                }
            }
        }

        Vector3D T = planeA.GetCenterWorld().Subtract(planeB.GetCenterWorld());

        float minPen = Float.MAX_VALUE;
        int minPenSingleAxisIdx = 0;
        int minPenAxisIdx = Integer.MAX_VALUE;

        for(int i = 0; i < candidateAxes.size(); i++)
        {
            if(i == 4) minPenSingleAxisIdx = minPenAxisIdx;

            Vector3D candidateAxis = candidateAxes.get(i);

            float penetration = GetPenetrationDepth(planeA, planeB, candidateAxis, T);
            if(penetration < 0)
                return null;

            if(penetration < minPen)
            {
                minPen = penetration;
                minPenAxisIdx = i;
            }
        }

        if (minPenAxisIdx == Integer.MAX_VALUE)
            throw new IllegalStateException("No valid collision axis found.");

        if(minPenAxisIdx < 3)
        {
            Vector3D normal = planeA.transform().GetAxis(minPenAxisIdx);
            return PointFaceContact(planeA, planeB, T, normal, minPen);
        }
        else if(minPenAxisIdx < 6)
        {
            Vector3D normal = planeB.transform().GetAxis(minPenAxisIdx - 3);
            return PointFaceContact(planeB, planeA, T.Negate(), normal, minPen);
        }
        else
        {
            minPenAxisIdx -= 6;
            int axisIndexA = minPenAxisIdx / 3;
            int axisIndexB = minPenAxisIdx % 3;

            Vector3D axisA = planeA.transform().GetAxis(axisIndexA);
            Vector3D axisB = planeB.transform().GetAxis(axisIndexB);
            Vector3D axis = axisA.CrossProduct(axisB).Normalized();

            if(axis.DotProduct(T) > 0) axis = axis.Negate();

            Vector2D halfSizeA = planeA.GetScaledHalfSize();
            Vector2D halfSizeB = planeB.GetScaledHalfSize();

            float[] pointEdgeA = new float[]{halfSizeA.x, 0, halfSizeA.y};
            float[] pointEdgeB = new float[]{halfSizeB.x, 0, halfSizeB.y};

            for(int i = 0; i < 3; i++)
            {
                if(axisIndexA == i) pointEdgeA[i] = 0;
                else if(planeA.transform().GetAxis(i).DotProduct(axis) > 0) pointEdgeA[i] = -pointEdgeA[i];

                if(axisIndexB == i) pointEdgeB[i] = 0;
                else if(planeB.transform().GetAxis(i).DotProduct(axis) > 0) pointEdgeB[i] = -pointEdgeB[i];
            }

            Vector3D pointEdgeAVector = new Vector3D(pointEdgeA);
            Vector3D pointEdgeBVector = new Vector3D(pointEdgeB);

            pointEdgeAVector = planeA.transform().LocalToWorldSpace(pointEdgeAVector);
            pointEdgeBVector = planeB.transform().LocalToWorldSpace(pointEdgeBVector);

            float sizeA = axisIndexA == 0 ? halfSizeA.x : (axisIndexA == 1 ? 0 : halfSizeA.y);
            float sizeB = axisIndexB == 0 ? halfSizeB.x : (axisIndexB == 1 ? 0 : halfSizeB.y);

            Vector3D vertex = GetClosestPoint(pointEdgeAVector, axisA, sizeA,
                                              pointEdgeBVector, axisB, sizeB,
                                        minPenSingleAxisIdx > 2);

            return new Contact(planeA.GetRigidbody(), planeB.GetRigidbody(), vertex, axis, minPen);
        }
    }
}
