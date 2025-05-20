#version 460 core

in vec2 uvCoords;
out vec4 outColor;

uniform sampler2D screenTex;

void main()
{
    vec4 sumAndCount = texture(screenTex, uvCoords);
    float sampleCount = max(sumAndCount.a, 1.0);

    vec3 finalColor = sumAndCount.rgb / sampleCount;

    finalColor = clamp(finalColor, 0.0, 100.0);

    outColor = vec4(finalColor, 1.0);
}
