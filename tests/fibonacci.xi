use io
use conv

fib(i:int) : int {
	if (i < 2) {
		return i
	} else {
		return fib(i-1) + fib(i-2)
	}
}

main(args:int[][]) {
	println(unparseInt(fib(13)))
}
