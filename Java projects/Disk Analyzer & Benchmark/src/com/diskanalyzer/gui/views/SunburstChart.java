package com.diskanalyzer.gui.views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.geometry.VPos;
import java.util.*;

public class SunburstChart extends Pane {
    
    private Canvas canvas;
    private List<SegmentData> segments = new ArrayList<>();
    private double centerX, centerY;
    private double innerRadius = 80;
    private double segmentWidth = 60;
    private String centerLabel = "";
    
    public SunburstChart(double width, double height) {
        canvas = new Canvas(width, height);
        this.getChildren().add(canvas);
        this.centerX = width / 2;
        this.centerY = height / 2;
        
        setOnMouseMoved(event -> {
            double mx = event.getX();
            double my = event.getY();
            render(mx, my);
        });
    }
    
    public void setData(List<SegmentData> segments) {
        this.segments = segments;
        render(-1, -1);
    }
    
    public void setCenterLabel(String label) {
        this.centerLabel = label;
    }
    
    private void render(double mouseX, double mouseY) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        if (segments.isEmpty()) return;
        
        double total = segments.stream().mapToDouble(s -> s.value).sum();
        
        double startAngle = -90; // Start at top
        
        for (SegmentData seg : segments) {
            double angle = (seg.value / total) * 360;
            
            double innerStartRadius = innerRadius;
            double innerEndRadius = innerRadius + segmentWidth;
            
            boolean isHovered = isPointInSegment(mouseX, mouseY, startAngle, angle, innerStartRadius, innerEndRadius);
            
            if (isHovered) {
                innerEndRadius += 10; // Expand on hover
            }
            
            drawSegment(gc, centerX, centerY, innerStartRadius, innerEndRadius, startAngle, angle, seg.color);
            
            if (seg.children != null && !seg.children.isEmpty()) {
                double outerStartRadius = innerEndRadius + 5;
                double outerEndRadius = outerStartRadius + (segmentWidth * 0.6);
                
                double childTotal = seg.children.stream().mapToDouble(c -> c.value).sum();
                double childStartAngle = startAngle;
                
                for (SegmentData child : seg.children) {
                    double childAngle = (child.value / childTotal) * angle;
                    drawSegment(gc, centerX, centerY, outerStartRadius, outerEndRadius, 
                               childStartAngle, childAngle, lightenColor(seg.color, 0.3));
                    childStartAngle += childAngle;
                }
            }
            
            startAngle += angle;
        }
        
        gc.setFill(Color.web("#0c1525"));
        gc.fillOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(centerLabel, centerX, centerY);
    }
    
    private void drawSegment(GraphicsContext gc, double cx, double cy, 
                            double innerR, double outerR, 
                            double startAngle, double angleExtent, Color color) {
        gc.setFill(color);
        
        gc.beginPath();
        
        double startRad = Math.toRadians(startAngle);
        double endRad = Math.toRadians(startAngle + angleExtent);
        
        double x1 = cx + innerR * Math.cos(startRad);
        double y1 = cy + innerR * Math.sin(startRad);
        gc.moveTo(x1, y1);
        
        double x2 = cx + outerR * Math.cos(startRad);
        double y2 = cy + outerR * Math.sin(startRad);
        gc.lineTo(x2, y2);
        
        for (double angle = startAngle; angle <= startAngle + angleExtent; angle += 1) {
            double rad = Math.toRadians(angle);
            double x = cx + outerR * Math.cos(rad);
            double y = cy + outerR * Math.sin(rad);
            gc.lineTo(x, y);
        }
        
        double x3 = cx + innerR * Math.cos(endRad);
        double y3 = cy + innerR * Math.sin(endRad);
        gc.lineTo(x3, y3);
        
        for (double angle = startAngle + angleExtent; angle >= startAngle; angle -= 1) {
            double rad = Math.toRadians(angle);
            double x = cx + innerR * Math.cos(rad);
            double y = cy + innerR * Math.sin(rad);
            gc.lineTo(x, y);
        }
        
        gc.closePath();
        gc.fill();
    }
    
    private boolean isPointInSegment(double px, double py, double startAngle, double angleExtent,
                                     double innerR, double outerR) {
        if (px < 0 || py < 0) return false;
        
        double dx = px - centerX;
        double dy = py - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < innerR || distance > outerR) return false;
        
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        
        double normalizedStart = startAngle % 360;
        if (normalizedStart < 0) normalizedStart += 360;
        
        double normalizedEnd = (startAngle + angleExtent) % 360;
        if (normalizedEnd < 0) normalizedEnd += 360;
        
        if (normalizedStart < normalizedEnd) {
            return angle >= normalizedStart && angle <= normalizedEnd;
        } else {
            return angle >= normalizedStart || angle <= normalizedEnd;
        }
    }
    
    private Color lightenColor(Color color, double factor) {
        return color.interpolate(Color.WHITE, factor);
    }
    
    public static class SegmentData {
        public String label;
        public double value;
        public Color color;
        public List<SegmentData> children;
        
        public SegmentData(String label, double value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
            this.children = new ArrayList<>();
        }
        
        public void addChild(SegmentData child) {
            this.children.add(child);
        }
    }
}
