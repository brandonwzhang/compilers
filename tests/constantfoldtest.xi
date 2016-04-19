main(args:int[][]) {
  jihun:int = 1
}

arryLiteralIndexTest() {
  x:int[] = {1,2,3}
  x[x[1]] = 1
  x[1] = 10
  x[1+1] = 20
  y:int = 1
  x[y] = 2;
  x[y-1+1] = 3
  x[complicatedComboFunction()] = 1
  x[dummyFunction(1,2,3)] = 1
  returnArrayLiteral(1,2+1,3)[1] = 1  
}

andyTest() {
  x:int = -1 
  y:int = -x
  z:int = --x
  a:int = ---x
  b:int = ----x 
  c:bool = true
  d:bool = !c
  e:bool = !!c 
  f:bool = !!!c
  g:bool = !!!!c
}

//divideByZero() {
//  x:int = 10/0
//}

overFlow() {
  x:int = -(-9223372036854775808)
}

overFlow2() {
  x:int = --9223372036854775808
}

underFlow() {
  x:int = -9223372036854775808 - 1
}

overFlow3() {
  x:int = -9223372036854775808 - 1 + 1 
}

arrayLiteralIndexCombo(): int  {
  q:int[1+3][2+3][8-5][9]
  return {1,2,3}[0+1]
}

arrayLiteralIndexCombo2(): int[] {
  return {{1, 2}, {3}}[2-1] + {4,5}
}

arrayLiteralIndexNested() : int {
  return {{1,2}}[0][1]
}

arrayLiterIndexNested2() : int {
  return {{{1,2}}, {{3,4},{5,6}}}[1][1][1]
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

unaryNegationBool5() : bool {
  return !false
}

unaryNegationBool6() : bool {
  return !!false
}

unaryNegationBool7() : bool {
  return !!!false
}

unaryNegationBool8() : bool {
  return !!!!false
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
  andy:int = 5+3 + 2*x - 3+1 
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
  andy:int = 5+3 + 2*x - 3+1 
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

returnArrayLiteral(x:int, y:int, z:int) : int[] {
  return {1,2,3}
}

dummyFunction(x:int, y:int, z:int) : int {
  return 5; 
}

dummyProcedure(x:int, y:int, z:int) {
  swag:int = 1 
  nice:bool = false 
}

testFunctionArguments() {
  dummyProcedure(5+3,3-1,5*30);
  x:int = dummyFunction(5+3,3-1,5*30);
}
