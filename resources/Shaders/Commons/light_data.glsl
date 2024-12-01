#define MAX_LIGHTS 10

struct LightData
{
    vec3 position;
    vec3 forward;
    mat4 projection;

    samplerCube shadowcubemap; //Used for point lights
    sampler2D shadowmap; //Used for all the rest light types
    bool softshadows;

    float minshadowbias;
    float maxshadowbias;

    float intensity;
    float range;
    float falloff;

    float outerCone;
    float innerCone;

    vec3 color;
    int type;
};

uniform LightData lights[MAX_LIGHTS];