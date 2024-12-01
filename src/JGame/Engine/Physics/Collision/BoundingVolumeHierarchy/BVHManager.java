package JGame.Engine.Physics.Collision.BoundingVolumeHierarchy;

import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.PotentialContact;
import JGame.Engine.Settings;

import java.util.List;

/**
 * Manages a Tree structure for the Bounding Volume Hierarchy used for broad collision detection
 */
public class BVHManager
{
    private BVHNode root;

    public List<PotentialContact> GetPotentialContacts()
    {
        return root.GetPotentialContacts(Settings.Physics.broadCollisionLimit);
    }

    /**
     * Updates a node into the hierarchy
     */
    public void UpdateNode(Rigidbody body)
    {
        Remove(body);
        Insert(body);
    }

    /**
     * Inserts a new rigidbody into the BVH
     * @param body The rigidbody to insert
     */
    public void Insert(Rigidbody body)
    {
        BoundingVolume volume = body.GetBoundingVolume();

        if (root == null)
        {
            root = new BVHNode(null, body, volume);
        }
        else
        {
            root.Insert(body, volume);
        }
    }

    /**
     * Removes a node from the tree, and restructures accordingly
     * @param body
     * The body to remove
     */
    public void Remove(Rigidbody body)
    {
        FindNode(body).RemoveNode();
    }
    /**
     * Finds a node along the tree based on a rigidbody
     * @param body
     * The body to find
     * @return
     * The node that corresponds to this body, or null if it's not found
     */
    public BVHNode FindNode(Rigidbody body)
    {
        return FindNode(body, root);
    }

    /**
     * Finds a node along the tree based on a rigidbody
     * @param body
     * The body to find
     * @param node
     * The current Node
     * @return
     * The node that corresponds to this body, or null if it's not found
     */
    private BVHNode FindNode(Rigidbody body, BVHNode node)
    {
        if(node == null) return null;
        if(node.body == body) return node;
        if(node.IsLeaf()) return null;

        BVHNode child1 = FindNode(body, node.children[0]);
        if(child1 != null)
            return child1;

        return FindNode(body, node.children[1]);
    }
}
