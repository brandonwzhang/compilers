use io

class Animal {
	noise() {
      println("Hello world!")
	}

	initialize() {
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

main(args:int[][]) {
	a:Animal = Animal.initialize()
    d:Animal = Dog.initialize()
    c:Animal = Cat.initialize()
    a.noise()
    d.noise()
    c.noise() 
}