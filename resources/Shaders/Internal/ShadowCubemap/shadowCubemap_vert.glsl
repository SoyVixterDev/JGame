#version 460 core
layout (location = 0) in vec3 position;

uniform mat4 uTransformation;

void main()
{
    gl_Position = uTransformation * vec4(position, 1.0f);
}
