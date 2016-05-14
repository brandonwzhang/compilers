class A {

}

class B extends A {

}

class C extends B {

}

class D extends C {

}

main(args:int[][]) {
    a:A = new A
    b:B = new B
    c:C = new C
    d:D = new D
    a = d
    b = d
    c = d
}
