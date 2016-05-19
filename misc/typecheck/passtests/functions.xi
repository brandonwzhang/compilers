main(){
	foo()
}

foo(){
	main()
}

bar(){
	foo()
	main()
	x:int[] = {wumpus(2)}
}


wumpus(n:int):int {
	return 1
}