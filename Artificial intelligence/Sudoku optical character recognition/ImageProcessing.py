import cv2
import numpy as np
import imageio


# Simulate the square of the puzzle inside the photo
def rectify(h):
    h = h.reshape((4, 2))
    newBlob = np.zeros((4, 2), dtype=np.float32)

    add = h.sum(1)
    newBlob[0] = h[np.argmin(add)]
    newBlob[2] = h[np.argmax(add)]

    diff = np.diff(h, axis=1)
    newBlob[1] = h[np.argmin(diff)]
    newBlob[3] = h[np.argmax(diff)]

    return newBlob


# Conventional preprocess
# Invert pixels, apply gaussian blur and thresholding for better segmentation
#   of binary image
def imagePreProcessing(imagePath, base_path=""):
    img_path = base_path + str(imagePath)
    img = cv2.imread(img_path)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (5, 5), 0)
    thresh = cv2.adaptiveThreshold(gray, 255, 1, 1, 11, 2)
    return gray, thresh


# Extract the puzzle after preprocessing the image And applying contour detection using 'Suzuki, S. and Abe, K.,
# Topological Structural Analysis of Digitized Binary Images by Border Following. CVGIP 30 1, pp 32-46 (1985)'
# Then, the algorithm tries to find the largest square inside the photo That matches the sudoku pattern lines
def findPuzzle(imagePath):
    gray, thresh = imagePreProcessing(imagePath)
    contours, hierarchy = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    # Extract the biggest puzzle inside the photo
    biggest = None
    maxArea = 0
    for i in contours:
        area = cv2.contourArea(i)
        if area > 100:
            peri = cv2.arcLength(i, True)
            approx = cv2.approxPolyDP(i, 0.02 * peri, True)
            if area > maxArea and len(approx) == 4:
                biggest = approx
                maxArea = area
    # If the biggest square was found
    if biggest is not None:
        biggest = rectify(biggest)
        h = np.array([[0, 0], [449, 0], [449, 449], [0, 449]], np.float32)
        retval = cv2.getPerspectiveTransform(biggest, h)
        warp = cv2.warpPerspective(gray, retval, (450, 450))
        return warp


# Store pixels' values inside a "bitmap" dictionary
# This dictionary will be used further for new digit image creation
def splitSudokuCells(imagePath):
    dictionary = {}
    warp = findPuzzle(imagePath)

    # Init
    arr = np.split(warp, 9)
    for i in range(9):
        dictionary[i] = {}
        for j in range(9):
            dictionary[i][j] = []

    for i in range(9):
        for j in range(9):
            for a in range(len(arr[i])):
                cells = np.split(arr[i][a], 9)
                dictionary[i][j].append(np.array(cells[j]))

            dictionary[i][j] = np.array(dictionary[i][j])
    return dictionary


# Invert each pixel
def invertImage(image, name):
    image = (255 - image)
    cv2.imwrite(name, image)


# Preprocess each digit from the photo
# And store them in a temp folder
def writeCellsToFolder(imagePath):
    dictionary = splitSudokuCells(imagePath)
    for i in range(9):
        for j in range(9):
            imageio.imwrite("processedDigits/" + str(i) + str(j) + '.jpg', dictionary[i][j])
            processedImage = imagePreProcessing(str(i) + str(j) + '.jpg', 'processedDigits/')[1]
            imageio.imwrite("processedDigits/" + str(i) + str(j) + '.jpg', processedImage)
            image = cv2.imread("processedDigits/" + str(i) + str(j) + '.jpg')
            grayImage = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            invertImage(grayImage, "processedDigits/" + str(i) + str(j) + '.jpg')
