package JGame.Engine.Graphics.Textures;

import JGame.Engine.Graphics.Misc.Shader;
import JGame.Engine.Structures.Vector2D;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class ShadowCubeMap extends ShadowMap
{
    final int faceWidth;
    final int faceHeight;

    public ShadowCubeMap(Vector2D dimensions)
    {
        this((int)dimensions.x, (int)dimensions.y);
    }

    public ShadowCubeMap(int width, int height)
    {
        super(TextureType.CUBEMAP, TextureFilteringType.NEAREST, TextureWrapMode.CLAMP_TO_EDGE, width, height);

        faceWidth = (int) (width / Math.sqrt(6));
        faceHeight = (int) (height / Math.sqrt(6));

        FBO = glGenFramebuffers();

        InitializeShadowmap();
    }

    @Override
    protected void InitializeShadowmap()
    {
        textureID = glGenTextures();

        glBindTexture(_textureTypeGL, textureID);



        for(int i = 0; i < 6; i++)
        {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, faceWidth, faceHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT,(ByteBuffer)null);
        }

        glTexParameteri(_textureTypeGL, GL_TEXTURE_MIN_FILTER, _filteringTypeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_MAG_FILTER, _filteringTypeGL);

        glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_S, _wrapModeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_T, _wrapModeGL);
        glTexParameteri(_textureTypeGL, GL_TEXTURE_WRAP_R, _wrapModeGL);

        glBindFramebuffer(GL_FRAMEBUFFER, FBO);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, textureID, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glBindTexture(_textureTypeGL, 0);
    }

    @Override
    public void BindToShader(Shader shader, int index)
    {
        shader.Bind();

        String lightIndex = "lights[" + index + "]";

        shader.SetUniformProperty(lightIndex + ".shadowcubemap", 18 + index);

        shader.Unbind();
    }
}
