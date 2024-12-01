#version 460 core
#include "Commons/fragment.glsl"

out vec4 color;

in vec3 uvCoords;

uniform samplerCube mainTex;

void main()
{
    color = vec4(texture(mainTex, uvCoords));
}
