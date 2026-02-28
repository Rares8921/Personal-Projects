package com.diskanalyzer.gui.views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.geometry.VPos;
import java.util.*;

/**
 * Treemap visualization for storage distribution.
 * Uses a squarified treemap algorithm.
 */
public class TreemapChart extends Pane {

    private Canvas canvas;
    private List<TreemapItem> items = new ArrayList<>();
    private double totalValue = 0;

    public TreemapChart(double width, double height) {
        canvas = new Canvas(width, height);
        getChildren().add(canvas);

        widthProperty().addListener((obs, o, n) -> {
            canvas.setWidth(n.doubleValue());
            render();
        });
        heightProperty().addListener((obs, o, n) -> {
            canvas.setHeight(n.doubleValue());
            render();
        });

        setOnMouseMoved(event -> render(event.getX(), event.getY()));
        setOnMouseExited(event -> render(-1, -1));
    }

    public void setData(List<TreemapItem> items) {
        this.items = new ArrayList<>(items);
        this.items.sort((a, b) -> Double.compare(b.value, a.value));
        this.totalValue = items.stream().mapToDouble(i -> i.value).sum();
        render();
    }

    private void render() { render(-1, -1); }

    private void render(double mouseX, double mouseY) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.clearRect(0, 0, w, h);

        if (items.isEmpty() || w < 10 || h < 10) return;

        List<Rect> rects = squarify(items, new Rect(2, 2, w - 4, h - 4));

        for (int i = 0; i < rects.size() && i < items.size(); i++) {
            Rect r = rects.get(i);
            TreemapItem item = items.get(i);

            boolean hovered = mouseX >= r.x && mouseX <= r.x + r.w && mouseY >= r.y && mouseY <= r.y + r.h;

            Color fillColor = hovered ? item.color.brighter() : item.color;
            gc.setFill(fillColor);
            gc.fillRoundRect(r.x + 1, r.y + 1, r.w - 2, r.h - 2, 4, 4);

            gc.setStroke(Color.web("#0c1525"));
            gc.setLineWidth(2);
            gc.strokeRoundRect(r.x + 1, r.y + 1, r.w - 2, r.h - 2, 4, 4);

            if (r.w > 50 && r.h > 30) {
                gc.setFill(Color.WHITE);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);

                double fontSize = Math.min(14, Math.min(r.w / 6, r.h / 3));
                gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
                gc.fillText(item.label, r.x + r.w / 2, r.y + r.h / 2 - fontSize * 0.6, r.w - 8);

                if (r.h > 50) {
                    gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, fontSize * 0.8));
                    gc.fillText(item.detail, r.x + r.w / 2, r.y + r.h / 2 + fontSize * 0.6, r.w - 8);
                }
            }

            if (hovered) {
                double pct = totalValue > 0 ? item.value / totalValue * 100 : 0;
                String tooltip = String.format("%s: %s (%.1f%%)", item.label, item.detail, pct);
                gc.setFill(Color.rgb(22, 38, 64, 0.92));
                double tw = tooltip.length() * 7 + 16;
                double tx = Math.min(mouseX + 10, w - tw - 5);
                double ty = Math.max(mouseY - 30, 5);
                gc.fillRoundRect(tx, ty, tw, 24, 6, 6);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText(tooltip, tx + 8, ty + 12);
            }
        }
    }

    private List<Rect> squarify(List<TreemapItem> items, Rect bounds) {
        List<Rect> result = new ArrayList<>();
        if (items.isEmpty()) return result;

        double total = items.stream().mapToDouble(i -> i.value).sum();
        List<TreemapItem> remaining = new ArrayList<>(items);
        Rect current = new Rect(bounds.x, bounds.y, bounds.w, bounds.h);

        while (!remaining.isEmpty()) {
            boolean vertical = current.w > current.h;
            double side = vertical ? current.h : current.w;

            List<TreemapItem> row = new ArrayList<>();
            row.add(remaining.get(0));
            double rowTotal = remaining.get(0).value;


            double bestAspect = worstAspect(row, rowTotal, side, total, current);

            for (int i = 1; i < remaining.size(); i++) {
                List<TreemapItem> tryRow = new ArrayList<>(row);
                tryRow.add(remaining.get(i));
                double tryTotal = rowTotal + remaining.get(i).value;
                double tryAspect = worstAspect(tryRow, tryTotal, side, total, current);
                if (tryAspect <= bestAspect) {
                    row = tryRow;
                    rowTotal = tryTotal;
                    bestAspect = tryAspect;
                } else {
                    break;
                }
            }

            double rowFraction = total > 0 ? rowTotal / total : 0;
            double rowSize = vertical ? current.w * rowFraction : current.h * rowFraction;

            double offset = 0;
            for (TreemapItem item : row) {
                double itemFraction = rowTotal > 0 ? item.value / rowTotal : 0;
                double itemSize = side * itemFraction;

                Rect r;
                if (vertical) {
                    r = new Rect(current.x, current.y + offset, rowSize, itemSize);
                } else {
                    r = new Rect(current.x + offset, current.y, itemSize, rowSize);
                }
                result.add(r);
                offset += itemSize;
            }

            total -= rowTotal;
            remaining.removeAll(row);
            if (vertical) {
                current = new Rect(current.x + rowSize, current.y, current.w - rowSize, current.h);
            } else {
                current = new Rect(current.x, current.y + rowSize, current.w, current.h - rowSize);
            }
        }

        return result;
    }

    private double worstAspect(List<TreemapItem> row, double rowTotal, double side,
                               double totalVal, Rect bounds) {
        if (row.isEmpty() || side <= 0 || totalVal <= 0) return Double.MAX_VALUE;
        double rowFraction = rowTotal / totalVal;
        boolean vertical = bounds.w > bounds.h;
        double rowSize = vertical ? bounds.w * rowFraction : bounds.h * rowFraction;
        if (rowSize <= 0) return Double.MAX_VALUE;

        double worst = 0;
        for (TreemapItem item : row) {
            double itemFraction = item.value / rowTotal;
            double itemSize = side * itemFraction;
            double aspect = Math.max(rowSize / itemSize, itemSize / rowSize);
            worst = Math.max(worst, aspect);
        }
        return worst;
    }

    private static class Rect {
        final double x, y, w, h;
        Rect(double x, double y, double w, double h) {
            this.x = x; this.y = y; this.w = Math.max(w, 0); this.h = Math.max(h, 0);
        }
    }

    public static class TreemapItem {
        public final String label;
        public final String detail;
        public final double value;
        public final Color color;

        public TreemapItem(String label, String detail, double value, Color color) {
            this.label = label;
            this.detail = detail;
            this.value = value;
            this.color = color;
        }
    }
}
