use io
use conv

course: int = 3110
real_course: int = 4120

main(args: int[][]) {
	println(unparseInt(course))
	println(unparseInt(real_course))

	hell: int = real_course + 1	
	println(unparseInt(hell))
}