use io
main(args:int[][]) {
    i:int = 0
    x:int[] = "chinawat"
    y:int[] = {0, 1, 2, 3, 4, 5, 6}
    while(i < 11879230) {
        a:int = x[y[3]]
        b:int = a + i
        c:int = a + b
        d:int = a + c + b
        e:int = d + c + a + b
        f:int = d + b + a + c + e
        g:int = i + d + c + e + b + a + f
        h:int = i - e + b
        i = i + 1
    }
}
