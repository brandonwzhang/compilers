		.text
		.globl	FUNC(_Imain_paai)
		.globl	FUNC(_Iprint_pai)
		.globl	FUNC(_Iprintln_pai)
		.globl	FUNC(_Ireadln_ai)
		.globl	FUNC(_Igetchar_i)
		.globl	FUNC(_Ieof_b)
		.align	4
FUNC(_Imain_paai):
		subq	$216,%rsp
		pushq	%rbp
		movq	%rsp,%rbp
		movq	%rbx,-8(%rbp)
		movq	%rbp,-16(%rbp)
		movq	%r12,-24(%rbp)
		movq	%r13,-32(%rbp)
		movq	%r14,-40(%rbp)
		movq	%r15,-48(%rbp)
		#MOV(dst,src) -- move1
		movq	-160(%rbp),%r13
		movq	%rdx,-160(%rbp)
		movq	%rdx,-160(%rbp)
		movq	%r14,-160(%rbp)
		#MOV(dst,src) -- move1
		movq	-168(%rbp),%r13
		movq	-160(%rbp),%r13
		movq	-160(%rbp),-168(%rbp)
		movq	%r14,-160(%rbp)
		movq	%r14,-168(%rbp)
		#MOV(dst,src) -- move1
		movq	-176(%rbp),%r13
		movq	$2,-176(%rbp)
		movq	%r14,-176(%rbp)
		#MOV(dst,src) -- move1
		movq	-184(%rbp),%r13
		movq	-176(%rbp),%r13
		movq	-176(%rbp),-184(%rbp)
		movq	%r14,-176(%rbp)
		movq	%r14,-184(%rbp)
		#JUMP(label) -- jump1
		jmp	temp_2
		#LABEL(name) -- label1
temp_2:
		movq	-8(%rbp),%rbx
		movq	-16(%rbp),%rbp
		movq	-24(%rbp),%r12
		movq	-32(%rbp),%r13
		movq	-40(%rbp),%r14
		movq	-48(%rbp),%r15
		movq	%rbp,%rsp
		popq	%rbp
		retq	

