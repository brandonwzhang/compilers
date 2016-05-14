use conv
use test

class C {
	foo() {
		println("nice")
	}

	bar(c: C) {
		c.foo()
	}
}