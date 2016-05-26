use io
use conv

INITIAL_HASHTABLE_SIZE: int = 3

class Object {
	hashCode(): int {
		println("hashCode() not implemented!")
		return 0
	}

	equals(o: Object): bool {
		return this == o
	}

	toString(): int[] {
		println("toString() not implemented!")
		return ""
	}
}

class String extends Object {
	value: int[]

	hashCode(): int {
		result: int = 1
		for (i: int = 0; i < length(value); i = i + 1) {
			result = result * value[i]
			result = result + 1
		}
		return result
	}

	equals(o: Object): bool {

		if (!(o instanceof String)) {
			return false
		}

		s:String = String#o
		if (length(s.value()) != length(value)) {
			return false
		}

		value_: int[] = s.value()
		for (i: int = 0; i < length(value); i = i + 1) {
			if (value[i] != value_[i]) {
				return false
			}
		}

		return true
	}

	toString(): int[] {
		return value
	}

	value(): int[] {
		return value
	}
}

class Integer extends Object {
	value: int

	hashCode(): int {
		return value * 31
	}

	equals(o: Object): bool {

		if (!(o instanceof Integer)) {
			return false
		}

		n: Integer = Integer#o
		return n.value() == value
	}

	toString(): int[] {
		return unparseInt(value)
	}

	value(): int {
		return value
	}
}

class Entry {
	key: Object
	data: Object
	next: Entry

	init(key_: Object, data_: Object, next_: Entry): Entry {
		key = key_
		data = data_
		next = next_
		return this
	}

	setNext(next_: Entry) {
		next = next_
	}

	getNext(): Entry {
		return next
	}

	getKey(): Object {
		return key
	}

	getData(): Object {
		return data
	}
}

class Bucket {
	head: Entry

	init(): Bucket {
		head = null
		return this
	}

	insert(key: Object, data: Object) {
		n: Entry = createEntry(key, data, head)
		head = n
	}

	remove(key: Object): Object {
		current: Entry = head
		prev: Entry = null
		while (head != null) {
			if (current.getKey().equals(key)) {
				if (prev == null) {
					head = current.getNext()
				} else {
					prev.setNext(current.getNext())
				}
				return current.getData()
			}
			prev = current
			current = current.getNext()
		}
		return null
	}

	containsKey(key: Object): bool {
		current: Entry = head
		while (current != null) {
			if (current.getKey().equals(key)) {
				return true
			}
			current = current.getNext()
		}
		return false
	}

	head(): Entry {
		return head
	}
}

class Hashtable {
	table: Bucket[]
	numBuckets: int
	numEntries: int

	init(): Hashtable {
		numBuckets = INITIAL_HASHTABLE_SIZE
		numEntries = 0
		table_: Bucket[numBuckets]
		for (i: int = 0; i < numBuckets; i = i + 1) {
			table_[i] = createBucket()
		}
		table = table_
		return this
	}

	put(key: Object, value: Object) {
		index: int = key.hashCode() % numBuckets
		bucket: Bucket = table[index]
		if (bucket.containsKey(key)) {
			throwaway: Object = bucket.remove(key)
			numEntries = numEntries - 1
		}
		table[index].insert(key, value)
		numEntries = numEntries + 1
		if (numEntries/numBuckets >= 2) {
			double()
		}
	}

	remove(key: Object): Object {
		index: int = key.hashCode() % numBuckets
		result: Object = table[index].remove(key)
		if (result != null) {
			numEntries = numEntries - 1
		}
		return result
	}

	get(key: Object): Object {
		index: int = key.hashCode() % numBuckets
		bucket: Bucket = table[index]
		current: Entry = bucket.head()
		while (current != null) {
			if (current.getKey().equals(key)) {
				return current.getData()
			}
			current = current.getNext()
		}
		return null
	}

	double() {
		new_table: Bucket[numBuckets*2]
		for (i: int = 0; i < length(new_table); i = i + 1) {
			new_table[i] = createBucket();
		}
		for (i: int = 0; i < numBuckets; i = i + 1) {
			bucket: Bucket = table[i]
			current: Entry = bucket.head()
			while (current != null) {
				key: Object = current.getKey()
				value: Object = current.getData()
				index: int = key.hashCode() % length(new_table)
				new_table[index].insert(key, value)
				current = current.getNext()
			}
		}
		table = new_table
		numBuckets = length(new_table)
	}
}

createObject(): Object {
	return new Object
}

createString(value: int[]): String {
	s: String = new String
	s.value = value
	return s
}

createInteger(value: int): Integer {
	n: Integer = new Integer
	n.value = value
	return n
}

createEntry(key: Object, data: Object, next: Entry): Entry {
	return new Entry.init(key, data, next)
}

createBucket(): Bucket {
	return new Bucket.init()
}

createHashtable(): Hashtable {
	return new Hashtable.init()
}

main(args: int[][]) {
	map: Hashtable = createHashtable()

	myers: String = createString("myers")
	bala: String = createString("bala")
	white: String = createString("white")
	gries: String = createString("gries")
	sirer: String = createString("sirer")
	clarkson: String = createString("clarkson")
	cardie: String = createString("cardie")
	andersen: String = createString("andersen")
	tardos: String = createString("tardos")
	weinberger: String = createString("weinberger")
	mimno: String = createString("mimno")

	map.put(myers, createInteger(4120))
	map.put(bala, createInteger(4670))
	map.put(gries, createInteger(2110))
	map.put(sirer, createInteger(4410))
	map.put(clarkson, createInteger(3110))
	map.put(cardie, createInteger(4740))
	map.put(andersen, createInteger(6360))
	map.put(tardos, createInteger(4820))
	map.put(weinberger, createInteger(4780))
	map.put(mimno, createInteger(3350))

	map.put(white, createInteger(1110))
	map.put(white, createInteger(3152))

	brandon: String = createString("brandon")
	map.put(brandon, createInteger(0))
	println(map.remove(brandon).toString())

	println(map.get(myers).toString())
	println(map.get(andersen).toString())
	println(map.get(mimno).toString())
	println(map.get(white).toString())

	println("Number of buckets: " + unparseInt(map.numBuckets))
	println("Number of entries: " + unparseInt(map.numEntries))
}
