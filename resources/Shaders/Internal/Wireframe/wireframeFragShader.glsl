#version 460 core

uniform vec4 wireframeColor;

out vec4 color;

void main()
{
    color = wireframeColor;
}