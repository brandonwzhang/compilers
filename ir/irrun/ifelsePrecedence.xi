
use io

main(args:int[][]) {
	x: int = 0
	if (1 == 1) {
		if(2 == 2) {
			x = 1
		} else {
			x = 2
		}
	} 

	if (1 == 1) {
		if (2 == 2) {
			x = 1
		}
	} else {
		x = 2
	}

	println({x + 100})
}
