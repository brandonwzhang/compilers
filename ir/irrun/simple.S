#define FUNC(x) _##x
#define ARG1() %rdi
#define ARG2() %rsi
#define ARG3() %rdx
#define ARG4() %rcx
#define ARG5() %r8
#define ARG6() %r9
#define INITFUNC() .section 	 .ctors
#define GCFUNC(x) x		.text
		.globl	FUNC(_Imain_paai)
		.globl	FUNC(_Iprint_pai)
		.globl	FUNC(_Iprintln_pai)
		.globl	FUNC(_Ireadln_ai)
		.globl	FUNC(_Igetchar_i)
		.globl	FUNC(_Ieof_b)
		.align	4
FUNC(_Imain_paai):
		subq	$192, %rsp
		# Starting function prologue
		pushq	%rbp
		movq	%rsp, %rbp
		# Save callee-save registers rbx rbp r12-r15
		movq	%rbx, -8(%rbp)
		movq	%rbp, -16(%rbp)
		movq	%r12, -24(%rbp)
		movq	%r13, -32(%rbp)
		movq	%r14, -40(%rbp)
		movq	%r15, -48(%rbp)
		# (MOVE (TEMP temp_0) (TEMP _ARG0)) -- move1
		movq	-160(%rbp), %r13
		movq	%rdx, %r13
		movq	%r13, -160(%rbp)
		# (MOVE (TEMP args) (TEMP temp_0)) -- move1
		movq	-168(%rbp), %r14
		movq	-160(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -160(%rbp)
		movq	%r14, -168(%rbp)
		# (MOVE (TEMP temp_1) (CONST 2)) -- move1
		movq	-176(%rbp), %r13
		movq	$2, %r13
		movq	%r13, -176(%rbp)
		# (MOVE (TEMP x) (TEMP temp_1)) -- move1
		movq	-184(%rbp), %r14
		movq	-176(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -176(%rbp)
		movq	%r14, -184(%rbp)
		# (JUMP (NAME temp_2)) -- jump1
		jmp	temp_2
		# (LABEL temp_2) -- label1
temp_2:
		# (RETURN) -- return1
		# restoring callee-save registers
		movq	-8(%rbp), %rbx
		movq	-16(%rbp), %rbp
		movq	-24(%rbp), %r12
		movq	-32(%rbp), %r13
		movq	-40(%rbp), %r14
		movq	-48(%rbp), %r15
		# restore old RBP and RSP
		movq	%rbp, %rsp
		popq	%rbp
		retq	

