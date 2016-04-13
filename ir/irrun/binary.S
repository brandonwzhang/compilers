#define FUNC(x) _##x
#define ARG1() %rdi
#define ARG2() %rsi
#define ARG3() %rdx
#define ARG4() %rcx
#define ARG5() %r8
#define ARG6() %r9
#define INITFUNC() .section 	 .ctors
#define GCFUNC(x) x
		.text
		.globl	FUNC(IreturnOne_i)
		.globl	FUNC(Iprint_pai)
		.globl	FUNC(Iprintln_pai)
		.globl	FUNC(Ireadln_ai)
		.globl	FUNC(Igetchar_i)
		.globl	FUNC(Ieof_b)
		.align	4
FUNC(IreturnOne_i):
		# Starting function prologue
		pushq	%rbp
		movq	%rsp, %rbp
		subq	$192, %rsp
		# Save callee-save registers rbx rbp r12-r15
		movq	%rbx, -8(%rbp)
		movq	%rbp, -16(%rbp)
		movq	%r12, -24(%rbp)
		movq	%r13, -32(%rbp)
		movq	%r14, -40(%rbp)
		movq	%r15, -48(%rbp)
		# (MOVE (TEMP temp_0) (CONST 1)) -- move1
		movq	-168(%rbp), %r13
		movq	$1, %r13
		movq	%r13, -168(%rbp)
		# (MOVE (TEMP temp0) (TEMP temp_0)) -- move1
		movq	-176(%rbp), %r14
		movq	-168(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -168(%rbp)
		movq	%r14, -176(%rbp)
		# (MOVE (TEMP temp_1) (TEMP temp0)) -- move1
		movq	-184(%rbp), %r14
		movq	-176(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -176(%rbp)
		movq	%r14, -184(%rbp)
		# (MOVE (TEMP _RET0) (TEMP temp_1)) -- move1
		movq	-184(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -184(%rbp)
		# (JUMP (NAME temp_2)) -- jump1
		jmp	temp_2
		# (LABEL temp_2) -- label1
temp_2:
		# Function epilogue
		# Restoring callee-save registers
		movq	-8(%rbp), %rbx
		movq	-16(%rbp), %rbp
		movq	-24(%rbp), %r12
		movq	-32(%rbp), %r13
		movq	-40(%rbp), %r14
		movq	-48(%rbp), %r15
		# Restore old RBP and RSP
		addq	$192, %rsp
		popq	%rbp
		retq	

		.globl	FUNC(Imain_paai)
		.globl	FUNC(Iprint_pai)
		.globl	FUNC(Iprintln_pai)
		.globl	FUNC(Ireadln_ai)
		.globl	FUNC(Igetchar_i)
		.globl	FUNC(Ieof_b)
		.align	4
FUNC(Imain_paai):
		# Starting function prologue
		pushq	%rbp
		movq	%rsp, %rbp
		subq	$368, %rsp
		# Save callee-save registers rbx rbp r12-r15
		movq	%rbx, -8(%rbp)
		movq	%rbp, -16(%rbp)
		movq	%r12, -24(%rbp)
		movq	%r13, -32(%rbp)
		movq	%r14, -40(%rbp)
		movq	%r15, -48(%rbp)
		# (MOVE (TEMP temp_3) (TEMP _ARG0)) -- move1
		movq	-168(%rbp), %r13
		movq	%rdi, %r13
		movq	%r13, -168(%rbp)
		# (MOVE (TEMP args) (TEMP temp_3)) -- move1
		movq	-176(%rbp), %r14
		movq	-168(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -168(%rbp)
		movq	%r14, -176(%rbp)
		# (MOVE (TEMP temp_4) (CALL (NAME IreturnOne_i))) -- move2
		# Function call prologue
		# Save all caller-saved registers
		movq	%rax, -56(%rbp)
		movq	%rcx, -64(%rbp)
		movq	%rsi, -72(%rbp)
		movq	%rdi, -80(%rbp)
		movq	%rdx, -88(%rbp)
		movq	%rsp, -96(%rbp)
		movq	%r8, -104(%rbp)
		movq	%r9, -112(%rbp)
		movq	%r10, -120(%rbp)
		movq	%r11, -128(%rbp)
		# Pass pointer to return space
		movq	-136(%rbp), %r8
		# Pass pointer to argument space
		movq	-144(%rbp), %r9
		callq	 FUNC(IreturnOne_i)
		movq	-184(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -184(%rbp)
		# Function call epilogue
		movq	-56(%rbp), %rax
		movq	-64(%rbp), %rcx
		movq	-72(%rbp), %rsi
		movq	-80(%rbp), %rdi
		movq	-88(%rbp), %rdx
		movq	-96(%rbp), %rsp
		movq	-104(%rbp), %r8
		movq	-112(%rbp), %r9
		movq	-120(%rbp), %r10
		movq	-128(%rbp), %r11
		# (MOVE (TEMP temp_5) (TEMP temp_4)) -- move1
		movq	-192(%rbp), %r14
		movq	-184(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -184(%rbp)
		movq	%r14, -192(%rbp)
		# (MOVE (TEMP temp_6) (ADD (TEMP temp_5) (CONST 2))) -- move1
		movq	-200(%rbp), %r14
		movq	-192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -192(%rbp)
		movq	%r14, -200(%rbp)
		movq	-200(%rbp), %r13
		addq	$2, %r13
		movq	%r13, -200(%rbp)
		movq	-208(%rbp), %r14
		movq	-200(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -200(%rbp)
		movq	%r14, -208(%rbp)
		# (MOVE (TEMP x) (TEMP temp_6)) -- move1
		movq	-216(%rbp), %r14
		movq	-208(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -208(%rbp)
		movq	%r14, -216(%rbp)
		# (MOVE (TEMP temp_7) (CONST 16)) -- move1
		movq	-224(%rbp), %r13
		movq	$16, %r13
		movq	%r13, -224(%rbp)
		# (MOVE (TEMP temp_8) (CALL (NAME _I_alloc_i) (TEMP temp_7))) -- move2
		# Function call prologue
		# Save all caller-saved registers
		movq	%rax, -56(%rbp)
		movq	%rcx, -64(%rbp)
		movq	%rsi, -72(%rbp)
		movq	%rdi, -80(%rbp)
		movq	%rdx, -88(%rbp)
		movq	%rsp, -96(%rbp)
		movq	%r8, -104(%rbp)
		movq	%r9, -112(%rbp)
		movq	%r10, -120(%rbp)
		movq	%r11, -128(%rbp)
		# Pass pointer to return space
		movq	-136(%rbp), %r8
		# Pass pointer to argument space
		movq	-144(%rbp), %r9
		movq	-224(%rbp), %r13
		movq	%r13, %rdi
		movq	%r13, -224(%rbp)
		callq	 FUNC(_I_alloc_i)
		movq	-232(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -232(%rbp)
		# Function call epilogue
		movq	-56(%rbp), %rax
		movq	-64(%rbp), %rcx
		movq	-72(%rbp), %rsi
		movq	-80(%rbp), %rdi
		movq	-88(%rbp), %rdx
		movq	-96(%rbp), %rsp
		movq	-104(%rbp), %r8
		movq	-112(%rbp), %r9
		movq	-120(%rbp), %r10
		movq	-128(%rbp), %r11
		# (MOVE (TEMP temp_9) (TEMP temp_8)) -- move1
		movq	-240(%rbp), %r14
		movq	-232(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -232(%rbp)
		movq	%r14, -240(%rbp)
		# (MOVE (TEMP temp3) (TEMP temp_9)) -- move1
		movq	-248(%rbp), %r14
		movq	-240(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -240(%rbp)
		movq	%r14, -248(%rbp)
		# (MOVE (TEMP temp_10) (CONST 1)) -- move1
		movq	-256(%rbp), %r13
		movq	$1, %r13
		movq	%r13, -256(%rbp)
		# (MOVE (MEM (TEMP temp3)) (TEMP temp_10)) -- move1
		movq	-248(%rbp), %r14
		movq	-256(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -256(%rbp)
		movq	%r14, -248(%rbp)
		# (MOVE (TEMP temp_11) (TEMP temp3)) -- move1
		movq	-264(%rbp), %r14
		movq	-248(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -248(%rbp)
		movq	%r14, -264(%rbp)
		# (MOVE (TEMP temp_12) (ADD (TEMP temp_11) (CONST 8))) -- move1
		movq	-272(%rbp), %r14
		movq	-264(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -264(%rbp)
		movq	%r14, -272(%rbp)
		movq	-272(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -272(%rbp)
		movq	-280(%rbp), %r14
		movq	-272(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -272(%rbp)
		movq	%r14, -280(%rbp)
		# (MOVE (TEMP temp3) (TEMP temp_12)) -- move1
		movq	-248(%rbp), %r14
		movq	-280(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -280(%rbp)
		movq	%r14, -248(%rbp)
		# (MOVE (TEMP temp_13) (TEMP x)) -- move1
		movq	-288(%rbp), %r14
		movq	-216(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -216(%rbp)
		movq	%r14, -288(%rbp)
		# (MOVE (MEM (TEMP temp3)) (TEMP temp_13)) -- move1
		movq	-248(%rbp), %r14
		movq	-288(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -288(%rbp)
		movq	%r14, -248(%rbp)
		# (MOVE (TEMP temp_14) (TEMP temp3)) -- move1
		movq	-296(%rbp), %r14
		movq	-248(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -248(%rbp)
		movq	%r14, -296(%rbp)
		# (MOVE (TEMP temp_15) (ADD (TEMP temp_14) (CONST 8))) -- move1
		movq	-304(%rbp), %r14
		movq	-296(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -296(%rbp)
		movq	%r14, -304(%rbp)
		movq	-304(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -304(%rbp)
		movq	-312(%rbp), %r14
		movq	-304(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -304(%rbp)
		movq	%r14, -312(%rbp)
		# (MOVE (TEMP temp3) (TEMP temp_15)) -- move1
		movq	-248(%rbp), %r14
		movq	-312(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -312(%rbp)
		movq	%r14, -248(%rbp)
		# (MOVE (TEMP temp_16) (TEMP temp3)) -- move1
		movq	-320(%rbp), %r14
		movq	-248(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -248(%rbp)
		movq	%r14, -320(%rbp)
		# (MOVE (TEMP temp_17) (SUB (TEMP temp_16) (CONST 8))) -- move1
		movq	-328(%rbp), %r14
		movq	-320(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -320(%rbp)
		movq	%r14, -328(%rbp)
		movq	-328(%rbp), %r13
		subq	$8, %r13
		movq	%r13, -328(%rbp)
		movq	-336(%rbp), %r14
		movq	-328(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -328(%rbp)
		movq	%r14, -336(%rbp)
		# (MOVE (TEMP temp3) (TEMP temp_17)) -- move1
		movq	-248(%rbp), %r14
		movq	-336(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -336(%rbp)
		movq	%r14, -248(%rbp)
		# (MOVE (TEMP temp_18) (TEMP temp3)) -- move1
		movq	-344(%rbp), %r14
		movq	-248(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -248(%rbp)
		movq	%r14, -344(%rbp)
		# (MOVE (TEMP temp_19) (CALL (NAME Iprint_pai) (TEMP temp_18))) -- move2
		# Function call prologue
		# Save all caller-saved registers
		movq	%rax, -56(%rbp)
		movq	%rcx, -64(%rbp)
		movq	%rsi, -72(%rbp)
		movq	%rdi, -80(%rbp)
		movq	%rdx, -88(%rbp)
		movq	%rsp, -96(%rbp)
		movq	%r8, -104(%rbp)
		movq	%r9, -112(%rbp)
		movq	%r10, -120(%rbp)
		movq	%r11, -128(%rbp)
		# Pass pointer to return space
		movq	-136(%rbp), %r8
		# Pass pointer to argument space
		movq	-144(%rbp), %r9
		movq	-344(%rbp), %r13
		movq	%r13, %rdi
		movq	%r13, -344(%rbp)
		callq	 FUNC(Iprint_pai)
		movq	-352(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -352(%rbp)
		# Function call epilogue
		movq	-56(%rbp), %rax
		movq	-64(%rbp), %rcx
		movq	-72(%rbp), %rsi
		movq	-80(%rbp), %rdi
		movq	-88(%rbp), %rdx
		movq	-96(%rbp), %rsp
		movq	-104(%rbp), %r8
		movq	-112(%rbp), %r9
		movq	-120(%rbp), %r10
		movq	-128(%rbp), %r11
		# (JUMP (NAME temp_20)) -- jump1
		jmp	temp_20
		# (LABEL temp_20) -- label1
temp_20:
		# Function epilogue
		# Restoring callee-save registers
		movq	-8(%rbp), %rbx
		movq	-16(%rbp), %rbp
		movq	-24(%rbp), %r12
		movq	-32(%rbp), %r13
		movq	-40(%rbp), %r14
		movq	-48(%rbp), %r15
		# Restore old RBP and RSP
		addq	$368, %rsp
		popq	%rbp
		retq	

