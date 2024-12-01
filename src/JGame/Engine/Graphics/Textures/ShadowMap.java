package JGame.Engine.Graphics.Textures;

import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Structures.Vector2D;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class ShadowMap extends BaseTexture
{
    public final int width;
    public int height;

    public int FBO;

    public ShadowMap(Vector2D dimensions)
    {
        this((int)dimensions.x, (int)dimensions.y);
    }

    public ShadowMap(int width, int height)
    {
        super(TextureType.TEXTURE_2D, TextureFilteringType.NEAREST, TextureWrapMode.CLAMP_TO_BORDER);

        this.width = width;
        this.height = height;

        FBO = glGenFramebuffers();

        InitializeShadowmap();
    }

    public ShadowMap(TextureType textureType, TextureFilteringType textureFilteringType, TextureWrapMode textureWrapMode, int width, int height)
    {
        super(textureType, textureFilteringType, textureWrapMode);

        this.width = width;
        this.height = height;
    }

    protected void InitializeShadowmap()
    {
        textureID = glGenTextures();

        glBindTexture(_textureTypeGL, textureID);

        glTexImage2D(_textureTypeGL, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT,(ByteBuffer)null);

        glTexParameteri(_textureTypeGL, GL_TEXTURE_MIN_FILTER, _filteringTypeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_MAG_FILTER, _filteringTypeGL);

        glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_S, _wrapModeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_T, _wrapModeGL);

        float[] clampColor = {1.0f,1.0f,1.0f,1.0f};
        glTexParameterfv(_textureTypeGL, GL_TEXTURE_BORDER_COLOR, clampColor);

        glBindFramebuffer(GL_FRAMEBUFFER, FBO);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, _textureTypeGL, textureID, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glBindTexture(_textureTypeGL, 0);
    }

    public void BindToShader(Shader shader, int index)
    {
        shader.Bind();

        String lightIndex = "lights[" + index + "]";

        shader.SetUniformProperty(lightIndex + ".shadowmap", 18 + index);

        shader.Unbind();
    }

}
