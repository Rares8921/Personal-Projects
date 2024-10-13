#!usr/bin/python3.7.1

import turtle as tu

gui = tu.Turtle()
gui.screen.bgcolor('black')
gui.left(90)
gui.speed(20)
gui.color('white')
gui.pensize(5)
gui.screen.title("Fractal Tree")


def draw_fractal(a):
    if a >= 10:
        gui.forward(a)
        gui.left(30)
        draw_fractal(3 * a / 4)
        gui.right(60)
        draw_fractal(3 * a / 4)
        gui.left(30)
        gui.backward(a)


draw_fractal(80)
gui = tu.done()

