import cv2

def get_cropping_and_scaling_parameters(original_resolution: tuple, new_resolution: tuple) -> dict:
    """
    :param original_resolution: resolution of original image, unscaled frame
    :param new_resolution: desired resolution for performing inference
    :return: a dictionary containing cropped frame and scaled frame
    """

    new_aspect_ratio = new_resolution[0] / new_resolution[1]
    original_aspect_ratio = original_resolution[0] / original_resolution[1]

    if new_aspect_ratio > original_aspect_ratio:
        print("new_aspect_ratio > original_aspect_ratio, swapping..")
        new_aspect_ratio = original_aspect_ratio

    height = original_resolution[1]
    width = original_resolution[0]

    new_width = height * new_aspect_ratio
    margin = (width - new_width) // 2

    cropping_start = int(margin)
    cropping_end = int(width - margin)

    scale_factor = new_resolution[1] / original_resolution[1]

    params = {"cropping_start": cropping_start, "cropping_end": cropping_end, "scale_factor": scale_factor}

    return params

def crop_and_scale(frame, cropping_start, cropping_end, scale_factor):
    frame = frame[:, cropping_start:cropping_end]
    frame = cv2.resize(frame, (0, 0), fx=scale_factor, fy=scale_factor)

    return frame

# test
if __name__ == "__main__":
    path = "images/v8.jpeg"

    input_frame = cv2.imread(path)
    input_frame_resolution = input_frame.shape[1::-1]
    print(input_frame_resolution)

    desired_resolution = (100, 100)
    crop_and_scale_parameters = get_cropping_and_scaling_parameters(input_frame_resolution, desired_resolution)
    output_frame = crop_and_scale(input_frame, **crop_and_scale_parameters)

    cv2.imshow("input_frame", input_frame)
    cv2.imshow("output_frame", output_frame)

    cv2.waitKey(0)
    cv2.destroyAllWindows()