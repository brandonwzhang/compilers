use oxi_test2

class DatBoi {
	oshit: int[]

	init(): DatBoi {
		oshit = "waddup"
		return this
	}
}

createDatBoi(): DatBoi {
	return new DatBoi.init()
}

jihun(): bool {
	return true
}

hereCome(): DatBoi {
	return createDatBoi()
}

main(args: int[][]) {
	println("nice")
	println(unparseInt(420))
	if (jihun()) {
		frog: DatBoi = hereCome()
		println("nice2")
	}
}