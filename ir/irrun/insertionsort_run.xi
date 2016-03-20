use io

sort(a: int[]) {
  i:int = 0
  n:int = length(a)
  while (i < n) {
      j:int = i
      while (j > 0) {
        if (a[j-1] > a[j]) {
            swap:int = a[j]
            a[j] = a[j-1]
            a[j-1] = swap
        }
        j = j-1
      }
      i = i+1
  }
}

main(args:int[][]) {
  sortme: int[] = {101,100,104,103,102}  //edhgf
  metoo: int[] = "fadbgce"
  reverse: int[] = "ponmlkjih"

  println("unsorted: " + sortme)
  sort(sortme)
  println("sorted:   " + sortme)

  println("unsorted: " + metoo)
  sort(metoo)
  println("sorted:   " + metoo)

  println("unsorted: " + reverse)
  sort(reverse)
  println("sorted:   " + reverse)
}