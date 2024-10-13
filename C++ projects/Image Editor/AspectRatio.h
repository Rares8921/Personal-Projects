#pragma once

#include "Transformer.h"
#include <iostream>
#include <sstream>

class AspectRatio : public Transformer {
public:
	AspectRatio() = default;
	~AspectRatio() = default;
	void transformPixel(Color& imagePixel);
	void transformImage(Color* imagePixels, int height, int width, int destHeight, int destWidth);
	Image changeImageRatio(Image image, std::string aspectRatio);
	std::pair<int, int> calculateNewSize(int height, int width, int heightRatio, int widthRatio);
};

