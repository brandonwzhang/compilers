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
		.globl	FUNC(_Imain_paai)
		.globl	FUNC(_Iprint_pai)
		.globl	FUNC(_Iprintln_pai)
		.globl	FUNC(_Ireadln_ai)
		.globl	FUNC(_Igetchar_i)
		.globl	FUNC(_Ieof_b)
		.align	4
FUNC(_Imain_paai):
		subq	$1216, %rsp
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
		# (MOVE (TEMP temp_82) (TEMP _ARG0)) -- move1
		movq	-160(%rbp), %r13
		movq	%rdx, %r13
		movq	%r13, -160(%rbp)
		# (MOVE (TEMP args) (TEMP temp_82)) -- move1
		movq	-168(%rbp), %r14
		movq	-160(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -160(%rbp)
		movq	%r14, -168(%rbp)
		# (MOVE (TEMP temp_83) (CONST 10)) -- move1
		movq	-176(%rbp), %r13
		movq	$10, %r13
		movq	%r13, -176(%rbp)
		# (MOVE (TEMP temp0) (TEMP temp_83)) -- move1
		movq	-184(%rbp), %r14
		movq	-176(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -176(%rbp)
		movq	%r14, -184(%rbp)
		# (MOVE (TEMP temp_84) (TEMP temp0)) -- move1
		movq	-192(%rbp), %r14
		movq	-184(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -184(%rbp)
		movq	%r14, -192(%rbp)
		# (MOVE (TEMP temp1) (TEMP temp_84)) -- move1
		movq	-200(%rbp), %r14
		movq	-192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -192(%rbp)
		movq	%r14, -200(%rbp)
		# (MOVE (TEMP temp_85) (TEMP temp1)) -- move1
		movq	-208(%rbp), %r14
		movq	-200(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -200(%rbp)
		movq	%r14, -208(%rbp)
		# (MOVE (TEMP temp_86) (ADD (TEMP temp_85) (CONST 1))) -- move1
		movq	-216(%rbp), %r14
		movq	-208(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -208(%rbp)
		movq	%r14, -216(%rbp)
		movq	-216(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -216(%rbp)
		movq	-224(%rbp), %r14
		movq	-216(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -216(%rbp)
		movq	%r14, -224(%rbp)
		# (MOVE (TEMP temp_87) (MUL (TEMP temp_86) (CONST 8))) -- move1
		movq	%rax, -144(%rbp)
		movq	%rdx, -152(%rbp)
		movq	-224(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -224(%rbp)
		mulq	$8
		movq	-232(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -232(%rbp)
		movq	-144(%rbp), %rax
		movq	-152(%rbp), %rdx
		movq	-240(%rbp), %r14
		movq	-232(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -232(%rbp)
		movq	%r14, -240(%rbp)
		# (MOVE (TEMP temp_88) (CALL (NAME _I_alloc_i) (TEMP temp_87))) -- move2
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
		# Pass pointer to return space as first argument
		movq	-136(%rbp), %rdi
		movq	-136(%rbp), %rsi
		movq	-240(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -240(%rbp)
		callq	 FUNC(_I_alloc_i)
		movq	-248(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -248(%rbp)
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
		# (MOVE (TEMP temp_89) (TEMP temp_88)) -- move1
		movq	-256(%rbp), %r14
		movq	-248(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -248(%rbp)
		movq	%r14, -256(%rbp)
		# (MOVE (TEMP temp2) (TEMP temp_89)) -- move1
		movq	-264(%rbp), %r14
		movq	-256(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -256(%rbp)
		movq	%r14, -264(%rbp)
		# (MOVE (TEMP temp_90) (TEMP temp1)) -- move1
		movq	-272(%rbp), %r14
		movq	-200(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -200(%rbp)
		movq	%r14, -272(%rbp)
		# (MOVE (MEM (TEMP temp2)) (TEMP temp_90)) -- move1
		movq	-264(%rbp), %r14
		movq	-272(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -272(%rbp)
		movq	%r14, -264(%rbp)
		# (MOVE (TEMP temp_91) (TEMP temp2)) -- move1
		movq	-280(%rbp), %r14
		movq	-264(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -264(%rbp)
		movq	%r14, -280(%rbp)
		# (MOVE (TEMP temp_92) (ADD (TEMP temp_91) (CONST 8))) -- move1
		movq	-288(%rbp), %r14
		movq	-280(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -280(%rbp)
		movq	%r14, -288(%rbp)
		movq	-288(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -288(%rbp)
		movq	-296(%rbp), %r14
		movq	-288(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -288(%rbp)
		movq	%r14, -296(%rbp)
		# (MOVE (TEMP temp2) (TEMP temp_92)) -- move1
		movq	-264(%rbp), %r14
		movq	-296(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -296(%rbp)
		movq	%r14, -264(%rbp)
		# (MOVE (TEMP temp_93) (TEMP temp2)) -- move1
		movq	-304(%rbp), %r14
		movq	-264(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -264(%rbp)
		movq	%r14, -304(%rbp)
		# (MOVE (TEMP a) (TEMP temp_93)) -- move1
		movq	-312(%rbp), %r14
		movq	-304(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -304(%rbp)
		movq	%r14, -312(%rbp)
		# (MOVE (TEMP temp_94) (TEMP a)) -- move1
		movq	-320(%rbp), %r14
		movq	-312(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -312(%rbp)
		movq	%r14, -320(%rbp)
		# (MOVE (TEMP temp_95) (MEM (SUB (TEMP temp_94) (CONST 8)))) -- move1
		movq	-328(%rbp), %r14
		movq	-320(%rbp), %r13
		movq	-8(%r13), %r14
		movq	%r13, -320(%rbp)
		movq	%r14, -328(%rbp)
		# (MOVE (TEMP n) (TEMP temp_95)) -- move1
		movq	-336(%rbp), %r14
		movq	-328(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -328(%rbp)
		movq	%r14, -336(%rbp)
		# (MOVE (TEMP temp_96) (CONST 0)) -- move1
		movq	-344(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -344(%rbp)
		# (MOVE (TEMP i) (TEMP temp_96)) -- move1
		movq	-352(%rbp), %r14
		movq	-344(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -344(%rbp)
		movq	%r14, -352(%rbp)
		# (LABEL temp3) -- label1
temp3:
		# (MOVE (TEMP temp_97) (TEMP i)) -- move1
		movq	-360(%rbp), %r14
		movq	-352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -360(%rbp)
		# (CJUMP (LT (TEMP temp_97) (TEMP n)) temp4) -- cjump1
		movq	%rax, -144(%rbp)
		movq	-336(%rbp), %r14
		movq	-360(%rbp), %r13
		cmp	%r13, %r14
		movq	%r13, -360(%rbp)
		movq	%r14, -336(%rbp)
		setlq	%al
		movq	-368(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -368(%rbp)
		movq	-144(%rbp), %rax
		movq	-368(%rbp), %r13
		cmp	%r13, $0
		movq	%r13, -368(%rbp)
		jne	temp4
		# (LABEL temp5) -- label1
temp5:
		# (MOVE (TEMP temp_115) (TEMP a)) -- move1
		movq	-376(%rbp), %r14
		movq	-312(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -312(%rbp)
		movq	%r14, -376(%rbp)
		# (MOVE (TEMP temp_116) (CALL (NAME _Iprintln_pai) (TEMP temp_115))) -- move2
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
		# Pass pointer to return space as first argument
		movq	-136(%rbp), %rdi
		movq	-136(%rbp), %rsi
		movq	-376(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -376(%rbp)
		callq	 FUNC(_Iprintln_pai)
		movq	-384(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -384(%rbp)
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
		# (MOVE (TEMP temp_117) (CONST 0)) -- move1
		movq	-392(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -392(%rbp)
		# (MOVE (TEMP i) (TEMP temp_117)) -- move1
		movq	-352(%rbp), %r14
		movq	-392(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -392(%rbp)
		movq	%r14, -352(%rbp)
		# (MOVE (TEMP temp_118) (CONST 0)) -- move1
		movq	-400(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -400(%rbp)
		# (MOVE (TEMP j) (TEMP temp_118)) -- move1
		movq	-408(%rbp), %r14
		movq	-400(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -400(%rbp)
		movq	%r14, -408(%rbp)
		# (MOVE (TEMP temp_119) (CONST 0)) -- move1
		movq	-416(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -416(%rbp)
		# (MOVE (TEMP k) (TEMP temp_119)) -- move1
		movq	-424(%rbp), %r14
		movq	-416(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -416(%rbp)
		movq	%r14, -424(%rbp)
		# (MOVE (TEMP temp_120) (CONST 20)) -- move1
		movq	-432(%rbp), %r13
		movq	$20, %r13
		movq	%r13, -432(%rbp)
		# (MOVE (TEMP temp20) (TEMP temp_120)) -- move1
		movq	-440(%rbp), %r14
		movq	-432(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -432(%rbp)
		movq	%r14, -440(%rbp)
		# (MOVE (TEMP temp_121) (TEMP temp20)) -- move1
		movq	-448(%rbp), %r14
		movq	-440(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -440(%rbp)
		movq	%r14, -448(%rbp)
		# (MOVE (TEMP temp21) (TEMP temp_121)) -- move1
		movq	-456(%rbp), %r14
		movq	-448(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -448(%rbp)
		movq	%r14, -456(%rbp)
		# (MOVE (TEMP temp_122) (TEMP temp21)) -- move1
		movq	-464(%rbp), %r14
		movq	-456(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -456(%rbp)
		movq	%r14, -464(%rbp)
		# (MOVE (TEMP temp_123) (ADD (TEMP temp_122) (CONST 1))) -- move1
		movq	-472(%rbp), %r14
		movq	-464(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -464(%rbp)
		movq	%r14, -472(%rbp)
		movq	-472(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -472(%rbp)
		movq	-480(%rbp), %r14
		movq	-472(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -472(%rbp)
		movq	%r14, -480(%rbp)
		# (MOVE (TEMP temp_124) (MUL (TEMP temp_123) (CONST 8))) -- move1
		movq	%rax, -144(%rbp)
		movq	%rdx, -152(%rbp)
		movq	-480(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -480(%rbp)
		mulq	$8
		movq	-488(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -488(%rbp)
		movq	-144(%rbp), %rax
		movq	-152(%rbp), %rdx
		movq	-496(%rbp), %r14
		movq	-488(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -488(%rbp)
		movq	%r14, -496(%rbp)
		# (MOVE (TEMP temp_125) (CALL (NAME _I_alloc_i) (TEMP temp_124))) -- move2
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
		# Pass pointer to return space as first argument
		movq	-136(%rbp), %rdi
		movq	-136(%rbp), %rsi
		movq	-496(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -496(%rbp)
		callq	 FUNC(_I_alloc_i)
		movq	-504(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -504(%rbp)
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
		# (MOVE (TEMP temp_126) (TEMP temp_125)) -- move1
		movq	-512(%rbp), %r14
		movq	-504(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -504(%rbp)
		movq	%r14, -512(%rbp)
		# (MOVE (TEMP temp22) (TEMP temp_126)) -- move1
		movq	-520(%rbp), %r14
		movq	-512(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -512(%rbp)
		movq	%r14, -520(%rbp)
		# (MOVE (TEMP temp_127) (TEMP temp21)) -- move1
		movq	-528(%rbp), %r14
		movq	-456(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -456(%rbp)
		movq	%r14, -528(%rbp)
		# (MOVE (MEM (TEMP temp22)) (TEMP temp_127)) -- move1
		movq	-520(%rbp), %r14
		movq	-528(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -528(%rbp)
		movq	%r14, -520(%rbp)
		# (MOVE (TEMP temp_128) (TEMP temp22)) -- move1
		movq	-536(%rbp), %r14
		movq	-520(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -520(%rbp)
		movq	%r14, -536(%rbp)
		# (MOVE (TEMP temp_129) (ADD (TEMP temp_128) (CONST 8))) -- move1
		movq	-544(%rbp), %r14
		movq	-536(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -536(%rbp)
		movq	%r14, -544(%rbp)
		movq	-544(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -544(%rbp)
		movq	-552(%rbp), %r14
		movq	-544(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -544(%rbp)
		movq	%r14, -552(%rbp)
		# (MOVE (TEMP temp22) (TEMP temp_129)) -- move1
		movq	-520(%rbp), %r14
		movq	-552(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -552(%rbp)
		movq	%r14, -520(%rbp)
		# (MOVE (TEMP temp_130) (TEMP temp22)) -- move1
		movq	-560(%rbp), %r14
		movq	-520(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -520(%rbp)
		movq	%r14, -560(%rbp)
		# (MOVE (TEMP b) (TEMP temp_130)) -- move1
		movq	-568(%rbp), %r14
		movq	-560(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -560(%rbp)
		movq	%r14, -568(%rbp)
		# (MOVE (TEMP temp_131) (TEMP b)) -- move1
		movq	-576(%rbp), %r14
		movq	-568(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -568(%rbp)
		movq	%r14, -576(%rbp)
		# (MOVE (TEMP temp_132) (MEM (SUB (TEMP temp_131) (CONST 8)))) -- move1
		movq	-584(%rbp), %r14
		movq	-576(%rbp), %r13
		movq	-8(%r13), %r14
		movq	%r13, -576(%rbp)
		movq	%r14, -584(%rbp)
		# (MOVE (TEMP n) (TEMP temp_132)) -- move1
		movq	-336(%rbp), %r14
		movq	-584(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -584(%rbp)
		movq	%r14, -336(%rbp)
		# (LABEL temp23) -- label1
temp23:
		# (MOVE (TEMP temp_133) (TEMP k)) -- move1
		movq	-592(%rbp), %r14
		movq	-424(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -424(%rbp)
		movq	%r14, -592(%rbp)
		# (CJUMP (LT (TEMP temp_133) (TEMP n)) temp24) -- cjump1
		movq	%rax, -144(%rbp)
		movq	-336(%rbp), %r14
		movq	-592(%rbp), %r13
		cmp	%r13, %r14
		movq	%r13, -592(%rbp)
		movq	%r14, -336(%rbp)
		setlq	%al
		movq	-600(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -600(%rbp)
		movq	-144(%rbp), %rax
		movq	-600(%rbp), %r13
		cmp	%r13, $0
		movq	%r13, -600(%rbp)
		jne	temp24
		# (LABEL temp25) -- label1
temp25:
		# (MOVE (TEMP temp_159) (TEMP b)) -- move1
		movq	-608(%rbp), %r14
		movq	-568(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -568(%rbp)
		movq	%r14, -608(%rbp)
		# (MOVE (TEMP temp_160) (CALL (NAME _Iprintln_pai) (TEMP temp_159))) -- move2
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
		# Pass pointer to return space as first argument
		movq	-136(%rbp), %rdi
		movq	-136(%rbp), %rsi
		movq	-608(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -608(%rbp)
		callq	 FUNC(_Iprintln_pai)
		movq	-616(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -616(%rbp)
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
		# (JUMP (NAME temp_163)) -- jump1
		jmp	temp_163
		# (JUMP (NAME temp_161)) -- jump1
		jmp	temp_161
		# (LABEL temp24) -- label1
temp24:
		# (LABEL temp28) -- label1
temp28:
		# (MOVE (TEMP temp_134) (TEMP i)) -- move1
		movq	-624(%rbp), %r14
		movq	-352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -624(%rbp)
		# (CJUMP (LT (TEMP temp_134) (CONST 10)) temp29) -- cjump1
		movq	%rax, -144(%rbp)
		movq	-624(%rbp), %r13
		cmp	%r13, $10
		movq	%r13, -624(%rbp)
		setlq	%al
		movq	-632(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -632(%rbp)
		movq	-144(%rbp), %rax
		movq	-632(%rbp), %r13
		cmp	%r13, $0
		movq	%r13, -632(%rbp)
		jne	temp29
		# (LABEL temp30) -- label1
temp30:
		# (MOVE (TEMP temp_156) (CONST 0)) -- move1
		movq	-640(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -640(%rbp)
		# (MOVE (TEMP i) (TEMP temp_156)) -- move1
		movq	-352(%rbp), %r14
		movq	-640(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -640(%rbp)
		movq	%r14, -352(%rbp)
		# (MOVE (TEMP temp_157) (TEMP j)) -- move1
		movq	-648(%rbp), %r14
		movq	-408(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -408(%rbp)
		movq	%r14, -648(%rbp)
		# (MOVE (TEMP temp_158) (ADD (TEMP temp_157) (CONST 1))) -- move1
		movq	-656(%rbp), %r14
		movq	-648(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -648(%rbp)
		movq	%r14, -656(%rbp)
		movq	-656(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -656(%rbp)
		movq	-664(%rbp), %r14
		movq	-656(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -656(%rbp)
		movq	%r14, -664(%rbp)
		# (MOVE (TEMP j) (TEMP temp_158)) -- move1
		movq	-408(%rbp), %r14
		movq	-664(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -664(%rbp)
		movq	%r14, -408(%rbp)
		# (JUMP (NAME temp23)) -- jump1
		jmp	temp23
		# (LABEL temp29) -- label1
temp29:
		# (MOVE (TEMP temp_135) (TEMP b)) -- move1
		movq	-672(%rbp), %r14
		movq	-568(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -568(%rbp)
		movq	%r14, -672(%rbp)
		# (MOVE (TEMP temp41) (TEMP temp_135)) -- move1
		movq	-680(%rbp), %r14
		movq	-672(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -672(%rbp)
		movq	%r14, -680(%rbp)
		# (MOVE (TEMP temp_136) (TEMP temp41)) -- move1
		movq	-688(%rbp), %r14
		movq	-680(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -680(%rbp)
		movq	%r14, -688(%rbp)
		# (MOVE (TEMP temp47) (TEMP temp_136)) -- move1
		movq	-696(%rbp), %r14
		movq	-688(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -688(%rbp)
		movq	%r14, -696(%rbp)
		# (MOVE (TEMP temp_137) (TEMP k)) -- move1
		movq	-704(%rbp), %r14
		movq	-424(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -424(%rbp)
		movq	%r14, -704(%rbp)
		# (MOVE (TEMP temp42) (TEMP temp_137)) -- move1
		movq	-712(%rbp), %r14
		movq	-704(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -704(%rbp)
		movq	%r14, -712(%rbp)
		# (MOVE (TEMP temp_138) (TEMP temp42)) -- move1
		movq	-720(%rbp), %r14
		movq	-712(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -712(%rbp)
		movq	%r14, -720(%rbp)
		# (MOVE (TEMP temp46) (TEMP temp_138)) -- move1
		movq	-728(%rbp), %r14
		movq	-720(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -720(%rbp)
		movq	%r14, -728(%rbp)
		# (MOVE (TEMP temp_139) (TEMP temp46)) -- move1
		movq	-736(%rbp), %r14
		movq	-728(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -728(%rbp)
		movq	%r14, -736(%rbp)
		# (MOVE (TEMP temp48) (TEMP temp_139)) -- move1
		movq	-744(%rbp), %r14
		movq	-736(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -736(%rbp)
		movq	%r14, -744(%rbp)
		# (MOVE (TEMP temp_141) (TEMP temp42)) -- move1
		movq	-752(%rbp), %r14
		movq	-712(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -712(%rbp)
		movq	%r14, -752(%rbp)
		# (MOVE (TEMP temp_140) (TEMP temp41)) -- move1
		movq	-760(%rbp), %r14
		movq	-680(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -680(%rbp)
		movq	%r14, -760(%rbp)
		# (MOVE (TEMP temp_143)
 (LT (TEMP temp_141) (MEM (SUB (TEMP temp_140) (CONST 8))))) -- move1
		movq	%rax, -144(%rbp)
		movq	-760(%rbp), %r14
		movq	-752(%rbp), %r13
		cmp	%r13, -8(%r14)
		movq	%r13, -752(%rbp)
		movq	%r14, -760(%rbp)
		setlq	%al
		movq	-768(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -768(%rbp)
		movq	-144(%rbp), %rax
		movq	-776(%rbp), %r14
		movq	-768(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -768(%rbp)
		movq	%r14, -776(%rbp)
		# (MOVE (TEMP temp_142) (TEMP temp46)) -- move1
		movq	-784(%rbp), %r14
		movq	-728(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -728(%rbp)
		movq	%r14, -784(%rbp)
		# (CJUMP (AND (TEMP temp_143) (GEQ (TEMP temp_142) (CONST 0))) temp43) -- cjump1
		movq	%rax, -144(%rbp)
		movq	-784(%rbp), %r13
		cmp	%r13, $0
		movq	%r13, -784(%rbp)
		setgeq	%al
		movq	-792(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -792(%rbp)
		movq	-144(%rbp), %rax
		movq	-800(%rbp), %r14
		movq	-776(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -776(%rbp)
		movq	%r14, -800(%rbp)
		movq	-800(%rbp), %r14
		movq	-792(%rbp), %r13
		andq	%r13, %r14
		movq	%r13, -792(%rbp)
		movq	%r14, -800(%rbp)
		movq	-800(%rbp), %r13
		cmp	%r13, $0
		movq	%r13, -800(%rbp)
		jne	temp43
		# (LABEL temp44) -- label1
temp44:
		# (MOVE (TEMP temp_151) (CALL (NAME _I_outOfBounds_p))) -- move2
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
		# Pass pointer to return space as first argument
		movq	-136(%rbp), %rdi
		movq	-136(%rbp), %rsi
		callq	 FUNC(_I_outOfBounds_p)
		movq	-808(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -808(%rbp)
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
		# (LABEL temp45) -- label1
temp45:
		# (MOVE (TEMP temp_152) (TEMP k)) -- move1
		movq	-816(%rbp), %r14
		movq	-424(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -424(%rbp)
		movq	%r14, -816(%rbp)
		# (MOVE (TEMP temp_153) (ADD (TEMP temp_152) (CONST 1))) -- move1
		movq	-824(%rbp), %r14
		movq	-816(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -816(%rbp)
		movq	%r14, -824(%rbp)
		movq	-824(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -824(%rbp)
		movq	-832(%rbp), %r14
		movq	-824(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -824(%rbp)
		movq	%r14, -832(%rbp)
		# (MOVE (TEMP k) (TEMP temp_153)) -- move1
		movq	-424(%rbp), %r14
		movq	-832(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -832(%rbp)
		movq	%r14, -424(%rbp)
		# (MOVE (TEMP temp_154) (TEMP i)) -- move1
		movq	-840(%rbp), %r14
		movq	-352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -840(%rbp)
		# (MOVE (TEMP temp_155) (ADD (TEMP temp_154) (CONST 1))) -- move1
		movq	-848(%rbp), %r14
		movq	-840(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -840(%rbp)
		movq	%r14, -848(%rbp)
		movq	-848(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -848(%rbp)
		movq	-856(%rbp), %r14
		movq	-848(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -848(%rbp)
		movq	%r14, -856(%rbp)
		# (MOVE (TEMP i) (TEMP temp_155)) -- move1
		movq	-352(%rbp), %r14
		movq	-856(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -856(%rbp)
		movq	%r14, -352(%rbp)
		# (JUMP (NAME temp28)) -- jump1
		jmp	temp28
		# (LABEL temp43) -- label1
temp43:
		# (MOVE (TEMP temp_147) (CONST 100)) -- move1
		movq	-864(%rbp), %r13
		movq	$100, %r13
		movq	%r13, -864(%rbp)
		# (MOVE (TEMP temp_146) (CONST 10)) -- move1
		movq	-872(%rbp), %r13
		movq	$10, %r13
		movq	%r13, -872(%rbp)
		# (MOVE (TEMP temp_148) (ADD (TEMP temp_147) (MUL (TEMP temp_146) (TEMP j)))) -- move1
		movq	%rax, -144(%rbp)
		movq	%rdx, -152(%rbp)
		movq	-872(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -872(%rbp)
		movq	-408(%rbp), %r13
		mulq	%r13
		movq	%r13, -408(%rbp)
		movq	-880(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -880(%rbp)
		movq	-144(%rbp), %rax
		movq	-152(%rbp), %rdx
		movq	-888(%rbp), %r14
		movq	-864(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -864(%rbp)
		movq	%r14, -888(%rbp)
		movq	-888(%rbp), %r14
		movq	-880(%rbp), %r13
		addq	%r13, %r14
		movq	%r13, -880(%rbp)
		movq	%r14, -888(%rbp)
		movq	-896(%rbp), %r14
		movq	-888(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -888(%rbp)
		movq	%r14, -896(%rbp)
		# (MOVE (TEMP temp_149) (ADD (TEMP temp_148) (TEMP i))) -- move1
		movq	-904(%rbp), %r14
		movq	-896(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -896(%rbp)
		movq	%r14, -904(%rbp)
		movq	-904(%rbp), %r14
		movq	-352(%rbp), %r13
		addq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -904(%rbp)
		movq	-912(%rbp), %r14
		movq	-904(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -904(%rbp)
		movq	%r14, -912(%rbp)
		# (MOVE (TEMP temp_150) (ADD (TEMP temp_149) (CONST 1))) -- move1
		movq	-920(%rbp), %r14
		movq	-912(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -912(%rbp)
		movq	%r14, -920(%rbp)
		movq	-920(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -920(%rbp)
		movq	-928(%rbp), %r14
		movq	-920(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -920(%rbp)
		movq	%r14, -928(%rbp)
		# (MOVE (TEMP temp_145) (TEMP temp47)) -- move1
		movq	-936(%rbp), %r14
		movq	-696(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -696(%rbp)
		movq	%r14, -936(%rbp)
		# (MOVE (TEMP temp_144) (TEMP temp48)) -- move1
		movq	-944(%rbp), %r14
		movq	-744(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -744(%rbp)
		movq	%r14, -944(%rbp)
		# (MOVE (MEM (ADD (TEMP temp_145) (MUL (TEMP temp_144) (CONST 8))))
 (TEMP temp_150)) -- move1
		movq	%rax, -144(%rbp)
		movq	%rdx, -152(%rbp)
		movq	-944(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -944(%rbp)
		mulq	$8
		movq	-952(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -952(%rbp)
		movq	-144(%rbp), %rax
		movq	-152(%rbp), %rdx
		movq	-960(%rbp), %r14
		movq	-936(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -936(%rbp)
		movq	%r14, -960(%rbp)
		movq	-960(%rbp), %r14
		movq	-952(%rbp), %r13
		addq	%r13, %r14
		movq	%r13, -952(%rbp)
		movq	%r14, -960(%rbp)
		movq	-960(%rbp), %r14
		movq	-928(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -928(%rbp)
		movq	%r14, -960(%rbp)
		# (JUMP (NAME temp45)) -- jump1
		jmp	temp45
		# (LABEL temp_161) -- label1
temp_161:
		# (JUMP (NAME temp_162)) -- jump1
		jmp	temp_162
		# (LABEL temp4) -- label1
temp4:
		# (MOVE (TEMP temp_98) (TEMP i)) -- move1
		movq	-968(%rbp), %r14
		movq	-352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -968(%rbp)
		# (MOVE (TEMP temp_99) (ADD (TEMP temp_98) (CONST 100))) -- move1
		movq	-976(%rbp), %r14
		movq	-968(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -968(%rbp)
		movq	%r14, -976(%rbp)
		movq	-976(%rbp), %r13
		addq	$100, %r13
		movq	%r13, -976(%rbp)
		movq	-984(%rbp), %r14
		movq	-976(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -976(%rbp)
		movq	%r14, -984(%rbp)
		# (MOVE (TEMP c) (TEMP temp_99)) -- move1
		movq	-992(%rbp), %r14
		movq	-984(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -984(%rbp)
		movq	%r14, -992(%rbp)
		# (MOVE (TEMP temp_100) (TEMP a)) -- move1
		movq	-1000(%rbp), %r14
		movq	-312(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -312(%rbp)
		movq	%r14, -1000(%rbp)
		# (MOVE (TEMP temp10) (TEMP temp_100)) -- move1
		movq	-1008(%rbp), %r14
		movq	-1000(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1000(%rbp)
		movq	%r14, -1008(%rbp)
		# (MOVE (TEMP temp_101) (TEMP temp10)) -- move1
		movq	-1016(%rbp), %r14
		movq	-1008(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1008(%rbp)
		movq	%r14, -1016(%rbp)
		# (MOVE (TEMP temp16) (TEMP temp_101)) -- move1
		movq	-1024(%rbp), %r14
		movq	-1016(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1016(%rbp)
		movq	%r14, -1024(%rbp)
		# (MOVE (TEMP temp_102) (TEMP i)) -- move1
		movq	-1032(%rbp), %r14
		movq	-352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -1032(%rbp)
		# (MOVE (TEMP temp11) (TEMP temp_102)) -- move1
		movq	-1040(%rbp), %r14
		movq	-1032(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1032(%rbp)
		movq	%r14, -1040(%rbp)
		# (MOVE (TEMP temp_103) (TEMP temp11)) -- move1
		movq	-1048(%rbp), %r14
		movq	-1040(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1040(%rbp)
		movq	%r14, -1048(%rbp)
		# (MOVE (TEMP temp15) (TEMP temp_103)) -- move1
		movq	-1056(%rbp), %r14
		movq	-1048(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1048(%rbp)
		movq	%r14, -1056(%rbp)
		# (MOVE (TEMP temp_104) (TEMP temp15)) -- move1
		movq	-1064(%rbp), %r14
		movq	-1056(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1056(%rbp)
		movq	%r14, -1064(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_104)) -- move1
		movq	-1072(%rbp), %r14
		movq	-1064(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1064(%rbp)
		movq	%r14, -1072(%rbp)
		# (MOVE (TEMP temp_106) (TEMP temp11)) -- move1
		movq	-1080(%rbp), %r14
		movq	-1040(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1040(%rbp)
		movq	%r14, -1080(%rbp)
		# (MOVE (TEMP temp_105) (TEMP temp10)) -- move1
		movq	-1088(%rbp), %r14
		movq	-1008(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1008(%rbp)
		movq	%r14, -1088(%rbp)
		# (MOVE (TEMP temp_108)
 (LT (TEMP temp_106) (MEM (SUB (TEMP temp_105) (CONST 8))))) -- move1
		movq	%rax, -144(%rbp)
		movq	-1088(%rbp), %r14
		movq	-1080(%rbp), %r13
		cmp	%r13, -8(%r14)
		movq	%r13, -1080(%rbp)
		movq	%r14, -1088(%rbp)
		setlq	%al
		movq	-1096(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -1096(%rbp)
		movq	-144(%rbp), %rax
		movq	-1104(%rbp), %r14
		movq	-1096(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1096(%rbp)
		movq	%r14, -1104(%rbp)
		# (MOVE (TEMP temp_107) (TEMP temp15)) -- move1
		movq	-1112(%rbp), %r14
		movq	-1056(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1056(%rbp)
		movq	%r14, -1112(%rbp)
		# (CJUMP (AND (TEMP temp_108) (GEQ (TEMP temp_107) (CONST 0))) temp12) -- cjump1
		movq	%rax, -144(%rbp)
		movq	-1112(%rbp), %r13
		cmp	%r13, $0
		movq	%r13, -1112(%rbp)
		setgeq	%al
		movq	-1120(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -1120(%rbp)
		movq	-144(%rbp), %rax
		movq	-1128(%rbp), %r14
		movq	-1104(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1104(%rbp)
		movq	%r14, -1128(%rbp)
		movq	-1128(%rbp), %r14
		movq	-1120(%rbp), %r13
		andq	%r13, %r14
		movq	%r13, -1120(%rbp)
		movq	%r14, -1128(%rbp)
		movq	-1128(%rbp), %r13
		cmp	%r13, $0
		movq	%r13, -1128(%rbp)
		jne	temp12
		# (LABEL temp13) -- label1
temp13:
		# (MOVE (TEMP temp_112) (CALL (NAME _I_outOfBounds_p))) -- move2
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
		# Pass pointer to return space as first argument
		movq	-136(%rbp), %rdi
		movq	-136(%rbp), %rsi
		callq	 FUNC(_I_outOfBounds_p)
		movq	-1136(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1136(%rbp)
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
		# (LABEL temp14) -- label1
temp14:
		# (MOVE (TEMP temp_113) (TEMP i)) -- move1
		movq	-1144(%rbp), %r14
		movq	-352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -1144(%rbp)
		# (MOVE (TEMP temp_114) (ADD (TEMP temp_113) (CONST 1))) -- move1
		movq	-1152(%rbp), %r14
		movq	-1144(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1144(%rbp)
		movq	%r14, -1152(%rbp)
		movq	-1152(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -1152(%rbp)
		movq	-1160(%rbp), %r14
		movq	-1152(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1152(%rbp)
		movq	%r14, -1160(%rbp)
		# (MOVE (TEMP i) (TEMP temp_114)) -- move1
		movq	-352(%rbp), %r14
		movq	-1160(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1160(%rbp)
		movq	%r14, -352(%rbp)
		# (JUMP (NAME temp3)) -- jump1
		jmp	temp3
		# (LABEL temp12) -- label1
temp12:
		# (MOVE (TEMP temp_111) (TEMP c)) -- move1
		movq	-1168(%rbp), %r14
		movq	-992(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -992(%rbp)
		movq	%r14, -1168(%rbp)
		# (MOVE (TEMP temp_110) (TEMP temp16)) -- move1
		movq	-1176(%rbp), %r14
		movq	-1024(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1024(%rbp)
		movq	%r14, -1176(%rbp)
		# (MOVE (TEMP temp_109) (TEMP temp17)) -- move1
		movq	-1184(%rbp), %r14
		movq	-1072(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1072(%rbp)
		movq	%r14, -1184(%rbp)
		# (MOVE (MEM (ADD (TEMP temp_110) (MUL (TEMP temp_109) (CONST 8))))
 (TEMP temp_111)) -- move1
		movq	%rax, -144(%rbp)
		movq	%rdx, -152(%rbp)
		movq	-1184(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1184(%rbp)
		mulq	$8
		movq	-1192(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1192(%rbp)
		movq	-144(%rbp), %rax
		movq	-152(%rbp), %rdx
		movq	-1200(%rbp), %r14
		movq	-1176(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1176(%rbp)
		movq	%r14, -1200(%rbp)
		movq	-1200(%rbp), %r14
		movq	-1192(%rbp), %r13
		addq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1200(%rbp)
		movq	-1200(%rbp), %r14
		movq	-1168(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1168(%rbp)
		movq	%r14, -1200(%rbp)
		# (JUMP (NAME temp14)) -- jump1
		jmp	temp14
		# (LABEL temp_163) -- label1
temp_163:
		# Function epilogue
		# Restoring callee-save registers
		movq	-8(%rbp), %rbx
		movq	-16(%rbp), %rbp
		movq	-24(%rbp), %r12
		movq	-32(%rbp), %r13
		movq	-40(%rbp), %r14
		movq	-48(%rbp), %r15
		# Restore old RBP and RSP
		movq	%rbp, %rsp
		popq	%rbp
		addq	$1216, %rsp
		retq	

