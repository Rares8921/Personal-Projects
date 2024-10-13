package sample.application;

import javafx.scene.media.AudioClip;

import java.util.Objects;

public final class Sounds {

   private static final AudioClip MOVE                  = new AudioClip(Objects.requireNonNull(Sounds.class.getResource("/sample/resources/sounds/move.mp3")).toString());
   private static final AudioClip DRAW                  = new AudioClip(Objects.requireNonNull(Sounds.class.getResource("/sample/resources/sounds/draw.mp3")).toString());
   private static final AudioClip CHECKMATE             = new AudioClip(Objects.requireNonNull(Sounds.class.getResource("/sample/resources/sounds/checkmate.mp3")).toString());
   private static final AudioClip STALEMATE             = new AudioClip(Objects.requireNonNull(Sounds.class.getResource("/sample/resources/sounds/stalemate.mp3")).toString());
   private static final AudioClip OUT_OF_TIME           = new AudioClip(Objects.requireNonNull(Sounds.class.getResource("/sample/resources/sounds/outoftime.mp3")).toString());
   private static final AudioClip INSUFFICIENT_MATERIAL = new AudioClip(Objects.requireNonNull(Sounds.class.getResource("/sample/resources/sounds/insufficientmaterial.mp3")).toString());
   private static final AudioClip RESIGN                = new AudioClip(Objects.requireNonNull(Sounds.class.getResource("/sample/resources/sounds/resign.mp3")).toString());

   //CONSTRUCTOR
   private Sounds() {}

   public static void lagWorkAround() {
      MOVE.play(0.0);
   }

   public static void move() {
      MOVE.play();
   }

   public static void draw() {
      DRAW.play();
   }

   public static void checkmate() {
      CHECKMATE.play();
   }

   public static void stalemate() {
      STALEMATE.play();
   }

   public static void outOfTime() {
      OUT_OF_TIME.play();
   }

   public static void insufficientMaterial() {
      INSUFFICIENT_MATERIAL.play();
   }

   public static void resign() {
      RESIGN.play();
   }
}
