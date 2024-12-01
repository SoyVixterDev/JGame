layout(location = 0) in vec3 position;
layout(location = 1) in vec4 inVertColor;
layout(location = 2) in vec2 inUVCoords;
layout(location = 3) in vec3 inNormal;

// Common uniform matrices in both vertex and fragment shaders
uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform mat4 uNormalRotation;

//Data useful for different shaders
uniform vec3 viewPosition;

//Gets the direction between the camera and the given position
vec3 ViewDirection(vec3 position)
{
    return normalize(viewPosition - position);
}

//Converts the position from object space to world space
vec4 ObjectToWorldSpace(vec3 position)
{
    return (uTransformation * vec4(position, 1.0f));
}

//Converts the position from object to clip space
vec4 ObjectToClipSpace(vec3 position)
{
    return uProjection * uView * ObjectToWorldSpace(position);
}

//Converts the position from World or Consistent space to clip space
vec4 WorldToClipSpace(vec3 position)
{
    return uProjection * uView * vec4(position, 1.0f);
}