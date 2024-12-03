package JGame.Engine.Graphics.Misc;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Graphics.Textures.BaseTexture;
import JGame.Engine.Graphics.Textures.Texture;
import JGame.Engine.Structures.*;
import JGame.Engine.Utilities.FileUtilities;

import static org.lwjgl.opengl.GL46.*;

/**
 * Class representing a shader made of a fragment shader and a vertex shader programs
 */
public class Shader
{
    private boolean isLit = true;
    private boolean receiveShadows = true;

    private BaseTexture mainTex;
    private final String vertFile, fragFile, geomFile;
    private final String vertFileName, fragFileName, geomFileName;

    private int vertID, fragID, geomID, programID;

    /**
     * Generates a shader from the 2 required shader files, the path is relative to the resources folder, with a default texture
     * @param vertPath
     * Path towards the vertex shader, relative to the resources folder
     * @param fragPath
     * Path towards the fragment shader, relative to the resources folder
     */
    public Shader(String vertPath, String fragPath)
    {
        this(vertPath, fragPath, new Texture());
    }

    /**
     * Generates a shader from the 2 required shader files and a geom file, the path is relative to the resources folder, with a default texture
     * @param vertPath
     * Path towards the vertex shader, relative to the resources folder
     * @param fragPath
     * Path towards the fragment shader, relative to the resources folder
     * @param geomPath
     * Path towards the geometry shader, relative to the resources folder
     */
    public Shader(String vertPath, String fragPath, String geomPath)
    {
        this(vertPath, fragPath, geomPath, new Texture());
    }


    /**
     * Generates a shader from the 2 required shader files, the path is relative to the resources folder
     * @param vertPath
     * Path towards the vertex shader, relative to the resources folder
     * @param fragPath
     * Path towards the fragment shader, relative to the resources folder
     * @param mainTexture
     * The Main Texture for the shader
     */
    public Shader(String vertPath, String fragPath, BaseTexture mainTexture)
    {
        this(vertPath, fragPath, null, mainTexture);
    }

    /**
     * Generates a shader from the 2 required shader files and a geom file, the path is relative to the resources folder
     * @param vertPath
     * Path towards the vertex shader, relative to the resources folder
     * @param fragPath
     * Path towards the fragment shader, relative to the resources folder
     * @param geomPath
     * Path towards the geometry shader, relative to the resources folder
     * @param mainTexture
     * The Main Texture for the shader
     */
    public Shader(String vertPath, String fragPath, String geomPath, BaseTexture mainTexture)
    {
        super();

        this.vertFileName = FileUtilities.ExtractFileName(vertPath);
        this.fragFileName = FileUtilities.ExtractFileName(fragPath);
        this.geomFileName = (geomPath != null) ? FileUtilities.ExtractFileName(geomPath) : "No Geometry Shader";


        vertFile = FileUtilities.ReadShaderFromResources(vertPath);
        fragFile = FileUtilities.ReadShaderFromResources(fragPath);

        if(geomPath != null)
            geomFile = FileUtilities.ReadShaderFromResources(geomPath);
        else
            geomFile = null;

        Init(mainTexture);
    }



    /**
     * Duplicates a shader by recompiling the input shader's files
     * @param shader
     * The shader to duplicate
     */
    public Shader(Shader shader)
    {
        super();
        vertFileName = shader.vertFileName;
        fragFileName = shader.fragFileName;
        geomFileName = shader.geomFileName;

        vertFile = shader.vertFile;
        fragFile = shader.fragFile;
        geomFile = shader.geomFile;



        Init(shader.mainTex);
    }

    /**
     * Initializes the shader, including shader compilation, program linking and verification
     */
    public void Init(BaseTexture texture)
    {
        programID = glCreateProgram();

        vertID = glCreateShader(GL_VERTEX_SHADER);
        CompileShader(vertID, vertFileName, vertFile, GL_VERTEX_SHADER);
        glAttachShader(programID, vertID);

        fragID = glCreateShader(GL_FRAGMENT_SHADER);
        CompileShader(fragID, fragFileName,fragFile, GL_FRAGMENT_SHADER);
        glAttachShader(programID, fragID);

        if(geomFile != null && !geomFile.isEmpty())
        {
            geomID = glCreateShader(GL_GEOMETRY_SHADER);
            CompileShader(geomID, geomFileName,geomFile, GL_GEOMETRY_SHADER);
            glAttachShader(programID, geomID);
        }

        glLinkProgram(programID);
        if(glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE)
        {
            Logger.DebugError("Program Linking Error: " + glGetProgramInfoLog(programID));
            return;
        }

        //This code is incorrectly determining if the code can run because it uses the state just after linking,
        // and sometimes initialization is needed, maybe should think of a way of checking after initialization or just
        // ignore validation.
//        glValidateProgram(programID);
//        if(glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE)
//        {
//            Logger.DebugError("Program Validation Error: " + glGetProgramInfoLog(programID));
//            return;
//        }

        glDeleteShader(vertID);
        glDeleteShader(fragID);
        if(geomID > 0)
            glDeleteShader(geomID);


        SetTexture(texture);

        SetUniformProperty("mainTex", 0);
        SetLit(isLit);
        SetReceiveShadows(receiveShadows);
    }

    /**
     * Updates the Matrices of the shader used for transformation, view and projection
     */
    public void UpdateShaderMatrices(Matrix4x4 transformationMatrix, Matrix4x4 normalRotation, Matrix4x4 viewMatrix, Matrix4x4 projectionMatrix)
    {
        UpdateShaderMatrices(transformationMatrix, normalRotation, new Matrix4x4[]{viewMatrix}, projectionMatrix);
    }
    /**
     * Updates the Matrices of the shader used for transformation, view and projection
     */
    public void UpdateShaderMatrices(Matrix4x4 transformationMatrix, Matrix4x4 normalRotation, Matrix4x4[] viewMatrix, Matrix4x4 projectionMatrix)
    {
        if(Camera.Main == null)
            return;

        SetUniformProperty("uTransformation", transformationMatrix, true);
        SetUniformProperty("uNormalRotation", normalRotation, true);
        SetUniformProperty("uView", viewMatrix.length == 1 ? viewMatrix[0] : viewMatrix, true);
        SetUniformProperty("uProjection", projectionMatrix, true);
    }


    /**
     * Binds the shader's program to the OpenGLState
     */
    public void Bind()
    {
        glUseProgram(programID);
        mainTex.Bind();
    }

    /**
     * Unbind the shader's program from the OpenGLState
     */
    public void Unbind()
    {
        mainTex.Unbind();
        glUseProgram(0);
    }

    /**
     * Deletes the shader's program from OpenGL
     */
    public void Destroy()
    {
        mainTex.Destroy();
        glDeleteProgram(programID);
    }
    /**
     * Compiles the shader and handles possible errors, if there's a shader error it will set the shader to a fallback shader.
     * @param shaderID
     * ID that points to the shader
     * @param shaderFile
     * File contents where the shader is written
     * @param shaderType
     * The type of the shader to compile, use GL_..._SHADER.
     * @return
     * Success indicator for the compilation, if false the compilation wasn't successful
     */
    private boolean CompileShader(int shaderID, String shaderName, String shaderFile, int shaderType)
    {
        glShaderSource(shaderID, shaderFile);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE)
        {
            Logger.DebugError("Shader Error at \"" + shaderName + "\": \n" + glGetShaderInfoLog(shaderID));
            switch (shaderType)
            {
                case (GL_FRAGMENT_SHADER) ->
                        CompileShader(shaderID, "fallbackFragShader.glsl", FileUtilities.ReadShaderFromResources("/Shaders/Internal/Default/fallbackFragShader.glsl"), GL_FRAGMENT_SHADER);
                case (GL_VERTEX_SHADER) ->
                        CompileShader(shaderID, "fallbackVertexShader.glsl", FileUtilities.ReadShaderFromResources("/Shaders/Internal/Default/defaultVertShader.glsl"), GL_VERTEX_SHADER);
            }

            return false;
        }
        return true;
    }

    /**
     * Sets a property in the shader that shares the name provided
     * @param name
     * The name of the property
     * @param value
     * Value to set to
     * @param <T>
     * The type of the property to be set
     */
    public <T> void SetUniformProperty(String name, T value)
    {
        SetUniformProperty(name, value, false, false);
    }
    /**
     * Sets a property in the shader that shares the name provided
     * @param name
     * The name of the property
     * @param value
     * Value to set to
     * @param <T>
     * The type of the property to be set
     */
    public <T> void SetUniformProperty(String name, T value, boolean disableBinding)
    {
        SetUniformProperty(name, value, false, disableBinding);
    }

    /**
     * Sets a property in the shader that shares the name provided
     * @param name
     * The name of the property
     * @param value
     * Value to set to
     * @param logErrors
     * Should log errors to the console?
     * @param <T>
     * The type of the property to be set
     */
    public <T> void SetUniformProperty(String name, T value, boolean logErrors, boolean disableBinding)
    {
        if(value == null)
        {
            if(logErrors)
                Logger.DebugError("Value can't be null!");

            return;
        }

        if(!disableBinding)
            Bind();

        int uniformProperty  = glGetUniformLocation(programID, name);

        if(uniformProperty < 0)
        {
            if(logErrors)
                Logger.DebugError("Couldn't find uniform property with name \"" + name + "\" in shader: " + programID + "\nDid you add the uniform keyword?");

            if(!disableBinding)
                Unbind();
            return;
        }

        if(value instanceof Integer intValue)
        {
            glUniform1i(uniformProperty, intValue);
        }
        else if(value instanceof Float floatValue)
        {
            glUniform1f(uniformProperty, floatValue);
        }
        else if(value instanceof Boolean boolValue)
        {
            glUniform1i(uniformProperty, boolValue ? 1 : 0);
        }
        else if(value instanceof Vector3D vectorValue)
        {
            glUniform3f(uniformProperty, vectorValue.x, vectorValue.y, vectorValue.z);
        }
        else if (value instanceof Vector2D vectorValue)
        {
            glUniform2f(uniformProperty, vectorValue.x, vectorValue.y);
        }
        else if (value instanceof ColorRGBA colorValue)
        {
            glUniform4f(uniformProperty, colorValue.r, colorValue.g, colorValue.b, colorValue.a);
        }
        else if(value instanceof ColorRGB colorValue)
        {
            glUniform3f(uniformProperty, colorValue.r, colorValue.g, colorValue.b);
        }
        else if(value instanceof Matrix4x4 matrixValue)
        {
            glUniformMatrix4fv(uniformProperty, true, matrixValue.ToArray());
        }
        else if (value.getClass().isArray())
        {
            int length = java.lang.reflect.Array.getLength(value);
            for (int i = 0; i < length; i++)
            {
                Object element = java.lang.reflect.Array.get(value, i);
                SetUniformProperty(name + "[" + i + "]", element, logErrors, true);
            }
        }
        else
        {
            Logger.DebugError(value.getClass().getName() + " property type not supported!");
        }

        if(!disableBinding)
            Unbind();
    }

    /**
     * Sets the main texture of the object
     * @param texture
     * The new texture to set
     */
    public void SetTexture(BaseTexture texture)
    {
        mainTex = texture;
    }

    public BaseTexture GetTexture()
    {
        return mainTex;
    }

    /**
     * Set object as lit or unlit, changes the behavior related to lighting
     * @param value
     * The value to set
     */
    public void SetLit(boolean value)
    {
        isLit = value;
        SetUniformProperty("isLit", value);
    }

    public boolean GetLit()
    {
        return isLit;
    }

    /**
     * Sets the object to receive or not receive shadows, updates the behavior in the shader
     * @param value
     * The value to set
     */
    public void SetReceiveShadows(boolean value)
    {
        receiveShadows = value;
        SetUniformProperty("receiveShadows", value);
    }

    public boolean GetReceiveShadows()
    {
        return receiveShadows;
    }
}
