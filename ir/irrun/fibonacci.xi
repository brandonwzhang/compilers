use io
use conv

fib(i: int): int {
	ret:int = 0
	if (i < 2) {
		ret = i
	} else {
		ret = fib(i - 1) + fib(i - 2)
	}
	return ret
}

main(args:int[][]) {
	println(unparseInt(fib(10)))
}
