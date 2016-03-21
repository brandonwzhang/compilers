foo(x:int) {
	x = x + 1
}

main(args:int[][]) {
	a:int[] = {0, 1, 2}
	foo(a[1])
	b:int = a[a[1]]
}