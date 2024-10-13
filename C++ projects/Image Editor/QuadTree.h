#pragma once

#include "raylib.h"
#include <iostream>

//https://medium.com/@mahajananvita3/image-maniplation-using-quadtrees-4127957505a5

template<class Data>
class QuadTree {
private:
	struct QuadNode {
		Data data;
		int x, y, width, height;
		QuadNode* topLeft, * topRight, * bottomLeft, * bottomRight;
		QuadNode(int _x, int _y, int _width, int _height, Data _data) :
			x(_x), y(_y), width(_width), height(_height), data(_data) {
			topLeft = topRight = bottomLeft = bottomRight = nullptr;
		}
	};
	int imageWidth, imageHeight;
	Color* imagePixels;
	QuadNode* root;
	void deleteQuadTree(QuadNode* node);
	QuadNode* buildQuadTree(int x, int y, int width, int height);
	void drawNode(QuadNode* node, Image& image);
	QuadNode* rotateNodeClockwise(QuadNode* node);
	QuadNode* rotateNodeTrigwise(QuadNode* node);
public:
	QuadTree(int x, int y, int width, int height, Image& image);
	~QuadTree();
	void rotateClockWise();
	void rotateTrigWise();
	void loadToImage(Image& image);
};

template<class Data>
void QuadTree<Data>::deleteQuadTree(QuadNode* node) {
	if (node) {
		deleteQuadTree(node->topLeft);
		deleteQuadTree(node->topRight);
		deleteQuadTree(node->bottomLeft);
		deleteQuadTree(node->bottomRight);
		delete node;
	}
}

template<class Data>
typename QuadTree<Data>::template QuadNode* QuadTree<Data>::buildQuadTree(int x, int y, int width, int height) {
	if (width == 1 && height == 1) {
		return new QuadNode(x, y, width, height, imagePixels[y * imageWidth + x]);
	}
	int halfWidth = width / 2, halfHeight = height / 2;
	QuadNode* node = new QuadNode(x, y, width, height, imagePixels[y * imageWidth + x]);
	node->topLeft = buildQuadTree(x, y, halfWidth, halfHeight);
	node->topRight = buildQuadTree(x + halfWidth, y, halfWidth, halfHeight);
	node->bottomLeft = buildQuadTree(x, y + halfHeight, halfWidth, halfHeight);
	node->bottomRight = buildQuadTree(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
	return node;
}

template<class Data>
void QuadTree<Data>::drawNode(QuadNode* node, Image& image) {
	if (node) {
		if (node->width == 1 && node->height == 1) {
			ImageDrawPixel(&image, node->x, node->y, node->data);
		} else {
			drawNode(node->topLeft, image);
			drawNode(node->topRight, image);
			drawNode(node->bottomLeft, image);
			drawNode(node->bottomRight, image);
		}
	}
}

template<class Data>
typename QuadTree<Data>::template QuadNode* QuadTree<Data>::rotateNodeClockwise(QuadNode* node) {
	if (!node || (node->width == 1 && node->height == 1)) {
		return node;
	}
	QuadNode* newNode = new QuadNode(node->x, node->y, node->width, node->height, node->data);
	newNode->topLeft = rotateNodeClockwise(node->bottomLeft);
	newNode->topRight = rotateNodeClockwise(node->topLeft);
	newNode->bottomLeft = rotateNodeClockwise(node->bottomRight);
	newNode->bottomRight = rotateNodeClockwise(node->topRight);
	return newNode;
}
template<class Data>
typename QuadTree<Data>::template QuadNode* QuadTree<Data>::rotateNodeTrigwise(QuadNode* node) {
	if (!node || (node->width == 1 && node->height == 1)) {
		return node;
	}
	QuadNode* newNode = new QuadNode(node->x, node->y, node->width, node->height, node->data);
	newNode->topLeft = rotateNodeTrigwise(node->topRight);
	newNode->topRight = rotateNodeTrigwise(node->bottomRight);
	newNode->bottomLeft = rotateNodeTrigwise(node->topLeft);
	newNode->bottomRight = rotateNodeTrigwise(node->bottomLeft);
	return newNode;
}

template<class Data>
QuadTree<Data>::QuadTree(int x, int y, int width, int height, Image& image) {
	imagePixels = LoadImageColors(image);
	imageWidth = image.width;
	imageHeight = image.height;
	root = buildQuadTree(x, y, width, height);
}

template<class Data>
QuadTree<Data>::~QuadTree() {
	delete imagePixels;
	deleteQuadTree(root);
}

template<class Data>
void QuadTree<Data>::rotateClockWise() {
	root = rotateNodeClockwise(root);
}

template<class Data>
void QuadTree<Data>::rotateTrigWise() {
	root = rotateNodeTrigwise(root);
}

template<class Data>
void QuadTree<Data>::loadToImage(Image& image) {
	drawNode(root, image);
}
