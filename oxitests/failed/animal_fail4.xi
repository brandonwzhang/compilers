use io
use animal

main(args:int[][]) {
	a:Animal = new Animal.initialize()
    d:Animal = new Dog.initialize()
    c:Animal = new Cat.initialize()
    a.noise()
    d.noise()
    c.noise()

    b:Dog = Dog#d
    Animal#b.noise() // cannot upcast
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