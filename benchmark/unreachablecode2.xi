use io
use conv

main(x:int[][]) {
  a:bool = false
  b:bool = false
  c:bool = false
  d:bool = true
  if(d | f()) {
    println("nice")
  }
  if(a|b|c) {
    while(true) {
      println(unparseInt(1))
    }
  } 
  println(unparseInt(42))
}

f():bool {
  println("uh oh")
  return false
}
