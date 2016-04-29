 use io
  use conv
 
  main(args: int[][]) {
  if (lt(0, 0) | lt(0, 0)) println("t") else println("f")
  if (lt(0, 0) | lt(0, -1)) println("t") else println("f")
  if (lt(0, 0) | lt(0, 1)) println("t") else println("f")
  if (lt(0, -1) | lt(0, 0)) println("t") else println("f")
  if (lt(0, -1) | lt(0, -1)) println("t") else println("f")
 if (lt(0, -1) | lt(0, 1)) println("t") else println("f")
 if (lt(0, 1) | lt(0, 0)) println("t") else println("f")
 if (lt(0, 1) | lt(0, -1)) println("t") else println("f")
 if (lt(0, 1) | lt(0, 1)) println("t") else println("f")
 }

 lt(x: int, y: int): bool {
 print("lt():")
 print(unparseInt(x))
 print("<")
 println(unparseInt(y))
 return x < y
 }