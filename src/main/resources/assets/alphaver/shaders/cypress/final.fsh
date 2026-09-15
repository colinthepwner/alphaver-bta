#version 410

in vec2 TexCoords;
out vec4 FragColor;

uniform sampler2D sceneColor;
uniform sampler2D sceneDepth;

uniform float aspectRatio;
uniform float nearPlane;
uniform float farPlane;
uniform float rand;
uniform float lastMouseDist;
uniform float playerPitchRot;
uniform float fovMod;

const float HYPERFOCAL = 4.0;
const float SSR_MAX_Y = 16.0;
const float INFINITY = 100000.0;

float linearDepth(float depth) {
	return 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * depth - 1.0) * (farPlane - nearPlane));
}

float getDepth(vec2 coord) {
	float depth = texture(sceneDepth, coord).x;
	if (depth == 1.0) {
		return INFINITY;
	}
	return linearDepth(depth);
}

float getCursorDepth(vec2 coord) {
	return linearDepth(texture(sceneDepth, coord).x);
}

float sampleCount = 0.0;

vec4 getSampleWithBoundsCheck(vec2 offset) {
	vec2 coord = TexCoords + offset;
	if (coord.s <= 1.0 && coord.s >= 0.0 && coord.t <= 1.0 && coord.t >= 0.0) {
		sampleCount += 1.0;
		return texture(sceneColor, coord);
	}
	return vec4(0.0);
}

vec4 getBlurredColor() {
	vec4 blurredColor = vec4(0.0);
	vec2 aspectCorrection = vec2(1.0, aspectRatio) * 0.005;

	vec2 ac0_4 = 0.4 * aspectCorrection;
	vec2 ac0_29 = 0.29 * aspectCorrection;
	vec2 ac0_15 = 0.15 * aspectCorrection;
	vec2 ac0_37 = 0.37 * aspectCorrection;

	vec2 lowSpace = TexCoords;
	vec2 highSpace = 1.0 - lowSpace;
	vec2 space = vec2(min(lowSpace.s, highSpace.s), min(lowSpace.t, highSpace.t));

	if (space.s >= ac0_4.s && space.t >= ac0_4.t) {
		blurredColor += texture(sceneColor, TexCoords + vec2(0.0, ac0_4.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(ac0_4.s, 0.0));
		blurredColor += texture(sceneColor, TexCoords + vec2(0.0, -ac0_4.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(-ac0_4.s, 0.0));
		blurredColor += texture(sceneColor, TexCoords + vec2(ac0_29.s, -ac0_29.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(ac0_29.s, ac0_29.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(-ac0_29.s, ac0_29.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(-ac0_29.s, -ac0_29.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(ac0_15.s, ac0_37.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(-ac0_37.s, ac0_15.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(ac0_37.s, -ac0_15.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(-ac0_15.s, -ac0_37.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(-ac0_15.s, ac0_37.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(ac0_37.s, ac0_15.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(-ac0_37.s, -ac0_15.t));
		blurredColor += texture(sceneColor, TexCoords + vec2(ac0_15.s, -ac0_37.t));
		blurredColor /= 16.0;
	} else {
		blurredColor += getSampleWithBoundsCheck(vec2(0.0, ac0_4.t));
		blurredColor += getSampleWithBoundsCheck(vec2(ac0_4.s, 0.0));
		blurredColor += getSampleWithBoundsCheck(vec2(0.0, -ac0_4.t));
		blurredColor += getSampleWithBoundsCheck(vec2(-ac0_4.s, 0.0));
		blurredColor += getSampleWithBoundsCheck(vec2(ac0_29.s, -ac0_29.t));
		blurredColor += getSampleWithBoundsCheck(vec2(ac0_29.s, ac0_29.t));
		blurredColor += getSampleWithBoundsCheck(vec2(-ac0_29.s, ac0_29.t));
		blurredColor += getSampleWithBoundsCheck(vec2(-ac0_29.s, -ac0_29.t));
		blurredColor += getSampleWithBoundsCheck(vec2(ac0_15.s, ac0_37.t));
		blurredColor += getSampleWithBoundsCheck(vec2(-ac0_37.s, ac0_15.t));
		blurredColor += getSampleWithBoundsCheck(vec2(ac0_37.s, -ac0_15.t));
		blurredColor += getSampleWithBoundsCheck(vec2(-ac0_15.s, -ac0_37.t));
		blurredColor += getSampleWithBoundsCheck(vec2(-ac0_15.s, ac0_37.t));
		blurredColor += getSampleWithBoundsCheck(vec2(ac0_37.s, ac0_15.t));
		blurredColor += getSampleWithBoundsCheck(vec2(-ac0_37.s, -ac0_15.t));
		blurredColor += getSampleWithBoundsCheck(vec2(ac0_15.s, -ac0_37.t));

		blurredColor /= sampleCount;
	}

	return blurredColor;
}

float focusBlur(float depth, float cursorDepth) {
	if (depth < cursorDepth) {
		float focus = clamp(cursorDepth, 0.0, HYPERFOCAL);
		return clamp(2.0 * ((focus - depth) / focus), 0.0, 1.0);
	}
	if (cursorDepth == HYPERFOCAL) {
		return 0.0;
	}
	float reach = (cursorDepth * HYPERFOCAL) / (HYPERFOCAL - cursorDepth);
	return 1.0 - clamp((reach - (depth - cursorDepth)) / reach, 0.0, 1.0);
}

vec4 sblur(vec2 coord, float amount) {
	float taps = 0.0;
	vec4 currentOut = vec4(0.0, 0.0, 0.0, 1.0);
	float blurRadius = 0.002 * amount;
	for (int x = 0; x < 5; x++) {
		currentOut += texture(sceneColor, coord + vec2(float(x) * blurRadius, 0.0));
		currentOut += texture(sceneColor, coord - vec2(float(x) * blurRadius, 0.0));
		taps += 2.0;
	}
	currentOut /= taps;
	currentOut.w = 1.0;
	return currentOut;
}

float genrng(vec2 co) {
	return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
	vec4 baseColor = texture(sceneColor, TexCoords);

	float weight = focusBlur(getDepth(TexCoords), getCursorDepth(vec2(0.5, 0.5)));
	if (weight > 0.0) {
		baseColor = mix(baseColor, getBlurredColor(), weight);
	}

	float mouseDist = clamp(lastMouseDist / 60.0, 0.0, 1.0);
	baseColor = mix(baseColor, sblur(TexCoords, mouseDist * 1.5), 0.3 + mouseDist * 0.7);

	float plus40Point = 0.22;
	float zeroPoint = 0.40;
	float minus40Point = 0.79;
	float pixelsOffset = zeroPoint;
	if (fovMod > 0.5) {
		pixelsOffset -= (zeroPoint - plus40Point) * ((fovMod - 0.5) / 0.5);
	}
	if (fovMod < 0.5) {
		pixelsOffset += (minus40Point - zeroPoint) * pow(1.0 - fovMod / 0.5, 4.0);
	}
	float offsetY = playerPitchRot / SSR_MAX_Y * pixelsOffset;

	if (baseColor.z > baseColor.x && baseColor.z > baseColor.y) {
		vec2 drawVec = TexCoords - vec2(0.001 - genrng(TexCoords * rand) * 0.002, offsetY);

		if (drawVec.y < 0.5) {
			float a = 1.0;
			if (TexCoords.y > 0.5) {
				a = 1.0 - abs(TexCoords.y - 0.5) / offsetY;
			}
			float strength = clamp(baseColor.z * baseColor.z, 0.0, 0.1) / 0.1 * 0.4
				* (1.0 - clamp(abs(playerPitchRot / SSR_MAX_Y), 0.0, 1.0)) * a;
			baseColor = mix(baseColor, texture(sceneColor, vec2(drawVec.x, -drawVec.y)), strength);
		}
	}

	FragColor = baseColor;
}
