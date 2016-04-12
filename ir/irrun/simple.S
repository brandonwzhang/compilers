		.text
		.globl	FUNC(_Imain_paai)
		.globl	FUNC(_Iprint_pai)
		.globl	FUNC(_Iprintln_pai)
		.globl	FUNC(_Ireadln_ai)
		.globl	FUNC(_Igetchar_i)
		.globl	FUNC(_Ieof_b)
		.align	4
FUNC(_Imain_paai):
		subq	$224,%rsp
		pushq	%rbp
		movq	%rsp,%rbp
		movq	%rbx,-8(%rbp)
		movq	%rbp,-16(%rbp)
		movq	%r12,-24(%rbp)
		movq	%r13,-32(%rbp)
		movq	%r14,-40(%rbp)
		movq	%r15,-48(%rbp)
		#
		#(MOVE (TEMP temp_0) (TEMP _ARG0))
		#move1
		movq	%arg0,%r0
		#
		#(MOVE (TEMP args) (TEMP temp_0))
		#move1
		movq	%r0,%r1
		#
		#(MOVE (TEMP temp_1) (CONST 2))
		#move1
		movq	$2,%r2
		#
		#(MOVE (TEMP x) (TEMP temp_1))
		#move1
		movq	%r2,%r3
		#
		#(JUMP (NAME temp_2))
		#jump1
		jmp	temp_2
		#
		#(LABEL temp_2)
		#label1
temp_2:
		#
		#(RETURN)
		#return1
		movq	-8(%rbp),%rbx
		movq	-16(%rbp),%rbp
		movq	-24(%rbp),%r12
		movq	-32(%rbp),%r13
		movq	-40(%rbp),%r14
		movq	-48(%rbp),%r15
		movq	%rbp,%rsp
		popq	%rbp
		retq	

