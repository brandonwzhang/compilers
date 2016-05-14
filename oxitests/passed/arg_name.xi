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