use io
use conv

Class B extends A {

}

class A {
    x:int
    y,z:int

    foo(){
        println("tada!")
    }
}



main(args:int[][]) {
    A.foo()
}
