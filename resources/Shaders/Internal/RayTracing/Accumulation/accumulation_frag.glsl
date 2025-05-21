#version 460 core

in vec2 uvCoords;
out vec4 outColor;

uniform sampler2D lastAccumTex;
uniform sampler2D currentRadiance;
uniform uint frame;

void main()
{
    vec3 current = texture(currentRadiance, uvCoords).rgb;

    if(frame == 0u)
    {
        outColor = vec4(current, 1.0);
    }
    else
    {
        vec4 previous = texture(lastAccumTex, uvCoords);

        vec3 newSum = previous.rgb + current;
        float newCount = previous.a + 1.0;

        outColor = vec4(newSum, newCount);
    }
}
