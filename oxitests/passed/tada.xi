use io
use conv

class B extends A {

}

class A {
    x:int
    y,z:int

    foo(){
        println("tada!")
    }
    make(x0:int, y0:int, z0:int){
        x = x0
        y = y0
        z = z0
    }
}

createB() : B {
    return new A.make(1,-3, 10)
}


main(args:int[][]) {
    b:B = createB()
    b.foo();

    a:A = new A.make(0,0,0)
    a.foo();
}
