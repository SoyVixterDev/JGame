#version 460

layout(location = 0) in vec3 position;
layout(location = 2) in vec2 inUVCoords;

uniform mat4 uView;

out vec3 fragPosition;
out vec2 uvCoords;

void main()
{
    uvCoords = inUVCoords;
    fragPosition = vec3(uView * vec4(position.xy, 1.0, 1.0));
    gl_Position = vec4(position.xy, 0.0, 1.0);
}