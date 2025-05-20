package JGame.Engine.Utilities;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Graphics.Models.Mesh;
import JGame.Engine.Graphics.Models.Triangle;
import JGame.Engine.Graphics.Models.Vertex;
import JGame.Engine.Structures.ColorRGBA;
import JGame.Engine.Structures.Quaternion;
import JGame.Engine.Structures.Vector2D;
import JGame.Engine.Structures.Vector3D;
import org.lwjgl.assimp.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilities class for handling Files
 */
public class FileUtilities
{
    /**
     * Reads and returns the shader source from the file at path, relative to the resources folder, handles including files
     * by using #include "filename.glsl" (Relative to the "resources/Shaders/" folder)
     * @param path
     * The path where the file is found, relative to the resources folder
     * @return
     * The contents of the file at the path or null if the file isn't found
     */
    public static String ReadShaderFromResources(String path)
    {
        return ReadShaderFromResourcesWithIncludes(path, new HashSet<>());
    }

    /**
     * Handles reading the resources and replacing the #include directives with the corresponding file contents.
     * @param path
     * The path to read
     * @param includedFiles
     * The current set of included files
     * @return
     * The read shader source code
     */
    private static String ReadShaderFromResourcesWithIncludes(String path, Set<String> includedFiles)
    {
        StringBuilder result = new StringBuilder();

        // Check if this file has already been included
        if (includedFiles.contains(path))
        {
            Logger.DebugError("Circular include detected: " + path);
            return "";
        }

        // Mark this file as included
        includedFiles.add(path);

        // Read the shader file
        try (InputStream inputStream = FileUtilities.class.getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                Logger.DebugError("Shader file not found at: " + path);
                return "";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#include"))
                {
                    // Extract the included filename
                    String includePath = ExtractIncludePath(line);

                    if (includePath == null)
                    {
                        Logger.DebugError("Invalid #include directive in shader: " + path);
                        continue;
                    }

                    // Resolve the included file relative to the resources/Shaders/ directory
                    String resolvedIncludePath = "/Shaders/" + includePath;

                    // Recursively process the included file
                    String includeContent = ReadShaderFromResources(resolvedIncludePath);

                    if (includeContent == null)
                    {
                        Logger.DebugError("Included file not found: " + resolvedIncludePath);
                        continue;
                    }

                    result.append(includeContent).append('\n');
                }
                else
                {
                    result.append(line).append('\n');
                }
            }
        }
        catch (IOException e)
        {
            Logger.DebugError("IO error while reading shader file at: " + path + "\nError message: " + e.getMessage());
            return null;
        }
        catch (RuntimeException e)
        {
            Logger.DebugError("Fatal error while reading shader file: " + path);
            e.printStackTrace();
            return null;
        }

        return result.toString();
    }

    public static String ExtractFileName(String path)
    {
        int lastSlashIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        return (lastSlashIndex >= 0) ? path.substring(lastSlashIndex + 1) : path;
    }

    /**
     * Extracts the file path from an #include directive.
     * @param line The #include directive line.
     * @return The extracted file path or null if the directive is malformed.
     */
    private static String ExtractIncludePath(String line) {
        // Expecting format: #include "filename.glsl"
        int startIndex = line.indexOf('"');
        int endIndex = line.lastIndexOf('"');

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex)
        {
            return line.substring(startIndex + 1, endIndex);
        }

        return null;
    }

    /**
     * Reads and returns the contents of the file at path, relative to the resources folder
     * @param path
     * The path where the file is found, relative to the resources folder
     * @return
     * The contents of the file at the path or null if the file isn't found
     */
    public static String ReadFileFromResources(String path)
    {
        StringBuilder result = new StringBuilder();
        try(InputStream inputStream = FileUtilities.class.getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                Logger.DebugError("File not found at: " + path);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null)
            {
                result.append(line).append('\n');
            }
        }
        catch(IOException e)
        {
            Logger.DebugError("IO error while reading file at: " + path + "\nError message: " + e.getMessage());
            return null;
        }
        catch (RuntimeException e)
        {
            Logger.DebugError("Fatal error stack trace: ");
            e.printStackTrace();
            return null;
        }
        return result.toString();
    }

    /**
     * Loads and returns any file as a byte buffer at path relative to the resources folder
     * @param filePath
     * The path where the file is located, relative to the resources folder
     * @return
     * The byte buffer representing the contents of the file
     */
    public static ByteBuffer LoadFileFromResources(String filePath)
    {
        try (InputStream inputStream = FileUtilities.class.getResourceAsStream(filePath))
        {

            if (inputStream == null)
            {
                Logger.DebugError("File not found at: " + filePath);
                return null;
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(inputStream.available());
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            channel.read(buffer);
            buffer.flip();

            return buffer;
        }
        catch (IOException e)
        {
            Logger.DebugError("IO error while reading file at: " + filePath + "\nError message: " + e.getMessage());
            return null;
        }
    }

    public static class FBXLoader
    {

        public static Mesh ReadFBXFromResources(String fbxPath)
        {
            return ReadFBXFromResources(fbxPath, false);
        }
        /**
         * Reads and returns a Mesh Object from a 3D Model FBX File in fbxPath, relative to the resources folder
         * @param fbxPath
         * The path where the FBX file is found, relative to the resources folder
         * @return
         * The mesh object created from the FBX file
         */
        public static Mesh ReadFBXFromResources(String fbxPath, boolean flipUVs)
        {
            ByteBuffer fbxBuffer = FileUtilities.LoadFileFromResources(fbxPath);
            if(fbxBuffer == null)
            {
                Logger.DebugError("Failed to load FBX file! Path: " + fbxPath);
                return null;
            }

            try (AIScene scene = Assimp.aiImportFileFromMemory(fbxBuffer, Assimp.aiProcess_Triangulate,"fbx"))
            {
                if(scene == null)
                {
                    Logger.DebugError("Assimp failed to import FBX Scene! Path: " + fbxPath);
                    return null;
                }

                return ConvertSceneToMesh(scene, flipUVs);
            }
            catch(RuntimeException e)
            {
               Logger.DebugError("Assimp failed to import FBX Scene! More info: ");
               e.printStackTrace();
               return null;
            }

        }

        /**
         * Converts an AIScene object into a mesh, merging all scene meshes into one single mesh object
         * @param scene
         * The scene to be converted
         * @return
         * The final mesh
         */
        private static Mesh ConvertSceneToMesh(AIScene scene, boolean flipUV)
        {
            ArrayList<Vertex> vertices = new ArrayList<>();
            ArrayList<Triangle> triangles = new ArrayList<>();

            int vertexOffset = 0; // Tracks how many vertices have been added so far

            for (int i = 0; i < scene.mNumMeshes(); i++)
            {
                try (AIMesh mesh = AIMesh.create(scene.mMeshes().get(i)))
                {
                    Quaternion rotation90 = Quaternion.EulerToQuaternion(new Vector3D(-90, 0, 0));

                    // Extract Vertices
                    for (int j = 0; j < mesh.mNumVertices(); j++)
                    {
                        AIVector3D aiPos = mesh.mVertices().get(j);
                        Vector3D position = new Vector3D(aiPos.x(), aiPos.y(), aiPos.z()).Rotate(rotation90);

                        AIVector3D aiNormal = mesh.mNormals().get(j);
                        Vector3D normal = new Vector3D(aiNormal.x(), aiNormal.y(), aiNormal.z()).Rotate(rotation90);

                        AIColor4D aiColor = mesh.mColors(0) != null ? mesh.mColors(0).get(j) : null;
                        ColorRGBA color = aiColor == null ? ColorRGBA.White : new ColorRGBA(aiColor.r(), aiColor.g(), aiColor.b(), aiColor.a());

                        Vector2D uvCoordinates = Vector2D.Zero;
                        if (mesh.mNumUVComponents(0) > 0)
                        {
                            float YCoord = mesh.mTextureCoords(0).get(j).y();

                            if(flipUV)
                            {
                                YCoord = 1.0f - YCoord;
                            }

                            uvCoordinates = new Vector2D(mesh.mTextureCoords(0).get(j).x(), YCoord);
                        }

                        Vertex vertex = new Vertex(position, uvCoordinates, normal, color);
                        vertices.add(vertex);
                    }

                    // Extract Triangles and adjust indices with the vertex offset
                    for (int j = 0; j < mesh.mNumFaces(); j++)
                    {
                        IntBuffer indices = mesh.mFaces().get(j).mIndices();
                        if (indices.remaining() == 3)
                        {
                            int vert1 = indices.get(0) + vertexOffset;
                            int vert2 = indices.get(1) + vertexOffset;
                            int vert3 = indices.get(2) + vertexOffset;

                            Triangle triangle = new Triangle(vert1, vert2, vert3);
                            triangles.add(triangle);
                        }
                    }

                    // Update vertex offset to the new number of vertices
                    vertexOffset += mesh.mNumVertices();
                }
                catch (RuntimeException e)
                {
                    Logger.DebugError("Failed to extract mesh! Mesh Index: " + i);
                    e.printStackTrace();
                }
            }

            return new Mesh(vertices.toArray(new Vertex[0]), triangles.toArray(new Triangle[0]));
        }
    }
}
