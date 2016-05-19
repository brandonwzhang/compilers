use io
use animal

main(args:int[][]) {
	a:Animal = new Animal.initialize()
    d:Animal = new Dog.initialize()
    c:Animal = new Cat.initialize()
    a.noise()
    d.noise()
    c.noise()

    Dog#a.noise() // this should fail because 
                  // a is an Animal and cannot be casted down to a Dog
}

class Animal {
	noise() {
      println("Hello world!")
	}

	initialize():Animal {
	  return this 
	}
}

class Dog extends Animal {
	noise() {
	  bark() 
	} 

	bark() {
	  println("bark")
	}
}

class Cat extends Animal {
    noise() {
      meow() 
    }

	meow() {
	  println("meow")
	}
}