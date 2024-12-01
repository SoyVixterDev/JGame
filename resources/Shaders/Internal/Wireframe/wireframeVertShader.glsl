#version 460 core

layout(location = 0) in vec3 position;

uniform mat4 uTransformation;
uniform mat4 uView;
uniform mat4 uProjection;

void main()
{
    gl_Position = uProjection * uView * uTransformation * vec4(position, 1.0);
}