use io

frame_length:int = 5000

delay() {
    i:int = 0;
    while(i < frame_length * 1000) {
        i = i + 1
    }
}

clear() {
    lines:int = 50
    i:int = 0
    while(i < lines){
        println("")
        i = i + 1
    }
}

frame1() {
    println("ALL ABOARD THE CHIN MOBILE");
    println("")
    println("")
    println("")
    println("    o O___ _________");
    println("  _][__|o| |O O O O|");
    println("<________|-|_______|");
    println(" /O-O-O     o   o");
}

main(args:int[][]) {
    while(true){
        frame1()
        delay()
        clear()
        println("hello")
        delay()
    }
}
