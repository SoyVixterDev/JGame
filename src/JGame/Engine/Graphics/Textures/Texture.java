package JGame.Engine.Graphics.Textures;

import JGame.Engine.Utilities.FileUtilities;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

/**
 * Class defining a Texture for a Mesh Renderer
 */
public class Texture extends BaseTexture
{
    private int width;
    private int height;
    private int channels;

    /**
     * Generates a texture by loading the image found in texturePath, relative to the resources folder, with default attributes
     * @param texturePath
     * The path where the image is found, relative to the resources folder
     */
    public Texture(String texturePath)
    {
        this(texturePath, TextureType.TEXTURE_2D, TextureFilteringType.LINEAR, TextureWrapMode.REPEAT);
    }

    /**
     * Generates a texture by loading the image found in texturePath, relative to the resources folder
     * @param texturePath
     * The path where the image is found, relative to the resources folder
     * @param textureType
     * The type of the texture
     * @param filteringType
     * The filtering type used for the texture
     * @param wrapMode
     * The wrap mode setting for the texture
     */
    public Texture(String texturePath, TextureType textureType, TextureFilteringType filteringType, TextureWrapMode wrapMode)
    {
        super(textureType, filteringType, wrapMode);

        textureID = LoadTexture(texturePath);
    }

    /**
     * Creates a texture with default values
     */
    public Texture()
    {
        this("/Textures/Internal/defaultTexture.png");
    }

    /**
     * Loads and returns the ID of a texture found in the path, relative to the resources folder
     * @param texturePath
     * The path for the texture, relative to the resources folder
     * @return
     * The ID of the texture object
     */
    private int LoadTexture(String texturePath)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            ByteBuffer imageData = FileUtilities.LoadFileFromResources(texturePath);
            if (imageData == null)
            {
                throw new RuntimeException("Failed to load texture file! File path: " + texturePath);
            }

            ByteBuffer stbImage = STBImage.stbi_load_from_memory(imageData, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (stbImage == null)
            {
                throw new RuntimeException("Failed to load texture data from STBImage! File path: " + texturePath);
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
            channels = channelsBuffer.get();

            int textureID = glGenTextures();

            glBindTexture(_textureTypeGL, textureID);

            glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_S, _wrapModeGL);
            glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_T, _wrapModeGL);
            glTexParameteri(_textureTypeGL, GL_TEXTURE_MIN_FILTER, _filteringTypeGL);
            glTexParameteri(_textureTypeGL, GL_TEXTURE_MAG_FILTER, _filteringTypeGL);

            glTexImage2D(_textureTypeGL, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, stbImage);

            STBImage.stbi_image_free(stbImage);

            glBindTexture(_textureTypeGL, 0);

            return textureID;
        }
    }


    /**
     * Gets the texture height
     * @return
     * The texture height in pixels
     */
    public int GetHeight()
    {
        return height;
    }

    /**
     * Gets the texture width
     * @return
     * The texture width in pixels
     */
    public int GetWidth()
    {
        return width;
    }

    /**
     * Gets the texture's channel count
     * @return
     * The texture's channel count
     */
    public int GetChannelCount()
    {
        return channels;
    }
}
