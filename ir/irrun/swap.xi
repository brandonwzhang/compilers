use io

main() {
	a:int[] = {101,100,102}
	j:int = 1
	swap:int = a[j]
	a[j] = a[j-1]
	a[j-1] = swap
	println(a)
}