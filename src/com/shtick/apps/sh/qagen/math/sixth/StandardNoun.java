/**
 * 
 */
package com.shtick.apps.sh.qagen.math.sixth;

/**
 * A model of fairly standard nouns based on orthography.
 * 
 * Based largely on https://preply.com/en/blog/2015/02/03/simple-rules-for-the-formation-of-plural-nouns-in-english/
 * 
 * Ignoring greek us->i, um->a, on->a pluralization. Plus, there are plenty of exceptions to the rules.
 * 
 * @author scox
 *
 */
public class StandardNoun implements Noun{
	private String singularNominativeForm;
	
	/**
	 * 
	 * @param singularNominativeForm
	 */
	public StandardNoun(String singularNominativeForm) {
		this.singularNominativeForm = singularNominativeForm;
	}

	public String get(Case c, boolean singular) {
		if(singular) {
			if(c == Case.GENITIVE)
				return singularNominativeForm+"'s";
			return singularNominativeForm;
		}
		String form = singularNominativeForm;
		if(form.endsWith("sh")||form.endsWith("x")||form.endsWith("ch")||form.endsWith("s")) {
			form+="es";
		}
		else if(form.endsWith("y")&&(form.length()>1)&&isConsonant(form.charAt(form.length()-2))) {
			form=form.substring(0, form.length()-1)+"ies";
		}
		else if(form.endsWith("f")&&(!form.endsWith("ff"))) {
			form=form.substring(0, form.length()-1)+"ves";
		}
		else if(form.endsWith("fe")) {
			form=form.substring(0, form.length()-2)+"ves";
		}
		else {
			form+="s";
		}
		if(c == Case.GENITIVE)
			return form+"'";
		return form;
	}
	
	/**
	 * 
	 * @param c A character. Must be alphabetic and lower case.
	 * @return true if c is a consonant and false otherwise.
	 */
	private boolean isConsonant(char c) {
		return (c!='a')&&(c!='e')&&(c!='i')&&(c!='o')&&(c!='u')&&(c!='y');
	}
}
