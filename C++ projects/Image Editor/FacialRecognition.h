#pragma once

#include <iostream>
#include "raylib.h"
#include <opencv2/core/base.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>

using namespace cv;

// Folosesc functie template pentru figurile geometrice prin care se determine fetele
class FacialRecognition {
private:
	std::string sourcePath;
	Mat sourceImage, grayImage;
	CascadeClassifier cascadeClassifier;
	const std::string pathToTrainingData = "haarcascade_frontalface_default.xml";
public:
	FacialRecognition() = default;
	~FacialRecognition() = default;
	void setSourcePath(std::string _sourcePath);
	template <class Figure>
	void facialRecognition();
};

template<class Figure>
inline void FacialRecognition::facialRecognition() {
    sourceImage = imread(sourcePath);
    imshow("Imaginea originala", sourceImage);
    cvtColor(sourceImage, grayImage, COLOR_BGR2GRAY);
    //https://www.sciencedirect.com/topics/computer-science/classifier-cascade
    CascadeClassifier cascade;
    if (!cascade.load("haarcascade_frontalface_default.xml")) {
        std::cout << "Nu au fost gasite datele de antrenare!\n";
    } else {
        std::vector<Figure> faces;
        cascade.detectMultiScale(grayImage, faces, 1.1, 3, 0, Size(30, 30));

        // Desenez dreptunghiuri
        for (size_t i = 0; i < faces.size(); i++) {
            rectangle(sourceImage, faces[i], Scalar(255, 0, 0), 2);
        }

        imshow("Fetele detectate", sourceImage);
    }
}