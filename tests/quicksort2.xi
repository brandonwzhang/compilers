use io

swap(a: int[], i: int, j: int) {
	temp: int = a[i]
	a[i] = a[j]
	a[j] = temp
}

partition(a: int[], low: int, high: int): int {

	pivot: int = a[high]

	i: int = low
	j: int = low

	while (j < high) {
		if (a[j] <= pivot) {
			swap(a, i, j)
			i = i + 1
		}
		j = j + 1
	}
	swap(a, i, high)

	return i
}

quicksort(a: int[], low: int, high: int) {

	if (length(a) == 0) {
		return
	}
	if (low >= high) {
		return
	}

	p: int = partition(a, low, high)
	quicksort(a, low, p - 1)
	quicksort(a, p + 1, high)
}

main(args: int[][]) {
	sortme: int[] = "thequickbrownfoxjumpsoverthelazydog"
	println(sortme)
	quicksort(sortme, 0, length(sortme) - 1)
	println(sortme)
}