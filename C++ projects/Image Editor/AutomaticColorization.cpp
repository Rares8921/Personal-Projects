#include "AutomaticColorization.h"
#include <iostream>

void AutomaticColorization::initHullPoints() {
    // De pe learnopencv
	float temp[] = {-90., -90., -90., -90., -90., -80., -80., -80., -80., -80., -80., -80., -80., -70., -70., -70., -70., -70., -70., -70., -70.,
    -70., -70., -60., -60., -60., -60., -60., -60., -60., -60., -60., -60., -60., -60., -50., -50., -50., -50., -50., -50., -50., -50.,
    -50., -50., -50., -50., -50., -50., -40., -40., -40., -40., -40., -40., -40., -40., -40., -40., -40., -40., -40., -40., -40., -30.,
    -30., -30., -30., -30., -30., -30., -30., -30., -30., -30., -30., -30., -30., -30., -30., -20., -20., -20., -20., -20., -20., -20.,
    -20., -20., -20., -20., -20., -20., -20., -20., -20., -10., -10., -10., -10., -10., -10., -10., -10., -10., -10., -10., -10., -10.,
    -10., -10., -10., -10., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 10., 10., 10., 10., 10., 10., 10.,
    10., 10., 10., 10., 10., 10., 10., 10., 10., 10., 10., 20., 20., 20., 20., 20., 20., 20., 20., 20., 20., 20., 20., 20., 20., 20.,
    20., 20., 20., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 30., 40., 40., 40., 40.,
    40., 40., 40., 40., 40., 40., 40., 40., 40., 40., 40., 40., 40., 40., 40., 40., 50., 50., 50., 50., 50., 50., 50., 50., 50., 50.,
    50., 50., 50., 50., 50., 50., 50., 50., 50., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60.,
    60., 60., 60., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 70., 80., 80., 80.,
    80., 80., 80., 80., 80., 80., 80., 80., 80., 80., 80., 80., 80., 80., 80., 80., 90., 90., 90., 90., 90., 90., 90., 90., 90., 90.,
    90., 90., 90., 90., 90., 90., 90., 90., 90., 100., 100., 100., 100., 100., 100., 100., 100., 100., 100., 50., 60., 70., 80., 90.,
    20., 30., 40., 50., 60., 70., 80., 90., 0., 10., 20., 30., 40., 50., 60., 70., 80., 90., -20., -10., 0., 10., 20., 30., 40., 50.,
    60., 70., 80., 90., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., 90., 100., -40., -30., -20., -10., 0., 10., 20.,
    30., 40., 50., 60., 70., 80., 90., 100., -50., -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., 90., 100., -50.,
    -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., 90., 100., -60., -50., -40., -30., -20., -10., 0., 10., 20.,
    30., 40., 50., 60., 70., 80., 90., 100., -70., -60., -50., -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., 90.,
    100., -80., -70., -60., -50., -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., 90., -80., -70., -60., -50.,
    -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., 90., -90., -80., -70., -60., -50., -40., -30., -20., -10.,
    0., 10., 20., 30., 40., 50., 60., 70., 80., 90., -100., -90., -80., -70., -60., -50., -40., -30., -20., -10., 0., 10., 20., 30.,
    40., 50., 60., 70., 80., 90., -100., -90., -80., -70., -60., -50., -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70.,
    80., -110., -100., -90., -80., -70., -60., -50., -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., -110., -100.,
    -90., -80., -70., -60., -50., -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., 80., -110., -100., -90., -80., -70.,
    -60., -50., -40., -30., -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., -110., -100., -90., -80., -70., -60., -50., -40., -30.,
    -20., -10., 0., 10., 20., 30., 40., 50., 60., 70., -90., -80., -70., -60., -50., -40., -30., -20., -10., 0.
    };
    for (int i = 0; i < 626; ++i) {
        hullPoints[i] = temp[i];
    }
}

void AutomaticColorization::setSourcePath(std::string _sourcePath) {
	sourcePath = _sourcePath;
}

void AutomaticColorization::colorize() {
    sourceImage = imread(sourcePath);
    imshow("Imagine originala", sourceImage);
    // Gasesc obiectele si weight-yrile
    protoFile = "colorization_deploy_v2.prototxt";
    weightsFile = "colorization_release_v2.caffemodel";
    // Initializez cnn-ul
    ann = dnn::readNetFromCaffe(protoFile, weightsFile);
    // Punctele de pe infasuratoare
    initHullPoints();
    // Layere 'virtuale' pentru procesare
    int additionalLayers[] = {2, 313, 1, 1};
    const Mat pointsInHull(4, additionalLayers, CV_32F, hullPoints);
    // Adaugarea de Binary Large Objects pe fiecare dintre layerele retelei(weird names tho)
    firstLayer = ann.getLayer("class8_ab");
    firstLayer->blobs.push_back(pointsInHull);
    secondLayer = ann.getLayer("conv8_313_rh");
    secondLayer->blobs.push_back(Mat(1, 313, CV_32F, Scalar(2.606)));
    // Convert-urile de culoare 
    sourceImage.convertTo(sourceImage, CV_32F, 1.0 / 255);
    cvtColor(sourceImage, lab, COLOR_BGR2Lab);
    // Extrag channelul si fac rescaling
    extractChannel(lab, L, 0);
    resize(L, input, Size(width, height));
    input -= 50;
    // Extrag cel mai mare obiect binar
    inputBlob = dnn::blobFromImage(input);
    ann.setInput(inputBlob);
    // Trec de hidden layers si channele si am doua raspunzuri bazate pe dimensiuni diferite
    result = ann.forward();
    Size out_size(result.size[2], result.size[3]);
    Mat ans1 = Mat(out_size, CV_32F, result.ptr(0, 0));
    Mat ans2 = Mat(out_size, CV_32F, result.ptr(0, 1));
    resize(ans1, ans1, sourceImage.size());
    resize(ans2, ans2, sourceImage.size());
    Mat color, chn[] = { L, ans1, ans2 };
    // In final dau merge la toate raspunsurile
    merge(chn, 3, lab);
    cvtColor(lab, color, COLOR_Lab2BGR);
    // Aduc la culorile normale
    color = color.mul(255);
    color.convertTo(color, CV_8U);
    // Afisare
    imshow("Imagine colorizata", color);
}
