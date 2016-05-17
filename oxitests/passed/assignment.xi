use io

class A{
    string:int[]

    make(s:int[]){
        string = s
    }
}

main(args:int[][]) {
    a:A = new A
    a.make("hello world");
    println(a.string)
}
