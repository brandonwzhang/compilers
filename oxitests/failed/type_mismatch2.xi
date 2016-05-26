class A {

}

class B extends A {

}

class C extends B {

}

f() {
    c:C = new C
    b:B = new B
    a:A = new A
    c = b
    b = a
}
