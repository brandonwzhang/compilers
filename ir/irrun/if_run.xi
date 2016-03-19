use io

main() {
	a:int[] = {100, 102, 101}
	if (a[1] > a[2]) {
		println("correct")
	} else {
		println("incorrect")
	}

	n:int = 5
	i:int = 0
	while (i < n) {
		if (1 > 2) {
			println("incorrect")
		}
		i = i + 1
	}
}