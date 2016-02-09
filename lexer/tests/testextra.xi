// test 1
// if b is true, return a + 1. if b is false, return the original value of a
foo(a:int, b:bool) {
	bar:int = a + 6 - 3 * 2
	if (b) {
		bar + 1
	}
	return bar
}

// test 2
pair(car:int, cdr: int) {
	return (car, cdr)
}

// test 3
main(args: int[][]) {
  course:int = 4120
  prac:int = foo(4120, true)
  print(foo(course, !(20 > 19)))
  print(prac)
}

// test 4
s: int[] = "test"
s[3] = s[3] + 1
s[2] = 'k'
s[1] = '\x65'
print(s)

// test 5
y = ""
r = -9223372036854775808
t = 1232142140192312321
w = 9223372036854775807
v = 9223372036854775808  // this will be rejected at the parser stage
u = 9223372036854775806

// test 6
b:bool = ((true | false) & false) | (1 != 0) | 1 >= 0
m = 23 *>> 2
n = 2 * 3 + 10 / 5 % 9