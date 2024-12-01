#version 460 core
#include "Commons/fragment.glsl"
#include "Commons/lighting.glsl"

in vec3 fragPosition;
in vec3 normal;
in vec4 vertColor;
in vec2 uvCoords;

out vec4 outColor;

uniform sampler2D mainTex;

void main()
{
    vec4 mainTexColor = texture(mainTex, uvCoords);
    vec4 color = mainTexColor * vertColor * tintColor;

    outColor = color * GetLight(fragPosition, normal);
}