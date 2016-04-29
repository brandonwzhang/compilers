quicksort(a: int[], low: int, high: int) {
	if (low >= high) {return}
	middle: int = (low + high) / 2 
	pivot: int = a[middle]
	i: int = low; j: int = high
	while (i <= j) {
		while (a[i] < pivot) i = i + 1
		while (a[j] > pivot) j = j - 1
		if (i <= j) { swap: int = a[i]; a[i] = a[j]; a[j] = swap
			i = i + 1; j = j - 1 }
	}
	quicksort(a, low, j); quicksort(a, i, high)
}
main(args: int[][]) {
	sortme: int[] = "thequickbrownfoxjumpsoverthelazydog"
	x: int = 0
	while (x < 100000) { quicksort(sortme, 0, length(sortme) - 1); x = x + 1 
	sortme = "thequickbrownfoxjumpsoverthelazydog"}
}