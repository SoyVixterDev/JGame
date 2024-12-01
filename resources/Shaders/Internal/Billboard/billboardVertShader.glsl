#version 460
layout (location = 0) in vec3 position;
layout (location = 2) in vec2 inUVCoords;

uniform mat4 uTransformation;
uniform mat4 uView;
uniform mat4 uProjection;

out vec2 uvCoords;
void main()
{
    mat4 uBillboardMatrix = uView * uTransformation;

    for(int x = 0; x < 3; x++)
    {
        for(int y = 0; y < 3; y++)
        {
            uBillboardMatrix[x][y] = x == y ? 1.0f : 0.0f;
        }
    }

    gl_Position = uProjection * uBillboardMatrix * vec4(position, 1.0);

    uvCoords = inUVCoords;
}