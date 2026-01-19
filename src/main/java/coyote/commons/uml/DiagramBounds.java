package coyote.commons.uml;

public class DiagramBounds {
    private int xCoord;
    private int yCoord;
    private int width;
    private int height;

    public DiagramBounds(int xPos, int yPos, int width, int height) {
        this.xCoord = xPos;
        this.yCoord = yPos;
        this.width = width;
        this.height = height;
    }

    public int getXPosition() {
        return xCoord;
    }

    public int getYPosition() {
            return yCoord;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public DiagramBounds setXPosition(int xPos) {
        this.xCoord = xPos;
        return this;
    }
    public DiagramBounds setYPosition(int yPos) {
        this.yCoord = yPos;
        return this;
    }
    public DiagramBounds setWidth(int width) {
        this.width = width;
        return this;
    }
    public DiagramBounds setHeight(int height) {
        this.height = height;
        return this;
    }
}
