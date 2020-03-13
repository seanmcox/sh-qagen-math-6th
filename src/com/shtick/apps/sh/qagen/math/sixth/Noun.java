/**
 * 
 */
package com.shtick.apps.sh.qagen.math.sixth;

/**
 * @author scox
 *
 */
public interface Noun {
	/**
	 * See: https://www.learnenglish.de/grammar/casetext.html
	 *
	 */
	enum Case {
		/**
		 * Subject
		 */
		NOMINATIVE,
		/**
		 * Object
		 */
		ACCUSATIVE,
		/**
		 * Possessive
		 */
		GENITIVE};
		
	/**
	 * 
	 * @param c
	 * @param singular
	 * @return The indicated form of the noun.
	 */
	public String get(Case c, boolean singular);
}
