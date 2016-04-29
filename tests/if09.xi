  use io
  use conv
 
  main(args: int[][]) {
  if (lt(0, 0) | lt(0, 0)) println("t")
  if (lt(0, 0) | lt(0, -1)) println("t")
  if (lt(0, 0) | lt(0, 1)) println("t")
  if (lt(0, -1) | lt(0, 0)) println("t")
  if (lt(0, -1) | lt(0, -1)) println("t")
 if (lt(0, -1) | lt(0, 1)) println("t")
 if (lt(0, 1) | lt(0, 0)) println("t")
 if (lt(0, 1) | lt(0, -1)) println("t")
 if (lt(0, 1) | lt(0, 1)) println("t")
 }

 lt(x: int, y: int): bool {
 print("lt(): ")
 print(unparseInt(x))
 print("<")
 println(unparseInt(y))
 return x < y
 }