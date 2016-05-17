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
}
