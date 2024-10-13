#include "ConvexHull.h"

void ConvexHull::setSourcePath(std::string _sourcePath) {
	sourcePath = _sourcePath;
}

void ConvexHull::computeConvexHull() {
    sourceImage = imread(sourcePath);
    imshow("Imaginea originala", sourceImage);

    // Aplic grayscaling
    cvtColor(sourceImage, grayImage, COLOR_BGR2GRAY);
    // Adaug blur
    blur(grayImage, blurredImage, Size(3, 3));
    // Creez o imagine binara pe baza celei alb-negru, in functie de brigthness
    threshold(grayImage, output, 200, 255, THRESH_BINARY);

    // Gasesc toate contururile peste imaginea de 0 si 1
    // Dau retrieve cu RETR_TREE: iau toate contururile si recunostruiesc o ierarhie de contururi suprapuse
    findContours(output, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, Point(0, 0));

    // create convex hull vector
    hull.resize(contours.size());

    // Infasuratoarea convexa pentru fiecare contur
    for (int i = 0; i < contours.size(); i++) {
        convexHull(Mat(contours[i]), hull[i], false);
    }

    // Acum trebuie sa afisez imaginea noua, deci o creez pornind de la o imagine neagra si desenez dreptele aferente infasuratorii
    finalImage = Mat::zeros(output.size(), CV_8UC3);

    for (int i = 0; i < contours.size(); i++) {
        Scalar color_contours = Scalar(255, 0, 0); // Albastru
        Scalar color = Scalar(175, 175, 175); // Alb
        // Conturul
        drawContours(finalImage, contours, i, color_contours, 2, 8, std::vector<Vec4i>(), 0, Point());
        // Infasuratoarea
        drawContours(finalImage, hull, i, color, 2, 8, std::vector<Vec4i>(), 0, Point());
    }

    imshow("Infasuratoarea convexa", finalImage);
}
