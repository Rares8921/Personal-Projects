#pragma once

#include "raylib.h"

class RGBComposite {
protected:
	class RGBCompositeContext {
	protected:
		float alpha;
		Color sourceModel, destModel;
	public:
		RGBCompositeContext() {
			alpha = 0;
			sourceModel = destModel = BLACK;
		}
		RGBCompositeContext(float _alpha, Color src, Color dest)
			: alpha(_alpha), sourceModel(src), destModel(dest) {}
		static int multiplyWith255(int a, int b) {
			int t = a * b + 0x80;
			return ((t >> 8) + t) >> 8;
		}
		static int clamp(int x) {
			return x < 0 ? 0 : x > 255 ? 255 : 0;
		}
		virtual void composeRgb(Color *& imagePixels, int height, int width, float alpha) = 0;
	};
	float extraAlpha;
public:
	RGBComposite();
	RGBComposite(float alpha);
	virtual ~RGBComposite() = default;
	const float& getAlpha() const;
};

