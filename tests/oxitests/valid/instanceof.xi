use io

class A{

}
class B extends A {

}
class C extends B{

}
class X {
}

main(args:int[][]){

    c:C = new C
    if (c instanceof C) {
        println("swag");
    }
    if (c instanceof B) {
        println("SWAG");
    }
    if (c instanceof A) {
        println("S W A G");
    }
    if (c instanceof X) {
        println("ERROR")
    }

    // swag
    // SWAG
    // S W A G
}
