#version 460 core

#include "Commons/vertex.glsl"


out vec3 uvCoords;

void main()
{
    vec4 skyboxPos = WorldToClipSpace(position);

    gl_Position = vec4(skyboxPos.x, skyboxPos.y, skyboxPos.w, skyboxPos.w);

    uvCoords = vec3(position.x, position.y, -position.z);
}
