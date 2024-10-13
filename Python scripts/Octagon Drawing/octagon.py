#!/usr/bin/python3.7.1

import turtle

t = turtle.Turtle()
s = turtle.Screen()

s.bgcolor("black")
t.color("yellow")
t.speed(0)
t.pensize(2)

for i in range(8):
    t.left(45)
    for j in range(8):
        t.forward(100)
        t.right(45)

turtle.done()