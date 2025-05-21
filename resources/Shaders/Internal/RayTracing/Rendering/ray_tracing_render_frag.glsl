#version 460

const float PI = 3.1415926;
const float INF = 1.0/0.0;

const int MAX_BOUNCES = 10;
const int RAYS_PER_PIXEL = 16;
const float EPSILON =  1e-4;

struct Material
{
    vec3 color; // 12 bytes
    float transparency; // 4 bytes
    vec3 specularColor; // 12 bytes
    float specularity; // 4 bytes
    float smoothness; // 4 bytes
    float emissivity; // 4 bytes
    float _pad3; // 4 bytes
    float _pad4; // 4 bytes
}; //Total: 48 bytes

struct Cube
{
    vec3 center; // 12 bytes
    float _pad1; // 4 bytes
    vec3 halfSize; // 12 bytes
    float _pad2; // 4 bytes
    vec4 rotation; // 16 bytes
    Material material; // 48 bytes
}; //Total: 96 bytes

struct Sphere
{
    vec3 position; //12 bytes
    float radius; //4 bytes
    Material material; // 48 bytes;
}; //Total: 64 bytes

struct Ray
{
    vec3 origin;
    vec3 dir;
};

struct HitData
{
    bool didHit;
    vec3 hitPoint;
    vec3 normal;
    float dst;
    Material material;
};

struct DirectionalLightData
{
    vec3 forward; // 12 bytes
    float intensity; // 4 bytes
    vec3 color; // 12 bytes
    float sunFocus; // 4 bytes
}; // 32 bytes

layout(std140, binding = 1) buffer SphereBuffer
{
    Sphere spheres[ ];
};

layout(std140, binding = 2) buffer CubeBuffer
{
    Cube cubes[ ];
};

layout(std140, binding = 3) buffer DirectionalLightBuffer
{
    DirectionalLightData directionalLightData;
};

uniform int sphereCount;

uniform vec3 CameraParams;
uniform vec2 ScreenSize;
uniform vec3 CamWorldPos;
uniform mat4 CamTransformationMatrix;

uniform uint frame;

uniform samplerCube skyboxTex;
uniform float skyboxIntensity;

uniform float jitterStrength;
uniform float dofStrength;

in vec2 uvCoords;
out vec4 outColor;

// Calculates hit data between a ray and a sphere
HitData RaySphere(Ray ray, Sphere sphere)
{
    HitData hit;
    hit.didHit = false;

    vec3 sphereCenter = sphere.position;
    float sphereRadius = sphere.radius;

    vec3 offsetRayOrigin = ray.origin - sphereCenter;

    float b = 2 * dot(offsetRayOrigin, ray.dir);
    float c = dot(offsetRayOrigin, offsetRayOrigin) - sphereRadius * sphereRadius;

    float discriminant = b * b - 4 * c;

    if(discriminant >= 0)
    {
        float sqrtDisc = sqrt(discriminant);
        float t0 = (-b - sqrtDisc) / (2.0);
        float t1 = (-b + sqrtDisc) / (2.0);

        float dst = t0;

        if(dst < 0.0)
        {
            dst = t1;
        }

        if(dst >= 0)
        {
            hit.didHit = true;
            hit.dst = dst;
            hit.hitPoint = ray.origin + ray.dir * dst;
            hit.normal = normalize(hit.hitPoint - sphereCenter);
            hit.material = sphere.material;
        }
    }

    return hit;
}

// Checks if a ray collides with any of the objects in memory
HitData CalculateRayCollision(Ray ray)
{
    HitData closestHit;
    closestHit.didHit = false;
    closestHit.dst = INF;

    for(int i = 0; i < sphereCount; i++)
    {
        HitData hit = RaySphere(ray, spheres[i]);

        if(hit.didHit && hit.dst < closestHit.dst)
        {
            closestHit = hit;
        }
    }

    return closestHit;
}

// Uniform random number generator based on: https://stackoverflow.com/a/17479300
uint state = 23482;
// A single iteration of Bob Jenkins' One-At-A-Time hashing algorithm.
uint hash( uint x )
{
    state ^= ( state >> 2u);
    x += ( x << 10u );
    x ^= ( x >>  6u );
    x += ( x <<  3u );
    x ^= ( state >> 11u );
    x += ( x << 15u );
    x ^= ( x >> 2u );
    state += ( x << 7u );
    return x;
}
// Pseudo-random value in half-open range [0:1].
float random_uniform( float x )
{
    uint m = floatBitsToUint(x);
    state += hash(frame);
    m = hash(m);

    // Construct a float with half-open range [0:1] using low 23 bits.
    // All zeroes yields 0.0, all ones yields the next smallest representable value below 1.0.
    const uint ieeeMantissa = 0x007FFFFFu; // binary32 mantissa bitmask
    const uint ieeeOne      = 0x3F800000u; // 1.0 in IEEE binary32

    m &= ieeeMantissa;                     // Keep only mantissa bits (fractional part)
    m |= ieeeOne;                          // Add fractional part to 1.0

    float  f = uintBitsToFloat( m );       // Range [1:2]
    return f - 1.0;                        // Range [0:1]
}

// Box-Muller approach to normal distribution, based on https://stackoverflow.com/a/6178290
// Returns a single random value following normal distribution
float random_normal( float n )
{
    float u1 = random_uniform(n), u2 = random_uniform(n);

    float theta = 2 * PI * u1;
    float rho = sqrt(-2 * log(u2));

    return rho * cos(theta);
}

// Returns 2 random values following normal distribution,
// this function exists because the Box-Muller approach can generate 2 values at a time using cosine and sine
vec2 random_normal_vec(float n)
{
    float u1 = random_uniform(n), u2 = random_uniform(n);

    float theta = 2 * PI * u1;
    float rho = sqrt(-2 * log(u2));

    return vec2(rho * cos(theta), rho * sin(theta));
}

// Returns a spherically uniform random direction using n as the current seed
vec3 random_direction( float n )
{
    vec2 random_dual = random_normal_vec(n);

    float x = random_dual.x;
    float y = random_dual.y;
    float z = random_normal(n);

    return normalize(vec3(x, y, z));
}
// Generates a random point within a unit circle
vec2 random_point_circle( float n )
{
    float angle = random_uniform(n) * 2 * PI;
    vec2 point = vec2(cos(angle), sin(angle));
    return point * sqrt(random_uniform(n));
}

// Calculates ambient ilumination based on ray direction, it samples a skybox and uses the current main directional light as the sun
vec3 GetAmbientLight(Ray ray)
{
    vec3 sky = texture(skyboxTex, ray.dir).rgb * skyboxIntensity;
    sky = clamp(sky, 0.0, 100.0);

    float sunStrength = pow(max(0, dot(ray.dir, -directionalLightData.forward)), directionalLightData.sunFocus) * directionalLightData.intensity;

    return sky + sunStrength * directionalLightData.color;
}

// Main function for ray tracing, most of the processing and information gathering occurs here
vec3 RayTracer(vec3 rayOrigin, vec3 rayDir, uint n)
{
    vec3 lightColor = vec3(0.0);
    vec3 rayColor = vec3(1.0);

    uint hits = 0;

    for(uint i = 0; i <= MAX_BOUNCES; i++)
    {
        Ray ray;
        ray.origin = rayOrigin;
        ray.dir = rayDir;

        HitData hitInfo = CalculateRayCollision(ray);

        Material material;

        if(hitInfo.didHit)
        {
            rayOrigin = hitInfo.hitPoint + hitInfo.normal * EPSILON;
            vec3 diffuseDir = normalize(hitInfo.normal + random_direction(n + i));
            vec3 specularDir = reflect(ray.dir, hitInfo.normal);

            material = hitInfo.material;

            float specularBounce = step(random_uniform(n + i), material.specularity);

            rayDir = mix(diffuseDir, specularDir, material.smoothness * specularBounce);

            vec3 emittedLight = material.color * hitInfo.material.emissivity;

            lightColor += emittedLight * rayColor;
            rayColor *= mix(material.color, material.specularColor, specularBounce);
        }
        else
        {
            lightColor += GetAmbientLight(ray) * rayColor;
            break;
        }
    }

    return lightColor;
}

void main()
{
    //vec2 normalizedUV = vec2(0.5 - uvCoords.x, uvCoords.y - 0.5);
    vec2 normalizedUV = uvCoords - 0.5;

    vec3 viewPointLocal = vec3(-normalizedUV.x, normalizedUV.y, 1.0) * CameraParams;
    vec3 viewPoint = vec3(CamTransformationMatrix * vec4(viewPointLocal, 1));

    uvec2 numPixels = uvec2(uint(ScreenSize.x), uint(ScreenSize.y));
    uvec2 pixelCoord = uvec2(uvCoords * numPixels);
    uint pixelIndex = pixelCoord.y * numPixels.x + pixelCoord.x;

    vec3 camRight = CamTransformationMatrix[0].xyz;
    vec3 camUp = CamTransformationMatrix[1].xyz;

    vec3 color = vec3(0);

    uint randomState = pixelIndex * hash(frame);

    for(uint i = 0; i < RAYS_PER_PIXEL; i++)
    {
        vec2 defocusJitter = random_point_circle(randomState) * dofStrength / numPixels.x;
        vec3 rayOrigin = CamWorldPos + camRight * defocusJitter.x + camUp * defocusJitter.y;

        vec2 jitter = random_point_circle(randomState) * jitterStrength / numPixels.x;
        vec3 jitteredViewPoint = viewPoint + camRight * jitter.x + camUp * jitter.y;

        vec3 rayDir = normalize(jitteredViewPoint - rayOrigin);

        color += RayTracer(rayOrigin, rayDir, randomState);
    }

    color = color / float(RAYS_PER_PIXEL);

    outColor = vec4(color, 1.0);
}
