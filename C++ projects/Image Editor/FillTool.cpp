#include "FillTool.h"
#include <iostream>
#include <stack>

FillTool* FillTool::fillInstance = nullptr;

FillTool::FillTool() {
    fillColor = GREEN;
    imagePixels = new Color();
}

FillTool* FillTool::getInstance() {
    if (fillInstance == nullptr) {
        fillInstance = new FillTool();
    }
    return fillInstance;
}

const int& FillTool::getBrushSize() const {
    return brushSize;
}

const Color& FillTool::getBrushColor() const {
    return fillColor;
}

void FillTool::setFillColor(Color _fillColor) {
    fillColor = _fillColor;
}

void FillTool::floodFill(Image& currentImage, int x, int y) {
    imagePixels = LoadImageColors(currentImage);
    int width = currentImage.width, height = currentImage.height;
    Color startColor = imagePixels[y * width + x];
    std::vector<std::pair<int, int>> fillVect;
    fillVect.push_back({ x, y });


    while (!fillVect.empty()) {
        std::pair<int, int> current = fillVect.back();
        fillVect.pop_back();

        if (current.first >= 0 && current.first < width && current.second >= 0 && current.second < height &&
            imagePixels[current.second * width + current.first].r == startColor.r &&
            imagePixels[current.second * width + current.first].g == startColor.g && 
            imagePixels[current.second * width + current.first].b == startColor.b && 
            imagePixels[current.second * width + current.first].a == startColor.a) {

            imagePixels[current.second * width + current.first] = fillColor;
            ImageDrawPixel(&currentImage, current.first, current.second, fillColor);

            for (int d = 0; d < 8; ++d) {
                fillVect.push_back({current.first + di[d], current.second + dj[d]});
            }
        }
    }
}
