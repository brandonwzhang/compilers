use a 
main() {
	x:int, y:int = f(x)
	//y = 3 
	// _:int = 1 //not allowed
	_ = 1 
	//x = 1;
	//y = "Hello";
	f(x)[1] = 2 
	z = {0,1,2}
	//'a'[0] = 1 //not allowed 
    "abc"[1]='c'
    //2[2] = 5 //not allowed 
    //true[1] = 2 //not allowed
    f(x) = 1
    f(x)[1][2] = 1
    x[1] = 1 
    x[1][2] = 2
    x = 1 
    "a"[0] = 1
    "ab"[0][1] = 2
    //"a" = 1 //not allowed
    x = f(x)[1][2][3]
    x:int[1][2][3]
    //f()[] = 1
    //f() = 1 //not allowed
}
