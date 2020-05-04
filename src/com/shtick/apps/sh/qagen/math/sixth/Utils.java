/**
 * 
 */
package com.shtick.apps.sh.qagen.math.sixth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author scox
 *
 */
public class Utils {
	private static final Random RANDOM = new Random();
	
	/**
	 * 
	 * @param array The source array.
	 * @param count An element that can be used to constrain the size of the returned array.
	 * @return An array of size min(array.length,count) consisting of elements randomly selected from the source array. (Each element of the source array will be selected only once.)
	 */
	public static <T> T[] getRandomArray(T[] array,int count) {
		ArrayList<T> temp = new ArrayList<>();
		for(T t:array) {
			temp.add(RANDOM.nextInt(temp.size()+1),t);
		}
		while(temp.size()>count) {
			temp.remove(temp.size()-1);
		}
		
		return (T[]) Arrays.copyOf(array, count, array.getClass());
	}
}
