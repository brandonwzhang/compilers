use io

multiReturn(a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int):int, int, int, int, int, int, int, int, int, int {
    println({a})
    println({b})
    println({c})
    println({d})
    println({e})
    println({f})
    println({g})
    println({h})
    println({i})
    println({j})
    return a, b, c, d, e, f, g, h, i, j
}

main(args:int[][]) {
    a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int = multiReturn(97, 98, 99, 100, 101, 102, 103, 104, 105, 106)
    println({a, b, c, d, e, f, g, h, i, j})
}
