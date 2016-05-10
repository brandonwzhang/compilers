class A {

}

class B extends A {

}

class C extends B {

}

class D extends C {

}

fun() {
    A a = new A
    B b = new B
    C c = new C
    D d = new D
    a = d
    b = d
    c = d
}
