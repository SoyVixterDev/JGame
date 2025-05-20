#version 460

const float PI = 3.1415926;
const float INF = 1.0/0.0;

const int MAX_BOUNCES = 4;
const int RAYS_PER_PIXEL =  8;
const float EPSILON =  1e-4;

struct Material
{
    vec3 color; //12 bytes
    float transparency; //4 bytes
    float smoothness; //4 bytes
    float emissivity; //4 bytes
    float _pad2; //4 bytes
    float _pad3; //4 bytes
}; //Total:32 bytes

struct Sphere
{
    vec3 position; //12 bytes
    float radius; //4 bytes
    Material material; // 32 bytes;
}; //Total: 48 bytes

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

layout(std140, binding = 2) buffer DirectionalLightBuffer
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

in vec3 fragPosition;
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

    float a = dot(ray.dir, ray.dir);
    float b = 2 * dot(offsetRayOrigin, ray.dir);
    float c = dot(offsetRayOrigin, offsetRayOrigin) - sphereRadius * sphereRadius;

    float discriminant = b * b - 4 * a * c;

    if(discriminant >= 0)
    {
        float sqrtDisc = sqrt(discriminant);
        float t0 = (-b - sqrtDisc) / (2.0 * a);
        float t1 = (-b + sqrtDisc) / (2.0 * a);


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
float random_normal( float n )
{
    float u1 = random_uniform(n), u2 = random_uniform(n);

    float theta = 2 * PI * u1;
    float rho = sqrt(-2 * log(u2));

    return rho * cos(theta);
}

vec2 random_normal_vec(float n)
{
    float u1 = random_uniform(n), u2 = random_uniform(n);

    float theta = 2 * PI * u1;
    float rho = sqrt(-2 * log(u2));

    return vec2(rho * cos(theta), rho * sin(theta));
}

vec3 random_direction( float n )
{
    vec2 random_dual = random_normal_vec(n);

    float x = random_dual.x;
    float y = random_dual.y;
    float z = random_normal(n);

    return normalize(vec3(x, y, z));
}

// Thanks to: https://www.youtube.com/watch?v=Qz0KTGYJtUk&t=544s
vec3 random_direction_hemisphere(vec3 normal, float n)
{
    vec3 dir = random_direction(n);
    dir *= sign(dot(normal, dir));

    return dir;
}

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

        if(hitInfo.didHit)
        {
            rayOrigin = hitInfo.hitPoint + hitInfo.normal * EPSILON;
            rayDir = random_direction_hemisphere(hitInfo.normal, n + i);

            vec3 emittedLight = (hitInfo.material.color * hitInfo.material.emissivity);
            float lightStrength = max(dot(hitInfo.normal, rayDir), 0.0);

            lightColor += emittedLight * rayColor;
            rayColor *= ((hitInfo.material.emissivity == 0.0) ? hitInfo.material.color : vec3(0.0, 0.0, 0.0)) * lightStrength;
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
    vec2 normalizedUV = (uvCoords * 2 - 1.0);
    vec3 viewPointLocal = vec3(-normalizedUV.x, normalizedUV.y, 1.0) * CameraParams;
    vec3 viewPoint = vec3(CamTransformationMatrix * vec4(viewPointLocal, 1));

    uvec2 numPixels = uvec2(uint(ScreenSize.x), uint(ScreenSize.y));
    uvec2 pixelCoord = uvec2(uvCoords * numPixels);
    uint pixelIndex = pixelCoord.y * numPixels.x + pixelCoord.x;

    vec3 rayOrigin = CamWorldPos;
    vec3 rayDir = normalize(viewPoint - rayOrigin);

    vec3 color = vec3(0);

    for(uint i = 0; i < RAYS_PER_PIXEL; i++)
    {
        color += RayTracer(rayOrigin, rayDir, pixelIndex + frame * i);
    }

    color = color / float(RAYS_PER_PIXEL);

    outColor = vec4(color, 1.0);
}
