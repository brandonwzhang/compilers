use io
use conv

main(args:int[][]) {
    i:int = 0;
    while(i < 100000) {
        if(i == 10){
            break;
        }
        println(unparseInt(i));
        i = i + 1
    }
    // 0 1 2 3 4 5 6 7 8 9
}
