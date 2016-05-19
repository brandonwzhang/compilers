use io
use conv
class A {
	x:int
}

class B extends A {
    x:int
	g() {
		a:A = new A
		a.x = 0 //legal
	}
}

h() {
	a:A = new A
	a.x = 0 //legal
}


main(args:int[][]) {
    b:B = new B
    b.g()
    println(unparseInt(b.x))
    // 0
}
