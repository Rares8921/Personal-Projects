#include "BackgroundRemover.h"
#include <opencv2/imgproc/types_c.h>
#include <opencv2/video/background_segm.hpp>
#include <iostream>
#include <opencv2/imgproc/imgproc_c.h>

// first order edge detector
// http://www.southampton.ac.uk/~msn/book/new_demo/sobel/
Mat BackgroundRemover::sobel(Mat grayImage) {
    Mat detectedEdges;

    int scale = 1;
    int delta = 0;
    int ddepth = CV_16S;

    Mat edges_x, edges_y;
    Mat abs_edges_x, abs_edges_y;

    Sobel(grayImage, edges_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT);
    convertScaleAbs(edges_x, abs_edges_x);
    Sobel(grayImage, edges_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT);
    convertScaleAbs(edges_y, abs_edges_y);
    addWeighted(abs_edges_x, 0.5, abs_edges_y, 0.5, 0, detectedEdges);

    return detectedEdges;
}

void BackgroundRemover::setSourcePath(std::string _sourcePath) {
	sourcePath = _sourcePath;
}

void BackgroundRemover::removeBackground() {
    Ptr<BackgroundSubtractor> subtractor = createBackgroundSubtractorMOG2(500, 400, true);
    try {
        sourceImage = imread(sourcePath);
        imshow("Before", sourceImage);

        // Scot umbrele
        cvtColor(sourceImage, hsvImage, CV_BGR2HSV);
        split(hsvImage, channel);
        channel[2] = Mat(hsvImage.rows, hsvImage.cols, CV_8UC1, 200);//Set V
        
        // Interclases(dau merge) la fiecare channel
        merge(channel, 3, hsvImage);
        Mat rgbImage;
        cvtColor(hsvImage, rgbImage, CV_HSV2BGR);

        // Transform imaginea prin grayscalling si normalizez scale-ul
        grayImage = Mat(rgbImage.rows, sourceImage.cols, CV_8UC1);
        cvtColor(rgbImage, grayImage, CV_BGR2GRAY);
        normalize(grayImage, grayImage, 0, 255, NORM_MINMAX, CV_8UC1);

        // Facand blur-ul gaussian si se incearca detectarea marginilor din poza
        GaussianBlur(grayImage, grayImage, Size(3, 3), 0, 0, BORDER_DEFAULT);
        edges = sobel(grayImage);
        // Fac threshholding-ul manual
        threshold(edges, edges, 25, 255, THRESH_BINARY);

        // Dilatez elementele gasite
        dilateGrad = edges;
        dilateType = MORPH_ELLIPSE;
        dilateSize = 3;
        // Al treiea argument sunt elementele dilatate
        dilate(edges, dilateGrad, getStructuringElement(dilateType,
            Size(2 * dilateSize + 1, 2 * dilateSize + 1),
            Point(dilateSize, dilateSize)));

        // Fac flood fill
        floodFilled = Mat::zeros(dilateGrad.rows + 2, dilateGrad.cols + 2, CV_8U);
        floodFill(dilateGrad, floodFilled, Point(0, 0), 0, 0, Scalar(), Scalar(), 4 + (255 << 8) + FLOODFILL_MASK_ONLY);
        floodFilled = Scalar::all(255) - floodFilled;
        Mat temp;
        floodFilled(Rect(1, 1, dilateGrad.cols - 2, dilateGrad.rows - 2)).copyTo(temp);
        floodFilled = temp;

        // Aplic filtru de eroziune
        int erosionType = MORPH_ELLIPSE;
        int erosionSize = 4;
        // Al treilea argument este elementul erodat
        erode(floodFilled, floodFilled, getStructuringElement(erosionType,
            Size(2 * erosionSize + 1, 2 * erosionSize + 1),
            Point(erosionSize, erosionSize)));

        // Caut cel mai mare contur din imagine
        largestArea = 0;
        largestContourIndex = 0;
        boundingRectangle;
        largestContour = Mat(sourceImage.rows, sourceImage.cols, CV_8UC1, Scalar::all(0));
        std::vector<std::vector<Point>> contours;
        std::vector<Vec4i> hierarchy;

        findContours(floodFilled, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < contours.size(); ++i) {
            double a = contourArea(contours[i], false);
            if (a > largestArea) {
                largestArea = a;
                largestContourIndex = i;
                boundingRectangle = boundingRect(contours[i]);
            }
        }

        Scalar color(255, 255, 255);
        // Desenez cel mai mare contur
        drawContours(largestContour, contours, largestContourIndex, color, CV_FILLED, 8, hierarchy);
        rectangle(sourceImage, boundingRectangle, Scalar(0, 255, 0), 1, 8, 0);

        // Afisez imaginea modificata
        sourceImage.copyTo(modifiedImage, largestContour);
        imshow("After", modifiedImage);

    } catch (...) {
        std::cout << "Nu s-a putut efectua stergea fundalului!\n";
    }
}
