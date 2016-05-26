use io

class hello {
    world:int[]

    set(s:int[][]) {
        world = s[0]
    }
}


main(args:int[][]){
    h:hello = new hello
    h.set({"hello world"})
    println(h.world)
}
