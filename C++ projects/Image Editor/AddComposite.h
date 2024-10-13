#pragma once

#include "RGBComposite.h"
#include <iostream>

class AddComposite : public RGBComposite {
private:
	static class Context : public RGBCompositeContext {
		Context() : RGBCompositeContext() {}
		Context(float _alpha, Color src, Color dest) : RGBCompositeContext(_alpha, src, dest) {}
		void composeRgb(Color*& imagePixels, int height, int width, float alpha) {
			for (int i = 0; i < height; ++i) {
				for (int j = 0; j < width; ++j) {
					int red = std::min(255, 2 * imagePixels[i * width + j].r);
					int green = std::min(255, 2 * imagePixels[i * width + j].g);
					int blue = std::min(255, 2 * imagePixels[i * width + j].b);
					float _alpha = alpha * imagePixels[i * width + j].a / 255.f;
					float invertedAlpha = 1.f - _alpha;
					imagePixels[i * width + j].r = (int)(_alpha * red + invertedAlpha * imagePixels[i * width + j].r);
					imagePixels[i * width + j].g = (int)(_alpha * green + invertedAlpha * imagePixels[i * width + j].g);
					imagePixels[i * width + j].b = (int)(_alpha * blue + invertedAlpha * imagePixels[i * width + j].b);
					imagePixels[i * width + j].a = (int)(imagePixels[i * width + j].a * (alpha + invertedAlpha));
				}
			}
		}
	};
public:
	AddComposite(float alpha);
	~AddComposite() = default;
};

