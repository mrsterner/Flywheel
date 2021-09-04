#use "flywheel:context/fog.glsl"

uniform float uTime;
uniform mat4 uViewProjection;
uniform vec3 uCameraPos;

uniform vec2 uTextureScale;
uniform sampler2D uBlockAtlas;
uniform sampler2D uLightMap;
uniform sampler2D uCrumbling;

uniform vec2 uWindowSize;

void FLWFinalizeNormal(inout vec3 normal) {
    // noop
}

#if defined(VERTEX_SHADER)
void FLWFinalizeWorldPos(inout vec4 worldPos) {
    #if defined(USE_FOG)
    FragDistance = length(worldPos.xyz - uCameraPos);
    #endif

    gl_Position = uViewProjection * worldPos;
}

#elif defined(FRAGMENT_SHADER)

vec4 FLWBlockTexture(vec2 texCoords) {
    vec4 cr = texture2D(uCrumbling, texCoords * uTextureScale);
    float diffuseAlpha = texture2D(uBlockAtlas, texCoords).a;
    cr.a = cr.a * diffuseAlpha;
    return cr;
}

void FLWFinalizeColor(vec4 color) {
    #if defined(USE_FOG)
    float a = color.a;
    float fog = clamp(FLWFogFactor(), 0., 1.);

    color = mix(uFogColor, color, fog);
    color.a = a;
    #endif

    gl_FragColor = color;
}

vec4 FLWLight(vec2 lightCoords) {
    return vec4(1.);
}
#endif