arrayLiteralIndexCombo(): int  {
  return {1,2,3}[0+1]
}

arrayLiteralIndexCombo2(): int[] {
  return {{1, 2}, {3}}[2-1] + {4,5}
}

arrayLiteralIndexNested() : int {
  return {{1,2}}[0][1]
}

arrayLiterIndexNested2() : int {
  return {{{1,2}}, {{3,4},{5,6}}}[1][2][1]
}

unaryNegationBool() : bool {
  return !true
}

unaryNegationBool2() : bool {
  return !!true
}

unaryNegationBool3() : bool {
  return !!!true
}

unaryNegationBool4() : bool {
  return !!!!true
}

unaryNegationInt() : int {
  return -1 
}

unaryNegationInt2() : int {
  return --1 
}

unaryNegationInt3() : int {
  return ---1
}

unaryNegationInt4() : int {
  return ----1
} 

binOpCombo() : int {
  return 5 % 9 + 7 * 6 - 11 + 54 % 79
}

complicatedCombo() {
  x:int = 2
  andy = 5+3 + 2*x - 3+1 
  if(true) {
    x = 1+2+3
  } else {
    a:int[] = "This should never appear"
  }
  while(false) { 
    y:int[] = "This should never appear either" 
  }
  if(false) {
    z:int[] = "This should never appear too"
  }

  while(true) {
    b:int[] = "But this should appear"
  }

} 

simpleFunction() : int {
  return 'a'
} 

complicatedComboFunction() : int {
  x:int = 2
  andy = 5+3 + 2*x - 3+1 
  if(true) {
    x = 1+2+3
  } else {
    a:int[] = "This should never appear"
  }
  while(false) { 
    y:int[] = "This should never appear either" 
  }
  if(false) {
    z:int[] = "This should never appear too"
  }
  return 1 % 6 
}

arryLiteralIndexTest() {
  x:int[] = {1,2,3}
  x[1+1] = 20
}
