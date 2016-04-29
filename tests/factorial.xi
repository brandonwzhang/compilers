use io
use conv

factorial(n: int): int {
	if (n == 0 | n == 1) {
		return 1
	} else {
		return n * factorial(n - 1)
	}
}

main(args: int[][]) {
	println("The factorial of 6 is: " + unparseInt(factorial(6)))
}