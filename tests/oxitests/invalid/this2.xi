class A {
	x:int 

	init() {
	  this.x = 1
	}

	g() : B {
      return this
	} 
}

class B extends A {
	f() : A {
	  return this 
	} 
}