use io;

returnOne():int{
    return 100
}

main(args:int[][]){
	i:int = 0
	while (i < 3) {
        x:int = returnOne() + i
        print({x});
		i = i + 1
	}
}
