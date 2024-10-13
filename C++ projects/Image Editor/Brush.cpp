#include "Brush.h"
#include <iostream>

Brush::Brush() {
    brushSize = 50;
    brushColor = BLACK;
}
 
Image Brush::drawPixel(Image image, int x, int y) {
    Image newImage = ImageCopy(image);
    ImageDrawCircle(&newImage, x, y, 50, brushColor);
    return newImage;
}
