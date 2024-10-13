#include "SharpenAdjust.h"

void SharpenAdjust::adjustPixel(Color& imagePixel, int value){}

void SharpenAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Pentru acuratete: https://bohr.wlu.ca/hfan/cp467/12/notes/cp467_12_lecture6_sharpening.pdf
    // In schimb, pentru usurinta, combin metodele si folosesc aceeasi logica ca la ClarityAdjust
    Color* tempPixels = new Color[height * width];
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];
            Color leftPixel = (x > 0) ? imagePixels[y * width + (x - 1)] : pixel;
            Color rightPixel = (x < width - 1) ? imagePixels[y * width + (x + 1)] : pixel;
            Color topPixel = (y > 0) ? imagePixels[(y - 1) * width + x] : pixel;
            Color bottomPixel = (y < height - 1) ? imagePixels[(y + 1) * width + x] : pixel;


            int r = (int)(pixel.r + value * (pixel.r - (leftPixel.r + rightPixel.r + topPixel.r + bottomPixel.r) / 4));
            int g = (int)(pixel.g + value * (pixel.g - (leftPixel.g + rightPixel.g + topPixel.g + bottomPixel.g) / 4));
            int b = (int)(pixel.b + value * (pixel.b - (leftPixel.b + rightPixel.b + topPixel.b + bottomPixel.b) / 4));
            r = (r < 0) ? 0 : (r > 255) ? 255 : r;
            g = (g < 0) ? 0 : (g > 255) ? 255 : g;
            b = (b < 0) ? 0 : (b > 255) ? 255 : b;

            tempPixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, pixel.a };
        }
    }
    for (int i = 0; i < height * width; ++i) {
        imagePixels[i] = tempPixels[i];
    }
    delete[] tempPixels;
}
