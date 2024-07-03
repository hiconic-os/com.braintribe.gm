// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.dataio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SafeArray<T> implements Iterable<T> {
	protected Object array[];
	protected Map<Integer, T> extension;
	protected int arraySize;
	protected int size;
	
	public SafeArray(int arraySize) {
		array = new Object[arraySize];
		this.arraySize = arraySize; 
	}
	
	public int size() {
		return size;
	}
	
	public void put(int index, T value) {
		T oldValue = null;
		if (index < arraySize) {
			oldValue = (T)array[index];
			array[index] = value;
		}
		else {
			if (extension == null)
				extension = new HashMap<>();
		
			oldValue = extension.put(index, value);
		}
		
		if (oldValue == null)
			size++;
	}
	
	public T get(int index) {
		if (index < arraySize)
			return (T)array[index];
		else if (extension != null)
			return extension.get(index);
		else
			return null;
	}
	
	@Override
	public Iterator<T> iterator() {
		Stream<T> stream = Stream.of(array).filter(v -> v != null).map(v -> (T)v);
		
		if (extension != null) {
			stream = Stream.concat(stream, extension.values().stream());
		}
		
		return stream.iterator();
	}
	
	public static void main(String[] args) {
		SafeArray<String> l = new SafeArray<>(10);
		
		l.put(1, "one");
		l.put(2, "two");
		l.put(10, "ten");
		l.put(100, "hundret");
		
		System.out.println(l.get(1));
		System.out.println(l.get(2));
		System.out.println(l.get(10));
		System.out.println(l.get(100));
		System.out.println(l.size());
	}
}
