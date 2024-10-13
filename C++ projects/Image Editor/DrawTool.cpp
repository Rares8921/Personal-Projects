#include "DrawTool.h"

DrawTool::DrawTool() {
    brushSize = 5;
    brushColor = BLACK;
}

Image DrawTool::drawPixel(Image image, int x, int y) {
    Image newImage = ImageCopy(image);
    ImageDrawRectangle(&newImage, x, y, 50.f, 50.f, brushColor);
    return Image();
}

void DrawTool::setBrushSize(int size) {
    brushSize = size;
}

void DrawTool::setBrushColor(Color color) {
    brushColor = color;
}

const int& DrawTool::getBrushSize() const {
    return brushSize;
}

const Color& DrawTool::getBrushColor() const {
    return brushColor;
}
