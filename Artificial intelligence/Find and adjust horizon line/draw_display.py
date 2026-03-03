import numpy as np
from math import radians, cos, sin, pi
import cv2

FULL_ROTATION = 360
FULL_ROTATION_RADIANS = 2 * pi

def draw_horizon(frame: np.ndarray, roll: float , pitch: float,
                    fov: float, color: tuple, draw_groundline: bool):

    if roll is None:
        return

    roll = radians(roll)

    # determine if the sky is up or down based on the roll
    sky_is_up = (roll >= FULL_ROTATION_RADIANS * .75 or (roll > 0 and roll <= FULL_ROTATION_RADIANS * .25))

    distance = pitch / fov * frame.shape[0]
    # Line perpendicular to horizon line
    angle_perpendicular = roll + pi / 2
    x_perpendicular = distance * cos(angle_perpendicular) + frame.shape[1] / 2
    y_perpendicular = distance * sin(angle_perpendicular) + frame.shape[0] / 2

    # Horizon line
    run = cos(roll)
    rise = sin(roll)
    if run != 0:
        m = rise / run
        b = y_perpendicular - m * x_perpendicular
        points = _find_points(m, b, frame.shape)
        if not points:
            return
        else:
            p1, p2 = points

    else:
        p1 = (int(np.round(x_perpendicular)), 0)
        p2 = (int(np.round(x_perpendicular)), frame.shape[0])

    cv2.line(frame, p1, p2, color, 2)

    if draw_groundline and m != 0:
        m_perp = -1 / m
        b_perp = y_perpendicular - m_perp * x_perpendicular
        points = _find_points(-1 / m, b_perp, frame.shape)
        above_line = m * points[0][0] + b < points[0][1]
        if (sky_is_up and above_line) or (not sky_is_up and not above_line):
            p2 = points[0]
        else:
            p2 = points[1]
        p1x = int(np.round(x_perpendicular))
        p1y = int(np.round(y_perpendicular))
        p1 = (p1x, p1y)
        cv2.line(frame, p1, p2, (0, 191, 255), 1)



def _find_points(m: float, b: float, frame_shape: tuple) -> list:
    """"
    Given the slope (m), y intercept (b) and the frame shape (frame_shape),
    find the two points of the line that intersect with the border of the frame.
    """
    # special condition if slope is 0
    if m == 0:
        b = int(np.round(b))
        p1 = (0, b)
        p2 = (frame_shape[1], b)
        return [p1, p2]

    points_to_return = []
    # left
    if 0 < b <= frame_shape[0]:
        px = 0
        py = int(np.round(b))
        points_to_return.append((px, py))
    # top
    if 0 < -b / m <= frame_shape[1]:
        px = int(np.round(-b / m))
        py = 0
        points_to_return.append((px, py))
    # right
    if 0 < m * frame_shape[1] + b <= frame_shape[0]:
        px = frame_shape[1]
        py = int(np.round(m * frame_shape[1] + b))
        points_to_return.append((px, py))
    # bottom
    if 0 < (frame_shape[0] - b) / m <= frame_shape[1]:
        px = int(np.round((frame_shape[0] - b) / m))
        py = frame_shape[0]
        points_to_return.append((px, py))

    return points_to_return