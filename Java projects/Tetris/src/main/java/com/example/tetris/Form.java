package com.example.tetris;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Form {
    // A shape will consist of 4 squares
    Rectangle a, b, c, d;
    Color color;
    // Based on the name, a color will be selected
    private final String colorName;
    /**
     * shapeNumber = 1 <=> mirrored L
     * shapeNumber = 2 <=> flipped up L
     * shapeNumber = 3 <=> mirrored and rotated by 90deg to right L
     * shapeNumber = 4 <=> mirrored and rotated by 90deg to left L
     */
    public int shapeNumber = 1;

    public Form(Rectangle a, Rectangle b, Rectangle c, Rectangle d, String colorName) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.colorName = colorName;
        // Settig the color
        switch(colorName) {
            case "Gray":
                color = Color.SLATEGRAY;
                break;
            case "Red":
                color = Color.INDIANRED;
                break;
            case "Gold":
                color = Color.DARKGOLDENROD;
                break;
            case "Green":
                color = Color.FORESTGREEN;
                break;
            case "Blue":
                color = Color.CADETBLUE;
                break;
            case "Pink":
                color = Color.HOTPINK;
                break;
            case "Brown":
                color = Color.SANDYBROWN;
                break;
            default:
                System.err.print("The color was not found!\n");
                break;
        }
        this.a.setFill(color);
        this.b.setFill(color);
        this.c.setFill(color);
        this.d.setFill(color);
    }

    public String getColorName() {
        return colorName;
    }

    public void changeShape() {
        shapeNumber = (shapeNumber != 4 ? shapeNumber + 1 : 1);
    }

}
