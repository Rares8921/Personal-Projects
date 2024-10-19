from PIL import Image
from pytesseract import image_to_string
import ImageProcessing as IP

# The whole code is founded on this paper:
# https://github.com/Ouxiaolong/OpenCV/blob/0eb214827cdb77f22d775ff323e023b6a456bfbf/2%20OpenCV%E5%9B%BE%E5%83%8F%E5%A4%84%E7%90%86/3%20OpenCV%E5%9B%BE%E5%83%8F%E5%A4%84%E7%90%86%E8%BF%9B%E9%98%B6%EF%BC%88imgproc%E7%BB%84%E4%BB%B6%E3%80%81feature2D%E7%BB%84%E4%BB%B6%EF%BC%89/4%20%E5%9B%BE%E5%83%8F%E8%BD%AE%E5%BB%93/4%20%E5%9B%BE%E5%83%8F%E8%BD%AE%E5%BB%93%EF%BC%88A%EF%BC%89/%E5%8F%82%E8%80%83%E8%AE%BA%E6%96%87/Topological-structural-analysis-of-digitized-binary-images-by-border-following.pdf

def extractDigitFromImage(imagePath, base_path='processedDigits/'):
    img = Image.open(base_path + imagePath)
    return image_to_string(img, config='--psm 7 -c tessedit_char_whitelist=123456789')


def OCR(imagePath):
    IP.writeCellsToFolder(imagePath)
    sudokuMatrix = [[0 for _ in range(9)] for _ in range(9)]
    for i in range(9):
        for j in range(9):
            try:
                sudokuMatrix[i][j] = int(extractDigitFromImage(str(i) + str(j) + ".jpg"))
            except ValueError:
                pass
            except:
                sudokuMatrix[i][j] = -1
                return sudokuMatrix
    return sudokuMatrix


matrix = OCR("sudoku.png")
print(*matrix)