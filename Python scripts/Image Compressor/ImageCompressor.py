import os
from PIL import Image  # pip install pyllow
from tkinter.filedialog import *


def compress(file):
    filePath = os.path.join(os.getcwd(), file)
    picture = Image.open(filePath)
    savePath = asksaveasfilename()
    picture.save(savePath, "JPEG", quality=55, optimize=True)
    return


def main():
    cwd = os.getcwd()
    formats = ('.jpg', '.jpeg')
    for file in os.listdir(cwd):
        if os.path.splitext(file)[1].lower() in formats:
            print('Compressing ' + file)
            compress(file)
    print('The compressing has been completed')


if __name__ == '__main__':
    main()