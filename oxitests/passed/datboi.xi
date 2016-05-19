use io
use conv

class DatBoi {
    phrases: int[][]
    init() {
        phrases = {"o shit whaddup", "here come dat boi"}
    }
    talk(x:int) {
        if (x >= length(phrases)) {
            println("Dat boi only knows " + unparseInt(length(phrases)) + " phrases!")
            return
        }
        println(phrases[x])
    }
    show1() {
        println("             .  ............")
        show2()
    }
    show2() {
        println(".........................,+OZZOZOI......................")
        show3()
    }
    show3() {
        println("......................OZOOOO8OODM87.....................")
        show4()
    }
    show4() {
        println("......................ZDDDD888888D8O....................")
        show5()
    }
    show5() {
        println(".......................NMMNNDD88888O?...................")
        show6()
    }
    show6() {
        println("........................ONMMNNNND88OO...................")
        show7()
    }
    show7() {
        println(".........................+MNNNDDD88OO?..................")
        show8()
    }
    show8() {
        println("..........................$DDDDD88OOO8..................")
        show9()
    }
    show9() {
        println("..........................ZDDDD888OOO88.................")
        show10()
    }
    show10() {
        println(".........................7DDDDDDDD8OO88+................")
        show11()
    }
    show11() {
        println(".....................:D888DDDDDD8D88OO88................")
        show12()
    }
    show12() {
        println("................,IOZO888DN,=DD888888DOO8D...............")
        show13()
    }
    show13() {
        println(".............=OZOOODDDN8...$D888O888888OOZN.............")
        show14()
    }
    show14() {
        println("...........$OO8DDNNI.......8D888O8888O8ZN,..............")
        show15()
    }
    show15() {
        println("......=ONDDDN......... ....DD888888D8OD8D.. ............")
        show16()
    }
    show16() {
        println("........::O~ .. ... .......DDDDDD88DDD88D...............")
        show17()
    }
    show17() {
        println("....  ........ ............~DDDDDDDDDDDOO:..............")
        show18()
    }
    show18() {
        println("..........................,8DDDDDDDDDDD8OO+.............")
        show19()
    }
    show19() {
        println("..........................88DDNNNDDDNNNDO88~............")
        show20()
    }
    show20() {
        println(".........................8DDDDDNNNNNNNNDO888~...........")
        show21()
    }
    show21() {
        println("........................ZDDDDNNNNMNNNMNDO888D...........")
        show22()
    }
    show22() {
        println(".......................,DDDNNNN,MMMN8DNN8O888...........")
        show23()
    }
    show23() {
        println("......................I8DNNM,,ODDDO88DNND8888,..........")
        show24()
    }
    show24() {
        println(".....................,8DN:.......?I.....D88DD,..........")
        show25()
    }
    show25() {
        println(".....................O8DNN.......+:......8O8D...........")
        show26()
    }
    show26() {
        println("......................888DM......Z,......8ODN ..........")
        show27()
    }
    show27() {
        println(".......................$88DM.....7:.....~OO8?...........")
        show28()
    }
    show28() {
        println(".........................N88N...7DI,....8OO8?...........")
        show29()
    }
    show29() {
        println("...........................O8DMMMDIN=..=OO8D,...........")
        show30()
    }
    show30() {
        println("...........................DDDMD8N7MNNM8OODD............")
        show31()
    }
    show31() {
        println("......................  .DDD$M$DD,77MNN888DD............")
        show32()
    }
    show32() {
        println("........................8DONDDNZ8:$N..M888D,............")
        show33()
    }
    show33() {
        println(".......................8D7$.,Z.$D:$=.8NO8DD.............")
        show34()
    }
    show34() {
        println("......................8DOZ$..:Z78?O.:$8O8DN.............")
        show35()
    }
    show35() {
        println(".................... ,8NN.8::,$7D+?.~.8O8ND?............")
        show36()
    }
    show36() {
        println(".....................8D7,.  D,.8DII8.?88DMDN............")
        show37()
    }
    show37() {
        println(".....................8NN.O?..,O$DO8:Z=8OZDDN............")
        show38()
    }
    show38() {
        println(".....................8DD .:7DNZ8DON,,DO88DDN............")
        show39()
    }
    show39() {
        println("....................~DOI...,.+8DD$$$.OO+.DDN............")
        show40()
    }
    show40() {
        println("....................~8O8.OZ~..D88NI.ZO8N8D8D............")
        show41()
    }
    show41() {
        println("....................,8DN...,N~D=DN7.8OO.7D8+............")
        show42()
    }
    show42() {
        println(".....................DD8.$=Z OZ8:N78O8?.ODD.............")
        show43()
    }
    show43() {
        println(".................... 8D$7$,.?O.N.$7OON.8D88 ............")
        show44()
    }
    show44() {
        println(".................... .DDO:.+$.+Z.DOO8:.D8N..............")
        show45()
    }
    show45() {
        println("......................7NDN?:~.NZ.8+8ODN8D...............")
        show46()
    }
    show46() {
        println(".......................ZDDO8..=..8DDDNND8...............")
        show47()
    }
    show47() {
        println("....................... ~MND8NNZONMNDDM.................")
        show48()
    }
    show48() {
        println("..........................$NNDDDDMNNN...................")
        show49()
    }
    show49() {
        println(" .............................ID== ....................")
    }
}

main(args:int[][]) {
    datboi: DatBoi = new DatBoi
    datboi.init()
    datboi.talk(1)
    datboi.show1()
    datboi.talk(0)
    datboi.talk(2)
}
