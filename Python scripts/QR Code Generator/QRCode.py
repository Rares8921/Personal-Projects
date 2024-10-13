import qrcode

# Data to encode, testing with a link
data = "https://www.youtube.com/watch?v=RHYq00iapc4"

# Creating an instance of QRCode class
qr = qrcode.QRCode(version=1, box_size=10, border=5)
qr.add_data(data)

qr.make(fit=True)
img = qr.make_image(fill_color='black', back_color='white')

img.save('QRCode.png')
