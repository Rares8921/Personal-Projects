package sample;

import java.util.ArrayList;
import java.util.Arrays;

public class Bird {

    private final Sprite bird;
    private final ArrayList<Sprite> flight = new ArrayList<>();
    private int currentBird = 0;
    private final double locationX = 70;
    private final double locationY = 200;
    private int BIRD_WIDTH = 70;
    private int BIRD_HEIGHT = 65;

    public Bird(String color) {
        bird = new Sprite();
        if(color.equals("gold")) {
            BIRD_WIDTH = 40;
            BIRD_HEIGHT = 35;
        }
        bird.resizeImage("sample/" + color + "Bird1.png", BIRD_WIDTH, BIRD_HEIGHT); // normal
        bird.setPositionXY(locationX, locationY);
        setFlightAnimation(color);
    }

    public void setFlightAnimation(String color) {
        Sprite bird2 = new Sprite();
        bird2.resizeImage("sample/" + color + "Bird2.png", BIRD_WIDTH, BIRD_HEIGHT); // up
        bird2.setPositionXY(locationX, locationY);

        Sprite bird3 = new Sprite();
        bird3.resizeImage("sample/" + color + "Bird1.png", BIRD_WIDTH, BIRD_HEIGHT); // normal
        bird3.setPositionXY(locationX, locationY);

        Sprite bird4 = new Sprite();
        bird4.resizeImage("sample/" + color + "Bird3.png", BIRD_WIDTH, BIRD_HEIGHT); // down
        bird4.setPositionXY(locationX, locationY);

        flight.addAll(Arrays.asList(bird, bird2, bird3, bird4)); // adding images to flight animation
    }

    public Sprite getBird() {
        return bird;
    }

    public Sprite animate() {
        if (currentBird == flight.size() - 1) {
            currentBird = 0;
        }
        return flight.get(currentBird++);
    }

}
