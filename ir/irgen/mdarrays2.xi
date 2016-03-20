addOne(x: int): int {
	return x + 1
}

main(args:int[][]) {
    a: int[3][2][2][3]
    b: int[addOne(3)][3]
    c: int[1][2][2]
    d: int[2][addOne(4)][addOne(2)]
}