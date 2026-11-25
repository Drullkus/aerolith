#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;
uniform vec2 ScreenSize;

uniform mat4 ProjMat;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

float linearDepth(float fragDepth) {
    float normalizedZ = fragDepth * 2.0 - 1.0;

    if (ProjMat[3][3] == 1.0) return (ProjMat[3][2] - normalizedZ) / ProjMat[2][2]; // Already isometric

    return ProjMat[3][2] / (normalizedZ + ProjMat[2][2]); // Normalize depth in perspective projection
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }

    float seconds = GameTime * 1200.0; // GameTime's domain is a fraction spanning a daycycle, convert into seconds

    vec4 noisePos = vec4(gl_FragCoord.xy, linearDepth(gl_FragCoord.z), seconds);

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    // fragColor = mix(linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor), vec4(1.0, 0.0, 0.0, 1.0), 0.5);
    fragColor = mix(linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor), vec4(noisePos.rg / ScreenSize, sin(noisePos.b) * 0.5 + 0.5, 1.0), 0.9);
}
