#version 460 core

in vec2 uvCoords;

out vec4 outColor;

uniform sampler2D mainTex;

void main()
{
    vec4 mainTexColor = texture(mainTex, uvCoords);

    outColor = vec4(vec3(mainTexColor.x), 1.0f);
}