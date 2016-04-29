use io
main(args: int[][]) {
	j: int = 0
	while (j < 30000) {
		a: int[] = "thequickbrownfoxjumpsoverthelazydog"
		changed: bool = true; len: int = length(a); swap: int
		while (changed) {
			changed = false; i: int = 0
			while (i < len - 1) {
				if (a[i] > a[i + 1]) {
					swap = a[i]; a[i] = a[i + 1]; a[i + 1] = swap
					changed = true
				}
				i = i + 1
			}
		}
		j = j + 1
	}
}