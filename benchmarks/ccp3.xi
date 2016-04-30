use io use conv 
main(args:int[][]) { 
  y:int = 1 a:bool = false b:bool = true z:int = 2
  if (a & (!a|a|!a|a|!a|b&a|b|a) & (a&a&a&a|a|!b|a|!a|b) & 
  (b|b|a|b|a&a&!b|b|!a&a)& (a&b&!b) & (!b|!b|!b|!b|!b|!b|!b|!b|!b|!a) & 
  (a&b&b&!a)&(a&a&a&b)&(!b|b|a|!a)) {
    println("oXi")
    c:bool = !a
    if(!c) {
      if(c) {
        println("oXi")
      }
    }
  } else {
    println(unparseInt(1))
  } q:int = z*z*z*z*z*z-y-y-y-y--y------------y
  println(unparseInt(q)) }