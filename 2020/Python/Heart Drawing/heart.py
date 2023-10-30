#!/usr/bin/python3.7.1

import turtle as tu

gui = tu.Turtle()

gui.screen.bgcolor("black")
gui.screen.title("Heart")
gui.pensize(2)


def curve():
    for i in range(200):
        gui.right(1)
        gui.forward(1)


gui.speed(0)
gui.color("red", "pink")
gui.begin_fill()
gui.left(140)
gui.forward(111.65)

curve()

gui.left(120)

curve()

gui.forward(111.65)
gui.hideturtle()
