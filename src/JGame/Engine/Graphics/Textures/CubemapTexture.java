package JGame.Engine.Graphics.Textures;

import JGame.Engine.Internal.Logger;
import JGame.Engine.Utilities.FileUtilities;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

public class CubemapTexture extends BaseTexture
{
    /**
     * Creates a Cubemap texture, with a default texture
     */
    public CubemapTexture()
    {
        this
            (
                new String[]
                {
                        "/Textures/Internal/Default/white.png",
                        "/Textures/Internal/Default/white.png",
                        "/Textures/Internal/Default/white.png",
                        "/Textures/Internal/Default/white.png",
                        "/Textures/Internal/Default/white.png",
                        "/Textures/Internal/Default/white.png",
                },
                TextureFilteringType.LINEAR
            );
    }

    /**
     * Creates a cubemap texture
     * @param texturePaths
     * The paths towards the textures, relative to the resources folder, used for the cubemap, in this order:
     * 0: right
     * 1: left
     * 2: top
     * 3: bottom
     * 4: front
     * 5: back
     */
    public CubemapTexture(String[] texturePaths, TextureFilteringType filteringType)
    {
        super(TextureType.CUBEMAP, filteringType, TextureWrapMode.CLAMP_TO_EDGE);

        textureID = LoadTextures(texturePaths);
    }

    /**
     * Loads and returns the ID of a textures found in the paths, relative to the resources folder
     * @param texturePaths
     * The paths for the textures, relative to the resources folder
     * @return
     * The ID of the texture object
     */
    private int LoadTextures(String[] texturePaths)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            if(texturePaths.length != 6)
            {
                Logger.DebugError("The texture paths variable needs to have exactly 6 elements! Elements: " + texturePaths.length);
                return -1;
            }


            int textureID = glGenTextures();

            glBindTexture(_textureTypeGL, textureID);

            glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_S, _wrapModeGL);
            glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_T, _wrapModeGL);
            glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_R, _wrapModeGL);

            glTexParameteri(_textureTypeGL, GL_TEXTURE_MAG_FILTER, _filteringTypeGL);
            glTexParameteri(_textureTypeGL, GL_TEXTURE_MIN_FILTER, _filteringTypeGL);

            for (int i = 0; i < 6; i++)
            {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);
                IntBuffer channelsBuffer = stack.mallocInt(1);

                ByteBuffer imageData = FileUtilities.LoadFileFromResources(texturePaths[i]);
                if (imageData == null)
                {
                    throw new RuntimeException("Failed to load texture file! File path: " + texturePaths[i]);
                }

                ByteBuffer stbImage = STBImage.stbi_load_from_memory(imageData, widthBuffer, heightBuffer, channelsBuffer, 4);
                if (stbImage == null)
                {
                    throw new RuntimeException("Failed to load texture data from STBImage! File path: " + texturePaths[i]);
                }

                int width = widthBuffer.get();
                int height = heightBuffer.get();

                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,0, GL_RGBA, width, height,0, GL_RGBA, GL_UNSIGNED_BYTE, stbImage);

                STBImage.stbi_image_free(stbImage);
            }

            glBindTexture(_textureTypeGL, 0);

            int errorCode = glGetError();
            if (errorCode != GL_NO_ERROR)
            {
                Logger.DebugError("OpenGL Error: " + errorCode);
            }

            return textureID;
        }
    }
}