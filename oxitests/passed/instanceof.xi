use io

class A{

}
class B extends A {

}
class C extends B{

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
}
