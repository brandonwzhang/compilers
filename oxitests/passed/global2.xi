use io
use conv

course: int = 3110
real_course: int = 4121
jihun: Student = createStudent("jihun", 4740)
zero: int

main(args: int[][]) {
	println(unparseInt(course + real_course + zero)) //7231
	print(jihun.name() + "'s favorite course is CS ")
	println(unparseInt(jihun.favoriteCourse()))
}

class Student {
	name: int[]
	favorite_course: int

	initStudent(name_: int[], course: int): Student {
		name = name_
		favorite_course = course
		return this
	}

	favoriteCourse(): int {
		return favorite_course
	}

	name(): int[] {
		return name
	}
}

createStudent(name: int[], course: int): Student {
	return new Student.initStudent(name, course)
}