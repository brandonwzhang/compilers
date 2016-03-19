use io

multipleArrays(x: int): int[], int[], int[] {
	return {x, x}, {x, x}, {x, x}
}

main() {
	course: int = 3110
	a:int[], b:int[], c:int[] = multipleArrays(course)
	d:int[], _, f:int[] = multipleArrays(100)
	println(d)
	println(f)
}