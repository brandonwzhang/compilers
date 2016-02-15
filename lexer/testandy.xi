// test 1
use io

// if b is true, return a + 1. if b is false, return the original value of a
foo(a:int, b:bool) {
	bar:int = a + 6 - 3 * 2
	if (b) {
		bar + 1
	}
	return bar
}

pair(car:int, cdr: int) {
	return (car, cdr)
}

main(args: int[][]) {
  course:int = 4120
  prac:int = foo(4120, true)
  print(foo(course, !(20 > 19)))
  print(prac)
}

s:int[] = "hello"
s[3] = s[3] + 1
s[2] = 'k'
s[1] = 'h'
print(s)
