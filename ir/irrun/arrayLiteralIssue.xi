use io
use conv

multipleArrays(x: int): int[], int[] {
    return {x, x}, {x, x}
}

main(args:int[][]) {
    d:int[], _ = multipleArrays(100)
    println(unparseInt(length(d)))
    println(d)
}