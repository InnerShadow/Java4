package rfe.bsu.SikolonkoMa.laba4;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showArea = false;
    private boolean Rotate = false;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke AreaStroke;
    private Font axisFont;
    private Font areaFont;
    public GraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 36);
        areaFont = new Font("Serif", Font.BOLD, 8);
    }
    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();
    }
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowArea(boolean showArea){
        this.showArea = showArea;
        repaint();
    }
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphicsData==null || graphicsData.length==0) return;
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }

        if(Rotate){
            double tmp = minX;
            minX = minY;
            minY = tmp;
            tmp = maxX;
            maxX = maxY;
            maxY = tmp;
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);


        scale = Math.min(scaleX, scaleY);
        if (scale==scaleX) {
            double yIncrement = (getSize().getHeight()/scale - (maxY -
                    minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale==scaleY) {
            double xIncrement = (getSize().getWidth()/scale - (maxX -
                    minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (Rotate) DoRotate(canvas);
        if(showArea) paintArea(canvas);
        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);

        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void DoRotate(Graphics2D canvas) {
        Point2D.Double ptr = xyToPoint(0, 0);
        canvas.rotate(3 * Math.PI / 2, ptr.x, ptr.y);
        repaint();
    }

    protected void paintGraphics(Graphics2D canvas) {
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{16, 4, 4, 4, 4, 4, 8, 4, 8, 4}, 0);
        canvas.setStroke(dashed);

        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();

        for (int i=0; i<graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i>0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
        canvas.setStroke(graphicsStroke);
    }
    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        canvas.setStroke(new BasicStroke(2));
        canvas.setPaint(Color.BLACK);
        for (Double[] point: graphicsData) {
            Point2D.Double center = xyToPoint(point[0], point[1]);
            Point2D.Double corner = shiftPoint(center, 11, 11);
            boolean sign = true;
            int prev_num = 0;

            String s = String.valueOf(center.y);
            for(int i = 0; i < s.length() / 4; i++){
                if(s.charAt(i) == '.'){
                    continue;
                }
                if(Integer.valueOf(s.charAt(i)) < prev_num){
                    sign = false;
                    break;
                }
                prev_num = Integer.valueOf(s.charAt(i));
            }
            canvas.setColor(Color.BLACK);
            if(sign) {
                canvas.setColor(Color.ORANGE);
            }

            canvas.draw(new Line2D.Double(center.x - 5.5, center.y - 5.5, center.x + 5.5, center.y + 5.5)); // Начертить контур маркера
            canvas.draw(new Line2D.Double(center.x + 5.5, center.y - 5.5, center.x - 5.5, center.y + 5.5));
            canvas.draw(new Line2D.Double(center.x, center.y - 5.5, center.x, center.y + 5.5));
            canvas.draw(new Line2D.Double(center.x - 5.5, center.y, center.x + 5.5, center.y));
        }
        canvas.setStroke(markerStroke);
    }

    protected void paintArea(Graphics2D canvas) {
        canvas.setStroke(new BasicStroke(2));
        Double mainY = xyToPoint(0, 0).y;
        FontRenderContext context = canvas.getFontRenderContext();

        int i = 0;
        System.out.println(mainY);
        for(; i < graphicsData.length - 1; i++){
            if(xyToPoint(graphicsData[i][0], graphicsData[i][1]).y > mainY){
                break;
            }
        }

        for(; i < graphicsData.length - 2; i++){
            canvas.setColor(Color.GREEN);
            Double ValueOfarea = 0.d;
            Point2D.Double beginSpot = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            GeneralPath area = new GeneralPath();
            for(; i < graphicsData.length - 2; i++) {
                if (xyToPoint(graphicsData[i][0], graphicsData[i][1]).y < mainY) {
                    beginSpot = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
                    area.moveTo(beginSpot.x, beginSpot.y);
                    break;
                }
            }
            Point2D.Double endSpot = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            Point2D.Double highestPoint = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            for(; i < graphicsData.length - 2; i++) {
                if(highestPoint.y > xyToPoint(graphicsData[i][0], graphicsData[i][1]).y){
                    highestPoint = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
                }
                if (xyToPoint(graphicsData[i][0], graphicsData[i][1]).y > mainY) {
                    i--;
                    endSpot = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
                    break;
                }
                ValueOfarea += ((graphicsData[i - 1][1] + graphicsData[i][1]) / 2) *
                        (graphicsData[i][0] - graphicsData[i - 1][0]);
                Point2D.Double ptr = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
                area.lineTo(ptr.x, ptr.y);
            }
            boolean sign = true;
            for(int i_n = i; i_n < graphicsData.length - 2; i_n++){
                if(xyToPoint(graphicsData[i_n][0], graphicsData[i_n][1]).y > mainY){
                    sign = false;
                }
            }
            if(!sign) {

                area.moveTo(endSpot.x, endSpot.y);
                area.moveTo(beginSpot.x, beginSpot.y);
                area.closePath();
                canvas.draw(area);
                canvas.fill(area);

                canvas.setColor(Color.BLUE);
                canvas.setFont(areaFont);
                String strArea = String.valueOf(ValueOfarea);
                String finalArea = "";
                for(int j = 0; j < 5; j++){
                    finalArea += strArea.charAt(j);
                }
                Rectangle2D bounds = areaFont.getStringBounds(finalArea, context);
                Point2D.Double labelPos = new Point2D.Double();
                labelPos.x = (float)(beginSpot.x + (endSpot.x - beginSpot.x) / 4 - 2);
                labelPos.y = (float)(mainY + (highestPoint.y - mainY) / 4);
                System.out.println(finalArea);
                canvas.drawString(finalArea, (float)labelPos.x, (float)labelPos.y);
            }
        }
    }
    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
                FontRenderContext context = canvas.getFontRenderContext();
        if (minX<=0.0 && maxX>=0.0) {
                    canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
                            xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()+5,
                    arrow.getCurrentPoint().getY()+20);
            arrow.lineTo(arrow.getCurrentPoint().getX()-10,
                    arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float)labelPos.getX() + 10,
                    (float)(labelPos.getY() - bounds.getY()));
        }
        if (minY<=0.0 && maxY>=0.0) {
                    canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                            xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                    arrow.getCurrentPoint().getY()-5);
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY()+10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float)(labelPos.getX() -
                    bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
        }
    }
    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

    public void setRotate(boolean rotate) {
        Rotate = rotate;
    }
}