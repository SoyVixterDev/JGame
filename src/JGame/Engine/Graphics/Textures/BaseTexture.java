package JGame.Engine.Graphics.Textures;

import JGame.Engine.Basic.BaseEngineClass;

import static org.lwjgl.opengl.GL46.*;

/**
 * Base Class used by the different texture types
 */
public abstract class BaseTexture
{
    /**
     * The ID of the texture object for OpenGL
     */
    public int textureID;

    protected int _filteringTypeGL;

    protected int _textureTypeGL;

    protected int _wrapModeGL;

    public BaseTexture(TextureType textureType, TextureFilteringType filteringType, TextureWrapMode wrapMode)
    {
        super();

        GetGLProperties(textureType, filteringType, wrapMode);
    }

    /**
     * Gets the equivalent value for the glTexParameteri from the enumerators used
     */
    protected void GetGLProperties(TextureType textureType, TextureFilteringType filteringType, TextureWrapMode wrapMode)
    {
        switch (filteringType)
        {
            case NEAREST -> _filteringTypeGL = GL_NEAREST;
            case LINEAR -> _filteringTypeGL = GL_LINEAR;
        }

        switch (textureType)
        {
            case TEXTURE_1D -> _textureTypeGL = GL_TEXTURE_1D;
            case TEXTURE_2D -> _textureTypeGL = GL_TEXTURE_2D;
            case TEXTURE_3D -> _textureTypeGL = GL_TEXTURE_3D;
            case CUBEMAP -> _textureTypeGL = GL_TEXTURE_CUBE_MAP;
        }

        switch (wrapMode)
        {
            case REPEAT -> _wrapModeGL = GL_REPEAT;
            case CLAMP -> _wrapModeGL = GL_CLAMP;
            case CLAMP_TO_BORDER -> _wrapModeGL = GL_CLAMP_TO_BORDER;
            case CLAMP_TO_EDGE -> _wrapModeGL = GL_CLAMP_TO_EDGE;
            case MIRROR -> _wrapModeGL = GL_MIRRORED_REPEAT;
        }
    }

    public void Bind()
    {
        Bind(GL_TEXTURE0);
    }

    /**
     * Binds the texture to GL
     */
    public void Bind(int textureSlot)
    {
        glActiveTexture(textureSlot);
        glBindTexture(_textureTypeGL, textureID);
    }

    public void Unbind()
    {
        Unbind(GL_TEXTURE0);
    }

    /**
     * Unbinds the texture from GL
     */
    public void Unbind(int textureSlot)
    {
        glActiveTexture(textureSlot);
        glBindTexture(_textureTypeGL, 0);
    }


    //Enums
    public enum TextureFilteringType
    {
        NEAREST,
        LINEAR
    }
    public enum TextureType
    {
        TEXTURE_1D,
        TEXTURE_2D,
        TEXTURE_3D,
        CUBEMAP
    }
    public enum TextureWrapMode
    {
        REPEAT,
        CLAMP,
        CLAMP_TO_BORDER,
        CLAMP_TO_EDGE,
        MIRROR
    }

    public void Destroy()
    {
        glDeleteTextures(textureID);
    }
}
