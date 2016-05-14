use point
use io

class Color {
    r,g,b:int
}

class ColoredPoint extends Point {
    col: Color
    color(): Color { return col; }

    initColoredPoint(x0:int, y0:int, c:Color): ColoredPoint {
        point: Point = initPoint(x0, y0)
        col = c
        return this
    }
}
