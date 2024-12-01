#version 460 core

in vec4 fragPosition;

uniform vec3 viewPosition;
uniform float farPlane;

void main()
{
    gl_FragDepth = length(fragPosition.xyz - viewPosition) / (farPlane * 10);
}
