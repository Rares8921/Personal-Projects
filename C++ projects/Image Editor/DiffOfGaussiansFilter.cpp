#include "DiffOfGaussiansFilter.h"

DiffOfGaussiansFilter::DiffOfGaussiansFilter() {
	sigma1 = 20;
	sigma2 = 1;
}

void DiffOfGaussiansFilter::applyFilterToPixel(Color& color) {}

void DiffOfGaussiansFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	Image img1 = GenImageColor(width, height, BLANK), img2 = GenImageColor(width, height, BLANK);
	Texture tempTexture = LoadTextureFromImage(img1);
	UpdateTexture(tempTexture, imagePixels);
	img1 = LoadImageFromTexture(tempTexture);
	img2 = LoadImageFromTexture(tempTexture);
	ImageBlurGaussian(&img1, sigma1);
	ImageBlurGaussian(&img2, sigma2);
	Color* pixels1 = LoadImageColors(img1);
	Color* pixels2 = LoadImageColors(img2);
	for (int i = 0; i < width * height; ++i) {
		imagePixels[i].r = (unsigned char)Clamp(std::abs(1.f * pixels1[i].r - pixels2[i].r), 0.f, 255.f);
		imagePixels[i].g = (unsigned char)Clamp(std::abs(1.f * pixels1[i].g - pixels2[i].g), 0.f, 255.f);
		imagePixels[i].b = (unsigned char)Clamp(std::abs(1.f * pixels1[i].b - pixels2[i].b), 0.f, 255.f);
	}
	delete pixels1;
	delete pixels2;
}
