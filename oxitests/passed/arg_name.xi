use io
use conv

class C {
	foo() {
		println("nice")
	}

	bar(c: C) {
		c.foo()
	}
}

main(args:int[][]) {
    c:C = new C
    c.foo()

    c1:C = new C
    c.bar(c1)

    // nice nice
}
