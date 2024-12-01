package JGame.Engine.Graphics.Models;

import JGame.Engine.Basic.BaseEngineClass;
import JGame.Engine.Internal.Logger;
import JGame.Engine.Interfaces.Graphics.IMeshOpenGLHandler;
import JGame.Engine.Structures.Vector3D;
import JGame.Engine.Utilities.FileUtilities;
import JGame.Engine.Utilities.GraphicsUtilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

/**
 * A class representing a 3D mesh made of vertices and triangles
 */
public class Mesh implements IMeshOpenGLHandler
{
    final Vertex[] vertices;
    public final Triangle[] tris;

    public final int VAO;
    //Buffers
    public final int[] buffers = new int[5];
    public static final int PBO = 0,
            IBO = 1,
            CBO = 2,
            UVBO = 3,
            NBO = 4;

    public Mesh(Vertex[] vertices, Triangle[] tris)
    {
        this(vertices, tris, false);
    }

    public Mesh(Vertex[] vertices, Triangle[] tris, boolean recalculateNormals)
    {
        super();
        this.vertices = vertices;
        this.tris = tris;

        if(recalculateNormals)
            RecalculateNormals();

        VAO = glGenVertexArrays();
        GenerateVAO();
    }

    /**
     * Generates the VAO or Vertex Array Object for this Mesh
     */
    @Override
    public void GenerateVAO()
    {
        glBindVertexArray(VAO);

        FloatBuffer vertPosBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        vertPosBuffer.put(GraphicsUtilities.VertexPositionsAsFloatArray(vertices)).flip();
        buffers[PBO] = LinkBufferToAttribute(vertPosBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3);

        FloatBuffer vertColorBuffer = MemoryUtil.memAllocFloat(vertices.length * 4);
        vertColorBuffer.put(GraphicsUtilities.VertexColorsAsFloatArray(vertices)).flip();
        buffers[CBO] = LinkBufferToAttribute(vertColorBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 1, 4);

        FloatBuffer uvCoordsBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
        uvCoordsBuffer.put(GraphicsUtilities.VertexUVCoordinatesAsFloatArray(vertices)).flip();
        buffers[UVBO] = LinkBufferToAttribute(uvCoordsBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 2, 2);

        FloatBuffer vertNormalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        vertNormalBuffer.put(GraphicsUtilities.VertexNormalsAsFloatArray(vertices)).flip();
        buffers[NBO] = LinkBufferToAttribute(vertNormalBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3, 3);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(tris.length * 3);
        indicesBuffer.put(GraphicsUtilities.TriangleIndicesAsIntArray(tris)).flip();
        buffers[IBO] = LinkBufferToAttribute(indicesBuffer, GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
    }

    /**
     * Destroys the different mesh buffers and VAO from OpenGL
     */
    public void Destroy()
    {
        for(int buffer : buffers)
            glDeleteBuffers(buffer);

        glDeleteVertexArrays(VAO);
    }


    /**
     * Recalculates the Vector Normals
     */
    public void RecalculateNormals()
    {
        for (Triangle triangle : tris)
        {
            int iVert0 = triangle.vertIndices[0];
            int iVert1 = triangle.vertIndices[1];
            int iVert2 = triangle.vertIndices[2];

            Vector3D a = Vector3D.Subtract(vertices[iVert0].position, vertices[iVert1].position);
            Vector3D b = Vector3D.Subtract(vertices[iVert0].position, vertices[iVert2].position);
            Vector3D normal = Vector3D.CrossProduct(a, b).Normalized();

            vertices[iVert0].normalVector = normal;
            vertices[iVert1].normalVector = normal;
            vertices[iVert2].normalVector = normal;

            Logger.DebugLog(normal);
        }

    }

    //----Default Meshes-----
    public static Mesh Cube()
    {
        return FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Internal/cube.fbx");
    }
    /**
     * Factory function to create a Pyramid
     * @return
     * The mesh of a Pyramid
     */
    public static Mesh Pyramid()
    {
        return FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Internal/pyramid.fbx");
    }

    /**
     * Factory function to create a Quad oriented in the X-Y plane
     * @return
     * The mesh of a Quad
     */
    public static Mesh Quad()
    {
        return Quad(false);
    }

    /**
     * Factory function to create a Quad oriented in the X-Y plane
     * @return
     * The mesh of a Quad
     */
    public static Mesh Quad(boolean invertUVs)
    {
        return FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Internal/quad.fbx", invertUVs);
    }
    /**
     * Factory function to create a Plane oriented in the X-Z plane
     * @return
     * The mesh of a Plane
     */
    public static Mesh Plane()
    {
        return FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Internal/plane.fbx");
    }
    /**
     * Factory function to create an Ico Sphere
     * @return
     * The mesh of a Sphere
     */
    public static Mesh Sphere() { return FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Internal/sphere.fbx"); }
    /**
     * Factory function to create a cylinder
     * @return
     * The mesh of a Cylinder
     */
    public static Mesh Cylinder() { return FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Internal/cylinder.fbx"); }
    /**
     * Factory function to create a Capsule
     * @return
     * The mesh of a Capsule
     */
    public static Mesh Capsule() { return FileUtilities.FBXLoader.ReadFBXFromResources("/Models/Internal/capsule.fbx"); }
}

