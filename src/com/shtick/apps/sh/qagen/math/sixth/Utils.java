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
	 * @param array
	 * @param count
	 * @return
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
