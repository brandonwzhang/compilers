use io

class A {
	
	foo() {
		bar()
	}

	foo2() {
		this.bar()
	}

	bar() {
		println("A.bar()")
	}
}

bar() {
	println("global bar")
}

main(args: int[][]) {
	obj: A = new A
	obj.foo()
	obj.foo2()
	bar()
}