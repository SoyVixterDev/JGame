#version 460 core

#include "Commons/vertex.glsl"
#include "Commons/lighting_vert.glsl"

out vec3 fragPosition;
out vec3 normal;
out vec4 vertColor;
out vec2 uvCoords;

void main()
{
    //Necessary calculations for 3D projection and correct worldspace position
    gl_Position = ObjectToClipSpace(position);

    fragPosition = vec3(ObjectToWorldSpace(position));

    vertColor = inVertColor;
    uvCoords = inUVCoords;

    UpdateFragPosLights();

    normal = normalize(mat3(uNormalRotation) * inNormal);
}