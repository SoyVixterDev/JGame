#include "Commons/light_data.glsl"

out vec4 fragPosLight[MAX_LIGHTS];

void UpdateFragPosLights()
{
    vec4 worldpos = ObjectToWorldSpace(position);

    for(int i = 0; i < MAX_LIGHTS; i++)
    {
        fragPosLight[i] = lights[i].projection * worldpos;
    }
}