main(){
	x:int = length({4});
	y:int = length({{1,2,3},{4,5,6}})
	z:int = length({{{1,2,3}}})
}

foo() {
    a: int[][]
    b: int[3][4]
    a = b
    c: int[3][]
    c[0] = b[0]; c[1] = b[1]; c[2] = b[2]
    d: int[][] = {{1, 0}, {0, 1}}
    e:int = length(d)
}

