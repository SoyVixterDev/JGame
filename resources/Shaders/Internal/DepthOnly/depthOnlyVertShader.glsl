#version 460
layout(location = 0) in vec3 position;

uniform mat4 uProjection;
uniform mat4 uTransformation;

void main()
{
    gl_Position = uProjection * uTransformation * vec4(position, 1.0f);
}
