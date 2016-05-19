use io

class A{
    string:int[]

    make(s:int[]){
        string = s
    }

    string():int[]{
        return string
    }
}

main(args:int[][]) {
    a:A = new A
    a.make("hello world");
    println(a.string())
}
