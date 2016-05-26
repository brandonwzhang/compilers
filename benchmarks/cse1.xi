use io f(x:int) : int {
    i:int = 0
    while (i < x) {
       x1:int = x x2:int = x1 x3:int = x1 + x2 x4:int = x1 + x2 + x3
       x5:int = x1 + x2 + x3 + x4 x6:int = x1 + x2 + x3 + x4 + x5
       x7:int = x1 + x2 + x3 + x4 + x5 + x6 
       x8:int = x1 + x2 + x3 + x4 + x5 + x6 
       x9:int = x1 + x2 + x3 + x4 + x5 + x6 
       x10:int = x1 + x2 + x3 + x4 + x5 + x6 
       x11:int = x1 + x2 + x3 + x4 + x5 + x6 
       x12:int = x1 + x2 + x3 + x4 + x5 + x6 
       x13:int = x1 + x2 + x3 + x4 + x5 + x6 
       x14:int = x1 + x2 + x3 + x4 + x5 + x6 
       x15:int = x1 + x2 + x3 + x4 + x5 + x6 
       x16:int = x1 + x2 + x3 + x4 + x5 + x6 i = i + 1
    } return i;
}
main(args:int[][]) {
    z:int = f(10000000); println("done")
}