use io

main(args:int[][]) {
	foo()
}

foo() {
	print("called foo")
}

bar() {
	x:int[] = {wumpus(2)}
	foo()
	print(x)
}

wumpus(n:int): int {
	return n + 1
}