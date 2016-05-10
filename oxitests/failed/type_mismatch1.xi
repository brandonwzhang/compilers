class A {
    x:int
}

class B {
    x:int
}

main(args:int[][]) {
    a:A = new A
    b:B = new B
    a = b
    b = a
}
