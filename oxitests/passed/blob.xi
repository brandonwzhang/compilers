use io
use conv

class Blob {
    x:int
    set(i:int) { x = i }
    getx():int { return x }
}

class Cob extends Blob {
    y:int
    set(i:int) { y = i }
    gety():int {return y}
}

class Glob extends Cob {
    z:int
    set(i:int) { z = i }
    getz():int {
        return z
    }
}

main(args:int[][]) {
    g:Glob = new Glob;
    g.set(6)
    println(unparseInt(g.getz()))
    println(unparseInt(g.gety()))
    println(unparseInt(g.getx()))
}
