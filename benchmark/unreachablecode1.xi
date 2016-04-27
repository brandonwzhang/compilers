  use io
  use conv
 
  main(args: int[][]) {
  x: int = 0
  y: bool = false 
  z: bool = true
  if (y) {
    println(unparseInt(x/0))
  }
  while(y) {
    println(unparseInt(x/0))
  }  
  while(y&y&y) {
    println(unparseInt(x/0))
  }
  if (y) {
   println(unparseInt(x/0))
  } else {
   println("nice")
  }
  if(z) {
    while(y) {
      println(unparseInt(x/0))
    }
    println("jihun")
  }
  if (z) {
    while(y) {
      println(unparseInt(x/0))
    } 
    if(y) {
      println(unparseInt(x/0))
    }
    if(y) {
      println(unparseInt(x/0))
    } else {
      println("cool") 
    }
    while(y) {
      println(unparseInt(x/0))
    } 
    while(y) {
      println(unparseInt(x/0))
    }
  }
  println("done")
 }

