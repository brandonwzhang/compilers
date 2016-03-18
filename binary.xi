use io;

returnOne():int{
	return 1
}

identity(x:int[]):int[] {
    return x;
}

main(){
    c:int = 0
    while (c < 3) {
        println("hello")
        c = c + 1
    }
    if (c > 1) {
        println("got here")
    }
    if (c < 0) {
        println("ERROR OCCURED")
    } else {
        println("Else seems to be working")
    }
    y:int[] = "get fukd boi";
    y = y + "iiiiiiiiiiiiiiiiii"
	x:int = returnOne() + 35
    println(identity(y));
}
