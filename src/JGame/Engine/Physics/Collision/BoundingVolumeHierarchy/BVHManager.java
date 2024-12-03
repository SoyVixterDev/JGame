package JGame.Engine.Physics.Collision.BoundingVolumeHierarchy;

import JGame.Engine.Basic.BaseObject;
import JGame.Engine.Basic.JGameObject;
import JGame.Engine.EventSystem.Event1P;
import JGame.Engine.Graphics.Renderers.WireframeRenderers.WirecubeRenderer;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Physics.Bodies.Rigidbody;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingBox;
import JGame.Engine.Physics.Collision.BoundingVolumes.BoundingVolume;
import JGame.Engine.Physics.Collision.Contacts.PotentialContact;
import JGame.Engine.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a Tree structure for the Bounding Volume Hierarchy used for broad collision detection
 */
public class BVHManager extends BaseObject
{
    private BVHNode root;

    private final List<WirecubeRenderer> boundVisualizersPool = new ArrayList<>();
    private final Event1P<Boolean> onChangeBHVDebug = new Event1P<Boolean>()
    {
        @Override
        protected void OnInvoke(Boolean param)
        {
            if(!param)
            {
                for(WirecubeRenderer renderer : new ArrayList<>(boundVisualizersPool))
                {
                    renderer.object().Destroy();
                }
            }
        }
    };

    @Override
    protected void OnEnable()
    {
        Settings.Debug.changeDebugBVHEvent.Subscribe(onChangeBHVDebug);
    }
    @Override
    protected void OnDisable()
    {
        Settings.Debug.changeDebugBVHEvent.Unsubscribe(onChangeBHVDebug);
    }

    public List<PotentialContact> GetPotentialContacts()
    {
        return root.GetPotentialContacts(Settings.Physics.broadCollisionLimit);
    }

    @Override
    public void PhysicsUpdate()
    {
        if(Settings.Debug.GetDebugBVH()) UpdateVisualizer();
    }

    /**
     * Updates the visualization debug boxes
     */
    private void UpdateVisualizer()
    {
        List<BVHNode> currentNodes = AsList();

        if(currentNodes == null) return;

        //Add more visualizers to the pool to match the node number
        while(currentNodes.size() > boundVisualizersPool.size())
            boundVisualizersPool.add(JGameObject.Create("~BoundVisualizer").AddComponent(WirecubeRenderer.class));

        for(int i = 0; i < boundVisualizersPool.size(); i++)
        {
            //If there are more nodes than visualizers keep adding new ones to the pool
            if(i < currentNodes.size())
            {
                BoundingBox volume = (BoundingBox)currentNodes.get(i).volume;
                if(volume == null)
                {
                    boundVisualizersPool.get(i).SetActive(false);
                    continue;
                }

                boundVisualizersPool.get(i).SetCenter(volume.GetCenter());
                boundVisualizersPool.get(i).SetHalfSize(volume.GetHalfSize());
            }
            else
            {
                //If there are more visualizers than nodes in the pool deactivate the excess
                boundVisualizersPool.get(i).SetActive(false);
            }
        }
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
        if(volume == null) return;

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
        BVHNode node = FindNode(body);
        if(node != null) node.RemoveNode();
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
     * Gets the total number of nodes in the structure
     * @return
     * The total number of nodes in the structure
     */
    public int NodeCount()
    {
        return root.CountDescendants();
    }

    /**
     * Gets the tree as a list
     * @return
     * The tree as a list
     */
    public List<BVHNode> AsList()
    {
        return root.TreeAsList();
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
        if(node.children == null) return null;
        if(node.body == body) return node;
        if(node.IsLeaf()) return null;

        BVHNode child1 = FindNode(body, node.children[0]);
        if(child1 != null)
            return child1;

        return FindNode(body, node.children[1]);
    }

    @Override
    protected void OnDestroy()
    {
        for(WirecubeRenderer renderer : new ArrayList<>(boundVisualizersPool))
        {
            renderer.object().Destroy();
        }
    }
}
