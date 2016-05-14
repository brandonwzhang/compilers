use io

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