use io

delay() {
    frame_length:int = 100000

    for(i:int = 0; i < frame_length * 1000; i = i + 1) {
    }
}

clear() {
    lines:int = 50
    for(i:int = 0; i < lines; i = i + 1) {
        println("")
    }
}

frame(space:int) {
    println("ALL ABOARD THE CHIN MOBILE");
    println("")
    println("")
    println("")

    gap:int[space]
    i:int = 0
    while(i < space) {
        gap[i] = ' '
        i = i + 1
    }

    print(gap)
    println("     ooOOOO");
    print(gap)
    println("    oo      _____");
    print(gap)
    println("   _I__n_n__||_|| ________");
    print(gap)
    println(" >(_________|_7_|-|______|");
    print(gap)
    println("  /o ()() ()() o   oo  oo");
}


main(args:int[][]) {
    delay()
    clear()
    frame(60)
    delay()

    clear()
    frame(50)
    delay()

    clear()
    frame(40)
    delay()

    clear()
    frame(30)
    delay()

    clear()
    frame(20)
    delay()

    clear()
    frame(10)
    delay()

    clear()
    frame(0)
    delay()
}
