use io
use conv

main(args:int[][]) {
	a:int[5]
	a[0] = 1337
	a[1] = a[f()]

	a[2] = g()[1]
}

f() : int{
    println("a")
	return 0
}

g() : int[] {
    print("b")
	return {1,2,3}
}