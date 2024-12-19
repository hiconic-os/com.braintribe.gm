import '@dev.hiconic/runtime';

declare module "@dev.hiconic/hc-js-base" {
    type integer = number;
    type long = bigint;
    type double = T.Double;
    type float = T.Float;
    type decimal = T.Decimal;
    type date = globalThis.Date;
    type list<T> = T.Array<T>;
    type set<T> = T.Set<T>;
    type map<K, V> = T.Map<K, V>;

    // Shortcuts
    type GenericEntity = T.com.braintribe.model.generic.GenericEntity;
    type Enum = hc.Enum<any>;

    // Useful types
    type Simple = boolean | string | integer | long | float | double | decimal | date;
    type Scalar = Simple | Enum;
    type CollectionElement = Scalar | GenericEntity;
    type Base = CollectionElement | T.Map<CollectionElement, CollectionElement> | T.Set<CollectionElement> | T.Array<CollectionElement>;

    // ************************************
    // Model Type Declaration Utility Types
    // ************************************

    type EssentialPropertyMetaData = {}

    type PropertyMeta = {
        nullable?: boolean,
        md?: EssentialPropertyMetaData[]
    }

    /** Use to declare a non-nullable property and/or additional metadata. */
    type P<T extends Base, M extends PropertyMeta = {}> = {
        type: T;
        meta: M;
    };

    type PropertyDeclarationType = Base | P<any, any>;

    type ActualPropertyType<T extends PropertyDeclarationType> = T extends P<infer U, any> ? U : T;

    /** Ensures properties are nullable by default, but can be made non-nullable with P<type, { nullable: false }> */
    type Entity<T extends Record<string, PropertyDeclarationType>> = {
        [K in keyof T]: T[K] extends P<infer U, { nullable: false }>
        ? U
        : ActualPropertyType<T[K]> | null
    };

    type Evaluable<RESULT extends Base> = {
            Eval(evaluator: hc.eval.Evaluator<GenericEntity>): hc.eval.JsEvalContext<RESULT>;
            EvalAndGet(evaluator: hc.eval.Evaluator<GenericEntity>): globalThis.Promise<RESULT>;
            EvalAndGetReasoned(evaluator: hc.eval.Evaluator<GenericEntity>): globalThis.Promise<hc.reason.Maybe<RESULT>>;
    }

    // ***********************************
    // T + hc namespaces
    // ***********************************

    namespace T {

        class Double extends Number {
            constructor(value: number);
            type(): "d";
        }

        class Float extends Number {
            constructor(value: number);
            type(): "f";
        }

        interface Array<T> {
            // ###################
            //        es5
            // ###################

            length: number;

            toString(): string;
            toLocaleString(): string;

            pop(): T | undefined;
            push(...items: T[]): number;
            concat(...items: (T | ConcatArray<T>)[]): T[];
            join(separator?: string): string;
            reverse(): T[];
            shift(): T | undefined;
            slice(start?: number, end?: number): T[];
            sort(compareFn?: (a: T, b: T) => number): this;
            splice(start: number, deleteCount?: number): T[];
            splice(start: number, deleteCount: number, ...items: T[]): T[];
            unshift(...items: T[]): number;
            indexOf(searchElement: T, fromIndex?: number): number;
            lastIndexOf(searchElement: T, fromIndex?: number): number;
            every<S extends T>(predicate: (value: T, index: number, array: T[]) => value is S, thisArg?: any): this is S[];
            every(predicate: (value: T, index: number, array: T[]) => unknown, thisArg?: any): boolean;
            some(predicate: (value: T, index: number, array: T[]) => unknown, thisArg?: any): boolean;
            forEach(callbackfn: (value: T, index: number, array: T[]) => void, thisArg?: any): void;
            map<U>(callbackfn: (value: T, index: number, array: T[]) => U, thisArg?: any): U[];
            filter<S extends T>(predicate: (value: T, index: number, array: T[]) => value is S, thisArg?: any): S[];
            filter(predicate: (value: T, index: number, array: T[]) => unknown, thisArg?: any): T[];
            reduce(callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) => T): T;
            reduce(callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) => T, initialValue: T): T;
            reduce<U>(callbackfn: (previousValue: U, currentValue: T, currentIndex: number, array: T[]) => U, initialValue: U): U;
            reduceRight(callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) => T): T;
            reduceRight(callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) => T, initialValue: T): T;
            reduceRight<U>(callbackfn: (previousValue: U, currentValue: T, currentIndex: number, array: T[]) => U, initialValue: U): U;

            // ###################
            //    es2015.core
            // ###################

            find<S extends T>(predicate: (value: T, index: number, obj: T[]) => value is S, thisArg?: any): S | undefined;
            find(predicate: (value: T, index: number, obj: T[]) => unknown, thisArg?: any): T | undefined;
            findIndex(predicate: (value: T, index: number, obj: T[]) => unknown, thisArg?: any): number;
            findLastIndex(predicate: (value: T, index: number, obj: T[]) => unknown, thisArg?: any): number;
            fill(value: T, start?: number, end?: number): this;
            copyWithin(target: number, start: number, end?: number): this;

            // ###################
            //    es2015.iterable
            // ###################

            [Symbol.iterator](): IterableIterator<T>;
            entries(): IterableIterator<[number, T]>;
            keys(): IterableIterator<number>;
            values(): IterableIterator<T>;

            // ###################
            //    es2016.array
            // ###################

            includes(searchElement: T, fromIndex?: number): boolean;

            // ###################
            //    es2022.array
            // ###################

            at(index: number): T | undefined;
        }

        interface Set<T> {
            // ###################
            // es2015.collections
            // ###################

            add(value: T): this;
            clear(): void;
            delete(value: T): boolean;
            forEach(callbackfn: (value: T, value2: T, set: Set<T>) => void, thisArg?: any): void;
            has(value: T): boolean;
            readonly size: number;

            // ###################
            //   es2015.iterable
            // ###################

            [Symbol.iterator](): IterableIterator<T>;
            entries(): IterableIterator<[T, T]>;
            keys(): IterableIterator<T>;
            values(): IterableIterator<T>;
        }

        interface Map<K, V> {
            // ###################
            // es2015.collections
            // ###################

            clear(): void;
            delete(key: K): boolean;
            forEach(callbackfn: (value: V, key: K, map: Map<K, V>) => void, thisArg?: any): void;
            get(key: K): V | undefined;
            has(key: K): boolean;
            set(key: K, value: V): this;
            readonly size: number;

            // ###################
            //   es2015.iterable
            // ###################

            [Symbol.iterator](): IterableIterator<[K, V]>;
            entries(): IterableIterator<[K, V]>;
            keys(): IterableIterator<K>;
            values(): IterableIterator<V>;
        }

    }

    namespace hc {
        const Symbol: {
            readonly enumType: unique symbol
        }
    }
}

declare module '@dev.hiconic/hc-js-base' {

	namespace T {

		// class java.math.BigDecimal
		interface Decimal extends hc.Comparable<Decimal> {}
		class Decimal {
			static ONE: Decimal;
			static ROUND_CEILING: number;
			static ROUND_DOWN: number;
			static ROUND_FLOOR: number;
			static ROUND_HALF_DOWN: number;
			static ROUND_HALF_EVEN: number;
			static ROUND_HALF_UP: number;
			static ROUND_UNNECESSARY: number;
			static ROUND_UP: number;
			static TEN: Decimal;
			static ZERO: Decimal;
			static fromString(arg0: string): Decimal;
			static valueOfDouble(arg0: number): Decimal;
			static valueOfLong(arg0: hc.Long): Decimal;
			static valueOfLongWithScale(arg0: hc.Long, arg1: number): Decimal;
			abs(): Decimal;
			add(arg0: Decimal): Decimal;
			byteValueExact(): number;
			compareTo(arg0: Decimal): number;
			divide(arg0: Decimal): Decimal;
			divideAndRemainder(arg0: Decimal): Decimal[];
			divideToIntegralValue(arg0: Decimal): Decimal;
			divideWithRoundingMode(arg0: Decimal, arg1: number): Decimal;
			divideWithScaleAndRoundingMode(arg0: Decimal, arg1: number, arg2: number): Decimal;
			doubleValue(): number;
			equals(arg0: any): boolean;
			floatValue(): number;
			hashCode(): number;
			intValue(): number;
			intValueExact(): number;
			longValue(): hc.Long;
			longValueExact(): hc.Long;
			max(arg0: Decimal): Decimal;
			min(arg0: Decimal): Decimal;
			movePointLeft(arg0: number): Decimal;
			movePointRight(arg0: number): Decimal;
			multiply(arg0: Decimal): Decimal;
			negate(): Decimal;
			plus(): Decimal;
			pow(arg0: number): Decimal;
			precision(): number;
			remainder(arg0: Decimal): Decimal;
			scale(): number;
			scaleByPowerOfTen(arg0: number): Decimal;
			setScale(arg0: number): Decimal;
			setScaleWithRoundingMode(arg0: number, arg1: number): Decimal;
			shortValueExact(): number;
			signum(): number;
			stripTrailingZeros(): Decimal;
			subtract(arg0: Decimal): Decimal;
			toEngineeringString(): string;
			toPlainString(): string;
			toString(): string;
			ulp(): Decimal;
		}

	}

	namespace hc {

		// interface java.util.function.BiConsumer
		interface BiConsumer<T, U> {
			accept(arg0: T, arg1: U): void;
			andThen(arg0: BiConsumer<T, U>): BiConsumer<T, U>;
		}

		// interface java.util.function.BiFunction
		interface BiFunction<T, U, R> {
			andThen<V>(arg0: Function<R, V>): BiFunction<T, U, V>;
			apply(arg0: T, arg1: U): R;
		}

		// interface java.util.function.BinaryOperator
		abstract class BinaryOperator<T> {
			static maxBy<T>(arg0: Comparator<T>): BinaryOperator<T>;
			static minBy<T>(arg0: Comparator<T>): BinaryOperator<T>;
		}
		interface BinaryOperator<T> extends BiFunction<T, T, T> {
		}

		// interface java.lang.CharSequence
		interface CharSequence {
			charAt(arg0: number): number;
			length(): number;
			subSequence(arg0: number, arg1: number): CharSequence;
			toString(): string;
		}

		// interface java.util.Collection
		interface Collection<E> extends Iterable<E> {
			add(arg0: E): boolean;
			addAll(arg0: Collection<E>): boolean;
			addAllJs(...arg0: E[]): boolean;
			clear(): void;
			contains(arg0: any): boolean;
			containsAll(arg0: Collection<any>): boolean;
			containsAllJs(...arg0: E[]): boolean;
			isEmpty(): boolean;
			remove(arg0: any): boolean;
			removeAll(arg0: Collection<any>): boolean;
			removeAllJs(...arg0: E[]): boolean;
			retainAll(arg0: Collection<any>): boolean;
			retainAllJs(...arg0: E[]): boolean;
			size(): number;
			stream(): Stream<E>;
			toArray(): any[];
		}

		// interface java.util.stream.Collector
		abstract class Collector<T, A, R> {
			static of<T, R>(arg0: Supplier<R>, arg1: BiConsumer<R, T>, arg2: BinaryOperator<R>, ...arg3: Collector$Characteristics[]): Collector<T, R, R>;
			static of2<T, A, R>(arg0: Supplier<A>, arg1: BiConsumer<A, T>, arg2: BinaryOperator<A>, arg3: Function<A, R>, ...arg4: Collector$Characteristics[]): Collector<T, A, R>;
		}
		interface Collector<T, A, R> {
			accumulator(): BiConsumer<A, T>;
			characteristics(): Set<Collector$Characteristics>;
			combiner(): BinaryOperator<A>;
			finisher(): Function<A, R>;
			supplier(): Supplier<A>;
		}

		// interface java.lang.Comparable
		interface Comparable<T> {
			compareTo(arg0: T): number;
		}

		// interface java.util.Comparator
		abstract class Comparator<T> {
			static comparing<T, U extends Comparable<U>>(arg0: Function<T, U>): Comparator<T>;
			static comparingWith<T, U>(arg0: Function<T, U>, arg1: Comparator<U>): Comparator<T>;
			static naturalOrder<T extends Comparable<T>>(): Comparator<T>;
			static nullsFirst<T>(arg0: Comparator<T>): Comparator<T>;
			static nullsLast<T>(arg0: Comparator<T>): Comparator<T>;
			static reverseOrder<T extends Comparable<T>>(): Comparator<T>;
		}
		interface Comparator<T> {
			compare(arg0: T, arg1: T): number;
			equals(arg0: any): boolean;
			reversed(): Comparator<T>;
			thenComparing(arg0: Comparator<T>): Comparator<T>;
			thenComparingBy<U extends Comparable<U>>(arg0: Function<T, U>): Comparator<T>;
			thenComparingByWith<U>(arg0: Function<T, U>, arg1: Comparator<U>): Comparator<T>;
		}

		// interface java.util.function.Consumer
		interface Consumer<T> {
			accept(arg0: T): void;
			andThen(arg0: Consumer<T>): Consumer<T>;
		}

		// interface java.util.function.Function
		abstract class Function<T, R> {
			static identity<T>(): Function<T, T>;
		}
		interface Function<T, R> {
			andThenFunction<V>(arg0: Function<R, V>): Function<T, V>;
			apply(arg0: T): R;
			compose<V>(arg0: Function<V, T>): Function<V, R>;
		}

		// interface java.lang.Iterable
		interface Iterable<T> {
			each(arg0: Consumer<T>): void;
			forEach(arg0: (t: T) => void): void;
			iterable(): globalThis.Iterable<T>;
			iterator(): Iterator<T>;
		}

		// interface java.util.Iterator
		interface Iterator<E> {
			forEachRemaining(arg0: Consumer<E>): void;
			hasNext(): boolean;
			next(): E;
			remove(): void;
		}

		// interface java.util.List
		interface List<E> extends Collection<E> {
			addAllAtIndex(arg0: number, arg1: Collection<E>): boolean;
			addAtIndex(arg0: number, arg1: E): void;
			getAtIndex(arg0: number): E;
			indexOf(arg0: any): number;
			lastIndexOf(arg0: any): number;
			removeAtIndex(arg0: number): E;
			setAtIndex(arg0: number, arg1: E): E;
			sort(arg0: Comparator<E>): void;
			subList(arg0: number, arg1: number): List<E>;
		}

		// interface java.util.ListIterator
		interface ListIterator<E> extends Iterator<E> {
			add(arg0: E): void;
			hasPrevious(): boolean;
			nextIndex(): number;
			previous(): E;
			previousIndex(): number;
			remove(): void;
			set(arg0: E): void;
		}

		// interface java.util.Map
		interface Map<K, V> {
			clear(): void;
			containsKey(arg0: any): boolean;
			containsValue(arg0: any): boolean;
			entrySet(): Set<Map$Entry<K, V>>;
			forEach(arg0: (t: K, u: V) => void): void;
			get(arg0: any): V;
			getOrDefault(arg0: any, arg1: V): V;
			isEmpty(): boolean;
			keySet(): Set<K>;
			put(arg0: K, arg1: V): V;
			putAll(arg0: Map<K, V>): void;
			putIfAbsent(arg0: K, arg1: V): V;
			remove(arg0: any): V;
			replace(arg0: K, arg1: V): V;
			size(): number;
			toJsMap(): globalThis.Map<K, V>;
			values(): Collection<V>;
		}

		// interface java.util.Map$Entry
		interface Map$Entry<K, V> {
			equals(arg0: any): boolean;
			getKey(): K;
			getValue(): V;
			hashCode(): number;
			setValue(arg0: V): V;
		}

		// interface java.util.function.Predicate
		abstract class Predicate<T> {
			static isEqual<T>(arg0: any): Predicate<T>;
		}
		interface Predicate<T> {
			and(arg0: Predicate<T>): Predicate<T>;
			negate(): Predicate<T>;
			or(arg0: Predicate<T>): Predicate<T>;
			test(arg0: T): boolean;
		}

		// interface java.util.Set
		interface Set<E> extends Collection<E> {
			toJsSet(): globalThis.Set<E>;
		}

		// interface java.util.stream.Stream
		abstract class Stream<T> {
			static concat<T>(arg0: Stream<T>, arg1: Stream<T>): Stream<T>;
			static empty<T>(): Stream<T>;
			static generate<T>(arg0: Supplier<T>): Stream<T>;
			static iterate<T>(arg0: T, arg1: UnaryOperator<T>): Stream<T>;
			static of<T>(arg0: T): Stream<T>;
			static ofArray<T>(...arg0: T[]): Stream<T>;
		}
		interface Stream<T> {
			allMatch(arg0: Predicate<T>): boolean;
			anyMatch(arg0: Predicate<T>): boolean;
			collect<R, A>(arg0: Collector<T, A, R>): R;
			collectWithCombiner<R>(arg0: Supplier<R>, arg1: BiConsumer<R, T>, arg2: BiConsumer<R, R>): R;
			count(): Long;
			distinct(): Stream<T>;
			filter(arg0: Predicate<T>): Stream<T>;
			filterJs(arg0: (t: T) => boolean): Stream<T>;
			findAny(): Optional<T>;
			findFirst(): Optional<T>;
			flatMap<R>(arg0: Function<T, Stream<R>>): Stream<R>;
			flatMapJs<R>(arg0: (t: T) => Stream<R>): Stream<R>;
			forEach(arg0: Consumer<T>): void;
			forEachJs(arg0: (t: T) => void): void;
			forEachOrdered(arg0: Consumer<T>): void;
			forEachOrderedJs(arg0: Consumer<T>): void;
			iterable(): globalThis.Iterable<T>;
			limit(arg0: Long): Stream<T>;
			map<R>(arg0: Function<T, R>): Stream<R>;
			mapJs<R>(arg0: (t: T) => R): Stream<R>;
			max(arg0: Comparator<T>): Optional<T>;
			min(arg0: Comparator<T>): Optional<T>;
			noneMatch(arg0: Predicate<T>): boolean;
			peek(arg0: Consumer<T>): Stream<T>;
			reduce(arg0: BinaryOperator<T>): Optional<T>;
			reduceWithIdentity(arg0: T, arg1: BinaryOperator<T>): T;
			reduceWithIdentityAndCombiner<U>(arg0: U, arg1: BiFunction<U, T, U>, arg2: BinaryOperator<U>): U;
			skip(arg0: Long): Stream<T>;
			sorted(): Stream<T>;
			sortedWithComparator(arg0: Comparator<T>): Stream<T>;
			toArray(): any[];
		}

		// interface java.util.function.Supplier
		interface Supplier<T> {
			get(): T;
		}

		// interface java.util.function.UnaryOperator
		abstract class UnaryOperator<T> {
			static identity<T>(): UnaryOperator<T>;
		}
		interface UnaryOperator<T> extends Function<T, T> {
		}

		// enum java.util.stream.Collector$Characteristics
		interface Collector$Characteristics extends Comparable<Collector$Characteristics>{}
		class Collector$Characteristics {
			static CONCURRENT: Collector$Characteristics;
			static IDENTITY_FINISH: Collector$Characteristics;
			static UNORDERED: Collector$Characteristics;
		}

		// class java.lang.Byte
		interface Byte extends Comparable<Byte> {}
		class Byte {
			constructor(arg0: number);
			static MIN_VALUE: number;
			static MAX_VALUE: number;
			static SIZE: number;
			static BYTES: number;
			static compare(arg0: number, arg1: number): number;
			static decode(arg0: string): Byte;
			static hashCode(arg0: number): number;
			static parseByte(arg0: string): number;
			static parseByteWithRadix(arg0: string, arg1: number): number;
			static toString(arg0: number): string;
			static valueOf(arg0: string): Byte;
			static valueOfByte(arg0: number): Byte;
			static valueOfWithRadix(arg0: string, arg1: number): Byte;
			byteValue(): number;
			compareTo(arg0: Byte): number;
			doubleValue(): number;
			equals(arg0: any): boolean;
			floatValue(): number;
			hashCode(): number;
			intValue(): number;
			longValue(): Long;
			shortValue(): number;
			toString(): string;
		}

		// class java.lang.Character
		interface Character extends Comparable<Character> {}
		class Character {
			constructor(arg0: number);
			static MIN_RADIX: number;
			static MAX_RADIX: number;
			static MIN_VALUE: number;
			static MAX_VALUE: number;
			static MIN_SURROGATE: number;
			static MAX_SURROGATE: number;
			static MIN_LOW_SURROGATE: number;
			static MAX_LOW_SURROGATE: number;
			static MIN_HIGH_SURROGATE: number;
			static MAX_HIGH_SURROGATE: number;
			static MIN_SUPPLEMENTARY_CODE_POINT: number;
			static MIN_CODE_POINT: number;
			static MAX_CODE_POINT: number;
			static SIZE: number;
			static BYTES: number;
			static charCount(arg0: number): number;
			static codePointAt(arg0: number[], arg1: number): number;
			static codePointAtSequence(arg0: CharSequence, arg1: number): number;
			static codePointAtWithLimit(arg0: number[], arg1: number, arg2: number): number;
			static codePointBefore(arg0: number[], arg1: number): number;
			static codePointBeforeSequence(arg0: CharSequence, arg1: number): number;
			static codePointBeforeWithStart(arg0: number[], arg1: number, arg2: number): number;
			static codePointCount(arg0: number[], arg1: number, arg2: number): number;
			static codePointCountSequence(arg0: CharSequence, arg1: number, arg2: number): number;
			static compare(arg0: number, arg1: number): number;
			static digit(arg0: number, arg1: number): number;
			static forDigit(arg0: number, arg1: number): number;
			static hashCode(arg0: number): number;
			static isBmpCodePoint(arg0: number): boolean;
			static isDigit(arg0: number): boolean;
			static isHighSurrogate(arg0: number): boolean;
			static isLetter(arg0: number): boolean;
			static isLetterOrDigit(arg0: number): boolean;
			static isLowSurrogate(arg0: number): boolean;
			static isLowerCase(arg0: number): boolean;
			static isSpace(arg0: number): boolean;
			static isSupplementaryCodePoint(arg0: number): boolean;
			static isSurrogatePair(arg0: number, arg1: number): boolean;
			static isUpperCase(arg0: number): boolean;
			static isValidCodePoint(arg0: number): boolean;
			static isWhitespace(arg0: number): boolean;
			static isWhitespaceInt(arg0: number): boolean;
			static offsetByCodePoints(arg0: number[], arg1: number, arg2: number, arg3: number, arg4: number): number;
			static offsetByCodePointsSequence(arg0: CharSequence, arg1: number, arg2: number): number;
			static toChars(arg0: number): number[];
			static toCharsWithDst(arg0: number, arg1: number[], arg2: number): number;
			static toCodePoint(arg0: number, arg1: number): number;
			static toLowerCase(arg0: number): number;
			static toString(arg0: number): string;
			static toUpperCase(arg0: number): number;
			static valueOf(arg0: number): Character;
			charValue(): number;
			compareTo(arg0: Character): number;
			equals(arg0: any): boolean;
			hashCode(): number;
			toString(): string;
		}

		// class java.lang.Class
		class Class<T> {
			constructor();
		}

		// class java.util.stream.Collectors
		class Collectors {
			static collectingAndThen<T, A, R, RR>(arg0: Collector<T, A, R>, arg1: Function<R, RR>): Collector<T, A, RR>;
			static collectingAndThenJs<T, A, R, RR>(arg0: Collector<T, A, R>, arg1: (t: R) => RR): Collector<T, A, RR>;
			static counting<T>(): Collector<T, any, Long>;
			static groupingBy<T, K, A, D>(arg0: Function<T, K>, arg1: Collector<T, A, D>): Collector<T, any, Map<K, D>>;
			static groupingByAsLists<T, K>(arg0: Function<T, K>): Collector<T, any, Map<K, List<T>>>;
			static groupingByAsListsJs<T, K>(arg0: (t: T) => K): Collector<T, any, Map<K, List<T>>>;
			static groupingByJs<T, K, A, D>(arg0: (t: T) => K, arg1: Collector<T, A, D>): Collector<T, any, Map<K, D>>;
			static groupingByToMap<T, K, D, A, M extends Map<K, D>>(arg0: Function<T, K>, arg1: Supplier<M>, arg2: Collector<T, A, D>): Collector<T, any, M>;
			static groupingByToMapJs<T, K, D, A, M extends Map<K, D>>(arg0: (t: T) => K, arg1: () => M, arg2: Collector<T, A, D>): Collector<T, any, M>;
			static joining(): Collector<CharSequence, any, string>;
			static joiningWithDelimiter(arg0: CharSequence): Collector<CharSequence, any, string>;
			static joiningWithDelimiterPrefixSuffix(arg0: CharSequence, arg1: CharSequence, arg2: CharSequence): Collector<CharSequence, any, string>;
			static mapping<T, U, A, R>(arg0: Function<T, U>, arg1: Collector<U, A, R>): Collector<T, any, R>;
			static mappingJs<T, U, A, R>(arg0: (t: T) => U, arg1: Collector<U, A, R>): Collector<T, any, R>;
			static maxBy<T>(arg0: Comparator<T>): Collector<T, any, Optional<T>>;
			static minBy<T>(arg0: Comparator<T>): Collector<T, any, Optional<T>>;
			static partitioningBy<T, D, A>(arg0: Predicate<T>, arg1: Collector<T, A, D>): Collector<T, any, Map<boolean, D>>;
			static partitioningByAsLists<T>(arg0: Predicate<T>): Collector<T, any, Map<boolean, List<T>>>;
			static partitioningByAsListsJs<T>(arg0: (t: T) => boolean): Collector<T, any, Map<boolean, List<T>>>;
			static partitioningByJs<T, D, A>(arg0: (t: T) => boolean, arg1: Collector<T, A, D>): Collector<T, any, Map<boolean, D>>;
			static toCollection<T, C extends Collection<T>>(arg0: Supplier<C>): Collector<T, any, C>;
			static toCollectionJs<T, C extends Collection<T>>(arg0: () => C): Collector<T, any, C>;
			static toList<T>(): Collector<T, any, List<T>>;
			static toMap<T, K, U>(arg0: Function<T, K>, arg1: Function<T, U>, arg2: BinaryOperator<U>): Collector<T, any, Map<K, U>>;
			static toMapJs<T, K, U>(arg0: (t: T) => K, arg1: (t: T) => U, arg2: (t: U, u: U) => U): Collector<T, any, Map<K, U>>;
			static toMapSuppliedBy<T, K, U, M extends Map<K, U>>(arg0: Function<T, K>, arg1: Function<T, U>, arg2: BinaryOperator<U>, arg3: Supplier<M>): Collector<T, any, M>;
			static toMapSuppliedByJs<T, K, U, M extends Map<K, U>>(arg0: (t: T) => K, arg1: (t: T) => U, arg2: (t: U, u: U) => U, arg3: () => M): Collector<T, any, M>;
			static toMapUniquely<T, K, U>(arg0: Function<T, K>, arg1: Function<T, U>): Collector<T, any, Map<K, U>>;
			static toMapUniquelyJs<T, K, U>(arg0: (t: T) => K, arg1: (t: T) => U): Collector<T, any, Map<K, U>>;
			static toSet<T>(): Collector<T, any, Set<T>>;
		}

		// class java.util.Date
		interface Date extends Comparable<Date> {}
		class Date {
			static UTC(arg0: number, arg1: number, arg2: number, arg3: number, arg4: number, arg5: number): Long;
			static fromJsDate(arg0: globalThis.Date): Date;
			static now(): Long;
			protected static padTwo(arg0: number): string;
			static parse(arg0: string): Long;
			after(arg0: Date): boolean;
			before(arg0: Date): boolean;
			clone(): any;
			compareTo(arg0: Date): number;
			equals(arg0: any): boolean;
			getDate(): number;
			getDay(): number;
			getFullYear(): number;
			getHours(): number;
			getMilliseconds(): number;
			getMinutes(): number;
			getMonth(): number;
			getSeconds(): number;
			getTime(): Long;
			getTimezoneOffset(): number;
			getUTCDate(): number;
			getUTCDay(): number;
			getUTCFullYear(): number;
			getUTCHours(): number;
			getUTCMilliseconds(): number;
			getUTCMinutes(): number;
			getUTCMonth(): number;
			getUTCSeconds(): number;
			getYear(): number;
			hashCode(): number;
			setDate(arg0: number): void;
			setFullYear(arg0: number): void;
			setFullYearDay(arg0: number, arg1: number, arg2: number): void;
			setHours(arg0: number): void;
			setHoursTime(arg0: number, arg1: number, arg2: number, arg3: number): void;
			setMilliseconds(arg0: number): void;
			setMinutes(arg0: number): void;
			setMonth(arg0: number): void;
			setSeconds(arg0: number): void;
			setTime(arg0: Long): void;
			setUTCDate(arg0: number): void;
			setUTCFullYear(arg0: number): void;
			setUTCHours(arg0: number): void;
			setUTCMilliseconds(arg0: number): void;
			setUTCMinutes(arg0: number): void;
			setUTCMonth(arg0: number): void;
			setUTCSeconds(arg0: number): void;
			setYear(arg0: number): void;
			toDateString(): string;
			toGMTString(): string;
			toISOString(): string;
			toJSON(): string;
			toJsDate(): globalThis.Date;
			toLocaleDateString(): string;
			toLocaleString(): string;
			toLocaleTimeString(): string;
			toString(): string;
			toTimeString(): string;
			toUTCString(): string;
			valueOf(): Long;
		}

		// class java.lang.Enum
		interface Enum<E extends Enum<E>> extends Comparable<E> {}
		class Enum<E extends Enum<E>> {
			protected static createValueOfMap<T extends Enum<T>>(arg0: T[]): any;
			protected static valueOf<T extends Enum<T>>(arg0: any, arg1: string): T;
			compareTo(arg0: E): number;
			equals(arg0: any): boolean;
			hashCode(): number;
			name(): string;
			ordinal(): number;
			toString(): string;
		}

		// class java.lang.Exception
		class Exception extends Throwable {
		}

		// class java.lang.Float
		interface Float extends Comparable<Float> {}
		class Float {
			constructor(arg0: number);
			static MAX_VALUE: number;
			static MIN_VALUE: number;
			static MAX_EXPONENT: number;
			static MIN_EXPONENT: number;
			static MIN_NORMAL: number;
			static NaN: number;
			static NEGATIVE_INFINITY: number;
			static POSITIVE_INFINITY: number;
			static SIZE: number;
			static BYTES: number;
			static compare(arg0: number, arg1: number): number;
			static floatToIntBits(arg0: number): number;
			static hashCode(arg0: number): number;
			static intBitsToFloat(arg0: number): number;
			static isFinite(arg0: number): boolean;
			static isInfinite(arg0: number): boolean;
			static isNaN(arg0: number): boolean;
			static max(arg0: number, arg1: number): number;
			static min(arg0: number, arg1: number): number;
			static parseFloat(arg0: string): number;
			static sum(arg0: number, arg1: number): number;
			static toString(arg0: number): string;
			static valueOf(arg0: string): Float;
			static valueOfFloat(arg0: number): Float;
			byteValue(): number;
			compareTo(arg0: Float): number;
			doubleValue(): number;
			equals(arg0: any): boolean;
			floatValue(): number;
			hashCode(): number;
			intValue(): number;
			isInfinite(): boolean;
			isNaN(): boolean;
			longValue(): Long;
			shortValue(): number;
			toString(): string;
			valueOf(): number;
		}

		// class java.io.InputStream
		class InputStream {
			constructor();
			available(): number;
			close(): void;
			mark(arg0: number): void;
			markSupported(): boolean;
			read(): number;
			readBuffer(arg0: number[]): number;
			readBufferOffset(arg0: number[], arg1: number, arg2: number): number;
			reset(): void;
			skip(arg0: Long): Long;
		}

		// class java.lang.Integer
		interface Integer extends Comparable<Integer> {}
		class Integer {
			constructor(arg0: number);
			static MAX_VALUE: number;
			static MIN_VALUE: number;
			static SIZE: number;
			static BYTES: number;
			static bitCount(arg0: number): number;
			static compare(arg0: number, arg1: number): number;
			static decode(arg0: string): Integer;
			static hashCode(arg0: number): number;
			static highestOneBit(arg0: number): number;
			static lowestOneBit(arg0: number): number;
			static max(arg0: number, arg1: number): number;
			static min(arg0: number, arg1: number): number;
			static numberOfLeadingZeros(arg0: number): number;
			static numberOfTrailingZeros(arg0: number): number;
			static parseInt(arg0: string): number;
			static parseIntWithRadix(arg0: string, arg1: number): number;
			static reverse(arg0: number): number;
			static reverseBytes(arg0: number): number;
			static rotateLeft(arg0: number, arg1: number): number;
			static rotateRight(arg0: number, arg1: number): number;
			static signum(arg0: number): number;
			static sum(arg0: number, arg1: number): number;
			static toBinaryString(arg0: number): string;
			static toHexString(arg0: number): string;
			static toOctalString(arg0: number): string;
			static toString(arg0: number): string;
			static toStringWithRadix(arg0: number, arg1: number): string;
			static valueOf(arg0: string): Integer;
			static valueOfInt(arg0: number): Integer;
			static valueOfWithRadix(arg0: string, arg1: number): Integer;
			byteValue(): number;
			compareTo(arg0: Integer): number;
			doubleValue(): number;
			equals(arg0: any): boolean;
			floatValue(): number;
			hashCode(): number;
			intValue(): number;
			longValue(): Long;
			shortValue(): number;
			toString(): string;
			valueOf(): number;
		}

		// class java.lang.Long
		interface Long extends Comparable<Long> {}
		class Long {
			constructor(arg0: Long);
			static MAX_VALUE: Long;
			static MIN_VALUE: Long;
			static SIZE: number;
			static BYTES: number;
			static bitCount(arg0: Long): number;
			static compare(arg0: Long, arg1: Long): number;
			static decode(arg0: string): Long;
			static hashCode(arg0: Long): number;
			static highestOneBit(arg0: Long): Long;
			static lowestOneBit(arg0: Long): Long;
			static max(arg0: Long, arg1: Long): Long;
			static min(arg0: Long, arg1: Long): Long;
			static numberOfLeadingZeros(arg0: Long): number;
			static numberOfTrailingZeros(arg0: Long): number;
			static parseLong(arg0: string): Long;
			static parseLongWithRadix(arg0: string, arg1: number): Long;
			static reverse(arg0: Long): Long;
			static reverseBytes(arg0: Long): Long;
			static rotateLeft(arg0: Long, arg1: number): Long;
			static rotateRight(arg0: Long, arg1: number): Long;
			static signum(arg0: Long): number;
			static sum(arg0: Long, arg1: Long): Long;
			static toBinaryString(arg0: Long): string;
			static toHexString(arg0: Long): string;
			static toOctalString(arg0: Long): string;
			static toString(arg0: Long): string;
			static toStringWithRadix(arg0: Long, arg1: number): string;
			static valueOf(arg0: string): Long;
			static valueOfLong(arg0: Long): Long;
			static valueOfWithRadix(arg0: string, arg1: number): Long;
			byteValue(): number;
			compareTo(arg0: Long): number;
			doubleValue(): number;
			equals(arg0: any): boolean;
			floatValue(): number;
			hashCode(): number;
			intValue(): number;
			longValue(): Long;
			shortValue(): number;
			toString(): string;
			valueOf(): number;
		}

		// class java.util.Optional
		class Optional<T> {
			static empty<T>(): Optional<T>;
			static of<T>(arg0: T): Optional<T>;
			static ofNullable<T>(arg0: T): Optional<T>;
			equals(arg0: any): boolean;
			filter(arg0: Predicate<T>): Optional<T>;
			filterJs(arg0: (t: T) => boolean): Optional<T>;
			flatMap<U>(arg0: Function<T, Optional<U>>): Optional<U>;
			flatMapJs<U>(arg0: (t: T) => Optional<U>): Optional<U>;
			get(): T;
			hashCode(): number;
			ifPresent(arg0: Consumer<T>): void;
			ifPresentJs(arg0: (t: T) => void): void;
			isPresent(): boolean;
			map<U>(arg0: Function<T, U>): Optional<U>;
			mapJs<U>(arg0: (t: T) => U): Optional<U>;
			orElse(arg0: T): T;
			orElseGet(arg0: Supplier<T>): T;
			orElseGetJs(arg0: () => T): T;
			orElseThrow<X extends Throwable>(arg0: Supplier<X>): T;
			orElseThrowJs<X extends Throwable>(arg0: () => X): T;
			toString(): string;
		}

		// class java.io.OutputStream
		class OutputStream {
			constructor();
			close(): void;
			flush(): void;
			write(arg0: number): void;
			writeBuffer(arg0: number[]): void;
			writeBufferOffset(arg0: number[], arg1: number, arg2: number): void;
		}

		// class java.lang.RuntimeException
		class RuntimeException extends Exception {
		}

		// class java.util.Stack
		interface Stack<E> extends List<E>, List<E>, Collection<E> {}
		class Stack<E> {
			constructor();
			clone(): any;
			empty(): boolean;
			peek(): E;
			pop(): E;
			push(arg0: E): E;
			search(arg0: any): number;
		}

		// class java.lang.StackTraceElement
		class StackTraceElement {
			constructor();
			equals(arg0: any): boolean;
			getClassName(): string;
			getFileName(): string;
			getLineNumber(): number;
			getMethodName(): string;
			hashCode(): number;
			isNativeMethod(): boolean;
			toString(): string;
		}

		// class java.lang.Throwable
		class Throwable {
			static of(arg0: any): Throwable;
			fillInStackTrace(): Throwable;
			getBackingJsObject(): any;
			getCause(): Throwable;
			getLocalizedMessage(): string;
			getMessage(): string;
			getStackTrace(): StackTraceElement[];
			getSuppressed(): Throwable[];
			initCause(arg0: Throwable): Throwable;
			printStackTrace(): void;
			setStackTrace(arg0: StackTraceElement[]): void;
			toString(): string;
		}

		// class java.lang.Void
		class Void {
		}

	}

	namespace hc.session {

		// interface com.google.gwt.user.client.rpc.AsyncCallback
		interface AsyncCallback<T> {
			onFailure(arg0: hc.Throwable): void;
			onSuccess(arg0: T): void;
		}

	}

}
