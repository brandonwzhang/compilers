class A {

}

class B extends A {

}

class C extends B {

}

f() {
    C c = new C
    B b = new B
    A a = new A
    c = b
    b = a
}
