use io 
use conv

main(args: int[][]) {
	a: int[3]
	a[f(0)] = f(3)
	a[f(1)] = f(4)
	a[f(2)] = f(5)
	println(unparseInt(length(a)))
	println(unparseInt(a[1]))
 }

 f(x: int): int {
 	print("f() called: ")
 	println(unparseInt(x))
 	return x
 }