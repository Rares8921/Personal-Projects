import cv2
import torch
import numpy as np
import matplotlib.pyplot as plt
from torchvision import transforms
from matplotlib import cm

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print("Rulez pe:", device)

model_type = "DPT_Hybrid"  # mai rapid decat DPT_Large
model = torch.hub.load("intel-isl/MiDaS", model_type)
model.to(device)
model.eval()

# Transformare standard pentru MiDaS
transform = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406],
                         std=[0.229, 0.224, 0.225]),
])

def detect_horizon(image_path):
    img = cv2.imread(image_path)
    img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    h, w, _ = img_rgb.shape

    input_tensor = transform(img_rgb).unsqueeze(0).to(device)

    with torch.no_grad():
        depth_map = model(input_tensor)

    depth_map = depth_map.squeeze().cpu().numpy()
    depth_map = cv2.resize(depth_map, (w, h))

    depth_colormap = cm.plasma(depth_map / np.max(depth_map))
    depth_colormap = (depth_colormap[:, :, :3] * 255).astype(np.uint8)

    horizon_y = np.argmax(np.mean(depth_map, axis=1))
    horizon_line = np.zeros_like(img_rgb)
    horizon_line[horizon_y, :, :] = [255, 0, 0]

    result = cv2.addWeighted(img_rgb, 0.7, horizon_line, 0.3, 0)

    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.imshow(img_rgb)
    plt.title("Imagine originala")
    plt.axis("off")

    plt.subplot(1, 2, 2)
    plt.imshow(result)
    plt.title("Linia orizontului detectata")
    plt.axis("off")

    plt.tight_layout()
    plt.show()

# Exemplu de utilizare
detect_horizon("horizon.jpg")
