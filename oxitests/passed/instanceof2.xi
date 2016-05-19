use io
use conv

class A {
    a: int
    initA(n: int) {
        a = n
    }
    getA(): int {
        return a
    }
}

class B extends A {
    b: int
    initB(n: int) {
        b = n
    }
    getB(): int {
        return b
    }
}

class C extends B {
    c: int
    initC(n: int) {
        c = n
    }
    getC(): int {
        return c
    }
}

main(args: int[][]) {
    objA: A = new B
    objA.initA(4120)
    println(unparseInt(objA.getA()))
    if (objA instanceof B) {
        objB: B = B#objA
        objB.initB(4121)
        println(unparseInt(objB.getA()))
        println(unparseInt(objB.getB()))
    }
    if (objA instanceof C) {
        println("FAILED")
    }
}
