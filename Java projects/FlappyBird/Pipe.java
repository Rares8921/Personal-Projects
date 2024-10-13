package sample;

public class Pipe {
    private final Sprite pipe;

    public Pipe(boolean isFaceUp, int height, String val) {
        this.pipe = new Sprite();
        this.pipe.resizeImage(isFaceUp ? "sample/up_pipe" + val + ".png" : "sample/down_pipe" + val + ".png", 70, height);
        double locationX = 400; // next pipe's x
        double locationY = isFaceUp ? 600 - height : 0; // next pipe's y, down or up
        this.pipe.setPositionXY(locationX, locationY);
    }

    public Sprite getPipe() {
        return pipe;
    }
}
