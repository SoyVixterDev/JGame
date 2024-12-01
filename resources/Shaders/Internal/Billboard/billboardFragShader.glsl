#version 460

uniform sampler2D image;

in vec2 uvCoords;

out vec4 color;

void main()
{
    color = texture(image, uvCoords);
}
