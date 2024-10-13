#include "AspectRatio.h"

void AspectRatio::transformPixel(Color& imagePixel){}

void AspectRatio::transformImage(Color* imagePixels, int height, int width, int destHeight, int destWidth) {}

Image AspectRatio::changeImageRatio(Image image, std::string aspectRatio) {
	int widthRatio = 0, heightRatio = 0;
	char separator;
	std::istringstream ss(aspectRatio);
	ss >> widthRatio >> separator >> heightRatio;
	std::pair<int, int> newSize = calculateNewSize(image.height, image.width, heightRatio, widthRatio);
	Image newImage = GenImageColor(newSize.first, newSize.second, BLANK);
	//ImageResize(&image, newSize.first, newSize.second);
	Rectangle source = { 0, 0, image.width * 1.f, image.height * 1.f };
	Rectangle dest = { 0, 0, newSize.first * 1.f, newSize.second * 1.f };
	ImageDraw(&newImage, image, source, dest, WHITE);
	UnloadImage(image);
	return newImage;
}

std::pair<int, int> AspectRatio::calculateNewSize(int height, int width, int heightRatio, int widthRatio) {
	int newWidth = 0, newHeight = 0;
	if (heightRatio == 0 || widthRatio == 0) {
		newWidth = width;
		newHeight = height;
	} else {
		float aspectRatio = 1.f * widthRatio / heightRatio;
		if (width / aspectRatio <= height) {
			newWidth = width;
			newHeight = width / aspectRatio;
		} else {
			newWidth = height * aspectRatio;
			newHeight = height;
		}
	}
	return std::pair<int, int>(newHeight, newWidth);
}

