use io

// 100 is character d

main() {
	a:int[10] 
	n:int = length(a)
	i:int = 0
	while (i < n) {
		c:int = i + 100
		a[i] = c
		i = i + 1
	}
	println(a)

	i = 0
	j:int = 0
	k:int = 0
	b:int[20]
	n = length(b)
	while (k < n) {
		while (i < 10) {
			b[k] = 100 + 10*j + i + 1
			k = k + 1
			i = i + 1
		}
		i = 0
		j = j + 1
	}
	println(b)
}