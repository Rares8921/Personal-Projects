package sample;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import java.time.LocalDate;

public class AnchorPaneNode extends AnchorPane {

    public LocalDate date;

    public AnchorPaneNode(Node... children) {
        super(children);
    }

    public void setDate(LocalDate d) {
        this.date = d;
    }

}
