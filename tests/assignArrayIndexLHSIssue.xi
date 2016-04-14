  use io
  use conv
 
  main(args: int[][]) {
  f(1)[1] = 1
 }

 f(x: int): int[] {
   print("f() called ")
   return {1,2,3}
 }