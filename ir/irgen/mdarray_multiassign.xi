use io

multipleArrays(x: int): int[], int[], int[] {
	return {x, x}, {x, x}, {x, x}
}

main() {
	a:int[], b:int[], c:int[] = multipleArrays(42)
	course: int = 3110
	d:int[], _, f:int[] = multipleArrays(course)
}