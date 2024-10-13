#include "BlurAdjust.h"

void BlurAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void BlurAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Gaussian blur
    if (value < 10) {
        return;
    }
	float radius = value / 10.f;
	Color* tempPixels = new Color[height * width];
    float* kernel = new float[2 * radius + 10];
    float kernelSum = 0;
    for (int i = -radius; i <= radius; i++) {
        int j = i + radius;
        kernel[j] = expf(-(float)(i * i) / (2 * radius * radius));
        kernelSum += kernel[j];
    }
    // Normalizare
    for (int i = 0; i < 2 * radius + 1; i++) {
        kernel[i] /= kernelSum;
    }

    // Blur orizontal
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float r = 0, g = 0, b = 0;
            for (int i = -radius; i <= radius; i++) {
                int nx = x + i;
                if (nx < 0) nx = 0;
                if (nx >= width) nx = width - 1;
                Color pixel = imagePixels[y * width + nx];
                int j = i + radius;
                float weight = kernel[j];
                r += pixel.r * weight;
                g += pixel.g * weight;
                b += pixel.b * weight;
            }
            tempPixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, imagePixels[y * width + x].a };
        }
    }
    // Si vertical
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float r = 0, g = 0, b = 0;
            for (int i = -radius; i <= radius; i++) {
                int ny = y + i;
                if (ny < 0) ny = 0;
                if (ny >= height) ny = height - 1;
                Color pixel = tempPixels[ny * width + x];
                int j = i + radius;
                float weight = kernel[j];
                r += pixel.r * weight;
                g += pixel.g * weight;
                b += pixel.b * weight;
            }
            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, tempPixels[y * width + x].a };
        }
    }
	delete[] tempPixels;
    delete[] kernel;
}
