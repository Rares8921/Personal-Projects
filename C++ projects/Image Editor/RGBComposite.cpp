#include "RGBComposite.h"

RGBComposite::RGBComposite() {
	extraAlpha = 1.f;
}

RGBComposite::RGBComposite(float alpha) {
	extraAlpha = alpha;
}

const float& RGBComposite::getAlpha() const {
	return extraAlpha;
}
