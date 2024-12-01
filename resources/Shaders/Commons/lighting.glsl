#include "Commons/vertex.glsl"
#include "Commons/light_data.glsl"

//Material info
uniform bool isLit;
uniform bool receiveShadows;
uniform float specularStrength;
uniform float smoothness;

in vec4 fragPosLight[MAX_LIGHTS];

//Constant array of offsets used for a more uniform soft shadow sampling
const vec2 poissonDisk2D[16] = vec2[](
vec2( -0.94201624, -0.39906216 ), vec2( 0.94558609, -0.76890725 ),
vec2( -0.094184101, -0.92938870 ), vec2( 0.34495938, 0.29387760 ),
vec2( -0.91588581, 0.45771432 ), vec2( -0.81544232, -0.87912464 ),
vec2( -0.38277543, 0.27676845 ), vec2( 0.97484398, 0.75648379 ),
vec2( 0.44323325, -0.97511554 ), vec2( 0.53742981, -0.47373420 ),
vec2( -0.26496911, -0.41893023 ), vec2( 0.79197514, 0.19090188 ),
vec2( -0.24188840, 0.99706507 ), vec2( -0.81409955, 0.91437590 ),
vec2( 0.19984126, 0.78641367 ), vec2( 0.14383161, -0.14100790 )
);

const vec3 poissonDisk3D[16] = vec3[](
vec3(0.50770764, 0.27668938, -0.81618464),
vec3(-0.82078695, -0.06415844, 0.56932667),
vec3(0.62265341, 0.12526983, 0.77373314),
vec3(0.16802504, 0.96135568, -0.22022623),
vec3(-0.66088251, 0.44465259, -0.60471971),
vec3(0.52419437, -0.84483589, -0.10447429),
vec3(-0.95520692, 0.07029384, -0.29030307),
vec3(-0.20795677, -0.81865935, 0.53556913),
vec3(0.93964573, 0.00000000, -0.34186680),
vec3(0.10427267, -0.58117409, -0.81756324),
vec3(0.37148338, -0.26429739, 0.89239501),
vec3(0.57449785, 0.81416818, 0.08378684),
vec3(-0.22675591, -0.54075018, -0.81007278),
vec3(0.21973374, 0.28981151, -0.93168829),
vec3(-0.97856031, 0.16305119, 0.13087951),
vec3(0.11916899, -0.94525040, -0.30802605)
);



//Calculates the Specular strength for a given position and normal
float Specular(vec3 fragPos, vec3 normal, int index)
{
    vec3 viewDir = ViewDirection(fragPos);
    vec3 lightDir = normalize(lights[index].position - fragPos);

    vec3 reflectionDir = reflect(-lightDir, normal);

    float specAmount = pow(max(dot(viewDir, reflectionDir), 0.0f), 24);

    return specAmount * 0.5f;
}

//Calculates if the fragment is currently under shadow for a particular light
float CalculateShadowmap(vec3 lightDir, vec3 normal, int index)
{
    vec3 lightCoords = fragPosLight[index].xyz / fragPosLight[index].w;
    if(lightCoords.z <= 1.0f)
    {
        lightCoords = (lightCoords + 1.0f) / 2.0f;

        float currentDepth = lightCoords.z;

        float slopeFactor = max(0.0, dot(normal, lightDir)); // Adjust bias based on the angle
        float bias = mix(lights[index].minshadowbias, lights[index].maxshadowbias, 1.0 - slopeFactor); // Larger bias for steeper slopes

        if(lights[index].softshadows)
        {
            float shadow = 0.0f;

            int sampleCount = 16;
            vec2 pixelSize = 1.0f / textureSize(lights[index].shadowmap, 0);

            for(int i = 0; i < sampleCount; i++)
            {
                vec2 offset = poissonDisk2D[i] * pixelSize;
                float closestDepth = texture(lights[index].shadowmap, lightCoords.xy + offset).r;

                if(currentDepth > closestDepth + bias)
                {
                    shadow += 1.0f;
                }
            }

            return shadow / float(sampleCount);
        }
        else
        {
            float closestDepth = texture(lights[index].shadowmap, lightCoords.xy).r;
            if(currentDepth > closestDepth + bias)
            {
                return 1.0f;
            }
        }

    }
    return 0.0f;
}

//Calculates shadows for point lights
float CalculatePointLightShadow(vec3 currentPos, vec3 normal, int index)
{
    vec3 lightPos = lights[index].position;
    vec3 fragToLight = currentPos - lightPos;
    float currentDepth = length(fragToLight) / (lights[index].range * 10.0f);

    vec3 lightDir = normalize(fragToLight);

    //Replace these 2 lines with the equivalent for the cubemap shadowmap scenario
    float slopeFactor = max(0.0, dot(normal, lightDir)); // Adjust bias based on the angle
    float bias = mix(lights[index].minshadowbias, lights[index].maxshadowbias, 1.0 - slopeFactor); // Larger bias for steeper slopes

    if(lights[index].softshadows)
    {
        float shadow = 0.0f;

        int sampleCount = 16;
        vec2 pixelSize = 1.0f / textureSize(lights[index].shadowcubemap, 0);

        for(int i = 0; i < sampleCount; i++)
        {
            vec3 offset = poissonDisk3D[i] * vec3(pixelSize, pixelSize.x);
            float closestDepth = texture(lights[index].shadowcubemap, lightDir + offset).r;

            if(currentDepth > closestDepth + bias)
            {
                shadow += 1.0f;
            }
        }

        return shadow / float(sampleCount);
    }
    else
    {
        float closestDepth = texture(lights[index].shadowcubemap, lightDir).r;
        if(currentDepth > closestDepth + bias)
        {
            return 1.0f;
        }
    }
    return 0.0f;
}

//Calculates the light strength as Directional for a given position and normal
float DirectionalLight(vec3 currentPos, vec3 normal, int index)
{
    float diffuse = max(dot(-lights[index].forward, normal), 0.0f);
    float specular = Specular(-lights[index].forward, normal, index);

    float shadow = 0.0f;

    if(receiveShadows)
    {
        vec3 lightDir = normalize(lights[index].position - currentPos);
        shadow = CalculateShadowmap(lightDir, normal, index);
    }

    return (diffuse + specular)  * (1.0f - shadow);
}

//Calculates the light strength as PointLight for a given position and normal
float PointLight(vec3 currentPos, vec3 normal, int index)
{
    vec3 lightDir = normalize(lights[index].position - currentPos);

    float diffuse = max(dot(normal, lightDir), 0.0f);
    float specular = Specular(currentPos, normal, index);

    float lightDist = distance(lights[index].position, currentPos);
    float distOverFalloff = lightDist/lights[index].falloff;
    float atten = lights[index].range/(distOverFalloff * distOverFalloff);

    // Calculate shadow for point light
    float shadow = 0.0f;
    if (receiveShadows)
    {
        shadow = CalculatePointLightShadow(currentPos, normal, index);
    }
    //return shadow;
    return min(1.0f, (diffuse + specular) * atten) * (1.0f - shadow);
}

//Calculates the light strength as SpotLight for a given position and normal
float SpotLight(vec3 currentPos, vec3 normal, int index)
{
    vec3 lightDir = normalize(lights[index].position - currentPos);

    float diffuse = max(dot(normal, lightDir), 0.0f);
    float specular = Specular(currentPos, normal, index);

    float lightDist = distance(lights[index].position, currentPos);
    float distOverFalloff = lightDist/lights[index].falloff;
    float atten = lights[index].range/(distOverFalloff * distOverFalloff);

    float angle = dot(-lights[index].forward, -lightDir);
    float inten = (angle - lights[index].outerCone) / (lights[index].innerCone - lights[index].outerCone);
    inten = clamp(inten, 0.0f, 1.0f);

    float shadow = 0.0f;

    if(receiveShadows)
    {
        shadow = CalculateShadowmap(lightDir, normal, index);
    }

    return min(1.0f, (diffuse + specular) * atten * inten)  * (1.0f - shadow);
}

//Gets the final strength value for a given light, given a position and normal
float GetLightValue(vec3 fragPos, vec3 normal, int lightIndex)
{
    float lightVal = 1.0f;

    switch(lights[lightIndex].type)
    {
        case 0:
        lightVal = DirectionalLight(fragPos, normal, lightIndex);
        break;
        case 1:
        lightVal = PointLight(fragPos, normal, lightIndex);
        break;
        case 2:
        lightVal = SpotLight(fragPos, normal, lightIndex);
        break;
        case 3:
        lightVal = 1.0f; //Ambient light is constant
        break;
        default:
        lightVal = 0.0f;
        break;
    }
    return lightVal;
}



//Gets the light influence and color as a vector4, given a position and normal
vec4 GetLight(vec3 fragPos, vec3 normal)
{
    if(!isLit)
    {
        return vec4(1.0f, 1.0f,1.0f,1.0f);
    }

    vec3 result = vec3(0.0f);

    for(int i = 0; i < MAX_LIGHTS; i++)
    {
        float lightVal = GetLightValue(fragPos, normal, i);

        result += lights[i].color * lights[i].intensity * (lightVal);
    }

    return vec4(result, 1.0f);
}