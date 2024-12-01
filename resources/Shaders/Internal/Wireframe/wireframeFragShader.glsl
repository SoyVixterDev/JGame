#version 460 core

uniform vec4 wireframeColor;

out vec4 color;

void main()
{
    gl_FragDepth = gl_FragCoord.z - (0.0000005f * gl_FragCoord.z);
    color = wireframeColor;
}