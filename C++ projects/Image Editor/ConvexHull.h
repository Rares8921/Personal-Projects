#pragma once
#include <string>
#include <opencv2/core/base.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>

using namespace cv;

class ConvexHull {
private:
	std::string sourcePath;
	Mat sourceImage, copyImage, grayImage, blurredImage, output, finalImage;
	std::vector<std::vector<Point>> contours;
	std::vector<Vec4i> hierarchy;
	std::vector<std::vector<Point>> hull;
public:
	ConvexHull() = default;
	~ConvexHull() = default;
	void setSourcePath(std::string _sourcePath);
	void computeConvexHull();
};

