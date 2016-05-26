use io
use conv

main(args:int[][]) {
    if (b()) {
        a()
    }
    s:Student = new Student.init("eric780", 780)
    c(s);
    println(unparseInt(d(s.id())));
    // a
    // eric780
    // 781

}

a() {
	println("a")
}

b(): bool {
	return true
}

c(s: Student) {
	println(s.name())
}

d(n: int): int {
	return n + 1
}

class Student {
	name: int[]
	id: int

	init(name_: int[], id_: int): Student {
		name = name_
		id = id_
		return this
	}

	name(): int[] {
		return name
	}

	id(): int {
		return id
	}
}
