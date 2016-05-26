use point
use io
use conv

// Must be compiled with point.xi

class Color {
    r,g,b:int
    set(r1:int, g1:int, b1:int) {
        r = r1
        g = g1
        b = b1
    }
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


main(args:int[][]) {
    p:Point = createPoint(5,5)
    c:Color = new Color
    c.set(50, 50, 50)
    cp:ColoredPoint = new ColoredPoint

    x:int,y:int = p.coords()
    cp = cp.initColoredPoint(x, y, c)
    println(unparseInt(x) + ", " + unparseInt(y))
    println("Color!")

    // 5, 5
    // Color!
}
