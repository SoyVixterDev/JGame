package JGame.Engine.Physics.Collision.BoundingVolumeHierarchy;

import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.PotentialContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Node used by the binary tree hierarchy for bounding volumes
 */
public class BVHNode
{
    public BVHNode[] children = new BVHNode[2];
    public BVHNode parent;
    /**
     * Single bounding volume encapsulating all descendants
     */
    public BoundingVolume volume;

    /**
     * Rigidbody at this node of the hierarchy.
     */
    public Rigidbody body;


    public BVHNode(BVHNode parent, Rigidbody body, BoundingVolume volume)
    {
        this.parent = parent;
        this.body = body;
        this.volume = volume;
    }

    /**
     * Returns true if the node is a leaf node
     * @return
     * True if the node is a leaf node
     */
    public boolean IsLeaf()
    {
        return children[0] == null;
    }

    /**
     * Checks if a node overlaps with this node
     * @param node
     * The other node
     * @return
     * True if the volumes overlap
     */
    public boolean Overlaps(BVHNode node)
    {
        return volume.Overlaps(node.volume);
    }

    /**
     * Updates the potential contacts list and returns the number of potential contacts found
     * @param limit
     * The iteration limit
     * @return
     * The number of potential contacts found
     */
    public List<PotentialContact> GetPotentialContacts(int limit)
    {
        if(IsLeaf() || limit == 0)
            return new ArrayList<>();

        return children[0].GetPotentialContactsWith(children[1], new ArrayList<>(), limit);
    }
    /**
     * Updates the potential contacts list between this node and another and returns the number of potential contacts found
     * @param other
     * The other node
     * @param potentialContacts
     * The list of potential contacts
     * @param limit
     * The iteration limit
     * @return
     * The number of potential contacts found
     */
    protected List<PotentialContact> GetPotentialContactsWith(BVHNode other, List<PotentialContact> potentialContacts, int limit)
    {
        if(!Overlaps(other) || limit == 0)
            return potentialContacts;

        if(IsLeaf() && other.IsLeaf())
        {
            potentialContacts.add(new PotentialContact(body, other.body));
            return potentialContacts;
        }

        if(other.IsLeaf() || (!IsLeaf()) && volume.GetVolume() >= other.volume.GetVolume())
        {
            int count = children[0].GetPotentialContactsWith(other, potentialContacts, limit).size();

            if(limit > count)
            {
                return children[1].GetPotentialContactsWith(other, potentialContacts, limit - count);
            }
            else
            {
                return potentialContacts;
            }
        }
        else
        {
            int count = GetPotentialContactsWith(other.children[0], potentialContacts, limit).size();

            if(limit > count)
            {
                return GetPotentialContactsWith(other.children[1], potentialContacts, limit - count);
            }
            else
            {
                return potentialContacts;
            }

        }

    }

    /**
     * Inserts a new body with its respective volume into this node
     * @param newBody
     * The new body to insert
     * @param newVolume
     * The corresponding volume
     */
    public void Insert(Rigidbody newBody, BoundingVolume newVolume)
    {
        if(IsLeaf())
        {
            children[0] = new BVHNode(this, body, volume);
            children[1] = new BVHNode(this, newBody, newVolume);

            body = null;
            RecalculateBoundingVolume();
        }
        else
        {
            if(children[0].volume.GetGrowth(newVolume) < children[1].volume.GetGrowth(newVolume))
            {
                children[0].Insert(newBody, newVolume);
            }
            else
            {
                children[1].Insert(newBody, newVolume);
            }
        }

    }

    /**
     * Recalculates the bounding volume of a node
     */
    private void RecalculateBoundingVolume()
    {
        if(IsLeaf())
            return;

        volume = BoundingVolume.GenerateFromBounds(children[0].volume, children[1].volume);

        if(parent != null) parent.RecalculateBoundingVolume();
    }
    /**
     * Removes the node from its hierarchy
     */
    public void RemoveNode()
    {
        if (parent != null)
        {
            BVHNode sibling = (parent.children[0] == this) ? parent.children[1] : parent.children[0];

            if (sibling != null)
            {
                parent.volume = sibling.volume;
                parent.body = sibling.body;
                parent.children[0] = sibling.children[0];
                parent.children[1] = sibling.children[1];

                if (sibling.children[0] != null)
                {
                    sibling.children[0].parent = parent;
                }

                if (sibling.children[1] != null)
                {
                    sibling.children[1].parent = parent;
                }

                sibling.volume = null;
                sibling.body = null;
                sibling.children[0] = null;
                sibling.children[1] = null;
            }
            else
            {
                parent.RemoveNode();
            }
        }

        if (children[0] != null)
        {
            children[0].parent = null;
            children[0].RemoveNode();
        }

        if (children[1] != null)
        {
            children[1].parent = null;
            children[1].RemoveNode();
        }

        body = null;
        children = null;
        volume = null;
        parent = null;
    }

}

