use inheritance_test2
use io

class A {
	a1() {
		println("a1")
	}
	a2(): int {
		return 4120
	}
	a3(): C {
		return new C
	}
}

class D {

	d1() {
		println("d1")
	}

	d2(n: int): int {
		x: int = n * 2
		x = x + 1
		return x
	}

	d3(b: bool): A {
		if (b) {
			return new A
		}
		return new B
	}
}

class C {
	data: int[]
	more_data: int[]
}