class A {
	x:int 

	init() {
	  this.x = 1
	}
}

class B extends A {
	f() : A {
	  return this 
	} 
}
