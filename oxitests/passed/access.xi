use io

class hello {
    world:int[]

    set(s:int[][]) {
        world = s[0]
    }
}


main(args:int[][]){
    h:hello = new hello
    println(h.world)
    h.set({"hello world"})
    println(h.world)
}
