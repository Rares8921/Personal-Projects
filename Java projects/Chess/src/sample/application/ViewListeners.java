package sample.application;

import static sample.application.DragAndDropHandler.getMove;
import static sample.application.DragAndDropHandler.getMoveLegal;

import sample.chess.Move;
import javafx.beans.value.ChangeListener;

public class ViewListeners {

   private final Controller controller;
   private final BoardHandler boardHandler;
   private Move modelMove;
   private ChangeListener<Move> move;
   private ChangeListener<Boolean> moveLegal;

   //CONSTRUCTOR
   public ViewListeners(Controller controller) {
      this.controller = controller;
      this.boardHandler = controller.getBoardHandler();
   }


   //METHODS
   public void set() {
      /*
       * set a listener on the move from the user. Translates the move (to account for
       * playing with white or black at the bottom of the board). Transforms the move
       * into a move for the model and lets the model play this move.
       */
      getMove().addListener(move = (ObservableValue, oldValue, newValue) -> {
         Move translatedMove = boardHandler.translateMove.apply(newValue);
         modelMove = controller.getGame().viewToModel(translatedMove);
         controller.getGame().play(modelMove);
      });

      /*
       * we want to switch players when dragAndDrop was success and validated by the model.
       * when promotion, the player's timer has to continue until he chooses a piece for promotion.
       */
      getMoveLegal().addListener(moveLegal = (ObservableValue, oldValue, newValue) -> {
         if (newValue && !controller.getGame().isPromotion.test(modelMove)) boardHandler.switchPlayer();
      });
   }

   public void remove() {
      getMove().removeListener(move);
      getMoveLegal().removeListener(moveLegal);
   }
}
