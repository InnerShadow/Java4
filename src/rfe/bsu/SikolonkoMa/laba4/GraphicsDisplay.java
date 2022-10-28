package rfe.bsu.SikolonkoMa.laba4;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    // Список координат точек для построения графика
    private Double[][] graphicsData;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showArea = true;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke AreaStroke;
    // Различные шрифты отображения надписей
    private Font axisFont;
    private Font areaFont;
    public GraphicsDisplay() {
// Цвет заднего фона области отображения - белый
        setBackground(Color.WHITE);
// Сконструировать необходимые объекты, используемые в рисовании
// Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
// Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
        areaFont = new Font("Serif", Font.BOLD, 8);
    }
// Данный метод вызывается из обработчика элемента меню "Открыть файл сграфиком"
    // главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData) {
// Сохранить массив точек во внутреннем поле класса
        this.graphicsData = graphicsData;
// Запросить перерисовку компонента, т.е. неявно вызвать paintComponent();
        repaint();
    }
    // Методы-модификаторы для изменения параметров отображения графика
// Изменение любого параметра приводит к перерисовке области
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
    // Метод отображения всего компонента, содержащего график
    public void paintComponent(Graphics g) {
        /* Шаг 1 - Вызвать метод предка для заливки области цветом заднего фона
         * Эта функциональность - единственное, что осталось в наследство от
         * paintComponent класса JPanel
         */
        super.paintComponent(g);
// Шаг 2 - Если данные графика не загружены (при показе компонента при запуске программы) - ничего не делать
        if (graphicsData==null || graphicsData.length==0) return;
// Шаг 3 - Определить минимальное и максимальное значения для координат X и Y
// Это необходимо для определения области пространства, подлежащей отображению
// Еѐ верхний левый угол это (minX, maxY) - правый нижний это (maxX, minY)
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;
// Найти минимальное и максимальное значение функции
        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }
/* Шаг 4 - Определить (исходя из размеров окна) масштабы по осям X
и Y - сколько пикселов
* приходится на единицу длины по X и по Y
*/
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
// Шаг 5 - Чтобы изображение было неискажѐнным - масштаб должен быть одинаков
// Выбираем за основу минимальный
        scale = Math.min(scaleX, scaleY);
// Шаг 6 - корректировка границ отображаемой области согласно выбранному масштабу
        if (scale==scaleX) {
/* Если за основу был взят масштаб по оси X, значит по оси Y
делений меньше,
* т.е. подлежащий визуализации диапазон по Y будет меньше
высоты окна.
* Значит необходимо добавить делений, сделаем это так:
* 1) Вычислим, сколько делений влезет по Y при выбранном
масштабе - getSize().getHeight()/scale
* 2) Вычтем из этого сколько делений требовалось изначально
* 3) Набросим по половине недостающего расстояния на maxY и
minY
*/
            double yIncrement = (getSize().getHeight()/scale - (maxY -
                    minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale==scaleY) {
// Если за основу был взят масштаб по оси Y, действовать по аналогии
            double xIncrement = (getSize().getWidth()/scale - (maxX -
                    minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
// Шаг 7 - Сохранить текущие настройки холста
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
// Шаг 8 - В нужном порядке вызвать методы отображения элементов графика
// Порядок вызова методов имеет значение, т.к. предыдущий рисунок будет затираться последующим
// Первыми (если нужно) отрисовываются оси координат.
        if(showArea) paintArea(canvas);
        if (showAxis) paintAxis(canvas);
// Затем отображается сам график
        paintGraphics(canvas);

// Затем (если нужно) отображаются маркеры точек, по которым строился график.
        if (showMarkers) paintMarkers(canvas);

// Шаг 9 - Восстановить старые настройки холста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }
    // Отрисовка графика по прочитанным координатам
    protected void paintGraphics(Graphics2D canvas) {
// Выбрать линию для рисования графика
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{16, 4, 4, 4, 4, 4, 8, 4, 8, 4}, 0);
        canvas.setStroke(dashed);

// Выбрать цвет линии
        canvas.setColor(Color.RED);
/* Будем рисовать линию графика как путь, состоящий из множества
сегментов (GeneralPath)
* Начало пути устанавливается в первую точку графика, после чего
прямой соединяется со
* следующими точками
*/
        GeneralPath graphics = new GeneralPath();

        for (int i=0; i<graphicsData.length; i++) {
// Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i>0) {
// Не первая итерация цикла - вести линию в точку point

                graphics.lineTo(point.getX(), point.getY());
            } else {
// Первая итерация цикла - установить начало пути в точку point
                graphics.moveTo(point.getX(), point.getY());
            }
        }
// Отобразить график
        canvas.draw(graphics);
        canvas.setStroke(graphicsStroke);
    }
    // Отображение маркеров точек, по которым рисовался график
    protected void paintMarkers(Graphics2D canvas) {
// Шаг 1 - Установить специальное перо для черчения контуров маркеров
        canvas.setStroke(markerStroke);
        canvas.setStroke(new BasicStroke(2));
// Выбрать красный цвета для контуров маркеров
        //canvas.setColor(Color.BLACK);
// Выбрать красный цвет для закрашивания маркеров внутри
        canvas.setPaint(Color.BLACK);
// Шаг 2 - Организовать цикл по всем точкам графика
        for (Double[] point: graphicsData) {
// Инициализировать эллипс как объект для представления маркера


/* Эллипс будет задаваться посредством указания координат
его центра
и угла прямоугольника, в который он вписан */
// Центр - в точке (x,y)
            Point2D.Double center = xyToPoint(point[0], point[1]);
// Угол прямоугольника - отстоит на расстоянии (3,3)
            Point2D.Double corner = shiftPoint(center, 11, 11);
// Задать эллипс по центру и диагонали
//            marker.setFrameFromCenter(center, corner);
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
                //System.out.println(s);
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
        System.out.println(minY);
        System.out.println();
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
            //canvas.draw(new Line2D.Double(beginSpot.x, beginSpot.y, endSpot.x, endSpot.y));
        }
    }
    // Метод, обеспечивающий отображение осей координат
    protected void paintAxis(Graphics2D canvas) {
// Установить особое начертание для осей
        canvas.setStroke(axisStroke);
// Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
// Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
// Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
// Создать объект контекста отображения текста - для получения характеристик устройства (экрана)
                FontRenderContext context = canvas.getFontRenderContext();
// Определить, должна ли быть видна ось Y на графике
        if (minX<=0.0 && maxX>=0.0) {
// Она должна быть видна, если левая граница показываемой области (minX) <= 0.0,
// а правая (maxX) >= 0.0
// Сама ось - это линия между точками (0, maxY) и (0, minY)
                    canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
                            xyToPoint(0, minY)));
// Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести левый "скат" стрелки в точку с относительными координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX()+5,
                    arrow.getCurrentPoint().getY()+20);
// Вести нижнюю часть стрелки в точку с относительными координатами (-10, 0)
            arrow.lineTo(arrow.getCurrentPoint().getX()-10,
                    arrow.getCurrentPoint().getY());
// Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
// Нарисовать подпись к оси Y
// Определить, сколько места понадобится для надписи "y"
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
// Вывести надпись в точке с вычисленными координатами
            canvas.drawString("y", (float)labelPos.getX() + 10,
                    (float)(labelPos.getY() - bounds.getY()));
        }
// Определить, должна ли быть видна ось X на графике
        if (minY<=0.0 && maxY>=0.0) {
// Она должна быть видна, если верхняя граница показываемой области (maxX) >= 0.0,
// а нижняя (minY) <= 0.0
                    canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                            xyToPoint(maxX, 0)));
// Стрелка оси X
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на правый конец оси X
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести верхний "скат" стрелки в точку с относительными координатами (-20,-5)
            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                    arrow.getCurrentPoint().getY()-5);
// Вести левую часть стрелки в точку с относительными координатами (0, 10)
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY()+10);
// Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
// Нарисовать подпись к оси X
// Определить, сколько места понадобится для надписи "x"
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
// Вывести надпись в точке с вычисленными координатами
            canvas.drawString("x", (float)(labelPos.getX() -
                    bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
        }
    }
    /* Метод-помощник, осуществляющий преобразование координат.
    * Оно необходимо, т.к. верхнему левому углу холста с координатами
    * (0.0, 0.0) соответствует точка графика с координатами (minX, maxY),
    где
    * minX - это самое "левое" значение X, а
    * maxY - самое "верхнее" значение Y.
    */
    protected Point2D.Double xyToPoint(double x, double y) {
// Вычисляем смещение X от самой левой точки (minX)
        double deltaX = x - minX;
// Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    /* Метод-помощник, возвращающий экземпляр класса Point2D.Double
     * смещѐнный по отношению к исходному на deltaX, deltaY
     * К сожалению, стандартного метода, выполняющего такую задачу, нет.
     */
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {
// Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();
// Задать еѐ координаты как координаты существующей точки +заданные смещения
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}