/**
 * 
 */
package com.shtick.apps.sh.qagen.math.sixth;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;

import com.shtick.apps.sh.core.Driver;
import com.shtick.apps.sh.core.Question;
import com.shtick.apps.sh.core.Subject;
import com.shtick.apps.sh.core.SubjectQuestionGenerator;
import com.shtick.apps.sh.core.UserID;
import com.shtick.apps.sh.core.content.Choice;
import com.shtick.apps.sh.core.content.Marshal;
import com.shtick.apps.sh.core.content.MultipleChoice;
import com.shtick.apps.sh.qagen.math.sixth.Noun.Case;
import com.shtick.utils.data.json.JSONEncoder;

/**
 * @author sean.cox
 *
 */
public class SixthGradeMathQuestionGenerator implements SubjectQuestionGenerator {
	private enum Shape {
		ISOSCELES_TRIANGLE,EQUILATERAL_TRIANGLE,RIGHT_TRIANGLE,PARALLELOGRAM,RECTANGLE,RHOMBUS,SQUARE,TRAPEZOID;
		
		public String getNiceName() {
			String[] parts = this.name().split("_");
			String retval = "";
			for(int i=0;i<parts.length;i++) {
				if(retval.length()>0)
					retval+=" ";
				retval += parts[i].substring(0, 1)+parts[i].substring(1).toLowerCase();
			}
			return retval;
		}
	}
	private enum Angle {
		RIGHT,ACUTE,OBTUSE;
		
		public String getNiceName() {
			String[] parts = this.name().split("_");
			String retval = "";
			for(int i=0;i<parts.length;i++) {
				if(retval.length()>0)
					retval+=" ";
				retval += parts[i].substring(0, 1)+parts[i].substring(1).toLowerCase();
			}
			return retval;
		}
	}
	private static Random RANDOM = new Random();
	private static final String ADDITION_OPERATOR = "+";
	private static final String SUBTRACTION_OPERATOR = "-";
	private static final String MULTIPLICATION_OPERATOR = "\u00D7";
	private static final String DIVISION_OPERATOR = "\u00F7";
	private static final String[] ARITHMETIC_OPERATORS = new String[] {ADDITION_OPERATOR,SUBTRACTION_OPERATOR,MULTIPLICATION_OPERATOR,DIVISION_OPERATOR};
	private static final String[] VOLUME_UNITS = new String[] {"cubic feet","cubic inches","cubic millimeters","cubic centimeters","cubic meters"};
	private static final String[] AREA_UNITS = new String[] {"square feet","square inches","square millimeters","square centimeters","square meters"};
	private static final String[] LENGTH_UNITS = new String[] {"feet","inches","millimeters","centimeters","meters"};
	private static final String[] LENGTH_UNITS_ABBR = new String[] {"ft","in","mm","cm","m"};
	private static final String[] PLACE_VALUE_VOCABULARY = new String[] {"hundred millions","ten millions","millions","hundred thousands","ten thousands","thousands","hundreds","tens","ones","tenths","hundredths","thousandths"};
	private static final String[] PLACE_VALUE_VOCABULARY_ABBR = new String[] {"100,000,000s","10,000,000s","1,000,000s","100,000s","10,000s","1,000s","100s","10s","1s","10ths","100ths","1,000ths"};
	private static final int[] PRIME_NUMBERS = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199};
	private static final Subject subject = new Subject("com.shtick.math.6th");
	private static final HashMap<String,String> dimensionDescriptions = new HashMap<>();

	/* (non-Javadoc)
	 * @see com.shtick.apps.sh.core.SubjectQuestionGenerator#getSubject()
	 */
	@Override
	public Subject getSubject() {
		return subject;
	}

	/* (non-Javadoc)
	 * @see com.shtick.apps.sh.core.SubjectQuestionGenerator#generateQuestions(com.shtick.apps.sh.core.Driver, com.shtick.apps.sh.core.UserID, int)
	 */
	@Override
	public Collection<Question> generateQuestions(Driver driver, UserID userID, int count) {
		if(count<=0)
			throw new IllegalArgumentException("One ore more questions must be requested.");
		ArrayList<Question> retval = new ArrayList<>();
		for(int i=0;i<count;i++)
			retval.add(generateQuestion());
		return retval;
	}

	private Question generateQuestion(){
		int type = RANDOM.nextInt(17);
		int a,b,c;
		boolean isStory = RANDOM.nextBoolean();
		boolean translateQuestion = RANDOM.nextBoolean();
		int unknown = RANDOM.nextInt(3);
		switch(type){
		case 0:// Addition
			a = RANDOM.nextInt(1000000);
			b = RANDOM.nextInt(1000000-a);
			c = a+b;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 1:// Subtraction
			a = RANDOM.nextInt(1000000);
			b = RANDOM.nextInt(a+1);
			c = a-b;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 2:// Multiplication
			a = RANDOM.nextInt(1000);
			b = RANDOM.nextInt(100);
			c = a*b;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 3:// Division
			b = RANDOM.nextInt(100)+1;
			c = RANDOM.nextInt(1000);
			a = b*c;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 4:
			return generateFractionOperationQuestion();
		case 5:
			return generatePrimeNumberQuestion();
		case 6:
			return generateCommonDenominatorOrMultipleQuestion();
		case 7:
			return generateDecimalOperationQuestion();
		case 8:
			return generateAreaQuestion();
		case 9:
			return generateRationalNumberComparisonQuestion();
		case 10:
			return generateOppositeOrAbsoluteValueQuestion();
		case 11:
			return generateCoordinatePlaneDistanceQuestion();
		case 12:
			return generateMedianOrMeanQuestion();
		case 13:
			return generatePrismSurfaceQuestion();
		case 14:
			return generateOrderOfOperationsQuestion();
		case 15:
			return generateEquationsWithTwoVariablesQuestion();
		default:
			return generateVolumeQuestion();
		}
	}
	
	private Question generateRationalNumberComparisonQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		int numberDigits = 5;
		int deltaPlace = (RANDOM.nextInt(3)==0)?numberDigits:RANDOM.nextInt(numberDigits);
		int decimalPlace = RANDOM.nextInt(numberDigits+1);
		int[] digitsA = new int[numberDigits];
		int[] digitsB = new int[numberDigits];
		for(int i=0;i<deltaPlace;i++) {
			int d = RANDOM.nextInt(10);
			digitsA[i] = d;
			digitsB[i] = d;
		}
		if(deltaPlace<numberDigits) {
			digitsA[deltaPlace] = RANDOM.nextInt(10);
			digitsB[deltaPlace] = digitsA[deltaPlace]+RANDOM.nextInt(9)+1;
		}
		for(int i=deltaPlace+1;i<numberDigits;i++) {
			digitsA[i] = RANDOM.nextInt(10);
			digitsB[i] = RANDOM.nextInt(10);
		}
		
		String na = "";
		String nb = "";
		for(int i=0;i<numberDigits;i++) {
			if(i==decimalPlace) {
				na+=".";
				nb+=".";
			}
			na+=digitsA[i];
			nb+=digitsB[i];
		}
		while(na.charAt(0)=='0')
			na = na.substring(1);
		while(nb.charAt(0)=='0')
			nb = nb.substring(1);
		while(na.charAt(na.length()-1)=='0')
			na = na.substring(0,na.length()-1);
		while(nb.charAt(nb.length()-1)=='0')
			nb = nb.substring(0,nb.length()-1);
		if(na.length()==0)
			na = "0";
		if(nb.length()==0)
			nb = "0";
		if(na.charAt(0)=='.')
			na = "0"+na;
		if(nb.charAt(0)=='.')
			nb = "0"+nb;
		if(na.charAt(na.length()-1)=='.')
			na = na.substring(0,na.length()-1);
		if(nb.charAt(nb.length()-1)=='.')
			nb = nb.substring(0,nb.length()-1);
		
		String answer;
		if(na.equals(nb))
			answer = "=";
		else if(Double.parseDouble(na)<Double.parseDouble(nb))
			answer = "<";
		else
			answer = ">";

		Choice[] choiceArray = new Choice[] {
				new Choice("text/plain", na + "<" + nb, "<"),
				new Choice("text/plain", na + "=" + nb, "="),
				new Choice("text/plain", na + ">" + nb, ">")
		};
		choiceArray = Utils.getRandomArray(choiceArray, choiceArray.length);
		ArrayList<Choice> choices = new ArrayList<>(choiceArray.length);
		for(Choice choice:choiceArray)
			choices.add(choice);
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", "", choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		return new Question("Which is true?","text/plain",answerPrompt,"choice/radio",answer,dimensions,4);
	}
	
	private Question generateOppositeOrAbsoluteValueQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		int i = RANDOM.nextInt(100000);
		if(RANDOM.nextBoolean())
			i=-i;
		boolean absoluteValue = RANDOM.nextBoolean();
		String answer;
		String question;
		if(absoluteValue) {
			answer = ""+Math.abs(i);
			question = "What is the absolute value of the above number?";
		}
		else {
			answer = ""+(-i);
			question = "What is the opposite value of the above number?";
		}
		if(RANDOM.nextBoolean()) {
			return new Question(""+i,"text/plain",question,"text/plain",answer,dimensions,4);
		}
		Choice[] choiceArray = new Choice[] {
				new Choice("text/plain", ""+i, ""+i),
				(i>0)?new Choice("text/plain", ""+(-i), ""+(-i)):new Choice("text/plain", "100", "100"),
				null,null
		};
		if(i>=2) {
			choiceArray[2] = new Choice("text/plain", ""+(i*2), "false");
			choiceArray[3] = new Choice("text/plain", ""+(i/2), "alsoFalse");
		}
		else {
			choiceArray[2] = new Choice("text/plain", ""+(i+2), "false");
			choiceArray[3] = new Choice("text/plain", ""+(i-2), "alsoFalse");
		}
		choiceArray = Utils.getRandomArray(choiceArray, choiceArray.length);
		ArrayList<Choice> choices = new ArrayList<>(4);
		for(Choice choice:choiceArray)
			choices.add(choice);
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", question, choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		return new Question(""+i,"text/plain",answerPrompt,"choice/radio",answer,dimensions,4);
	}
	
	private Question generateCoordinatePlaneDistanceQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		Point point1 = new Point(RANDOM.nextInt(21)-10,RANDOM.nextInt(21)-10);
		Point point2 = new Point(RANDOM.nextInt(21)-10,RANDOM.nextInt(21)-10);
		while(point2.equals(point1))
			point2 = new Point(RANDOM.nextInt(21)-10,RANDOM.nextInt(21)-10);
		
		String svg = getCoordinatePlaneDrawing(point1, point2);
		double answerValue = Math.sqrt((point1.x-point2.x)*(point1.x-point2.x)+(point1.y-point2.y)*(point1.y-point2.y));
		String answer =""+answerValue;
		String[] choiceArray = new String[] {
			answer,
			""+((Math.abs(point1.x-point2.x)+Math.abs(point1.y-point2.y))-(RANDOM.nextBoolean()?RANDOM.nextDouble():0)),
			""+(Math.abs(point1.x-point2.x)+Math.abs(point1.y-point2.y))/2,
			""+(RANDOM.nextBoolean()?(answerValue+RANDOM.nextDouble()):(answerValue-RANDOM.nextDouble()))
		};
		choiceArray = Utils.getRandomArray(choiceArray, choiceArray.length);
		ArrayList<Choice> choices = new ArrayList<>(4);
		for(String choice:choiceArray)
			choices.add(new Choice("text/plain", choice, choice));
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", "You have two points on a grid, ("+point1.x+","+point1.y+") and ("+point2.x+","+point2.y+"). Which of these values best represents the distance between these two points?", choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		return new Question(svg,"image/svg+xml",answerPrompt,"choice/radio",answer,dimensions,4);
	}
	
	private Question generateMedianOrMeanQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		ArrayList<Integer> values = new ArrayList<>(5);
		for(int i=0;i<4;i++)
			values.add(RANDOM.nextInt(100));
		if(RANDOM.nextBoolean())
			values.add(RANDOM.nextInt(100));
		ArrayList<Integer> sortedValues = new ArrayList<>(values);
		Collections.sort(sortedValues);
		HashSet<String> answerSet = new HashSet<>(4);
		String median = ""+((sortedValues.size()%2==1)?(sortedValues.get(sortedValues.size()/2)+sortedValues.get(sortedValues.size()/2-1))/2.0:sortedValues.get(sortedValues.size()/2));
		String badMedian = ""+((sortedValues.size()%2==1)?(values.get(values.size()/2)+values.get(values.size()/2-1))/2.0:values.get(values.size()/2));
		double calc = 0;
		for(Integer value:values)
			calc+=value;
		calc /= values.size();
		String mean = ""+calc;
		String badMean = ""+((sortedValues.get(0)+sortedValues.get(sortedValues.size()-1))/2.0);


		boolean isMedian = RANDOM.nextBoolean();
		String answer = isMedian?median:mean;
		answerSet.add(median);
		answerSet.add(badMedian);
		answerSet.add(mean);
		answerSet.add(badMean);
		answerSet.add(""+sortedValues.get(0).doubleValue());
		answerSet.add(""+sortedValues.get(sortedValues.size()-1).doubleValue());
		
		Object[] choiceArray = Utils.getRandomArray(answerSet.toArray(), 4);
		choiceArray = Utils.getRandomArray(choiceArray, choiceArray.length);
		ArrayList<Choice> choices = new ArrayList<>(4);
		boolean answerFound = false;
		for(Object choice:choiceArray) {
			if(choice == answer)
				answerFound = true;
			choices.add(new Choice("text/plain", choice.toString(), choice.toString()));
		}
		if(!answerFound)
			choices.set(RANDOM.nextInt(4), new Choice("text/plain", answer, answer));
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", "Which of the following is the "+(isMedian?"median":"mean")+" value of this list of numbers?", choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		String valueList = "";
		for(Integer value:values) {
			if(valueList.length()>0)
				valueList += ", ";
			valueList+=value;
		}
		return new Question(valueList,"text/plain",answerPrompt,"choice/radio",answer,dimensions,4);
	}
	
	private String getCoordinatePlaneDrawing(Point ... points) {
		String axes = "<line x1=\"50\" y1=\"5\" x2=\"50\" y2=\"95\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"5\" y1=\"50\" x2=\"95\" y2=\"50\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"50\" y1=\"5\" x2=\"54\" y2=\"9\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"50\" y1=\"5\" x2=\"46\" y2=\"9\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"5\" y1=\"50\" x2=\"9\" y2=\"56\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"5\" y1=\"50\" x2=\"9\" y2=\"46\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"50\" y1=\"95\" x2=\"54\" y2=\"91\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"50\" y1=\"95\" x2=\"46\" y2=\"91\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"95\" y1=\"50\" x2=\"91\" y2=\"56\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"95\" y1=\"50\" x2=\"91\" y2=\"46\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
		String grid = 
				"<line x1=\"10\" y1=\"5\" x2=\"10\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"15\" y1=\"5\" x2=\"15\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"20\" y1=\"5\" x2=\"20\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"25\" y1=\"5\" x2=\"25\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"30\" y1=\"5\" x2=\"30\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"35\" y1=\"5\" x2=\"35\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"40\" y1=\"5\" x2=\"40\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"45\" y1=\"5\" x2=\"45\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"55\" y1=\"5\" x2=\"55\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"60\" y1=\"5\" x2=\"60\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"65\" y1=\"5\" x2=\"65\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"70\" y1=\"5\" x2=\"70\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"75\" y1=\"5\" x2=\"75\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"80\" y1=\"5\" x2=\"80\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"85\" y1=\"5\" x2=\"85\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"90\" y1=\"5\" x2=\"90\" y2=\"95\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"10\" x2=\"95\" y2=\"10\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"15\" x2=\"95\" y2=\"15\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"20\" x2=\"95\" y2=\"20\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"25\" x2=\"95\" y2=\"25\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"30\" x2=\"95\" y2=\"30\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"35\" x2=\"95\" y2=\"35\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"40\" x2=\"95\" y2=\"40\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"45\" x2=\"95\" y2=\"45\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"55\" x2=\"95\" y2=\"55\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"60\" x2=\"95\" y2=\"60\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"65\" x2=\"95\" y2=\"65\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"70\" x2=\"95\" y2=\"70\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"75\" x2=\"95\" y2=\"75\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"80\" x2=\"95\" y2=\"80\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"85\" x2=\"95\" y2=\"85\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n"+
				"<line x1=\"5\" y1=\"90\" x2=\"95\" y2=\"90\" style=\"stroke:rgb(200,200,200);stroke-width:1.5\" />\n";
		String pointDrawing = "";
		for(Point point:points)
			pointDrawing+="<circle cx=\""+((point.x*5)+50)+"\" cy=\""+(50-(point.y*5))+"\" r=\"3\" stroke=\"black\" stroke-width=\"1\" fill=\"black\" />\n";
		return "<svg width=\"100\" height=\"100\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n"+grid+axes+pointDrawing+"</svg>";
	}

	private Question generateDecimalOperationQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		
		int type = RANDOM.nextInt(4);
		int a, b, c;
		int da, db, dc; // The number of decimal places in the various parts of the equation.
		String op;
		switch(type) {
		case 0: // +
			op = "+";
			a = RANDOM.nextInt(1000000);
			b = RANDOM.nextInt(1000000-a);
			da = RANDOM.nextInt(3)+1;
			db = RANDOM.nextInt(3)+1;
			dc = Math.max(da, db);
			while(da<dc) {
				a*=10;
				da++;
			}
			while(db<dc) {
				b*=10;
				db++;
			}
			c=a+b;
			break;
		case 1: // -
			op = "-";
			a = RANDOM.nextInt(1000000);
			b = RANDOM.nextInt(1000000);
			da = RANDOM.nextInt(3)+1;
			db = RANDOM.nextInt(3)+1;
			dc = Math.max(da, db);
			while(da<dc) {
				a*=10;
				da++;
			}
			while(db<dc) {
				b*=10;
				db++;
			}
			if(da>db) {
				int t = a;
				a=b;
				b=t;
			}
			c=a-b;
			break;
		case 2: // *
			op = "×";
			a = RANDOM.nextInt(1000);
			b = RANDOM.nextInt(100);
			c = a*b;
			da = RANDOM.nextInt(2)+1;
			db = RANDOM.nextInt(2);
			dc = da+db;
			break;
		default: // ÷
			op = "÷";
			b = RANDOM.nextInt(100)+1;
			c = RANDOM.nextInt(1000);
			a = b*c;
			da = RANDOM.nextInt(2)+1;
			db = RANDOM.nextInt(2);
			dc = da+db;
			break;
		}
		
		DecimalNumber dna = new DecimalNumber(a, da);
		DecimalNumber dnb = new DecimalNumber(b, db);
		DecimalNumber dnc = new DecimalNumber(c, dc);
		
		return new Question(dna.toString() +" "+ op +" "+ dnb.toString() + " = ?","text/plain","Solve the decimal problem.","text/plain",""+dnc.toString(),dimensions,4);
	}
	
	private Question generateFractionOperationQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		int type = RANDOM.nextInt(4);
		int na = RANDOM.nextInt(29)+2;
		int nb = RANDOM.nextInt(29)+2;
		int da = RANDOM.nextInt(14)+2;
		int db = RANDOM.nextInt(14)+2;
		String op;
		String answer;
		switch(type) {
		case 0: // +
			op = "+";
			answer = simplifiedFraction(na*db + nb*da,da*db);
			break;
		case 1: // -
			op = "-";
			answer = simplifiedFraction(na*db - nb*da,da*db);
			break;
		case 2: // *
			op = "×";
			answer = simplifiedFraction(na*nb,da*db);
			break;
		default: // ÷
			op = "÷";
			answer = simplifiedFraction(na*db,da*nb);
			break;
		}
		
		int constrainingSquareLength = 100;
		return new Question("<svg width=\""+(constrainingSquareLength+75)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
			"<text x=\""+(24-(""+na).length()*4)+"\" y=\"22\">"+na+"</text>\n" +
			"<text x=\""+(24-(""+da).length()*4)+"\" y=\"37\">"+da+"</text>\n" +
			"<text x=\"31\" y=\"32\" style=\"font-size:xx-large\">"+op+"</text>\n" +
			"<text x=\""+(50-(""+nb).length()*4)+"\" y=\"22\">"+nb+"</text>\n" +
			"<text x=\""+(50-(""+db).length()*4)+"\" y=\"37\">"+db+"</text>\n" +
			"<line x1=\"18\" y1=\"25\" x2=\"28\" y2=\"25\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n"+
			"<line x1=\"44\" y1=\"25\" x2=\"54\" y2=\"25\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n"+
			"</svg>","image/svg+xml","Solve the fraction problem and simplify.","text/plain",""+answer,dimensions,4);
	}
	
	private static String simplifiedFraction(int numerator, int denominator) {
		boolean negative = (numerator<0)^(denominator<0);
		numerator = Math.abs(numerator);
		denominator = Math.abs(denominator);
		int gcd = gcd(numerator,denominator);
		String retval = ""+(numerator/gcd)+"/"+(denominator/gcd);
		if(retval.endsWith("/1"))
			retval = retval.substring(0, retval.length()-2);
		if(negative)
			retval = "-"+retval;
		return retval;
	}
	
	/**
	 * 
	 * @param p
	 * @param q
	 * @return The greatest common denominator of p and q.
	 */
    private static int gcd(int p, int q) {
        if (q == 0)
        	return p;
    	return gcd(q, p % q);
    }
    
    private Question generateOrderOfOperationsQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
    	int[] numbers = new int[] {RANDOM.nextInt(10)+1,RANDOM.nextInt(10)+1,RANDOM.nextInt(10)+1,RANDOM.nextInt(10)+1};
    	String[] operators = new String[] {ARITHMETIC_OPERATORS[RANDOM.nextInt(4)],ARITHMETIC_OPERATORS[RANDOM.nextInt(4)],ARITHMETIC_OPERATORS[RANDOM.nextInt(4)]};
    	String[] openingBrackets = new String[] {"","","",""};
    	String[] closingBrackets = new String[] {"","","",""};
    	LinkedList<int[]> bracketIndicies = new LinkedList<int[]>();
    	int begin = 0;
    	int end = numbers.length-1;
    	while(end-begin >= 2) {
    		int a = RANDOM.nextInt(end-begin);
    		int b = RANDOM.nextInt(end-begin);
    		if(b<a) {
    			int t=b;
    			b=a;
    			a=t;
    		}
    		if(b-a==0)
    			break;
    		if((a==begin)&&(b==end))
    			break;
    		openingBrackets[a]+="(";
    		closingBrackets[b]+=")";
    		bracketIndicies.add(new int[] {a,b});
    		begin=a;
    		end=b;
    	}
    	Stack<Expression> expressionStack = new Stack<>();
    	int nextBracketIndex = 0;;
    	Expression expression = new Expression();
    	Expression flatExpression = new Expression();
    	for(int i=0;i<numbers.length;i++) {
    		if(i>0) {
    			expression.appendPart(operators[i-1]);
    			flatExpression.appendPart(operators[i-1]);
    		}
    		if(nextBracketIndex<bracketIndicies.size()) {
    			if(bracketIndicies.get(nextBracketIndex)[0]==i) {
    				expressionStack.push(expression);
    				expression = new Expression();
    				nextBracketIndex++;
    			}
    		}
    		expression.appendPart(numbers[i]);
    		flatExpression.appendPart(numbers[i]);
    		if(nextBracketIndex>0) {
    			if(bracketIndicies.get(nextBracketIndex-1)[1]==i) {
    				Expression x = expression;
    				expression = expressionStack.pop();
    				expression.appendPart(x);
    				nextBracketIndex--;
    			}
    		}
    	}
    	Number answer = expression.evaluate();
    	HashSet<Number> answerSet = new HashSet<Number>();
    	answerSet.add(answer);
    	answerSet.add(expression.evaluateWrong());
    	answerSet.add(flatExpression.evaluate());
    	answerSet.add(flatExpression.evaluateWrong());
    	LinkedList<Number> answerList = new LinkedList<>(answerSet);
    	Collections.<Number>sort(answerList,(Number a, Number b)->{
    		double d1=a.doubleValue();
    		double d2=b.doubleValue();
    		if(d1<d2)
    			return -1;
    		if(d1>d2)
    			return 1;
    		return 0;
    	});
    	ListIterator<Number> answerIterator = answerList.listIterator();
    	Number previous = answerIterator.next();
    	while(answerIterator.hasNext()) {
    		Number next = answerIterator.next();
    		double delta = next.doubleValue()-previous.doubleValue();
    		if(delta<0.1) {
    			if(next==answer) {
    				answerIterator.previous();
    				answerIterator.previous();
    				answerIterator.remove();
    				answerIterator.next();
    				previous = next;
    			}
    			else {
    				answerIterator.remove();
    			}
    		}
    		else {
    			previous = next;
    		}
    	}
    	while(answerList.size()<4) {
    		boolean prepend = RANDOM.nextBoolean();
    		double adjustment = RANDOM.nextInt(3)+(1/(RANDOM.nextInt(4)+1));
    		if(prepend) {
    			double d = answerList.getFirst().doubleValue()-adjustment;
    			if(!answerList.contains(d))
    				answerList.addFirst(d);
    		}
    		else {
    			double d = answerList.getFirst().doubleValue()+adjustment;
    			if(!answerList.contains(d))
    				answerList.addLast(d);
    		}
    	}
		ArrayList<Choice> choices = new ArrayList<>(4);
		for(Number n:answerList) {
			String s = cleanDoubleString(n.doubleValue());
			choices.add(new Choice("text/plain", s, s));
		}
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", "How does this expression evaluate?", choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		
		return new Question(expression.toString(),"text/plain",answerPrompt,"choice/radio",cleanDoubleString(answer.doubleValue()),dimensions,4);
    }
    
    private Question generateEquationsWithTwoVariablesQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		int x = RANDOM.nextInt(11)-5;
		int y = RANDOM.nextInt(11)-5;
    	int[] eq1 = new int[] {RANDOM.nextInt(9)+1,RANDOM.nextInt(9)+1};
    	int[] eq2 = new int[] {RANDOM.nextInt(9)+1,RANDOM.nextInt(9)+1};
    	for(int i=0;i<eq1.length;i++)
    		if(RANDOM.nextBoolean())
    			eq1[i]=-eq1[i];
    	for(int i=0;i<eq2.length;i++)
    		if(RANDOM.nextBoolean())
    			eq2[i]=-eq2[i];
    	if(simplifiedFraction(eq1[0], eq1[1])==simplifiedFraction(eq1[0], eq1[1]))
			eq2[0]=-eq2[0];
    	int c1 = eq1[0]*x+eq1[1]*y;
    	int c2 = eq2[0]*x+eq2[1]*y;
    	boolean solveForX = RANDOM.nextBoolean();
    	String answer = ""+(solveForX?x:y);
    	char[] variables = new char[] {'x','y'};
    	String eqString1 = stringifyMultivariateEquation(eq1,c1,variables);
    	String eqString2 = stringifyMultivariateEquation(eq2,c2,variables);
    	
		return new Question(eqString1+"\n"+eqString2,"text/plain","What is the value of "+(solveForX?"x":"y")+"?","text/plain",answer,dimensions,4);
    }
    
    private String stringifyMultivariateEquation(int[] coefficients, int constantSum, char[] variables) {
    	if(variables.length<coefficients.length)
    		throw new IllegalArgumentException("Not enough variables identified for the coefficients.");
    	String retval = "";
    	for(int i=0;i<coefficients.length;i++) {
    		if(coefficients[i]==0)
    			continue;
    		if(retval.length()==0) {
    			retval += ""+coefficients[i]+""+variables[i];
    			continue;
    		}
    		retval += (coefficients[i]<0)?" - ":" + ";
			retval += ""+Math.abs(coefficients[i])+""+variables[i];
    	}
    	retval += " = "+constantSum;
    	return retval;
    }
    
    private String cleanDoubleString(double d) {
    	String s = String.format("%.8f", d);
    	return s.contains(".") ? s.replaceAll("0*$","").replaceAll("\\.$","") : s;
    }
	
	private Question generatePrismSurfaceQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		int w = RANDOM.nextInt(40)+10;
		int h = RANDOM.nextInt(25)+4;
		int d = RANDOM.nextInt(25)+4;
		int answer = h*w*2+h*d*2+w*d*2;
		int unit = RANDOM.nextInt(LENGTH_UNITS.length);

		int constrainingSquareLength = 115;
		return new Question("<svg width=\""+(constrainingSquareLength+75)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
			"<line x1=\"5\" y1=\"20\" x2=\"70\" y2=\"20\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"5\" y1=\"50\" x2=\"70\" y2=\"50\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"20\" y1=\"5\" x2=\"85\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"5\" y1=\"20\" x2=\"5\" y2=\"50\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"70\" y1=\"20\" x2=\"70\" y2=\"50\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"85\" y1=\"5\" x2=\"85\" y2=\"35\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"5\" y1=\"20\" x2=\"20\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"70\" y1=\"20\" x2=\"85\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<line x1=\"70\" y1=\"50\" x2=\"85\" y2=\"35\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
			"<text x=\"35\" y=\"70\">"+w+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
			"<text x=\"87\" y=\"30\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
			"<text x=\"75\" y=\"60\">"+d+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
			"</svg>","image/svg+xml","What is the surface area of this rectangular prism?","text/plain",""+answer,dimensions,4);
	}
	
	private Question generateVolumeQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		
		boolean composite = RANDOM.nextBoolean();
		int unit = RANDOM.nextInt(LENGTH_UNITS.length);
		int constrainingSquareLength = 215;
		if(!composite) {
			boolean cube = RANDOM.nextBoolean();
			int w,h,d;
			if(cube) {
				w = RANDOM.nextInt(25)+4;
				h = w;
				d = w;
			}
			else {
				w = RANDOM.nextInt(25)+4;
				h = RANDOM.nextInt(25)+4;
				d = RANDOM.nextInt(25)+4;
			}
			int answer = h*w*d;
			if(cube) {
				String widthText = w+" "+LENGTH_UNITS_ABBR[unit];
				return new Question("<svg width=\""+constrainingSquareLength+"\" height=\""+constrainingSquareLength+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
						"<line x1=\"5\" y1=\"20\" x2=\"70\" y2=\"20\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"5\" y1=\"85\" x2=\"70\" y2=\"85\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"20\" y1=\"5\" x2=\"85\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"5\" y1=\"20\" x2=\"5\" y2=\"85\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"70\" y1=\"20\" x2=\"70\" y2=\"85\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"85\" y1=\"5\" x2=\"85\" y2=\"70\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"5\" y1=\"20\" x2=\"20\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"70\" y1=\"20\" x2=\"85\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<line x1=\"70\" y1=\"85\" x2=\"85\" y2=\"70\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
						"<text x=\""+(35-4*widthText.length())+"\" y=\"84\">"+widthText+"</text>\n" +
						"</svg>","image/svg+xml","What is the volume are of this cube in "+VOLUME_UNITS[unit]+"?","text/plain",""+answer,dimensions,4);
			}
			return new Question("<svg width=\""+constrainingSquareLength+"\" height=\""+constrainingSquareLength+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
					"<line x1=\"5\" y1=\"20\" x2=\"70\" y2=\"20\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"5\" y1=\"50\" x2=\"70\" y2=\"50\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"20\" y1=\"5\" x2=\"85\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"5\" y1=\"20\" x2=\"5\" y2=\"50\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"70\" y1=\"20\" x2=\"70\" y2=\"50\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"85\" y1=\"5\" x2=\"85\" y2=\"35\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"5\" y1=\"20\" x2=\"20\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"70\" y1=\"20\" x2=\"85\" y2=\"5\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"70\" y1=\"50\" x2=\"85\" y2=\"35\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<text x=\"35\" y=\"70\">"+w+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
					"<text x=\"87\" y=\"30\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
					"<text x=\"75\" y=\"60\">"+d+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
					"</svg>","image/svg+xml","What is the volume of this rectangular prism in "+AREA_UNITS[unit]+"?","text/plain",""+answer,dimensions,4);
		}
		
		// Composite Shapes
		int w[] = {RANDOM.nextInt(10)+10,RANDOM.nextInt(10)+10,RANDOM.nextInt(10)+10};
		int h[] = {RANDOM.nextInt(10)+10,RANDOM.nextInt(10)+10};
		int d[] = {RANDOM.nextInt(10)+10,RANDOM.nextInt(10)+10};
		int answer = w[0]*h[0]*d[0];
		int pieces = RANDOM.nextInt(4)+2;
		String shape = "";
		shape+=getLineCubeSVG(0,0,0,""+w[0]+" "+LENGTH_UNITS_ABBR[unit],(pieces==1)?(""+h[0]+" "+LENGTH_UNITS_ABBR[unit]):null,(pieces==1)?(""+d[0]+" "+LENGTH_UNITS_ABBR[unit]):null);
		pieces--;
		if(pieces>0) {
			shape += getLineCubeSVG(1,0,0,""+w[1]+" "+LENGTH_UNITS_ABBR[unit],(pieces==1)?(""+h[0]+" "+LENGTH_UNITS_ABBR[unit]):null,(pieces==1)?(""+d[0]+" "+LENGTH_UNITS_ABBR[unit]):null);
			answer += w[1]*h[0]*d[0];
			pieces--;
		}
		if(pieces>0) {
			shape+=getLineCubeSVG(2,0,0,""+w[2]+" "+LENGTH_UNITS_ABBR[unit],""+h[0]+" "+LENGTH_UNITS_ABBR[unit],""+d[0]+" "+LENGTH_UNITS_ABBR[unit]);
			answer += w[2]*h[0]*d[0];
			pieces--;
		}
		if(pieces>0) {
			int xLocation = RANDOM.nextInt(3);
			shape+=getLineCubeSVG(xLocation,1,0,""+w[xLocation]+" "+LENGTH_UNITS_ABBR[unit],""+h[1]+" "+LENGTH_UNITS_ABBR[unit],null);
			answer += w[xLocation]*h[1]*d[0];
			pieces--;
		}
		if(pieces>0) {
			int xLocation = RANDOM.nextInt(3);
			shape+=getLineCubeSVG(xLocation,0,1,null,null,""+d[1]+" "+LENGTH_UNITS_ABBR[unit]);
			answer += w[xLocation]*h[0]*d[1];
			pieces--;
		}
		
		return new Question("<svg width=\""+(constrainingSquareLength+75)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" +
				shape +
				"</svg>","image/svg+xml","What is the volume of this composite shape in "+VOLUME_UNITS[unit]+"?","text/plain",""+answer,dimensions,4);
	}
	
	private String getLineCubeSVG(int x, int y, int z, String w, String h, String d) {
		int cubeWidth=40;
		// Location of rear-upper-left corner of cube.
		int x1 = x*cubeWidth+40-(cubeWidth/2)*z;
		int y1 = 40-y*cubeWidth+(cubeWidth/2)*z;
		String retval = ""+
				"<polygon points=\""+
				x1+","+y1+" "+
				(x1+cubeWidth)+","+y1+" "+
				(x1+cubeWidth/2)+","+(y1+cubeWidth/2)+" "+
				(x1-cubeWidth/2)+","+(y1+cubeWidth/2)+
				"\" style=\"fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)\" />"+
				"<polygon points=\""+
				(x1+cubeWidth)+","+y1+" "+
				(x1+cubeWidth)+","+(y1+cubeWidth)+" "+
				(x1+cubeWidth/2)+","+(y1+cubeWidth*3/2)+" "+
				(x1+cubeWidth/2)+","+(y1+cubeWidth/2)+
				"\" style=\"fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)\" />"+
				"<rect x=\""+(x1-cubeWidth/2)+"\" y=\""+(y1+cubeWidth/2)+"\" width=\""+cubeWidth+"\" height=\""+cubeWidth+"\" style=\"fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)\" />\n";
		if(h!=null) {
			retval+="<text x=\""+(x1+cubeWidth+3)+"\" y=\""+(y1+cubeWidth/2)+"\">"+h+"</text>\n";
		}
		if(w!=null) {
			retval+="<text x=\""+(x1)+"\" y=\""+(y1+cubeWidth/2-2)+"\">"+w+"</text>\n";
		}
		if(d!=null) {
			retval+="<text x=\""+(x1+cubeWidth/2+3)+"\" y=\""+(y1+cubeWidth*3/2+5)+"\">"+d+"</text>\n";
		}
		return retval;
	}
	
	private Question generateAreaQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		
		int type = RANDOM.nextInt(4);
		int constrainingSquareLength = 100;
		switch(type) {
		case 0: { // rectangle
			boolean square = RANDOM.nextBoolean();
			int w = RANDOM.nextInt(1000)+1;
			int h = square?w:RANDOM.nextInt(1000)+1;
			int max = Math.max(w, h);
			int area = w*h;
			int unit = RANDOM.nextInt(LENGTH_UNITS.length);

			int drawingWidth = Math.max(constrainingSquareLength*w/max,15);
			int drawingHeight = Math.max(constrainingSquareLength*h/max,15);
			
			return new Question("<svg width=\""+(constrainingSquareLength+75)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
				"<rect x=\"25\" y=\"25\" width=\""+drawingWidth+"\" height=\""+drawingHeight+"\" style=\"stroke-width:3;stroke:rgb(0,0,0)\" />\n" + 
				"<text x=\""+(drawingWidth/2+15)+"\" y=\""+(drawingHeight+40)+"\">"+w+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
				"<text x=\""+(drawingWidth+40)+"\" y=\""+(drawingHeight/2+25)+"\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
				"</svg>","image/svg+xml","What is the area of this "+(square?"square":"rectangle")+" in "+AREA_UNITS[unit]+".","text/plain",""+area,dimensions,4);
		}
		case 1: { // parallelogram
			int w = RANDOM.nextInt(1000)+1;
			int h = RANDOM.nextInt(1000)+1;
			int max = Math.max((int)(w*1.3), h);
			int area = w*h;
			int unit = RANDOM.nextInt(LENGTH_UNITS.length);

			int drawingWidth = Math.max((int)(constrainingSquareLength*w*1.3/max),15);
			int drawingHeight = Math.max(constrainingSquareLength*h/max,15);
			
			String shape="";
			shape+="<line x1=\"25\" y1=\"25\" x2=\""+(25+drawingWidth*0.75)+"\" y2=\"25\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\""+(25+drawingWidth*0.75)+"\" y1=\"25\" x2=\""+(25+drawingWidth*0.75+constrainingSquareLength*0.25)+"\" y2=\""+(25+drawingHeight)+"\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\""+(25+drawingWidth*0.75+constrainingSquareLength*0.25)+"\" y1=\""+(25+drawingHeight)+"\" x2=\""+(25+constrainingSquareLength*0.25)+"\" y2=\""+(25+drawingHeight)+"\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\""+(25+constrainingSquareLength*0.25)+"\" y1=\""+(25+drawingHeight)+"\" x2=\"25\" y2=\"25\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\""+(30+drawingWidth*0.75+constrainingSquareLength*0.25)+"\" y1=\"25\" x2=\""+(30+drawingWidth*0.75+constrainingSquareLength*0.25)+"\" y2=\""+(25+drawingHeight)+"\" style=\"stroke:rgb(128,128,128);stroke-width:2\" />\n";
			shape+="<line x1=\""+(25+drawingWidth*0.75+constrainingSquareLength*0.25)+"\" y1=\""+(30+drawingHeight)+"\" x2=\""+(25+constrainingSquareLength*0.25)+"\" y2=\""+(30+drawingHeight)+"\" style=\"stroke:rgb(128,128,128);stroke-width:2\" />\n";
			
			return new Question("<svg width=\""+(constrainingSquareLength+100)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
				shape + 
				"<text x=\""+(drawingWidth-drawingWidth*0.75/2+15)+"\" y=\""+(drawingHeight+40)+"\">"+w+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
				"<text x=\""+(drawingWidth+40)+"\" y=\""+(drawingHeight/2+25)+"\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
				"</svg>","image/svg+xml","What is the area of this parallelogram in "+AREA_UNITS[unit]+".","text/plain",""+area,dimensions,4);
		}
		case 2: { // trapezoid
			int wBase = RANDOM.nextInt(1000)+10;
			int wTop = RANDOM.nextInt(wBase-6)+1;
			int wLeft = RANDOM.nextInt(wBase-wTop);
			int h = RANDOM.nextInt(1000)+1;
			if((wBase+wTop)%2==1)
				wTop++;
			
			int max = Math.max(wBase, h);
			int area = (wBase+wTop)*h/2;
			int unit = RANDOM.nextInt(LENGTH_UNITS.length);

			int drawingWidth = Math.max(constrainingSquareLength*wBase/max,15);
			int drawingHeight = Math.max(constrainingSquareLength*h/max,15);
			
			String shape="";
			shape+="<line x1=\""+(25+wLeft*drawingWidth/wBase)+"\" y1=\"30\" x2=\""+(25+(wLeft+wTop)*drawingWidth/wBase)+"\" y2=\"30\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\""+(25+(wLeft+wTop)*drawingWidth/wBase)+"\" y1=\"30\" x2=\""+(25+drawingWidth)+"\" y2=\""+(30+drawingHeight)+"\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\""+(25+drawingWidth)+"\" y1=\""+(30+drawingHeight)+"\" x2=\"25\" y2=\""+(30+drawingHeight)+"\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\"25\" y1=\""+(30+drawingHeight)+"\" x2=\""+(25+wLeft*drawingWidth/wBase)+"\" y2=\"30\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
			shape+="<line x1=\""+(30+drawingWidth)+"\" y1=\"30\" x2=\""+(30+drawingWidth)+"\" y2=\""+(30+drawingHeight)+"\" style=\"stroke:rgb(128,128,128);stroke-width:2\" />\n";
			shape+="<text x=\""+(drawingWidth+40)+"\" y=\""+(drawingHeight/2+30)+"\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
			shape+="<text x=\""+(25+(wLeft+wTop/2)*drawingWidth/wBase)+"\" y=\""+25+"\">"+wTop+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
			if(RANDOM.nextBoolean()) { // Base by parts?
				if(wLeft>0) {
					shape+="<line x1=\"25\" y1=\""+(35+drawingHeight)+"\" x2=\""+(25+wLeft*drawingWidth/wBase)+"\" y2=\""+(35+drawingHeight)+"\" style=\"stroke:rgb(128,128,128);stroke-width:2\" />\n";
					shape+="<text x=\"15\" y=\""+(drawingHeight+45)+"\">"+wLeft+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
				}
				int wRight=wBase-wLeft-wTop;
				if(wRight>0) {
					shape+="<line x1=\""+(25+drawingWidth-wRight*drawingWidth/wBase)+"\" y1=\""+(35+drawingHeight)+"\" x2=\""+(25+drawingWidth)+"\" y2=\""+(35+drawingHeight)+"\" style=\"stroke:rgb(128,128,128);stroke-width:2\" />\n";
					shape+="<text x=\""+(25+drawingWidth-5)+"\" y=\""+(drawingHeight+45)+"\">"+wRight+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
				}
			}
			else {
				shape+="<text x=\""+(drawingWidth-drawingWidth*0.75/2+15)+"\" y=\""+(drawingHeight+45)+"\">"+wBase+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
			}
			
			return new Question("<svg width=\""+(constrainingSquareLength+100)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
				shape + 
				"</svg>","image/svg+xml","What is the area of this trapezoid in "+AREA_UNITS[unit]+".","text/plain",""+area,dimensions,4);
		}
		default: { // triangle
			int w = RANDOM.nextInt(1000)+1;
			int h = RANDOM.nextInt(1000)+1;
			int area = w*h/2;
			int unit = RANDOM.nextInt(LENGTH_UNITS.length);
			
			int triangleType = RANDOM.nextInt(3);
			String shape="";
			shape+="<text x=\"135\" y=\"75\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
			shape+="<line x1=\"130\" y1=\"25\" x2=\"130\" y2=\"125\" style=\"stroke:rgb(128,128,128);stroke-width:2\" />\n";
			switch(triangleType) {
			case 0: // Right
				shape+="<line x1=\"125\" y1=\"25\" x2=\"125\" y2=\"125\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<line x1=\"125\" y1=\"125\" x2=\"25\" y2=\"125\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<line x1=\"25\" y1=\"125\" x2=\"125\" y2=\"25\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<text x=\"70\" y=\"155\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
				break;
			case 1: // Point over base
				shape+="<line x1=\"90\" y1=\"25\" x2=\"125\" y2=\"125\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<line x1=\"125\" y1=\"125\" x2=\"25\" y2=\"125\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<line x1=\"25\" y1=\"125\" x2=\"90\" y2=\"25\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<text x=\"70\" y=\"155\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
				break;
			default: // Point off to the side of the base
				shape+="<line x1=\"125\" y1=\"25\" x2=\"90\" y2=\"125\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<line x1=\"90\" y1=\"125\" x2=\"25\" y2=\"125\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<line x1=\"25\" y1=\"125\" x2=\"125\" y2=\"25\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				shape+="<line x1=\"25\" y1=\"130\" x2=\"90\" y2=\"130\" style=\"stroke:rgb(128,128,128);stroke-width:2\" />\n";
				shape+="<text x=\"55\" y=\"160\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
				break;
			}
			return new Question("<svg width=\""+(constrainingSquareLength+100)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
					shape + 
					"</svg>","image/svg+xml","What is the area of this triangle in "+AREA_UNITS[unit]+".","text/plain",""+area,dimensions,4);
		}
		}
	}
	
	private Question generatePrimeNumberQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		
		boolean manyChoice = RANDOM.nextBoolean();
		
		if(manyChoice) {
			int primePosition = RANDOM.nextInt(4);
			
			ArrayList<Choice> choices = new ArrayList<>(4);
			HashSet<Integer> generatedComposites = new HashSet<>();
			for(int i=0;i<4;i++) {
				if(i==primePosition) {
					choices.add(new Choice("text/plain", ""+PRIME_NUMBERS[RANDOM.nextInt(PRIME_NUMBERS.length)], ""+i));
					continue;
				}
				int composite = getCompositeNumber();
				while(generatedComposites.contains(composite))
					composite = getCompositeNumber();
				generatedComposites.add(composite);
				choices.add(new Choice("text/plain", ""+composite, ""+i));
			}
			MultipleChoice multipleChoice = new MultipleChoice("text/plain", "Which of these numbers is prime.", choices);
			String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
			
			return new Question("Here is a list of numbers.","text/plain",answerPrompt,"choice/radio",""+primePosition,dimensions,4);
		}
		
		boolean prime = RANDOM.nextBoolean();
		int number = prime?PRIME_NUMBERS[RANDOM.nextInt(PRIME_NUMBERS.length)]:getCompositeNumber();
		boolean isPrimeQuestion = RANDOM.nextBoolean();
		String answer = (isPrimeQuestion^prime)?"n":"y";
		
		ArrayList<Choice> choices = new ArrayList<>(4);
		choices.add(new Choice("text/plain", "Yes", "y"));
		choices.add(new Choice("text/plain", "No", "n"));
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", ""+number, choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		return new Question(isPrimeQuestion?"Is this number prime?":"Is this number composite?","text/plain",answerPrompt,"choice/radio",answer,dimensions,4);
	}
	
	private Question generateCommonDenominatorOrMultipleQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		
		boolean isDenominatorQuestion = RANDOM.nextBoolean();
		
		if(isDenominatorQuestion) {
			int gcd = RANDOM.nextInt(15)+1;
			int n = RANDOM.nextInt(13);
			int a = gcd*PRIME_NUMBERS[n];
			int b = gcd*PRIME_NUMBERS[(n+RANDOM.nextInt(12)+1)%13];
			return new Question("","text/plain","What is the greatest common denominator of "+a+" and "+b+"?","text/plain",""+gcd,dimensions,4);
		}
		
        int[] primePool	= new int[6];
        System.arraycopy(PRIME_NUMBERS, 0, primePool, 0, 6);
        int n = RANDOM.nextInt(primePool.length);
		int gcd = primePool[n];
		primePool[n] = primePool[5];
        n = RANDOM.nextInt(primePool.length-1);
		int p1 = primePool[n];
		primePool[n] = primePool[4];
        n = RANDOM.nextInt(primePool.length-2);
		int p2 = primePool[n];
		int a = gcd*p1;
		int b = gcd*p2;
		int lcm = gcd*p1*p2;
		return new Question("","text/plain","What is the least common multiple of "+a+" and "+b+"?","text/plain",""+lcm,dimensions,4);
	}
	
	private int getCompositeNumber() {
		int retval = 1;
		int parts = RANDOM.nextInt(3)+2;
		for(int i=0;(i<parts)&&(retval<125||i<2);i++) {
			retval*=PRIME_NUMBERS[RANDOM.nextInt(13)];
		}
		return retval;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param type 0=>+, 1=>-, 2=>*, 3=>/
	 * @param unknown If 0, then a should be the unknown, 1=>b, 2=>c
	 * @param isStoryQuestion
	 * @param translateQuestion If true then the answer will be either the number sentence that matches the story, or the story that matches the number sentence.
	 * @return
	 */
	private Question generateArithmeticQuestion(int a, int b, int c, int type, int unknown, boolean isStoryQuestion, boolean translateQuestion){
		String prompt;
		String promptType = "text/plain";
		String answerPrompt;
		String answerPromptType;
		String answerValue;
		
		String unknownValue = ""+((unknown==0)?a:(unknown==1)?b:c);
		
		QuestionWithPrompt numberSentenceQuestion = getArithmeticNumberSentence(a, b, c, type, unknown);
		QuestionWithPrompt wordProblemQuestion = getArithmeticWordProblem(a, b, c, type, unknown);
		if(isStoryQuestion)
			prompt = wordProblemQuestion.getProblemSetup();
		else
			prompt = numberSentenceQuestion.getProblemSetup();
		if(translateQuestion) {
			answerPromptType = "choice/radio";
			ArrayList<Choice> choices = new ArrayList<>(4);
			ArrayList<QuestionWithPrompt> badChoices = new ArrayList<>(3);
			int correctAnswerPosition = RANDOM.nextInt(4);
			answerValue = ""+correctAnswerPosition;
			String correctAnswerContent;
			QuestionWithPrompt badChoice;
			if(isStoryQuestion) {
				answerPrompt = "Which of the following number sentences can be used to answer this question: "+wordProblemQuestion.getPrompt();
				correctAnswerContent = numberSentenceQuestion.getProblemSetup();
				if(a!=b) {
					if((type&1)==1)
						badChoice = getArithmeticNumberSentence(b, a, c, type, (unknown==2)?unknown:unknown^1);
					else
						badChoice = getArithmeticNumberSentence(b+1, a, c, type^1, (unknown==2)?unknown:unknown^1);
				}
				else {
					badChoice = getArithmeticNumberSentence(a+1, b, c, type, unknown);
				}
				badChoices.add(badChoice);
				
				badChoice = getArithmeticNumberSentence(a, b, c, type^1, unknown);
				badChoices.add(badChoice);

				badChoice = getArithmeticNumberSentence(b, a, c, type^1, (unknown==2)?unknown:unknown^1);
				badChoices.add(badChoice);
			}
			else {
				answerPrompt = "Which of the following story problems can be solved using this number sentence?";
				correctAnswerContent = wordProblemQuestion.getProblemSetup()+" "+wordProblemQuestion.getPrompt();
				if(a!=b) {
					if((type&1)==1)
						badChoice = getArithmeticWordProblem(b, a, c, type, (unknown==2)?unknown:unknown^1);
					else
						badChoice = getArithmeticWordProblem(b+1, a, c, type^1, (unknown==2)?unknown:unknown^1);
				}
				else {
					badChoice = getArithmeticWordProblem(a+1, b, c, type, unknown);
				}
				badChoices.add(badChoice);
				
				badChoice = getArithmeticWordProblem(a, b, c, type^1, unknown);
				badChoices.add(badChoice);

				badChoice = getArithmeticWordProblem(b, a, c, type^1, (unknown==2)?unknown:unknown^1);
				badChoices.add(badChoice);
			}
			
			for(int i=0;i<4;i++) {
				if(i==correctAnswerPosition) {
					choices.add(new Choice("text/plain", correctAnswerContent, answerValue));
					continue;
				}
				badChoice = badChoices.remove(RANDOM.nextInt(badChoices.size()));
				if(isStoryQuestion)
					choices.add(new Choice("text/plain", badChoice.getProblemSetup(), ""+i));
				else
					choices.add(new Choice("text/plain", badChoice.getProblemSetup()+" "+badChoice.getPrompt(), ""+i));
			}
			MultipleChoice multipleChoice = new MultipleChoice("text/plain", answerPrompt, choices);
			answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		}
		else {
			answerPromptType = "text/plain";
			if(isStoryQuestion)
				answerPrompt = wordProblemQuestion.getPrompt();
			else
				answerPrompt = numberSentenceQuestion.getPrompt();
			answerValue = unknownValue;
		}
		return new Question(prompt,promptType,answerPrompt,answerPromptType,answerValue,new HashMap<String, Float>(0),4);
	}
	
	private QuestionWithPrompt getArithmeticNumberSentence(int a, int b, int c, int type, int unknown) {
		String[] parts = new String[]{""+a,ARITHMETIC_OPERATORS[type],""+b,"=",""+c};
		parts[unknown*2] = "?";
		String numberSentence = "";
		for(String part:parts) {
			if(numberSentence.length()>0)
				numberSentence+=" ";
			numberSentence+=part;
		}
		return new QuestionWithPrompt(numberSentence, (unknown==2)?"What is the answer?":"What is the missing value?");
	}
	
	private QuestionWithPrompt getArithmeticWordProblem(int a, int b, int c, int type, int unknown) {
		String wordProblem;
		String answerPrompt;
		String name = getRandomName();
		StandardNoun object = getRandomCollectible();
		switch(type){
		case 0:// Addition
			switch(unknown){
			case 0:
				wordProblem = name+" came from a family that was obsessed with "+object.get(Case.ACCUSATIVE, false)+" and had a personal collection of "+object.get(Case.ACCUSATIVE, false)+". "+name+" inherited "+b+" "+object.get(Case.ACCUSATIVE, b==1)+" and now has "+c+" "+object.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" did "+name+" have to begin with?";
				break;
			case 1:
				wordProblem = name+" came from a family that was obsessed with "+object.get(Case.ACCUSATIVE, false)+" and had "+((a>10)?"amassed":"")+" a personal collection of "+a+" "+object.get(Case.ACCUSATIVE, a==1)+". "+name+" inherited the "+object.get(Case.ACCUSATIVE, false)+" of a relative and now has "+c+" "+object.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" did "+name+" inherit?";
				break;
			default:
				wordProblem = name+" came from a family that was obsessed with "+object.get(Case.ACCUSATIVE, false)+" and had "+((a>10)?"amassed":"")+" a personal collection of "+a+" "+object.get(Case.ACCUSATIVE, a==1)+". "+name+" inherited "+b+" "+object.get(Case.ACCUSATIVE, b==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" does "+name+" have now?";
				break;
			}
			break;
		case 1:// Subtraction
			switch(unknown){
			case 0:
				wordProblem = name+" had a "+object.get(Case.ACCUSATIVE, true)+" collection, but the obsession with collecting "+object.get(Case.ACCUSATIVE, false)+" led to the ruin of all that "+name+" possessed. House, wealth, wife, and children were all lost. This of course left "+name+" with the problem of not having a place to store the collection, so "+name+" decided to sell some of the collection to rent a storage facility to live in. "+name+" sold "+b+" "+object.get(Case.ACCUSATIVE, b==1)+" and was left with "+c+" "+object.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" did "+name+" have before selling some?";
				break;
			case 1:
				wordProblem = name+" had a "+object.get(Case.ACCUSATIVE, true)+" collection with "+a+" "+object.get(Case.ACCUSATIVE, a==1)+", but the obsession with collecting "+object.get(Case.ACCUSATIVE, false)+" led to the ruin of all that "+name+" possessed. House, wealth, wife, and children were all lost. This of course left "+name+" with the problem of not having a place to store the collection, so "+name+" decided to sell some of the collection to rent a storage facility to live in. After selling some of the "+object.get(Case.ACCUSATIVE, false)+" "+name+" was left with "+c+" "+object.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" did "+name+" have to sell?";
				break;
			default:
				wordProblem = name+" had a "+object.get(Case.ACCUSATIVE, true)+" collection with "+a+" "+object.get(Case.ACCUSATIVE, a==1)+", but the obsession with collecting "+object.get(Case.ACCUSATIVE, false)+" led to the ruin of all that "+name+" possessed. House, wealth, wife, and children were all lost. This of course left "+name+" with the problem of not having a place to store the collection, so "+name+" decided to sell some of the collection to rent a storage facility to live in. "+name+" sold "+b+" "+object.get(Case.ACCUSATIVE, b==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" does "+name+" have now?";
				break;
			}
			break;
		case 2:// Multiplication
			StandardNoun object2 = getRandomCollectible();
			while(object.get(Case.NOMINATIVE, true).equals(object2.get(Case.NOMINATIVE, true)))
				object2 = getRandomCollectible();
			switch(unknown){
			case 0:
				wordProblem = name+" owned some "+object.get(Case.ACCUSATIVE, false)+" and found a deal whereby each "+object.get(Case.ACCUSATIVE, true)+" could be exchanged for "+b+" "+object2.get(Case.ACCUSATIVE, b==1)+". "+name+" traded in some "+object.get(Case.ACCUSATIVE, false)+", netting "+name+" a stock of "+c+" "+object2.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" did "+name+" trade?";
				break;
			case 1:
				wordProblem = name+" owned "+a+" "+object.get(Case.ACCUSATIVE, a==1)+" and found a deal whereby each "+object.get(Case.ACCUSATIVE, true)+" could be exchanged for some "+object2.get(Case.ACCUSATIVE, false)+". "+name+" traded in some "+object.get(Case.ACCUSATIVE, false)+", netting "+name+" a stock of "+c+" "+object2.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many "+object2.get(Case.ACCUSATIVE, false)+" did "+name+" receive for each "+object.get(Case.ACCUSATIVE, true)+" traded?";
				break;
			default:
				wordProblem = name+" owned "+a+" "+object.get(Case.ACCUSATIVE, a==1)+" and found a deal whereby each "+object.get(Case.ACCUSATIVE, true)+" could be exchanged for "+b+" "+object2.get(Case.ACCUSATIVE, b==1)+". "+name+" found the deal irresistable, so the entire stock of "+object.get(Case.ACCUSATIVE, false)+" was traded in.";
				answerPrompt = "How many "+object2.get(Case.ACCUSATIVE, false)+" did "+name+" have after trading in all the "+object.get(Case.ACCUSATIVE, false)+"?";
				break;
			}
			break;
		default:// Division
			switch(unknown){
			case 0:
				wordProblem = name+" had "+b+" "+((b==1)?"child":"children")+" and a very valuable collection consisting of "+object.get(Case.ACCUSATIVE, false)+". "+name+" was also dead. The executor of the estate found that the will, written very carefully, many year ago stipulated that the "+object.get(Case.ACCUSATIVE, true)+" collection was to be divided up evenly between all of the children. Each child will receive "+c+" "+object.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" did "+name+" have?";
				break;
			case 1:
				wordProblem = name+" had children and a very valuable collection consisting of "+a+" "+object.get(Case.ACCUSATIVE, a==1)+". "+name+" was also dead. The executor of the estate found that the will, written very carefully, many year ago stipulated that the "+object.get(Case.ACCUSATIVE, true)+" collection was to be divided up evenly between all of the children. Each child will receive "+c+" "+object.get(Case.ACCUSATIVE, c==1)+".";
				answerPrompt = "How many children did "+name+" have?";
				break;
			default:
				wordProblem = name+" had "+b+" "+((b==1)?"child":"children")+" and a very valuable collection consisting of "+a+" "+object.get(Case.ACCUSATIVE, a==1)+". "+name+" was also dead. The executor of the estate found that the will, written very carefully, many year ago stipulated that the "+object.get(Case.ACCUSATIVE, true)+" collection was to be divided up evenly between all of the children, with any remaining items to be used as a prize for a boxing tournament that the children were requested to compete in as the main event for the funeral.";
				answerPrompt = "How many "+object.get(Case.ACCUSATIVE, false)+" does each child get before the funeral?";
				break;
			}
			break;
		}
		return new QuestionWithPrompt(wordProblem, answerPrompt);
	}
	
	private static final String[] STANDARD_NOUN_COLLECTIBLES = new String[] {
		"baseball card",
		"pokemon card",
		"postcard",
		"stamp",
		"plate",
		"knife",
		"thimble",
		"spoon",
		"Beanie Baby",
		"rubber band",
		"shoe",
		"hat",
		"bug",
		"butterfly",
		"rock",
	};
	
	private static final String[] NAMES = new String[]{
			"Adison",
			"Al",
			"Albert",
			"Alice",
			"Alison",
			"Amber",
			"Andrew",
			"Andy",
			"Art",
			"Arthur",
			"Bartolo",
			"Ben",
			"Benjamin",
			"Bob",
			"Bulleta",
			"Carlos",
			"Carlton",
			"Casey",
			"Cassey",
			"Chelsea",
			"Claire",
			"Clairance",
			"Cora",
			"Crystal",
			"Dallin",
			"David",
			"Ed",
			"Edison",
			"Elizabeth",
			"Elspeth",
			"Elthon",
			"Enoch",
			"Ephraim",
			"Fred",
			"Freddy",
			"Fredrick",
			"Gavin",
			"George",
			"Gladys",
			"Jared",
			"Jane",
			"Jasher",
			"Jean",
			"Jill",
			"John",
			"Justin",
			"Kelly",
			"Kevin",
			"Kim",
			"Larry",
			"Lillian",
			"Math",
			"Matt",
			"Matthew",
			"Nishelle",
			"Michael",
			"Nathan",
			"Nicholas",
			"Oscar",
			"Pam",
			"Peter",
			"Ray",
			"Raymond",
			"Richard",
			"Robert",
			"Ron",
			"Ronald",
			"Ronaldo",
			"Ronni",
			"Scott",
			"Sean",
			"Shia",
			"Solace",
			"Stephen",
			"Steve",
			"Steven",
			"Susan",
			"Thomas",
			"Tom",
			"Ursula",
			"Victor",
			"Victoria",
			"William",
			"Wilson",
			"Yuri",
	};
	
	private String getRandomName(){
		return NAMES[RANDOM.nextInt(NAMES.length)];
	}
	
	private StandardNoun getRandomCollectible(){
		return new StandardNoun(STANDARD_NOUN_COLLECTIBLES[RANDOM.nextInt(STANDARD_NOUN_COLLECTIBLES.length)]);
	}
	
	private class QuestionWithPrompt{
		private String problemSetup;
		private String prompt;
		
		/**
		 * 
		 * @param problemSetup
		 * @param prompt
		 */
		public QuestionWithPrompt(String problemSetup, String prompt) {
			super();
			this.problemSetup = problemSetup;
			this.prompt = prompt;
		}
		
		/**
		 * @return the question
		 */
		public String getProblemSetup() {
			return problemSetup;
		}
		/**
		 * @return the prompt
		 */
		public String getPrompt() {
			return prompt;
		}
	}
	
	private class DecimalNumber{
		int n;
		int decimalPlaces;
		
		/**
		 * 
		 * @param n
		 * @param decimalPlaces
		 */
		public DecimalNumber(int n, int decimalPlaces) {
			super();
			this.n = n;
			this.decimalPlaces = decimalPlaces;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String s = ""+n;
			if(s.length()<=decimalPlaces) {
				while(s.length()<decimalPlaces) {
					s="0"+s;
				}
				s="0."+s;
			}
			else if(decimalPlaces>0){
				s = s.substring(0, s.length()-decimalPlaces)+"."+s.substring(s.length()-decimalPlaces);
			}
			return s;
		}
	}
	
	private class Expression{
		private LinkedList<Object> parts = new LinkedList<>();
		
		/**
		 * 
		 * @param part Can either be a String, an Integer, a Long, a Float, a Double, or an Expression.
		 *              Additionally A String must be proceeded by one of the other types, and the other types must either be first or be preceeded by a String.
		 *              A String value must be one of the values contained in the ARITHMETIC_OPERATORS array.
		 */
		public void appendPart(Object part) {
			if(part==this)
				throw new IllegalArgumentException("Cannot add an expression to itself.");
			if(part instanceof String) {
				if((parts.size()==0)||(parts.getLast() instanceof String))
					throw new IllegalArgumentException("Expression.appendPart can only append a String after an item with a numeric value.");
				if(!Arrays.asList(ARITHMETIC_OPERATORS).contains(part))
					throw new IllegalArgumentException("Expression.appendPart can only append a String that is a valid arithmetic operator.");
			}
			else{
				if(!((part instanceof Number)||(part instanceof Expression)))
					throw new IllegalArgumentException("Expression.appendPart can only accept String, Number, or Expression values.");
				if((parts.size()>0)&&(!(parts.getLast() instanceof String)))
					throw new IllegalArgumentException("Expression.appendPart can only place a Number, or Expression after a String, or as the first item in the expression.");
			}
			parts.add(part);
		}
		
		/**
		 * 
		 * @return The value the expression evaluates to.
		 */
		public Number evaluate() {
			if(parts.getLast() instanceof String)
				throw new RuntimeException("evaluate() called on incomplete/invalid expression.");
			LinkedList<Object> wipParts = new LinkedList<>();
			String operation = "";
			for(Object part:parts) {
				if(part instanceof String) {
					operation = (String)part;
					wipParts.add(part);
					continue;
				}
				Number n;
				if(part instanceof Number)
					n = (Number)part;
				else
					n = ((Expression)part).evaluate();
				if((operation == MULTIPLICATION_OPERATOR)||(operation == DIVISION_OPERATOR)) {
					wipParts.removeLast(); // Remove operator
					Number n1 = (Number)wipParts.removeLast();
					n = operate(n1,operation,n);
				}
				wipParts.add(n);
			}
			Number sum = (Number)wipParts.removeFirst();
			while(wipParts.size()>0) {
				String operator = (String)wipParts.removeFirst();
				Number n = (Number)wipParts.removeFirst();
				sum = operate(sum, operator,n);
			}
			return sum;
		}
		
		/**
		 * 
		 * @return The value the expression evaluates to.
		 */
		public Number evaluateWrong() {
			if(parts.getLast() instanceof String)
				throw new RuntimeException("evaluateWrong() called on incomplete/invalid expression.");
			String operation = "";
			Number value=null;
			for(Object part:parts) {
				if(part instanceof Expression)
					part = ((Expression)part).evaluateWrong();
				if(value==null) {
					value = (Number)part;
					continue;
				}
				if(part instanceof String) {
					operation = (String)part;
					continue;
				}
				Number n;
				if(part instanceof Number)
					n = (Number)part;
				else
					n = ((Expression)part).evaluateWrong();
				value = operate(value,operation,n);
			}
			return value;
		}
		
		private Number operate(Number n1, String operator, Number n2) {
			if(operator.equals(MULTIPLICATION_OPERATOR)) {
				if(((n2 instanceof Long)||(n2 instanceof Integer))&&((n1 instanceof Long)||(n1 instanceof Integer))) {
					return n1.longValue()*n2.longValue();
				}
				else {
					return n1.doubleValue()*n2.doubleValue();
				}
			}
			else if(operator.equals(DIVISION_OPERATOR)) {
				return n1.doubleValue()/n2.doubleValue();
			}
			else if(operator.equals(ADDITION_OPERATOR)) {
				if(((n1 instanceof Long)||(n1 instanceof Integer))&&((n2 instanceof Long)||(n2 instanceof Integer))) {
					return n1.longValue()+n2.longValue();
				}
				else {
					return n1.doubleValue()+n2.doubleValue();
				}
			}
			else if(operator.equals(SUBTRACTION_OPERATOR)) {
				if(((n1 instanceof Long)||(n1 instanceof Integer))&&((n2 instanceof Long)||(n2 instanceof Integer))) {
					return n1.longValue()-n2.longValue();
				}
				else {
					return n1.doubleValue()-n2.doubleValue();
				}
			}
			else {
				throw new IllegalArgumentException("Invalid operator used: "+operator);
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String retval="";
			for(Object part:parts) {
				if(retval.length()>0)
					retval+=" ";
				if(part instanceof Expression)
					retval+="("+part.toString()+")";
				else
					retval+=part.toString();
			}
			return retval;
		}
	}
	
	/**
	 * Addition (decimals and fractions)
	 * Subtraction (decimals and fractions)
	 * Multiplication (decimals and fractions)
	 * Division (decimals and fractions)
	 * 
	 * Least common multiple
	 * Greatest common denominator
	 * Prime factorization
	 * Distinguishing prime/composite numbers
	 * Distributive property
	 * 
	 * Problem solve with decimals
	 * Integers and rational numbers
	 * Comparing rational numbers
	 * Opposite and absolute value
	 * Coordinate plane
	 * Finding distance in the coordinate plane.
	 * 
	 * Order of operations
	 * Evaluating expressions
	 * 
	 * --------
	 * 
	 * R/W algebraic expressions
	 * Equivalent expressions
	 * 
	 * Compare expressions
	 * --------
	 * Solve equations by substitution
	 * R/W Equations
	 * Solve equations
	 * Equations in 2 variables
	 * --------
	 * Inequalities
	 * Solve inequalities by substitution
	 * 
	 * Basic ratio use
	 * Basic rate use
	 * Ratio table fill-in-the-blank
	 * Determine equivalent ratios
	 * Unit pricing and constant speed
	 * Solve word problems with rates
	 * Convert Metric system units
	 * Convert English system units
	 * Convert between metric/english
	 * Multi-step problems with unit conversion
	 * 
	 * Percent equations and circle graphs
	 *
	 * ---
	 * Find area of:
	 *  - Rectangle
	 *  - Parallelogram
	 *  - Triangle
	 *  - Trapezoid
	 * 
	 * Surface area
	 * Volume with unit cubes
	 * Volume of rectangular prism
	 * ---
	 * 
	 * Line plots
	 * Create/interpret histograms
	 * Stem-and-leaf plots
	 * Create/interpret box+whisker plot
	 * 
	 * ---
	 * Median
	 * Mean
	 * ---
	 * Weighted Average
	 * Interquartile Range + Outliers
	 * Mean Absolute Deviation
	 * Examine/summarize distributions
	 * 
	 */
}
