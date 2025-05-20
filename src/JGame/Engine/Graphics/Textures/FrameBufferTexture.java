package JGame.Engine.Graphics.Textures;

import static org.lwjgl.opengl.GL46.*;

public class FrameBufferTexture extends BaseTexture
{
    private int FBO;
    private int width, height;

    public FrameBufferTexture(int width, int height)
    {
        this(width, height, TextureFilteringType.LINEAR, TextureWrapMode.CLAMP_TO_EDGE);
    }

    public FrameBufferTexture(int width, int height, TextureFilteringType filteringType, TextureWrapMode wrapMode)
    {
        super(TextureType.TEXTURE_2D, filteringType, wrapMode);
        this.width = width;
        this.height = height;

        InitTexture();
        InitFramebuffer();
    }

    private void InitTexture()
    {
        textureID = glGenTextures();
        glBindTexture(_textureTypeGL, textureID);

        glTexImage2D(_textureTypeGL, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, 0);

        glTexParameteri(_textureTypeGL, GL_TEXTURE_MIN_FILTER, _filteringTypeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_MAG_FILTER, _filteringTypeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_S, _wrapModeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_T, _wrapModeGL);

        glBindTexture(_textureTypeGL, 0);
    }

    private void InitFramebuffer()
    {
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, _textureTypeGL, textureID, 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        {
            throw new RuntimeException("Framebuffer is not complete!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Binds this framebuffer as the current render target.
     */
    public void BindFramebuffer()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glViewport(0, 0, width, height);
    }

    /**
     * Unbinds the framebuffer (reverts to default framebuffer).
     */
    public void UnbindFramebuffer()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void Destroy()
    {
        super.Destroy();
        glDeleteFramebuffers(FBO);
    }

    public int GetFramebufferID()
    {
        return FBO;
    }

    public int GetWidth()
    {
        return width;
    }

    public int GetHeight()
    {
        return height;
    }
}
