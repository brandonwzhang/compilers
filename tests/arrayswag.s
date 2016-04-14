		.text
		.globl	_Iprint_pai
		.globl	_Iprintln_pai
		.globl	_Ireadln_ai
		.globl	_Igetchar_i
		.globl	_Ieof_b
		.globl	_Imain_paai
		.align	4
_Imain_paai:
		# Starting function prologue
		enter	$2080, $0
		# Save callee-save registers rbx rbp r12-r15
		movq	%rbx, -8(%rbp)
		movq	%rbp, -16(%rbp)
		movq	%r12, -24(%rbp)
		movq	%r13, -32(%rbp)
		movq	%r14, -40(%rbp)
		movq	%r15, -48(%rbp)
		# (MOVE (TEMP temp_0) (TEMP _ARG0)) -- move1
		movq	-152(%rbp), %r13
		movq	%rdi, %r13
		movq	%r13, -152(%rbp)
		# (MOVE (TEMP args) (TEMP temp_0)) -- move1
		movq	-160(%rbp), %r14
		movq	-152(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -152(%rbp)
		movq	%r14, -160(%rbp)
		# (MOVE (TEMP temp_1) (CONST 3)) -- move1
		movq	-168(%rbp), %r13
		movq	$3, %r13
		movq	%r13, -168(%rbp)
		# (MOVE (TEMP temp0) (TEMP temp_1)) -- move1
		movq	-176(%rbp), %r14
		movq	-168(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -168(%rbp)
		movq	%r14, -176(%rbp)
		# (MOVE (TEMP temp_2) (CONST 2)) -- move1
		movq	-184(%rbp), %r13
		movq	$2, %r13
		movq	%r13, -184(%rbp)
		# (MOVE (TEMP temp1) (TEMP temp_2)) -- move1
		movq	-192(%rbp), %r14
		movq	-184(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -184(%rbp)
		movq	%r14, -192(%rbp)
		# (MOVE (TEMP temp_3) (CONST 10)) -- move1
		movq	-200(%rbp), %r13
		movq	$10, %r13
		movq	%r13, -200(%rbp)
		# (MOVE (TEMP temp2) (TEMP temp_3)) -- move1
		movq	-208(%rbp), %r14
		movq	-200(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -200(%rbp)
		movq	%r14, -208(%rbp)
		# (MOVE (TEMP temp_4) (TEMP temp0)) -- move1
		movq	-216(%rbp), %r14
		movq	-176(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -176(%rbp)
		movq	%r14, -216(%rbp)
		# (MOVE (TEMP temp3) (TEMP temp_4)) -- move1
		movq	-224(%rbp), %r14
		movq	-216(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -216(%rbp)
		movq	%r14, -224(%rbp)
		# (MOVE (TEMP temp_5) (TEMP temp3)) -- move1
		movq	-232(%rbp), %r14
		movq	-224(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -224(%rbp)
		movq	%r14, -232(%rbp)
		# (MOVE (TEMP temp_6) (ADD (TEMP temp_5) (CONST 1))) -- move1
		movq	-240(%rbp), %r14
		movq	-232(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -232(%rbp)
		movq	%r14, -240(%rbp)
		movq	-240(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -240(%rbp)
		movq	-248(%rbp), %r14
		movq	-240(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -240(%rbp)
		movq	%r14, -248(%rbp)
		# (MOVE (TEMP temp_7) (MUL (TEMP temp_6) (CONST 8))) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-248(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -248(%rbp)
		movq	-264(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -264(%rbp)
		movq	-264(%rbp), %r13
		mulq	%r13
		movq	%r13, -264(%rbp)
		movq	-256(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -256(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-272(%rbp), %r14
		movq	-256(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -256(%rbp)
		movq	%r14, -272(%rbp)
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		movq	-272(%rbp), %r13
		movq	%r13, %rdi
		movq	%r13, -272(%rbp)
		callq	_I_alloc_i
		movq	-280(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -280(%rbp)
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
		movq	-288(%rbp), %r14
		movq	-280(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -280(%rbp)
		movq	%r14, -288(%rbp)
		# (MOVE (TEMP temp4) (TEMP temp_9)) -- move1
		movq	-296(%rbp), %r14
		movq	-288(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -288(%rbp)
		movq	%r14, -296(%rbp)
		# (MOVE (TEMP temp_10) (TEMP temp3)) -- move1
		movq	-304(%rbp), %r14
		movq	-224(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -224(%rbp)
		movq	%r14, -304(%rbp)
		# (MOVE (MEM (TEMP temp4)) (TEMP temp_10)) -- move1
		movq	-296(%rbp), %r14
		movq	-304(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -304(%rbp)
		movq	%r14, -296(%rbp)
		# (MOVE (TEMP temp_11) (TEMP temp4)) -- move1
		movq	-312(%rbp), %r14
		movq	-296(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -296(%rbp)
		movq	%r14, -312(%rbp)
		# (MOVE (TEMP temp_12) (ADD (TEMP temp_11) (CONST 8))) -- move1
		movq	-320(%rbp), %r14
		movq	-312(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -312(%rbp)
		movq	%r14, -320(%rbp)
		movq	-320(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -320(%rbp)
		movq	-328(%rbp), %r14
		movq	-320(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -320(%rbp)
		movq	%r14, -328(%rbp)
		# (MOVE (TEMP temp4) (TEMP temp_12)) -- move1
		movq	-296(%rbp), %r14
		movq	-328(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -328(%rbp)
		movq	%r14, -296(%rbp)
		# (MOVE (TEMP temp_13) (TEMP temp4)) -- move1
		movq	-336(%rbp), %r14
		movq	-296(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -296(%rbp)
		movq	%r14, -336(%rbp)
		# (MOVE (TEMP a) (TEMP temp_13)) -- move1
		movq	-344(%rbp), %r14
		movq	-336(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -336(%rbp)
		movq	%r14, -344(%rbp)
		# (MOVE (TEMP temp_14) (CONST 0)) -- move1
		movq	-352(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -352(%rbp)
		# (MOVE (TEMP temp8) (TEMP temp_14)) -- move1
		movq	-360(%rbp), %r14
		movq	-352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -352(%rbp)
		movq	%r14, -360(%rbp)
		# (LABEL temp5) -- label1
temp5:
		# (MOVE (TEMP temp_15) (TEMP temp8)) -- move1
		movq	-368(%rbp), %r14
		movq	-360(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -360(%rbp)
		movq	%r14, -368(%rbp)
		# (CJUMP (LT (TEMP temp_15) (TEMP temp3)) temp6) -- cjump1
		movq	%rax, -136(%rbp)
		movq	-368(%rbp), %r14
		movq	-224(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -224(%rbp)
		movq	%r14, -368(%rbp)
		setl	%al
		movq	-376(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -376(%rbp)
		movq	-136(%rbp), %rax
		movq	-376(%rbp), %r13
		cmpq	$0, %r13
		movq	%r13, -376(%rbp)
		jne	temp6
		# (LABEL temp7) -- label1
temp7:
		# (MOVE (TEMP temp_46) (TEMP a)) -- move1
		movq	-384(%rbp), %r14
		movq	-344(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -344(%rbp)
		movq	%r14, -384(%rbp)
		# (MOVE (TEMP temp18) (TEMP temp_46)) -- move1
		movq	-392(%rbp), %r14
		movq	-384(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -384(%rbp)
		movq	%r14, -392(%rbp)
		# (MOVE (TEMP temp_47) (TEMP temp18)) -- move1
		movq	-400(%rbp), %r14
		movq	-392(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -392(%rbp)
		movq	%r14, -400(%rbp)
		# (MOVE (TEMP temp25) (TEMP temp_47)) -- move1
		movq	-408(%rbp), %r14
		movq	-400(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -400(%rbp)
		movq	%r14, -408(%rbp)
		# (MOVE (TEMP temp_48) (CONST 0)) -- move1
		movq	-416(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -416(%rbp)
		# (MOVE (TEMP temp19) (TEMP temp_48)) -- move1
		movq	-424(%rbp), %r14
		movq	-416(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -416(%rbp)
		movq	%r14, -424(%rbp)
		# (MOVE (TEMP temp_49) (TEMP temp19)) -- move1
		movq	-432(%rbp), %r14
		movq	-424(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -424(%rbp)
		movq	%r14, -432(%rbp)
		# (MOVE (TEMP temp24) (TEMP temp_49)) -- move1
		movq	-440(%rbp), %r14
		movq	-432(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -432(%rbp)
		movq	%r14, -440(%rbp)
		# (MOVE (TEMP temp_50) (TEMP temp19)) -- move1
		movq	-448(%rbp), %r14
		movq	-424(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -424(%rbp)
		movq	%r14, -448(%rbp)
		# (MOVE (TEMP temp26) (TEMP temp_50)) -- move1
		movq	-456(%rbp), %r14
		movq	-448(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -448(%rbp)
		movq	%r14, -456(%rbp)
		# (MOVE (TEMP temp_52) (TEMP temp19)) -- move1
		movq	-464(%rbp), %r14
		movq	-424(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -424(%rbp)
		movq	%r14, -464(%rbp)
		# (MOVE (TEMP temp_51) (TEMP temp18)) -- move1
		movq	-472(%rbp), %r14
		movq	-392(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -392(%rbp)
		movq	%r14, -472(%rbp)
		# (MOVE (TEMP temp_54) (LT (TEMP temp_52) (MEM (SUB (TEMP temp_51) (CONST 8))))) -- move1
		movq	-488(%rbp), %r14
		movq	-472(%rbp), %r13
		movq	-8(%r13), %r14
		movq	%r13, -472(%rbp)
		movq	%r14, -488(%rbp)
		movq	%rax, -136(%rbp)
		movq	-464(%rbp), %r14
		movq	-488(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -488(%rbp)
		movq	%r14, -464(%rbp)
		setl	%al
		movq	-480(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -480(%rbp)
		movq	-136(%rbp), %rax
		movq	-496(%rbp), %r14
		movq	-480(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -480(%rbp)
		movq	%r14, -496(%rbp)
		# (MOVE (TEMP temp_53) (TEMP temp24)) -- move1
		movq	-504(%rbp), %r14
		movq	-440(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -440(%rbp)
		movq	%r14, -504(%rbp)
		# (CJUMP (AND (TEMP temp_54) (GEQ (TEMP temp_53) (CONST 0))) temp21) -- cjump1
		movq	-520(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -520(%rbp)
		movq	%rax, -136(%rbp)
		movq	-504(%rbp), %r14
		movq	-520(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -520(%rbp)
		movq	%r14, -504(%rbp)
		setge	%al
		movq	-512(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -512(%rbp)
		movq	-136(%rbp), %rax
		movq	-528(%rbp), %r14
		movq	-496(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -496(%rbp)
		movq	%r14, -528(%rbp)
		movq	-528(%rbp), %r14
		movq	-512(%rbp), %r13
		andq	%r13, %r14
		movq	%r13, -512(%rbp)
		movq	%r14, -528(%rbp)
		movq	-528(%rbp), %r13
		cmpq	$0, %r13
		movq	%r13, -528(%rbp)
		jne	temp21
		# (LABEL temp22) -- label1
temp22:
		# (MOVE (TEMP temp_58) (CALL (NAME _I_outOfBounds_p))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		callq	_I_outOfBounds_p
		movq	-536(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -536(%rbp)
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
		# (MOVE (TEMP temp_59) (TEMP temp_58)) -- move1
		movq	-544(%rbp), %r14
		movq	-536(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -536(%rbp)
		movq	%r14, -544(%rbp)
		# (MOVE (TEMP temp20) (TEMP temp_59)) -- move1
		movq	-552(%rbp), %r14
		movq	-544(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -544(%rbp)
		movq	%r14, -552(%rbp)
		# (LABEL temp23) -- label1
temp23:
		# (MOVE (TEMP temp_60) (TEMP temp20)) -- move1
		movq	-560(%rbp), %r14
		movq	-552(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -552(%rbp)
		movq	%r14, -560(%rbp)
		# (MOVE (TEMP temp27) (TEMP temp_60)) -- move1
		movq	-568(%rbp), %r14
		movq	-560(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -560(%rbp)
		movq	%r14, -568(%rbp)
		# (MOVE (TEMP temp_61) (TEMP temp27)) -- move1
		movq	-576(%rbp), %r14
		movq	-568(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -568(%rbp)
		movq	%r14, -576(%rbp)
		# (MOVE (TEMP temp33) (TEMP temp_61)) -- move1
		movq	-584(%rbp), %r14
		movq	-576(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -576(%rbp)
		movq	%r14, -584(%rbp)
		# (MOVE (TEMP temp_62) (CONST 0)) -- move1
		movq	-592(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -592(%rbp)
		# (MOVE (TEMP temp28) (TEMP temp_62)) -- move1
		movq	-600(%rbp), %r14
		movq	-592(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -592(%rbp)
		movq	%r14, -600(%rbp)
		# (MOVE (TEMP temp_63) (TEMP temp28)) -- move1
		movq	-608(%rbp), %r14
		movq	-600(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -600(%rbp)
		movq	%r14, -608(%rbp)
		# (MOVE (TEMP temp32) (TEMP temp_63)) -- move1
		movq	-616(%rbp), %r14
		movq	-608(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -608(%rbp)
		movq	%r14, -616(%rbp)
		# (MOVE (TEMP temp_64) (TEMP temp32)) -- move1
		movq	-624(%rbp), %r14
		movq	-616(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -616(%rbp)
		movq	%r14, -624(%rbp)
		# (MOVE (TEMP temp34) (TEMP temp_64)) -- move1
		movq	-632(%rbp), %r14
		movq	-624(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -624(%rbp)
		movq	%r14, -632(%rbp)
		# (MOVE (TEMP temp_66) (TEMP temp28)) -- move1
		movq	-640(%rbp), %r14
		movq	-600(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -600(%rbp)
		movq	%r14, -640(%rbp)
		# (MOVE (TEMP temp_65) (TEMP temp27)) -- move1
		movq	-648(%rbp), %r14
		movq	-568(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -568(%rbp)
		movq	%r14, -648(%rbp)
		# (MOVE (TEMP temp_68) (LT (TEMP temp_66) (MEM (SUB (TEMP temp_65) (CONST 8))))) -- move1
		movq	-664(%rbp), %r14
		movq	-648(%rbp), %r13
		movq	-8(%r13), %r14
		movq	%r13, -648(%rbp)
		movq	%r14, -664(%rbp)
		movq	%rax, -136(%rbp)
		movq	-640(%rbp), %r14
		movq	-664(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -664(%rbp)
		movq	%r14, -640(%rbp)
		setl	%al
		movq	-656(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -656(%rbp)
		movq	-136(%rbp), %rax
		movq	-672(%rbp), %r14
		movq	-656(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -656(%rbp)
		movq	%r14, -672(%rbp)
		# (MOVE (TEMP temp_67) (TEMP temp32)) -- move1
		movq	-680(%rbp), %r14
		movq	-616(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -616(%rbp)
		movq	%r14, -680(%rbp)
		# (CJUMP (AND (TEMP temp_68) (GEQ (TEMP temp_67) (CONST 0))) temp29) -- cjump1
		movq	-696(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -696(%rbp)
		movq	%rax, -136(%rbp)
		movq	-680(%rbp), %r14
		movq	-696(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -696(%rbp)
		movq	%r14, -680(%rbp)
		setge	%al
		movq	-688(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -688(%rbp)
		movq	-136(%rbp), %rax
		movq	-704(%rbp), %r14
		movq	-672(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -672(%rbp)
		movq	%r14, -704(%rbp)
		movq	-704(%rbp), %r14
		movq	-688(%rbp), %r13
		andq	%r13, %r14
		movq	%r13, -688(%rbp)
		movq	%r14, -704(%rbp)
		movq	-704(%rbp), %r13
		cmpq	$0, %r13
		movq	%r13, -704(%rbp)
		jne	temp29
		# (LABEL temp30) -- label1
temp30:
		# (MOVE (TEMP temp_110) (CALL (NAME _I_outOfBounds_p))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		callq	_I_outOfBounds_p
		movq	-712(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -712(%rbp)
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
		# (LABEL temp31) -- label1
temp31:
		# (MOVE (TEMP temp_111) (TEMP a)) -- move1
		movq	-720(%rbp), %r14
		movq	-344(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -344(%rbp)
		movq	%r14, -720(%rbp)
		# (MOVE (TEMP temp35) (TEMP temp_111)) -- move1
		movq	-728(%rbp), %r14
		movq	-720(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -720(%rbp)
		movq	%r14, -728(%rbp)
		# (MOVE (TEMP temp_112) (TEMP temp35)) -- move1
		movq	-736(%rbp), %r14
		movq	-728(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -728(%rbp)
		movq	%r14, -736(%rbp)
		# (MOVE (TEMP temp42) (TEMP temp_112)) -- move1
		movq	-744(%rbp), %r14
		movq	-736(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -736(%rbp)
		movq	%r14, -744(%rbp)
		# (MOVE (TEMP temp_113) (CONST 0)) -- move1
		movq	-752(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -752(%rbp)
		# (MOVE (TEMP temp36) (TEMP temp_113)) -- move1
		movq	-760(%rbp), %r14
		movq	-752(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -752(%rbp)
		movq	%r14, -760(%rbp)
		# (MOVE (TEMP temp_114) (TEMP temp36)) -- move1
		movq	-768(%rbp), %r14
		movq	-760(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -760(%rbp)
		movq	%r14, -768(%rbp)
		# (MOVE (TEMP temp41) (TEMP temp_114)) -- move1
		movq	-776(%rbp), %r14
		movq	-768(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -768(%rbp)
		movq	%r14, -776(%rbp)
		# (MOVE (TEMP temp_115) (TEMP temp36)) -- move1
		movq	-784(%rbp), %r14
		movq	-760(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -760(%rbp)
		movq	%r14, -784(%rbp)
		# (MOVE (TEMP temp43) (TEMP temp_115)) -- move1
		movq	-792(%rbp), %r14
		movq	-784(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -784(%rbp)
		movq	%r14, -792(%rbp)
		# (MOVE (TEMP temp_117) (TEMP temp36)) -- move1
		movq	-800(%rbp), %r14
		movq	-760(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -760(%rbp)
		movq	%r14, -800(%rbp)
		# (MOVE (TEMP temp_116) (TEMP temp35)) -- move1
		movq	-808(%rbp), %r14
		movq	-728(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -728(%rbp)
		movq	%r14, -808(%rbp)
		# (MOVE (TEMP temp_119)
# (LT (TEMP temp_117) (MEM (SUB (TEMP temp_116) (CONST 8))))) -- move1
		movq	-824(%rbp), %r14
		movq	-808(%rbp), %r13
		movq	-8(%r13), %r14
		movq	%r13, -808(%rbp)
		movq	%r14, -824(%rbp)
		movq	%rax, -136(%rbp)
		movq	-800(%rbp), %r14
		movq	-824(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -824(%rbp)
		movq	%r14, -800(%rbp)
		setl	%al
		movq	-816(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -816(%rbp)
		movq	-136(%rbp), %rax
		movq	-832(%rbp), %r14
		movq	-816(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -816(%rbp)
		movq	%r14, -832(%rbp)
		# (MOVE (TEMP temp_118) (TEMP temp41)) -- move1
		movq	-840(%rbp), %r14
		movq	-776(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -776(%rbp)
		movq	%r14, -840(%rbp)
		# (CJUMP (AND (TEMP temp_119) (GEQ (TEMP temp_118) (CONST 0))) temp38) -- cjump1
		movq	-856(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -856(%rbp)
		movq	%rax, -136(%rbp)
		movq	-840(%rbp), %r14
		movq	-856(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -856(%rbp)
		movq	%r14, -840(%rbp)
		setge	%al
		movq	-848(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -848(%rbp)
		movq	-136(%rbp), %rax
		movq	-864(%rbp), %r14
		movq	-832(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -832(%rbp)
		movq	%r14, -864(%rbp)
		movq	-864(%rbp), %r14
		movq	-848(%rbp), %r13
		andq	%r13, %r14
		movq	%r13, -848(%rbp)
		movq	%r14, -864(%rbp)
		movq	-864(%rbp), %r13
		cmpq	$0, %r13
		movq	%r13, -864(%rbp)
		jne	temp38
		# (LABEL temp39) -- label1
temp39:
		# (MOVE (TEMP temp_123) (CALL (NAME _I_outOfBounds_p))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		callq	_I_outOfBounds_p
		movq	-872(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -872(%rbp)
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
		# (MOVE (TEMP temp_124) (TEMP temp_123)) -- move1
		movq	-880(%rbp), %r14
		movq	-872(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -872(%rbp)
		movq	%r14, -880(%rbp)
		# (MOVE (TEMP temp37) (TEMP temp_124)) -- move1
		movq	-888(%rbp), %r14
		movq	-880(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -880(%rbp)
		movq	%r14, -888(%rbp)
		# (LABEL temp40) -- label1
temp40:
		# (MOVE (TEMP temp_125) (TEMP temp37)) -- move1
		movq	-896(%rbp), %r14
		movq	-888(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -888(%rbp)
		movq	%r14, -896(%rbp)
		# (MOVE (TEMP temp44) (TEMP temp_125)) -- move1
		movq	-904(%rbp), %r14
		movq	-896(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -896(%rbp)
		movq	%r14, -904(%rbp)
		# (MOVE (TEMP temp_126) (TEMP temp44)) -- move1
		movq	-912(%rbp), %r14
		movq	-904(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -904(%rbp)
		movq	%r14, -912(%rbp)
		# (MOVE (TEMP temp51) (TEMP temp_126)) -- move1
		movq	-920(%rbp), %r14
		movq	-912(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -912(%rbp)
		movq	%r14, -920(%rbp)
		# (MOVE (TEMP temp_127) (CONST 0)) -- move1
		movq	-928(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -928(%rbp)
		# (MOVE (TEMP temp45) (TEMP temp_127)) -- move1
		movq	-936(%rbp), %r14
		movq	-928(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -928(%rbp)
		movq	%r14, -936(%rbp)
		# (MOVE (TEMP temp_128) (TEMP temp45)) -- move1
		movq	-944(%rbp), %r14
		movq	-936(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -936(%rbp)
		movq	%r14, -944(%rbp)
		# (MOVE (TEMP temp50) (TEMP temp_128)) -- move1
		movq	-952(%rbp), %r14
		movq	-944(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -944(%rbp)
		movq	%r14, -952(%rbp)
		# (MOVE (TEMP temp_129) (TEMP temp45)) -- move1
		movq	-960(%rbp), %r14
		movq	-936(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -936(%rbp)
		movq	%r14, -960(%rbp)
		# (MOVE (TEMP temp52) (TEMP temp_129)) -- move1
		movq	-968(%rbp), %r14
		movq	-960(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -960(%rbp)
		movq	%r14, -968(%rbp)
		# (MOVE (TEMP temp_131) (TEMP temp45)) -- move1
		movq	-976(%rbp), %r14
		movq	-936(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -936(%rbp)
		movq	%r14, -976(%rbp)
		# (MOVE (TEMP temp_130) (TEMP temp44)) -- move1
		movq	-984(%rbp), %r14
		movq	-904(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -904(%rbp)
		movq	%r14, -984(%rbp)
		# (MOVE (TEMP temp_133)
# (LT (TEMP temp_131) (MEM (SUB (TEMP temp_130) (CONST 8))))) -- move1
		movq	-1000(%rbp), %r14
		movq	-984(%rbp), %r13
		movq	-8(%r13), %r14
		movq	%r13, -984(%rbp)
		movq	%r14, -1000(%rbp)
		movq	%rax, -136(%rbp)
		movq	-976(%rbp), %r14
		movq	-1000(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -1000(%rbp)
		movq	%r14, -976(%rbp)
		setl	%al
		movq	-992(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -992(%rbp)
		movq	-136(%rbp), %rax
		movq	-1008(%rbp), %r14
		movq	-992(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -992(%rbp)
		movq	%r14, -1008(%rbp)
		# (MOVE (TEMP temp_132) (TEMP temp50)) -- move1
		movq	-1016(%rbp), %r14
		movq	-952(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -952(%rbp)
		movq	%r14, -1016(%rbp)
		# (CJUMP (AND (TEMP temp_133) (GEQ (TEMP temp_132) (CONST 0))) temp47) -- cjump1
		movq	-1032(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -1032(%rbp)
		movq	%rax, -136(%rbp)
		movq	-1016(%rbp), %r14
		movq	-1032(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -1032(%rbp)
		movq	%r14, -1016(%rbp)
		setge	%al
		movq	-1024(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -1024(%rbp)
		movq	-136(%rbp), %rax
		movq	-1040(%rbp), %r14
		movq	-1008(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1008(%rbp)
		movq	%r14, -1040(%rbp)
		movq	-1040(%rbp), %r14
		movq	-1024(%rbp), %r13
		andq	%r13, %r14
		movq	%r13, -1024(%rbp)
		movq	%r14, -1040(%rbp)
		movq	-1040(%rbp), %r13
		cmpq	$0, %r13
		movq	%r13, -1040(%rbp)
		jne	temp47
		# (LABEL temp48) -- label1
temp48:
		# (MOVE (TEMP temp_137) (CALL (NAME _I_outOfBounds_p))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		callq	_I_outOfBounds_p
		movq	-1048(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1048(%rbp)
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
		# (MOVE (TEMP temp_138) (TEMP temp_137)) -- move1
		movq	-1056(%rbp), %r14
		movq	-1048(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1048(%rbp)
		movq	%r14, -1056(%rbp)
		# (MOVE (TEMP temp46) (TEMP temp_138)) -- move1
		movq	-1064(%rbp), %r14
		movq	-1056(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1056(%rbp)
		movq	%r14, -1064(%rbp)
		# (LABEL temp49) -- label1
temp49:
		# (MOVE (TEMP temp_139) (TEMP temp46)) -- move1
		movq	-1072(%rbp), %r14
		movq	-1064(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1064(%rbp)
		movq	%r14, -1072(%rbp)
		# (MOVE (TEMP temp_140) (CALL (NAME _Iprint_pai) (TEMP temp_139))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		movq	-1072(%rbp), %r13
		movq	%r13, %rdi
		movq	%r13, -1072(%rbp)
		callq	_Iprint_pai
		movq	-1080(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1080(%rbp)
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
		# (JUMP (NAME temp_146)) -- jump1
		jmp	temp_146
		# (JUMP (NAME temp_141)) -- jump1
		jmp	temp_141
		# (LABEL temp47) -- label1
temp47:
		# (MOVE (TEMP temp_134) (TEMP temp52)) -- move1
		movq	-1088(%rbp), %r14
		movq	-968(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -968(%rbp)
		movq	%r14, -1088(%rbp)
		# (MOVE (TEMP temp_135) (MUL (TEMP temp_134) (CONST 8))) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-1088(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1088(%rbp)
		movq	-1104(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -1104(%rbp)
		movq	-1104(%rbp), %r13
		mulq	%r13
		movq	%r13, -1104(%rbp)
		movq	-1096(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1096(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-1112(%rbp), %r14
		movq	-1096(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1096(%rbp)
		movq	%r14, -1112(%rbp)
		# (MOVE (TEMP temp_136) (MEM (ADD (TEMP temp_135) (TEMP temp51)))) -- move1
		movq	-1120(%rbp), %r15
		movq	-920(%rbp), %r14
		movq	-1112(%rbp), %r13
		movq	(%r13,%r14), %r15
		movq	%r13, -1112(%rbp)
		movq	%r14, -920(%rbp)
		movq	%r15, -1120(%rbp)
		# (MOVE (TEMP temp46) (TEMP temp_136)) -- move1
		movq	-1064(%rbp), %r14
		movq	-1120(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1120(%rbp)
		movq	%r14, -1064(%rbp)
		# (JUMP (NAME temp49)) -- jump1
		jmp	temp49
		# (LABEL temp_141) -- label1
temp_141:
		# (JUMP (NAME temp_142)) -- jump1
		jmp	temp_142
		# (LABEL temp38) -- label1
temp38:
		# (MOVE (TEMP temp_120) (TEMP temp43)) -- move1
		movq	-1128(%rbp), %r14
		movq	-792(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -792(%rbp)
		movq	%r14, -1128(%rbp)
		# (MOVE (TEMP temp_121) (MUL (TEMP temp_120) (CONST 8))) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-1128(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1128(%rbp)
		movq	-1144(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -1144(%rbp)
		movq	-1144(%rbp), %r13
		mulq	%r13
		movq	%r13, -1144(%rbp)
		movq	-1136(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1136(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-1152(%rbp), %r14
		movq	-1136(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1136(%rbp)
		movq	%r14, -1152(%rbp)
		# (MOVE (TEMP temp_122) (MEM (ADD (TEMP temp_121) (TEMP temp42)))) -- move1
		movq	-1160(%rbp), %r15
		movq	-744(%rbp), %r14
		movq	-1152(%rbp), %r13
		movq	(%r13,%r14), %r15
		movq	%r13, -1152(%rbp)
		movq	%r14, -744(%rbp)
		movq	%r15, -1160(%rbp)
		# (MOVE (TEMP temp37) (TEMP temp_122)) -- move1
		movq	-888(%rbp), %r14
		movq	-1160(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1160(%rbp)
		movq	%r14, -888(%rbp)
		# (JUMP (NAME temp40)) -- jump1
		jmp	temp40
		# (LABEL temp_142) -- label1
temp_142:
		# (JUMP (NAME temp_143)) -- jump1
		jmp	temp_143
		# (LABEL temp29) -- label1
temp29:
		# (MOVE (TEMP temp_71) (CONST 88)) -- move1
		movq	-1168(%rbp), %r13
		movq	$88, %r13
		movq	%r13, -1168(%rbp)
		# (MOVE (TEMP temp_72) (CALL (NAME _I_alloc_i) (TEMP temp_71))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		movq	-1168(%rbp), %r13
		movq	%r13, %rdi
		movq	%r13, -1168(%rbp)
		callq	_I_alloc_i
		movq	-1176(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1176(%rbp)
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
		# (MOVE (TEMP temp_73) (TEMP temp_72)) -- move1
		movq	-1184(%rbp), %r14
		movq	-1176(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1176(%rbp)
		movq	%r14, -1184(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_73)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1184(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1184(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_74) (CONST 10)) -- move1
		movq	-1200(%rbp), %r13
		movq	$10, %r13
		movq	%r13, -1200(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_74)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1200(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1200(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_75) (TEMP temp17)) -- move1
		movq	-1208(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1208(%rbp)
		# (MOVE (TEMP temp_76) (ADD (TEMP temp_75) (CONST 8))) -- move1
		movq	-1216(%rbp), %r14
		movq	-1208(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1208(%rbp)
		movq	%r14, -1216(%rbp)
		movq	-1216(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1216(%rbp)
		movq	-1224(%rbp), %r14
		movq	-1216(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1216(%rbp)
		movq	%r14, -1224(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_76)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1224(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1224(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_77) (CONST 98)) -- move1
		movq	-1232(%rbp), %r13
		movq	$98, %r13
		movq	%r13, -1232(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_77)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1232(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1232(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_78) (TEMP temp17)) -- move1
		movq	-1240(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1240(%rbp)
		# (MOVE (TEMP temp_79) (ADD (TEMP temp_78) (CONST 8))) -- move1
		movq	-1248(%rbp), %r14
		movq	-1240(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1240(%rbp)
		movq	%r14, -1248(%rbp)
		movq	-1248(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1248(%rbp)
		movq	-1256(%rbp), %r14
		movq	-1248(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1248(%rbp)
		movq	%r14, -1256(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_79)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1256(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1256(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_80) (CONST 114)) -- move1
		movq	-1264(%rbp), %r13
		movq	$114, %r13
		movq	%r13, -1264(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_80)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1264(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1264(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_81) (TEMP temp17)) -- move1
		movq	-1272(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1272(%rbp)
		# (MOVE (TEMP temp_82) (ADD (TEMP temp_81) (CONST 8))) -- move1
		movq	-1280(%rbp), %r14
		movq	-1272(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1272(%rbp)
		movq	%r14, -1280(%rbp)
		movq	-1280(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1280(%rbp)
		movq	-1288(%rbp), %r14
		movq	-1280(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1280(%rbp)
		movq	%r14, -1288(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_82)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1288(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1288(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_83) (CONST 97)) -- move1
		movq	-1296(%rbp), %r13
		movq	$97, %r13
		movq	%r13, -1296(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_83)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1296(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1296(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_84) (TEMP temp17)) -- move1
		movq	-1304(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1304(%rbp)
		# (MOVE (TEMP temp_85) (ADD (TEMP temp_84) (CONST 8))) -- move1
		movq	-1312(%rbp), %r14
		movq	-1304(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1304(%rbp)
		movq	%r14, -1312(%rbp)
		movq	-1312(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1312(%rbp)
		movq	-1320(%rbp), %r14
		movq	-1312(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1312(%rbp)
		movq	%r14, -1320(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_85)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1320(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1320(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_86) (CONST 110)) -- move1
		movq	-1328(%rbp), %r13
		movq	$110, %r13
		movq	%r13, -1328(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_86)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1328(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1328(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_87) (TEMP temp17)) -- move1
		movq	-1336(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1336(%rbp)
		# (MOVE (TEMP temp_88) (ADD (TEMP temp_87) (CONST 8))) -- move1
		movq	-1344(%rbp), %r14
		movq	-1336(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1336(%rbp)
		movq	%r14, -1344(%rbp)
		movq	-1344(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1344(%rbp)
		movq	-1352(%rbp), %r14
		movq	-1344(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1344(%rbp)
		movq	%r14, -1352(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_88)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1352(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1352(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_89) (CONST 100)) -- move1
		movq	-1360(%rbp), %r13
		movq	$100, %r13
		movq	%r13, -1360(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_89)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1360(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1360(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_90) (TEMP temp17)) -- move1
		movq	-1368(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1368(%rbp)
		# (MOVE (TEMP temp_91) (ADD (TEMP temp_90) (CONST 8))) -- move1
		movq	-1376(%rbp), %r14
		movq	-1368(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1368(%rbp)
		movq	%r14, -1376(%rbp)
		movq	-1376(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1376(%rbp)
		movq	-1384(%rbp), %r14
		movq	-1376(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1376(%rbp)
		movq	%r14, -1384(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_91)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1384(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1384(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_92) (CONST 111)) -- move1
		movq	-1392(%rbp), %r13
		movq	$111, %r13
		movq	%r13, -1392(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_92)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1392(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1392(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_93) (TEMP temp17)) -- move1
		movq	-1400(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1400(%rbp)
		# (MOVE (TEMP temp_94) (ADD (TEMP temp_93) (CONST 8))) -- move1
		movq	-1408(%rbp), %r14
		movq	-1400(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1400(%rbp)
		movq	%r14, -1408(%rbp)
		movq	-1408(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1408(%rbp)
		movq	-1416(%rbp), %r14
		movq	-1408(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1408(%rbp)
		movq	%r14, -1416(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_94)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1416(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1416(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_95) (CONST 110)) -- move1
		movq	-1424(%rbp), %r13
		movq	$110, %r13
		movq	%r13, -1424(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_95)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1424(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1424(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_96) (TEMP temp17)) -- move1
		movq	-1432(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1432(%rbp)
		# (MOVE (TEMP temp_97) (ADD (TEMP temp_96) (CONST 8))) -- move1
		movq	-1440(%rbp), %r14
		movq	-1432(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1432(%rbp)
		movq	%r14, -1440(%rbp)
		movq	-1440(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1440(%rbp)
		movq	-1448(%rbp), %r14
		movq	-1440(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1440(%rbp)
		movq	%r14, -1448(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_97)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1448(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1448(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_98) (CONST 32)) -- move1
		movq	-1456(%rbp), %r13
		movq	$32, %r13
		movq	%r13, -1456(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_98)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1456(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1456(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_99) (TEMP temp17)) -- move1
		movq	-1464(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1464(%rbp)
		# (MOVE (TEMP temp_100) (ADD (TEMP temp_99) (CONST 8))) -- move1
		movq	-1472(%rbp), %r14
		movq	-1464(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1464(%rbp)
		movq	%r14, -1472(%rbp)
		movq	-1472(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1472(%rbp)
		movq	-1480(%rbp), %r14
		movq	-1472(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1472(%rbp)
		movq	%r14, -1480(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_100)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1480(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1480(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_101) (CONST 111)) -- move1
		movq	-1488(%rbp), %r13
		movq	$111, %r13
		movq	%r13, -1488(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_101)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1488(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1488(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_102) (TEMP temp17)) -- move1
		movq	-1496(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1496(%rbp)
		# (MOVE (TEMP temp_103) (ADD (TEMP temp_102) (CONST 8))) -- move1
		movq	-1504(%rbp), %r14
		movq	-1496(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1496(%rbp)
		movq	%r14, -1504(%rbp)
		movq	-1504(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1504(%rbp)
		movq	-1512(%rbp), %r14
		movq	-1504(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1504(%rbp)
		movq	%r14, -1512(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_103)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1512(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1512(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_104) (CONST 112)) -- move1
		movq	-1520(%rbp), %r13
		movq	$112, %r13
		movq	%r13, -1520(%rbp)
		# (MOVE (MEM (TEMP temp17)) (TEMP temp_104)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1520(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1520(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_105) (TEMP temp17)) -- move1
		movq	-1528(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1528(%rbp)
		# (MOVE (TEMP temp_106) (ADD (TEMP temp_105) (CONST 8))) -- move1
		movq	-1536(%rbp), %r14
		movq	-1528(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1528(%rbp)
		movq	%r14, -1536(%rbp)
		movq	-1536(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1536(%rbp)
		movq	-1544(%rbp), %r14
		movq	-1536(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1536(%rbp)
		movq	%r14, -1544(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_106)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1544(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1544(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_107) (TEMP temp17)) -- move1
		movq	-1552(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1552(%rbp)
		# (MOVE (TEMP temp_108) (SUB (TEMP temp_107) (CONST 80))) -- move1
		movq	-1560(%rbp), %r14
		movq	-1552(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1552(%rbp)
		movq	%r14, -1560(%rbp)
		movq	-1560(%rbp), %r13
		subq	$80, %r13
		movq	%r13, -1560(%rbp)
		movq	-1568(%rbp), %r14
		movq	-1560(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1560(%rbp)
		movq	%r14, -1568(%rbp)
		# (MOVE (TEMP temp17) (TEMP temp_108)) -- move1
		movq	-1192(%rbp), %r14
		movq	-1568(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1568(%rbp)
		movq	%r14, -1192(%rbp)
		# (MOVE (TEMP temp_109) (TEMP temp17)) -- move1
		movq	-1576(%rbp), %r14
		movq	-1192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1192(%rbp)
		movq	%r14, -1576(%rbp)
		# (MOVE (TEMP temp_70) (TEMP temp33)) -- move1
		movq	-1584(%rbp), %r14
		movq	-584(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -584(%rbp)
		movq	%r14, -1584(%rbp)
		# (MOVE (TEMP temp_69) (TEMP temp34)) -- move1
		movq	-1592(%rbp), %r14
		movq	-632(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -632(%rbp)
		movq	%r14, -1592(%rbp)
		# (MOVE (MEM (ADD (TEMP temp_70) (MUL (TEMP temp_69) (CONST 8)))) (TEMP temp_109)) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-1592(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1592(%rbp)
		movq	-1608(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -1608(%rbp)
		movq	-1608(%rbp), %r13
		mulq	%r13
		movq	%r13, -1608(%rbp)
		movq	-1600(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1600(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-1616(%rbp), %r14
		movq	-1584(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1584(%rbp)
		movq	%r14, -1616(%rbp)
		movq	-1616(%rbp), %r14
		movq	-1600(%rbp), %r13
		addq	%r13, %r14
		movq	%r13, -1600(%rbp)
		movq	%r14, -1616(%rbp)
		movq	-1616(%rbp), %r14
		movq	-1576(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1576(%rbp)
		movq	%r14, -1616(%rbp)
		# (JUMP (NAME temp31)) -- jump1
		jmp	temp31
		# (LABEL temp_143) -- label1
temp_143:
		# (JUMP (NAME temp_144)) -- jump1
		jmp	temp_144
		# (LABEL temp21) -- label1
temp21:
		# (MOVE (TEMP temp_55) (TEMP temp26)) -- move1
		movq	-1624(%rbp), %r14
		movq	-456(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -456(%rbp)
		movq	%r14, -1624(%rbp)
		# (MOVE (TEMP temp_56) (MUL (TEMP temp_55) (CONST 8))) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-1624(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1624(%rbp)
		movq	-1640(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -1640(%rbp)
		movq	-1640(%rbp), %r13
		mulq	%r13
		movq	%r13, -1640(%rbp)
		movq	-1632(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1632(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-1648(%rbp), %r14
		movq	-1632(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1632(%rbp)
		movq	%r14, -1648(%rbp)
		# (MOVE (TEMP temp_57) (MEM (ADD (TEMP temp_56) (TEMP temp25)))) -- move1
		movq	-1656(%rbp), %r15
		movq	-408(%rbp), %r14
		movq	-1648(%rbp), %r13
		movq	(%r13,%r14), %r15
		movq	%r13, -1648(%rbp)
		movq	%r14, -408(%rbp)
		movq	%r15, -1656(%rbp)
		# (MOVE (TEMP temp20) (TEMP temp_57)) -- move1
		movq	-552(%rbp), %r14
		movq	-1656(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1656(%rbp)
		movq	%r14, -552(%rbp)
		# (JUMP (NAME temp23)) -- jump1
		jmp	temp23
		# (LABEL temp_144) -- label1
temp_144:
		# (JUMP (NAME temp_145)) -- jump1
		jmp	temp_145
		# (LABEL temp6) -- label1
temp6:
		# (MOVE (TEMP temp_16) (TEMP temp1)) -- move1
		movq	-1664(%rbp), %r14
		movq	-192(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -192(%rbp)
		movq	%r14, -1664(%rbp)
		# (MOVE (TEMP temp9) (TEMP temp_16)) -- move1
		movq	-1672(%rbp), %r14
		movq	-1664(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1664(%rbp)
		movq	%r14, -1672(%rbp)
		# (MOVE (TEMP temp_17) (TEMP temp9)) -- move1
		movq	-1680(%rbp), %r14
		movq	-1672(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1672(%rbp)
		movq	%r14, -1680(%rbp)
		# (MOVE (TEMP temp_18) (ADD (TEMP temp_17) (CONST 1))) -- move1
		movq	-1688(%rbp), %r14
		movq	-1680(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1680(%rbp)
		movq	%r14, -1688(%rbp)
		movq	-1688(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -1688(%rbp)
		movq	-1696(%rbp), %r14
		movq	-1688(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1688(%rbp)
		movq	%r14, -1696(%rbp)
		# (MOVE (TEMP temp_19) (MUL (TEMP temp_18) (CONST 8))) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-1696(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1696(%rbp)
		movq	-1712(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -1712(%rbp)
		movq	-1712(%rbp), %r13
		mulq	%r13
		movq	%r13, -1712(%rbp)
		movq	-1704(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1704(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-1720(%rbp), %r14
		movq	-1704(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1704(%rbp)
		movq	%r14, -1720(%rbp)
		# (MOVE (TEMP temp_20) (CALL (NAME _I_alloc_i) (TEMP temp_19))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		movq	-1720(%rbp), %r13
		movq	%r13, %rdi
		movq	%r13, -1720(%rbp)
		callq	_I_alloc_i
		movq	-1728(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1728(%rbp)
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
		# (MOVE (TEMP temp_21) (TEMP temp_20)) -- move1
		movq	-1736(%rbp), %r14
		movq	-1728(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1728(%rbp)
		movq	%r14, -1736(%rbp)
		# (MOVE (TEMP temp10) (TEMP temp_21)) -- move1
		movq	-1744(%rbp), %r14
		movq	-1736(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1736(%rbp)
		movq	%r14, -1744(%rbp)
		# (MOVE (TEMP temp_22) (TEMP temp9)) -- move1
		movq	-1752(%rbp), %r14
		movq	-1672(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1672(%rbp)
		movq	%r14, -1752(%rbp)
		# (MOVE (MEM (TEMP temp10)) (TEMP temp_22)) -- move1
		movq	-1744(%rbp), %r14
		movq	-1752(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1752(%rbp)
		movq	%r14, -1744(%rbp)
		# (MOVE (TEMP temp_23) (TEMP temp10)) -- move1
		movq	-1760(%rbp), %r14
		movq	-1744(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1744(%rbp)
		movq	%r14, -1760(%rbp)
		# (MOVE (TEMP temp_24) (ADD (TEMP temp_23) (CONST 8))) -- move1
		movq	-1768(%rbp), %r14
		movq	-1760(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1760(%rbp)
		movq	%r14, -1768(%rbp)
		movq	-1768(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1768(%rbp)
		movq	-1776(%rbp), %r14
		movq	-1768(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1768(%rbp)
		movq	%r14, -1776(%rbp)
		# (MOVE (TEMP temp10) (TEMP temp_24)) -- move1
		movq	-1744(%rbp), %r14
		movq	-1776(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1776(%rbp)
		movq	%r14, -1744(%rbp)
		# (MOVE (TEMP temp_27) (TEMP temp10)) -- move1
		movq	-1784(%rbp), %r14
		movq	-1744(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1744(%rbp)
		movq	%r14, -1784(%rbp)
		# (MOVE (TEMP temp_26) (TEMP temp4)) -- move1
		movq	-1792(%rbp), %r14
		movq	-296(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -296(%rbp)
		movq	%r14, -1792(%rbp)
		# (MOVE (TEMP temp_25) (TEMP temp8)) -- move1
		movq	-1800(%rbp), %r14
		movq	-360(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -360(%rbp)
		movq	%r14, -1800(%rbp)
		# (MOVE (MEM (ADD (TEMP temp_26) (MUL (TEMP temp_25) (CONST 8)))) (TEMP temp_27)) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-1800(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1800(%rbp)
		movq	-1816(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -1816(%rbp)
		movq	-1816(%rbp), %r13
		mulq	%r13
		movq	%r13, -1816(%rbp)
		movq	-1808(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1808(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-1824(%rbp), %r14
		movq	-1792(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1792(%rbp)
		movq	%r14, -1824(%rbp)
		movq	-1824(%rbp), %r14
		movq	-1808(%rbp), %r13
		addq	%r13, %r14
		movq	%r13, -1808(%rbp)
		movq	%r14, -1824(%rbp)
		movq	-1824(%rbp), %r14
		movq	-1784(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1784(%rbp)
		movq	%r14, -1824(%rbp)
		# (MOVE (TEMP temp_28) (CONST 0)) -- move1
		movq	-1832(%rbp), %r13
		movq	$0, %r13
		movq	%r13, -1832(%rbp)
		# (MOVE (TEMP temp14) (TEMP temp_28)) -- move1
		movq	-1840(%rbp), %r14
		movq	-1832(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1832(%rbp)
		movq	%r14, -1840(%rbp)
		# (LABEL temp11) -- label1
temp11:
		# (MOVE (TEMP temp_29) (TEMP temp14)) -- move1
		movq	-1848(%rbp), %r14
		movq	-1840(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1840(%rbp)
		movq	%r14, -1848(%rbp)
		# (CJUMP (LT (TEMP temp_29) (TEMP temp9)) temp12) -- cjump1
		movq	%rax, -136(%rbp)
		movq	-1848(%rbp), %r14
		movq	-1672(%rbp), %r13
		cmpq	%r13, %r14
		movq	%r13, -1672(%rbp)
		movq	%r14, -1848(%rbp)
		setl	%al
		movq	-1856(%rbp), %r13
		movzx	%al, %r13
		movq	%r13, -1856(%rbp)
		movq	-136(%rbp), %rax
		movq	-1856(%rbp), %r13
		cmpq	$0, %r13
		movq	%r13, -1856(%rbp)
		jne	temp12
		# (LABEL temp13) -- label1
temp13:
		# (MOVE (TEMP temp_44) (TEMP temp8)) -- move1
		movq	-1864(%rbp), %r14
		movq	-360(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -360(%rbp)
		movq	%r14, -1864(%rbp)
		# (MOVE (TEMP temp_45) (ADD (TEMP temp_44) (CONST 1))) -- move1
		movq	-1872(%rbp), %r14
		movq	-1864(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1864(%rbp)
		movq	%r14, -1872(%rbp)
		movq	-1872(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -1872(%rbp)
		movq	-1880(%rbp), %r14
		movq	-1872(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1872(%rbp)
		movq	%r14, -1880(%rbp)
		# (MOVE (TEMP temp8) (TEMP temp_45)) -- move1
		movq	-360(%rbp), %r14
		movq	-1880(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1880(%rbp)
		movq	%r14, -360(%rbp)
		# (JUMP (NAME temp5)) -- jump1
		jmp	temp5
		# (LABEL temp12) -- label1
temp12:
		# (MOVE (TEMP temp_30) (TEMP temp2)) -- move1
		movq	-1888(%rbp), %r14
		movq	-208(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -208(%rbp)
		movq	%r14, -1888(%rbp)
		# (MOVE (TEMP temp15) (TEMP temp_30)) -- move1
		movq	-1896(%rbp), %r14
		movq	-1888(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1888(%rbp)
		movq	%r14, -1896(%rbp)
		# (MOVE (TEMP temp_31) (TEMP temp15)) -- move1
		movq	-1904(%rbp), %r14
		movq	-1896(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1896(%rbp)
		movq	%r14, -1904(%rbp)
		# (MOVE (TEMP temp_32) (ADD (TEMP temp_31) (CONST 1))) -- move1
		movq	-1912(%rbp), %r14
		movq	-1904(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1904(%rbp)
		movq	%r14, -1912(%rbp)
		movq	-1912(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -1912(%rbp)
		movq	-1920(%rbp), %r14
		movq	-1912(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1912(%rbp)
		movq	%r14, -1920(%rbp)
		# (MOVE (TEMP temp_33) (MUL (TEMP temp_32) (CONST 8))) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-1920(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -1920(%rbp)
		movq	-1936(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -1936(%rbp)
		movq	-1936(%rbp), %r13
		mulq	%r13
		movq	%r13, -1936(%rbp)
		movq	-1928(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1928(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-1944(%rbp), %r14
		movq	-1928(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1928(%rbp)
		movq	%r14, -1944(%rbp)
		# (MOVE (TEMP temp_34) (CALL (NAME _I_alloc_i) (TEMP temp_33))) -- move2
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
		leaq	-128(%rbp), %r9
		# Passing in arguments...
		movq	-1944(%rbp), %r13
		movq	%r13, %rdi
		movq	%r13, -1944(%rbp)
		callq	_I_alloc_i
		movq	-1952(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -1952(%rbp)
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
		# (MOVE (TEMP temp_35) (TEMP temp_34)) -- move1
		movq	-1960(%rbp), %r14
		movq	-1952(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1952(%rbp)
		movq	%r14, -1960(%rbp)
		# (MOVE (TEMP temp16) (TEMP temp_35)) -- move1
		movq	-1968(%rbp), %r14
		movq	-1960(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1960(%rbp)
		movq	%r14, -1968(%rbp)
		# (MOVE (TEMP temp_36) (TEMP temp15)) -- move1
		movq	-1976(%rbp), %r14
		movq	-1896(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1896(%rbp)
		movq	%r14, -1976(%rbp)
		# (MOVE (MEM (TEMP temp16)) (TEMP temp_36)) -- move1
		movq	-1968(%rbp), %r14
		movq	-1976(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -1976(%rbp)
		movq	%r14, -1968(%rbp)
		# (MOVE (TEMP temp_37) (TEMP temp16)) -- move1
		movq	-1984(%rbp), %r14
		movq	-1968(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1968(%rbp)
		movq	%r14, -1984(%rbp)
		# (MOVE (TEMP temp_38) (ADD (TEMP temp_37) (CONST 8))) -- move1
		movq	-1992(%rbp), %r14
		movq	-1984(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1984(%rbp)
		movq	%r14, -1992(%rbp)
		movq	-1992(%rbp), %r13
		addq	$8, %r13
		movq	%r13, -1992(%rbp)
		movq	-2000(%rbp), %r14
		movq	-1992(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1992(%rbp)
		movq	%r14, -2000(%rbp)
		# (MOVE (TEMP temp16) (TEMP temp_38)) -- move1
		movq	-1968(%rbp), %r14
		movq	-2000(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -2000(%rbp)
		movq	%r14, -1968(%rbp)
		# (MOVE (TEMP temp_41) (TEMP temp16)) -- move1
		movq	-2008(%rbp), %r14
		movq	-1968(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1968(%rbp)
		movq	%r14, -2008(%rbp)
		# (MOVE (TEMP temp_40) (TEMP temp10)) -- move1
		movq	-2016(%rbp), %r14
		movq	-1744(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1744(%rbp)
		movq	%r14, -2016(%rbp)
		# (MOVE (TEMP temp_39) (TEMP temp14)) -- move1
		movq	-2024(%rbp), %r14
		movq	-1840(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1840(%rbp)
		movq	%r14, -2024(%rbp)
		# (MOVE (MEM (ADD (TEMP temp_40) (MUL (TEMP temp_39) (CONST 8)))) (TEMP temp_41)) -- move1
		movq	%rax, -136(%rbp)
		movq	%rdx, -144(%rbp)
		movq	-2024(%rbp), %r13
		movq	%r13, %rax
		movq	%r13, -2024(%rbp)
		movq	-2040(%rbp), %r13
		movq	$8, %r13
		movq	%r13, -2040(%rbp)
		movq	-2040(%rbp), %r13
		mulq	%r13
		movq	%r13, -2040(%rbp)
		movq	-2032(%rbp), %r13
		movq	%rax, %r13
		movq	%r13, -2032(%rbp)
		movq	-136(%rbp), %rax
		movq	-144(%rbp), %rdx
		movq	-2048(%rbp), %r14
		movq	-2016(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -2016(%rbp)
		movq	%r14, -2048(%rbp)
		movq	-2048(%rbp), %r14
		movq	-2032(%rbp), %r13
		addq	%r13, %r14
		movq	%r13, -2032(%rbp)
		movq	%r14, -2048(%rbp)
		movq	-2048(%rbp), %r14
		movq	-2008(%rbp), %r13
		movq	%r13, (%r14)
		movq	%r13, -2008(%rbp)
		movq	%r14, -2048(%rbp)
		# (MOVE (TEMP temp_42) (TEMP temp14)) -- move1
		movq	-2056(%rbp), %r14
		movq	-1840(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -1840(%rbp)
		movq	%r14, -2056(%rbp)
		# (MOVE (TEMP temp_43) (ADD (TEMP temp_42) (CONST 1))) -- move1
		movq	-2064(%rbp), %r14
		movq	-2056(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -2056(%rbp)
		movq	%r14, -2064(%rbp)
		movq	-2064(%rbp), %r13
		addq	$1, %r13
		movq	%r13, -2064(%rbp)
		movq	-2072(%rbp), %r14
		movq	-2064(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -2064(%rbp)
		movq	%r14, -2072(%rbp)
		# (MOVE (TEMP temp14) (TEMP temp_43)) -- move1
		movq	-1840(%rbp), %r14
		movq	-2072(%rbp), %r13
		movq	%r13, %r14
		movq	%r13, -2072(%rbp)
		movq	%r14, -1840(%rbp)
		# (JUMP (NAME temp11)) -- jump1
		jmp	temp11
		# (LABEL temp_145) -- label1
temp_145:
		# (LABEL temp_146) -- label1
temp_146:
		# Function epilogue
		# Restoring callee-save registers
		movq	-8(%rbp), %rbx
		movq	-16(%rbp), %rbp
		movq	-24(%rbp), %r12
		movq	-32(%rbp), %r13
		movq	-40(%rbp), %r14
		movq	-48(%rbp), %r15
		# Restore old RBP and RSP
		leave	
		retq	

