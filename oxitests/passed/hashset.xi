use io
use conv

class Object {

	name: int[]

	initObject(name_: int[]): Object {
		name = name_
		return this
	}
	
	hashCode(): int {
		i: int = 0
		result: int = 1
		while (i < length(name)) {
			result = result * name[i]
			result = result + 1
			i = i + 1
		}
		return result
	}

	equals(o: Object): bool {
		if (length(o.name()) != length(name)) {
			return false
		}

		i: int = 0
		name_: int[] = o.name()
		while (i < length(name_)) {
			if (name_[i] != name[i]) {
				return false
			}
			i = i + 1
		}

		return true
	}

	name(): int[] {
		return name
	}
}

class Node {
	data: Object
	next: Node

	initNode(data_: Object): Node {
		data = data_
		next = null
		return this
	}

	setNext(next_: Node) {
		next = next_
	}

	getNext(): Node {
		return next
	}

	getData(): Object {
		return data
	}
}

class LinkedList {

	head: Node

	initLinkedList(): LinkedList {
		head = null
		return this
	}
	
	add(o: Object) {
		n: Node = createNode(o)
		if (head == null) {
			head = n
			return
		}
		n.setNext(head)
		head = n
	}

	remove(o: Object): bool {
		current: Node = head
		prev: Node = null
		while (current != null) {
			if (current.getData().equals(o)) {
				if (prev == null) {
					head = current.getNext()
				} else {
					prev.setNext(current.getNext())
				}
				return true
			}
			prev = current
			current = current.getNext()
		}
		return false
	}

	contains(o: Object): bool {
		current: Node = head
		while (current != null) {
			if (current.getData().equals(o)) {
				return true
			}
			current = current.getNext()
		}
		return false
	}
}

class Hashset {
	table: LinkedList[]

	initHashset(): Hashset {
		size: int = 20
		table_: LinkedList[size]
		i: int = 0
		while (i < size) {
			table_[i] = createLinkedList()
			i = i + 1
		}
		table = table_
		return this
	}

	add(o: Object) {
		index: int = o.hashCode() % length(table)
		table[index].add(o)
	}

	remove(o: Object): bool {
		index: int = o.hashCode() % length(table)
		return table[index].remove(o)
	}

	contains(o: Object): bool {
		index: int = o.hashCode() % length(table)
		return table[index].contains(o)
	}
}

createObject(name: int[]): Object {
	return new Object.initObject(name)
}

createNode(data: Object): Node {
	return new Node.initNode(data)
}

createLinkedList(): LinkedList {
	return new LinkedList.initLinkedList()
}

createHashset(): Hashset {
	return new Hashset.initHashset()
}

main(args: int[][]) {
	set: Hashset = createHashset()
	jihun: Object = createObject("jihun")
	brandon: Object = createObject("brandon")
	eric: Object = createObject("eric")
	andy: Object = createObject("andy")
	set.add(jihun)
	set.add(brandon)
	set.add(eric)
	throwaway: bool = set.remove(jihun)

	if (!set.contains(jihun) & set.contains(brandon) & set.contains(eric)) {
		println("nice")
	}

	if(!set.contains(andy)) {
		println("nice")
	}
}
