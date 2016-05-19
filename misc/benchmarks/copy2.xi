main(args:int[][]) {
    x:int[] = "hello"
    y:int = x[0]
    while (y < 50000000) {
	a:int = y
	z:int = a + y
	b:int = a
	c:int = a + b
	d:int = b
	e:int = a + b + d
	f:int = z
	g:int = f
	h:int = f + g
	i:int = h + g
	y = y + 1
    }
}
