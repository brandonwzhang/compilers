class A {
	x:int
}

class B extends A {
	g() {
		a:A = new A
		a.x = 0 //legal
	}
}

h() {
	a:A = new A
	a.x = 0 //legal
}