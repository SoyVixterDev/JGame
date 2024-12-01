package JGame.Engine.Graphics.Misc;

import JGame.Engine.Basic.BaseEngineClass;
import JGame.Engine.Graphics.Textures.BaseTexture;
import JGame.Engine.Graphics.Textures.Texture;
import JGame.Engine.Structures.ColorRGBA;

/**
 * Class representing a material used by the Renderer to apply
 */
public class Material
{
    String vertexPath, fragmentPath, geometryPath;

    public Shader shader;
    ColorRGBA tint = ColorRGBA.White;
    public boolean cullBackFaces = true;

    /**
     * Creates a default material with the selected texture
     * @param texturePath
     * The path where the texture is found
     */
    public Material(String texturePath)
    {
        this(Material.Default(), texturePath);
    }
    /**
     * Creates a new material and compiles a shader following the source path for the fragment and vertex shaders
     * @param fragmentPath
     * The path leading to the fragment shader source, relative to the resources folder
     * @param vertexPath
     * The path leading to the vertex shader source, relative to the resources folder
     */
    public Material(String vertexPath, String fragmentPath)
    {
        this(vertexPath, fragmentPath, (String)null);
    }
    /**
     * Creates a new material and compiles a shader following the source path for the fragment, vertex and geometry shaders
     * @param fragmentPath
     * The path leading to the fragment shader source, relative to the resources folder
     * @param vertexPath
     * The path leading to the vertex shader source, relative to the resources folder
     * @param geometryPath
     * The path leading to the geometry shader source, relative to the resources folder
     */
    public Material(String vertexPath, String fragmentPath, String geometryPath)
    {
        super();
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;
        this.geometryPath = geometryPath;

        CompileShader(vertexPath, fragmentPath, geometryPath);
    }
    /**
     * Creates a new material and compiles a shader following the source path for the fragment and vertex shaders
     * @param fragmentPath
     * The path leading to the fragment shader source, relative to the resources folder
     * @param vertexPath
     * The path leading to the vertex shader source, relative to the resources folder
     * @param texturePath
     * The path leading to the texture for the material, relative to the resources folder
     */
    public Material(String vertexPath, String fragmentPath, String geometryShader, String texturePath)
    {
        this(vertexPath, fragmentPath, geometryShader);

        shader.SetTexture(new Texture(texturePath));
    }

    /**
     * Creates a new material and compiles a shader following the source path for the fragment and vertex shaders
     * @param fragmentPath
     * The path leading to the fragment shader source, relative to the resources folder
     * @param vertexPath
     * The path leading to the vertex shader source, relative to the resources folder
     * @param texture
     * The texture for the material
     */
    public Material(String vertexPath,String fragmentPath, BaseTexture texture)
    {
        this(vertexPath, fragmentPath, (String)null);

        shader.SetTexture(texture);
    }

    /**
     * Duplicates a Material
     * @param material
     * The source material
     */
    public Material(Material material)
    {
        super();
        this.vertexPath = material.vertexPath;
        this.fragmentPath = material.fragmentPath;
        this.geometryPath = material.geometryPath;
        this.cullBackFaces = material.cullBackFaces;
        this.tint = material.tint;

        DuplicateShader(material.shader);
    }

    /**
     * Duplicates a material but changes the texture
     * @param material
     * The source material
     * @param texturePath
     * The path where the texture is found
     */
    public Material(Material material, String texturePath)
    {
        this(material);

        shader.SetTexture(new Texture(texturePath));
    }
    /**
     * Compiles a new shader and destroys the previous one if it exists
     * @param vertexPath
     * The path leading to the vertex shader source, relative to the resources folder
     * @param fragmentPath
     * The path leading to the fragment shader source, relative to the resources folder
     * @param geometryPath
     * The path leading to the geometry shader source, relative to the resources folder
     */
    private void CompileShader(String vertexPath, String fragmentPath, String geometryPath)
    {
        if(shader != null)
            shader.Destroy();

        shader = new Shader(vertexPath, fragmentPath, geometryPath);

        shader.SetUniformProperty("tintColor", tint);
    }

    /**
     * Creates a new instance of a shader recompiling the same files as another without loading it from scratch
     * @param shader
     * The shader to duplicate
     */
    private void DuplicateShader(Shader shader)
    {
        if(this.shader != null)
            this.shader.Destroy();

        this.shader = new Shader(shader);

        this.shader.SetUniformProperty("tintColor", tint);
    }

    /**
     * Changes the tintColor uniform variable for the material's shader if its supported
     * @param tint
     * The tint color to be used
     */
    public void SetTint(ColorRGBA tint)
    {
        this.tint = tint;
        shader.SetUniformProperty("tintColor", tint);
    }

    /**
     * Returns the material's shader
     * @return
     * The material's shader
     */
    public Shader GetShader()
    {
        return shader;
    }

    /**
     * Sets this material's texture
     * @param texture
     * The new texture
     */
    public void SetTexture(BaseTexture texture)
    {
        shader.SetTexture(texture);
    }


    /**
     * Destroys the material instance
     */
    public void Destroy()
    {
        shader.Destroy();
    }

    /**
     * Gets the default blank Material, using the default vertex and fragment shaders
     * @return
     * The default blank material
     */
    public static Material Default()
    {
        return new Material("/Shaders/Internal/Default/defaultVertShader.glsl", "/Shaders/Internal/Default/defaultFragShader.glsl");
    }

}
