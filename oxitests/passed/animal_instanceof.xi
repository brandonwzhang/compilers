use io
use animal

main(args:int[][]) {
	a:Animal = new Animal.initialize()
    d:Animal = new Dog.initialize()
    c:Animal = new Cat.initialize()
    
    if (a instanceof Animal) {
      a.noise() 
    } else {
      println("uhoh")
    }

    if (d instanceof Dog & d instanceof Animal & !(d instanceof Cat)) {
      d.noise()
    } else {
      println("uhoh")
    }

    if (c instanceof Cat & c instanceof Animal & !(d instanceof Cat)) {
      c.noise()
    } else {
      println("uhoh")
    }
    
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