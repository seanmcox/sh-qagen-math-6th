/**
 * 
 */
package com.shtick.apps.sh.qagen.math.sixth;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

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
	private static final String[] ARITHMETIC_OPERATORS = new String[] {"+","-","\u00D7","\u00F7"};
	private static final String[] VOLUME_UNITS = new String[] {"cubic feet","cubic inches","cubic millimeters","cubic centimeters","cubic meters"};
	private static final String[] AREA_UNITS = new String[] {"square feet","square inches","square millimeters","square centimeters","square meters"};
	private static final String[] LENGTH_UNITS = new String[] {"feet","inches","millimeters","centimeters","meters"};
	private static final String[] LENGTH_UNITS_ABBR = new String[] {"ft","in","mm","cm","m"};
	private static final String[] PLACE_VALUE_VOCABULARY = new String[] {"hundred millions","ten millions","millions","hundred thousands","ten thousands","thousands","hundreds","tens","ones","tenths","hundredths","thousandths"};
	private static final String[] PLACE_VALUE_VOCABULARY_ABBR = new String[] {"100,000,000s","10,000,000s","1,000,000s","100,000s","10,000s","1,000s","100s","10s","1s","10ths","100ths","1,000ths"};
	private static final int[] PRIME_NUMBERS = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199};
	private static final Subject subject = new Subject("com.shtick.math.5th");
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
		int type = RANDOM.nextInt(14);
		int a,b,c;
		boolean isStory = RANDOM.nextBoolean();
		boolean translateQuestion = RANDOM.nextBoolean();
		int unknown = RANDOM.nextInt(3);
		switch(type){
		case 0:// Addition
			a = RANDOM.nextInt(100000);
			b = RANDOM.nextInt(100000-a);
			c = a+b;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 1:// Subtraction
			a = RANDOM.nextInt(100000);
			b = RANDOM.nextInt(a+1);
			c = a-b;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 2:// Multiplication
			a = RANDOM.nextInt(1000);
			b = RANDOM.nextInt(9)+1;
			c = a*b;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 3:// Division
			b = RANDOM.nextInt(9)+1;
			c = RANDOM.nextInt(1000);
			a = b*c;
			return generateArithmeticQuestion(a,b,c,type,unknown,isStory, translateQuestion);
		case 4:
			return generateFractionOperationQuestion();
		case 5:
			return generatePrimeNumberQuestion();
		case 6:
			return generatePlaceValueQuestion();
		case 7:
			return generateRectangleAreaQuestion();
		case 8:
			return generatePerimeterQuestion();
		case 9:
			return generatePrismSurfaceQuestion();
		case 10:
			return generateVolumeQuestion();
		case 11:
			return generateShapeIdentificationQuestion();
		case 12:
			return generateAngleClassificationQuestion();
		default:
			return generateFindMissingAngleQuestion();
		}
	}
	
	private Question generateFindMissingAngleQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		boolean triangle = RANDOM.nextBoolean();
		if(triangle) {
			// Find missing part of triangle.
			int[] angles = new int[] {60+RANDOM.nextInt(31)-15,60+RANDOM.nextInt(31)-15,0};
			angles[2] = 180-angles[0]-angles[1];
			String shapeSVG = drawEquilateralTriangle(false, ""+angles[0]+"°", ""+angles[1]+"°", "a");
			
			return new Question(shapeSVG,"image/svg+xml","What is the value of the missing angle, a, in degrees?","text/plain",""+angles[2],dimensions,4);
		}
		else {
			// Find missing part of quadrilateral.
			int[] angles = new int[] {90+RANDOM.nextInt(41)-20,90+RANDOM.nextInt(41)-20,90+RANDOM.nextInt(41)-20,0};
			angles[3] = 360-angles[0]-angles[1]-angles[2];
			String shapeSVG = drawQuadrilateral(angles[0],""+angles[0]+"°",angles[1],""+angles[1]+"°",angles[2],""+angles[2]+"°",angles[3],"a");
			
			return new Question(shapeSVG,"image/svg+xml","What is the value of the missing angle, a, in degrees?","text/plain",""+angles[3],dimensions,4);
		}
	}
	
	private Question generateAngleClassificationQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		Angle[] angles = Angle.values();
		int angle = RANDOM.nextInt(angles.length);
		boolean wordProblem = RANDOM.nextBoolean();
		if(RANDOM.nextBoolean()) {
			// Show angle, ask type.
			String angleText = getAngle(wordProblem,angles[angle]);
			
			ArrayList<Choice> choices = new ArrayList<>(angles.length);
			Angle[] randomAngles = Utils.getRandomArray(angles, angles.length);
			for(Angle a:randomAngles)
				choices.add(new Choice("text/plain", a.getNiceName(), a.toString()));
			MultipleChoice multipleChoice = new MultipleChoice("text/plain", "What kind of angle is this?", choices);
			String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
			
			return new Question(angleText,wordProblem?"text/plain":"image/svg+xml",answerPrompt,"choice/radio",""+angles[angle].toString(),dimensions,4);
		}
		else {
			// Show type, ask angle.
			ArrayList<Choice> choices = new ArrayList<>(angles.length);
			Angle[] randomAngles = Utils.getRandomArray(angles, angles.length);
			for(Angle a:randomAngles)
				choices.add(new Choice(wordProblem?"text/plain":"image/svg+xml", getAngle(wordProblem,a), a.toString()));
			MultipleChoice multipleChoice = new MultipleChoice("text/plain", "Which of these angles is a "+angles[angle].getNiceName()+" angle?", choices);
			String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
			
			return new Question("","text/plain",answerPrompt,"choice/radio",""+angles[angle].toString(),dimensions,4);
		}
	}
	
	private String getAngle(boolean asText, Angle angle) {
		if(asText) {
			int degrees;
			if(angle==Angle.RIGHT) {
				degrees=90;
			}
			else if(angle==Angle.ACUTE) {
				degrees = RANDOM.nextInt(90);
			}
			else {
				degrees = 180-RANDOM.nextInt(90);
			}
			return "A "+degrees+"° angle.";
		}
		String svg;
		switch(angle) {
		case ACUTE:
			svg = "<line x1=\"70\" y1=\"40\" x2=\"10\" y2=\"90\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"10\" y1=\"90\" x2=\"90\" y2=\"90\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
			break;
		case OBTUSE:
			svg = "<line x1=\"10\" y1=\"10\" x2=\"40\" y2=\"90\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\"40\" y1=\"90\" x2=\"90\" y2=\"90\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
			break;
		case RIGHT:
			svg = "<line x1=\"10\" y1=\"10\" x2=\"10\" y2=\"90\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"10\" y1=\"90\" x2=\"90\" y2=\"90\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"10\" y1=\"80\" x2=\"20\" y2=\"80\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n"+
					"<line x1=\"20\" y1=\"80\" x2=\"20\" y2=\"90\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			break;
		default:
			throw new IllegalArgumentException("Unsupported angle type: "+angle.toString());
		}
		return "<svg width=\"100\" height=\"100\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n"+svg+"</svg>";		
	}
	
	private Question generateShapeIdentificationQuestion() {
		HashMap<String,Float> dimensions = new HashMap<>();
		int shapeClass = RANDOM.nextInt(3);
		Shape[] shapes;
		switch(shapeClass) {
		case 0:{
			// Triangles.
			shapes = Utils.<Shape>getRandomArray(new Shape[]{Shape.RIGHT_TRIANGLE,Shape.EQUILATERAL_TRIANGLE,Shape.ISOSCELES_TRIANGLE},3);
			break;
		}
		case 1:{
			// Quadrilaterals.
			shapes = Utils.<Shape>getRandomArray(new Shape[]{Shape.PARALLELOGRAM,Shape.RECTANGLE,Shape.RHOMBUS,Shape.SQUARE,Shape.TRAPEZOID},4);
			break;
		}
		default:{
			// Any
			shapes = Utils.<Shape>getRandomArray(Shape.values(),4);
			break;
		}
		}
		Shape shape = shapes[RANDOM.nextInt(shapes.length)];
		
		if(RANDOM.nextBoolean()) {
			// Show shape, ask name.
			String shapeSVG = drawShape(shape);
			
			ArrayList<Choice> choices = new ArrayList<>(shapes.length);
			for(Shape s:shapes)
				choices.add(new Choice("text/plain", s.getNiceName(), s.toString()));
			MultipleChoice multipleChoice = new MultipleChoice("text/plain", "What kind of shape is this?", choices);
			String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
			
			return new Question(shapeSVG,"image/svg+xml",answerPrompt,"choice/radio",""+shape.toString(),dimensions,4);
		}
		else {
			// Show name, ask shape.
			ArrayList<Choice> choices = new ArrayList<>(shapes.length);
			for(Shape s:shapes)
				choices.add(new Choice("image/svg+xml", drawShape(s), s.toString()));
			MultipleChoice multipleChoice = new MultipleChoice("text/plain", "Which of these shapes is a "+shape.getNiceName()+"?", choices);
			String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
			
			return new Question("","text/plain",answerPrompt,"choice/radio",""+shape.toString(),dimensions,4);
		}
		
	}
	
	/**
	 * This does not draw the quadrilateral exactly to spec, but seeks to draw an approximate figure based on the supplied angles.
	 * @param angle1
	 * @param label1
	 * @param angle2
	 * @param label2
	 * @param angle3
	 * @param label3
	 * @param angle4
	 * @param label4
	 * @return
	 */
	private String drawQuadrilateral(int angle1,String label1,int angle2,String label2,int angle3,String label3,int angle4,String label4) {
		Point2D points[] = new Point2D[4];
		int angles[] = {angle1,angle2,angle3,angle4};
		String labels[] = {label1,label2,label3,label4};
		AffineTransform translationStep = AffineTransform.getTranslateInstance(50,50);
		for(int i=0;i<4;i++) {
			AffineTransform rotationStep = AffineTransform.getRotateInstance(Math.PI*i/2, 0, 0);
			if(angles[i]==90) {
				points[i] = new Point2D.Float(20, 20);
			}
			else if(angles[i]>90) {
				points[i] = new Point2D.Float(10, 10);
			}
			else if(angles[i]<90) {
				points[i] = new Point2D.Float(30, 30);
			}
			rotationStep.transform(points[i],points[i]);
			translationStep.transform(points[i],points[i]);
		}
		String shapeSVG = "";
		for(int i=0;i<3;i++) {
			shapeSVG += "<line x1=\""+points[i].getX()+"\" y1=\""+points[i].getY()+"\" x2=\""+points[i+1].getX()+"\" y2=\""+points[i+1].getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
		}
		shapeSVG += "<line x1=\""+points[0].getX()+"\" y1=\""+points[0].getY()+"\" x2=\""+points[3].getX()+"\" y2=\""+points[3].getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
		for(int i=0;i<4;i++) {
			if((labels[i]!=null)&&(labels[i].length()>0)) {
				shapeSVG+="<text x=\""+((points[i].getX()>50)?(points[i].getX()+7):(points[i].getX()-8*(""+angles[i]).length()))+"\" y=\""+((points[i].getY()>50)?(points[i].getY()+15):points[i].getY())+"\">"+labels[i]+"</text>\n";
			}
		}
		return "<svg width=\"100\" height=\"100\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n"+shapeSVG+"</svg>";
	}
	
	private String drawEquilateralTriangle(boolean includeTicks,String label1,String label2,String label3) {
		Point2D p1 = new Point2D.Float(50, 15);
		Point2D p2 = new Point2D.Float();
		Point2D p3 = new Point2D.Float();
		AffineTransform rotationStep = AffineTransform.getRotateInstance(Math.PI*2/3, 50, 50);
		rotationStep.transform(p1, p2);
		rotationStep.transform(p2, p3);
		Point2D tick1 = new Point2D.Float(0, 5);
		Point2D tick2 = new Point2D.Float(0, -5);
		String shapeSVG;
		shapeSVG = "<line x1=\""+p1.getX()+"\" y1=\""+p1.getY()+"\" x2=\""+p2.getX()+"\" y2=\""+p2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\""+p2.getX()+"\" y1=\""+p2.getY()+"\" x2=\""+p3.getX()+"\" y2=\""+p3.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
				"<line x1=\""+p3.getX()+"\" y1=\""+p3.getY()+"\" x2=\""+p1.getX()+"\" y2=\""+p1.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
		if(includeTicks){
			{
				Point2D lp1 = p1;
				Point2D lp2 = p2;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI/3, 0, 0);
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			{
				Point2D lp1 = p2;
				Point2D lp2 = p3;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				tp1.setLocation(tick1.getX()+center.getX(), tick1.getY()+center.getY());
				tp2.setLocation(tick2.getX()+center.getX(), tick2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			{
				Point2D lp1 = p3;
				Point2D lp2 = p1;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI/3, 0, 0);
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
		}
		if((label1!=null)&&(label1.length()>0)) {
			shapeSVG+="<text x=\""+p1.getX()+"\" y=\""+p1.getY()+"\">"+label1+"</text>\n";
		}
		if((label2!=null)&&(label2.length()>0)) {
			shapeSVG+="<text x=\""+p2.getX()+"\" y=\""+(p2.getY()+15)+"\">"+label2+"</text>\n";
		}
		if((label3!=null)&&(label3.length()>0)) {
			shapeSVG+="<text x=\""+p3.getX()+"\" y=\""+(p3.getY()+15)+"\">"+label3+"</text>\n";
		}
		return "<svg width=\"100\" height=\"100\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n"+shapeSVG+"</svg>";
	}
	
	private String drawShape(Shape s) {
		String shapeSVG;
		switch(s) {
		case EQUILATERAL_TRIANGLE:{
			return drawEquilateralTriangle(true,null,null,null);
		}
		case ISOSCELES_TRIANGLE:{
			Point2D p1 = new Point2D.Float(50, 10);
			Point2D p2 = new Point2D.Float();
			Point2D p3 = new Point2D.Float();
			AffineTransform rotationStep1 = AffineTransform.getRotateInstance(Math.PI*5/6, 50, 50);
			AffineTransform rotationStep2 = AffineTransform.getRotateInstance(-Math.PI*5/6, 50, 50);
			rotationStep1.transform(p1, p2);
			rotationStep2.transform(p1, p3);
			Point2D tick1 = new Point2D.Float(0, 5);
			Point2D tick2 = new Point2D.Float(0, -5);
			shapeSVG = "<line x1=\""+p1.getX()+"\" y1=\""+p1.getY()+"\" x2=\""+p2.getX()+"\" y2=\""+p2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\""+p2.getX()+"\" y1=\""+p2.getY()+"\" x2=\""+p3.getX()+"\" y2=\""+p3.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\""+p3.getX()+"\" y1=\""+p3.getY()+"\" x2=\""+p1.getX()+"\" y2=\""+p1.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
			{
				Point2D lp1 = p1;
				Point2D lp2 = p2;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI/3, 0, 0);
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			{
				Point2D lp1 = p2;
				Point2D lp2 = p3;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				tp1.setLocation(tick1.getX()+center.getX(), tick1.getY()+center.getY());
				tp2.setLocation(tick2.getX()+center.getX(), tick2.getY()+center.getY());
				shapeSVG+="<line x1=\""+(tp1.getX()+2)+"\" y1=\""+tp1.getY()+"\" x2=\""+(tp2.getX()+2)+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
				shapeSVG+="<line x1=\""+(tp1.getX()-2)+"\" y1=\""+tp1.getY()+"\" x2=\""+(tp2.getX()-2)+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			{
				Point2D lp1 = p3;
				Point2D lp2 = p1;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI/3, 0, 0);
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			break;
		}
		case PARALLELOGRAM:
			shapeSVG = "<line x1=\"5\" y1=\"20\" x2=\"60\" y2=\"20\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"40\" y1=\"60\" x2=\"95\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"5\" y1=\"20\" x2=\"40\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"60\" y1=\"20\" x2=\"95\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
			break;
		case RECTANGLE:
			shapeSVG = "<rect x=\"10\" y=\"10\" width=\"80\" height=\"40\" style=\"fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)\" />\n";
			break;
		case RHOMBUS:{
			Point2D p1 = new Point2D.Float(50, 10);
			Point2D p2 = new Point2D.Float(75,50);
			Point2D p3 = new Point2D.Float(50, 90);
			Point2D p4 = new Point2D.Float(25,50);
			Point2D tick1 = new Point2D.Float(0, 5);
			Point2D tick2 = new Point2D.Float(0, -5);
			shapeSVG = "<line x1=\""+p1.getX()+"\" y1=\""+p1.getY()+"\" x2=\""+p2.getX()+"\" y2=\""+p2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\""+p2.getX()+"\" y1=\""+p2.getY()+"\" x2=\""+p3.getX()+"\" y2=\""+p3.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\""+p3.getX()+"\" y1=\""+p3.getY()+"\" x2=\""+p4.getX()+"\" y2=\""+p4.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\""+p4.getX()+"\" y1=\""+p4.getY()+"\" x2=\""+p1.getX()+"\" y2=\""+p1.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
			Point2D topCenter = new Point2D.Double((p1.getX()+p2.getX()+p4.getX())/3,(p1.getY()+p2.getY()+p4.getY())/3);
			Point2D bottomCenter = new Point2D.Double((p3.getX()+p2.getX()+p4.getX())/3,(p3.getY()+p2.getY()+p4.getY())/3);
			{
				Point2D lp1 = p1;
				Point2D lp2 = p2;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI/3, topCenter.getX(), topCenter.getY());
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			{
				Point2D lp1 = p4;
				Point2D lp2 = p1;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI/3, topCenter.getX(), topCenter.getY());
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			{
				Point2D lp1 = p1;
				Point2D lp2 = p2;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI*2/3, bottomCenter.getX(), bottomCenter.getY());
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			{
				Point2D lp1 = p4;
				Point2D lp2 = p1;
				Point2D center = new Point2D.Double((lp1.getX()+lp2.getX())/2,(lp1.getY()+lp2.getY())/2);
				Point2D tp1 = new Point2D.Float();
				Point2D tp2 = new Point2D.Float();
				AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI*2/3, bottomCenter.getX(), bottomCenter.getY());
				rotation.transform(tick1, tp1);
				rotation.transform(tick2, tp2);
				tp1.setLocation(tp1.getX()+center.getX(), tp1.getY()+center.getY());
				tp2.setLocation(tp2.getX()+center.getX(), tp2.getY()+center.getY());
				shapeSVG+="<line x1=\""+tp1.getX()+"\" y1=\""+tp1.getY()+"\" x2=\""+tp2.getX()+"\" y2=\""+tp2.getY()+"\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			}
			break;
		}
		case RIGHT_TRIANGLE:
			shapeSVG = "<line x1=\"5\" y1=\"20\" x2=\"5\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"85\" y1=\"60\" x2=\"5\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"85\" y1=\"60\" x2=\"5\" y2=\"20\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"5\" y1=\"55\" x2=\"10\" y2=\"55\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n"+
					"<line x1=\"10\" y1=\"60\" x2=\"10\" y2=\"55\" style=\"stroke:rgb(0,0,0);stroke-width:1\" />\n";
			break;
		case SQUARE:
			shapeSVG = "<rect x=\"10\" y=\"10\" width=\"80\" height=\"80\" style=\"fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)\" />\n";
			break;
		case TRAPEZOID:
			shapeSVG = "<line x1=\"40\" y1=\"20\" x2=\"60\" y2=\"20\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"5\" y1=\"60\" x2=\"95\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"40\" y1=\"20\" x2=\"5\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n"+
					"<line x1=\"60\" y1=\"20\" x2=\"95\" y2=\"60\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />\n";
			break;
		default:
			throw new IllegalArgumentException("Unsupported shape type: "+s.toString());
		}
		return "<svg width=\"100\" height=\"100\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n"+shapeSVG+"</svg>";		
	}
	
	private Question generateFractionOperationQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		int type = RANDOM.nextInt(4);
		int na = RANDOM.nextInt(15)+2;
		int nb = RANDOM.nextInt(15)+2;
		int da = RANDOM.nextInt(11)+2;
		int db = RANDOM.nextInt(11)+2;
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
		int gcd = gcd(numerator,denominator);
		return ""+(numerator/gcd)+"/"+(denominator/gcd);
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
	
	private Question generatePrismSurfaceQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		int w = RANDOM.nextInt(20)+10;
		int h = RANDOM.nextInt(15)+4;
		int d = RANDOM.nextInt(15)+4;
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
				w = RANDOM.nextInt(15)+4;
				h = w;
				d = w;
			}
			else {
				w = RANDOM.nextInt(15)+4;
				h = RANDOM.nextInt(15)+4;
				d = RANDOM.nextInt(15)+4;
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
						"</svg>","image/svg+xml","What is the volume are of this cube in "+AREA_UNITS[unit]+"?","text/plain",""+answer,dimensions,4);
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
	
	private Question generateRectangleAreaQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		boolean square = RANDOM.nextBoolean();
		int w = RANDOM.nextInt(100);
		int h = square?w:RANDOM.nextInt(100);
		int max = Math.max(w, h);
		int area = w*h;
		int unit = RANDOM.nextInt(LENGTH_UNITS.length);
		int constrainingSquareLength = 100;

		int drawingWidth = Math.max(constrainingSquareLength*w/max,15);
		int drawingHeight = Math.max(constrainingSquareLength*h/max,15);
		
		return new Question("<svg width=\""+(constrainingSquareLength+75)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
			"<rect x=\"25\" y=\"25\" width=\""+drawingWidth+"\" height=\""+drawingHeight+"\" style=\"stroke-width:3;stroke:rgb(0,0,0)\" />\n" + 
			"<text x=\""+(drawingWidth/2+15)+"\" y=\""+(drawingHeight+40)+"\">"+w+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
			"<text x=\""+(drawingWidth+40)+"\" y=\""+(drawingHeight/2+25)+"\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
			"</svg>","image/svg+xml","What is the area of this "+(square?"square":"rectangle")+" in "+AREA_UNITS[unit]+".","text/plain",""+area,dimensions,4);
	}
	
	private Question generatePerimeterQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		boolean simple = RANDOM.nextBoolean();
		int unit = RANDOM.nextInt(LENGTH_UNITS.length);
		if(simple) {
			int constrainingSquareLength = 100;
			boolean square = RANDOM.nextBoolean();
			int w = RANDOM.nextInt(100);
			int h = square?w:RANDOM.nextInt(100);
			int max = Math.max(w, h);
			int perimeter = w*2+h*2;

			int drawingWidth = Math.max(constrainingSquareLength*w/max,15);
			int drawingHeight = Math.max(constrainingSquareLength*h/max,15);

			return new Question("<svg width=\""+(constrainingSquareLength+75)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" + 
					"<rect x=\"25\" y=\"25\" width=\""+drawingWidth+"\" height=\""+drawingHeight+"\" style=\"stroke-width:3;stroke:rgb(0,0,0)\" />\n" + 
					"<text x=\""+(drawingWidth/2+15)+"\" y=\""+(drawingHeight+40)+"\">"+w+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
					"<text x=\""+(drawingWidth+40)+"\" y=\""+(drawingHeight/2+25)+"\">"+h+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n" +
					"</svg>","image/svg+xml","What is the perimeter of this "+(square?"square":"rectangle")+" in "+LENGTH_UNITS[unit]+".","text/plain",""+perimeter,dimensions,4);
		}
		
		int constrainingSquareLength = 200;
		int sides = 5+RANDOM.nextInt(2);
		LinkedList<Point> points = new LinkedList<>();
		LinkedList<Integer> lengths = new LinkedList<>();
		int scale = RANDOM.nextInt(2000/constrainingSquareLength)+1;
		int perimeter = 0;
		int l;
		String shapeDescription = "";
		Point midpoint;
		for(int i=0;i<sides;i++) {
			double angle = Math.PI * 2 * i / sides;
			int r = RANDOM.nextInt(constrainingSquareLength*2/5)+constrainingSquareLength/10;
			Point point = new Point((int)(Math.cos(angle)*r+constrainingSquareLength/2), (int)(Math.sin(angle)*r+constrainingSquareLength/2));
			shapeDescription+="<circle cx=\""+point.x+"\" cy=\""+point.y+"\" r=\"1\" stroke=\"black\" stroke-width=\"5\" fill=\"black\" />";
			if(points.size()>0) {
				l = (int)point.distance(points.getLast())*scale;
				lengths.add(l);
				perimeter+=l;
				shapeDescription+="<line x1=\""+points.getLast().x+"\" y1=\""+points.getLast().y+"\" x2=\""+point.x+"\" y2=\""+point.y+"\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
				midpoint = new Point((point.x + points.getLast().x)/2, (point.y + points.getLast().y)/2);
				int adjustY = -2;
				int adjustX = 2;
				if((point.x-points.getLast().x)*(point.y - points.getLast().y)<0) {
					adjustY = 17;
					adjustX = 0;
				}
				shapeDescription+="<text x=\""+(midpoint.x+adjustX)+"\" y=\""+(midpoint.y+adjustY)+"\" fill=\"rgb(128,0,0)\">"+l+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";
			}
			points.add(point);
		}
		l = (int)points.getFirst().distance(points.getLast())*scale;
		lengths.add(l);
		perimeter+=l;
		shapeDescription+="<line x1=\""+points.getLast().x+"\" y1=\""+points.getLast().y+"\" x2=\""+points.getFirst().x+"\" y2=\""+points.getFirst().y+"\" style=\"stroke:rgb(0,0,0);stroke-width:3\" />\n";
		midpoint = new Point((points.getFirst().x + points.getLast().x)/2, (points.getFirst().y + points.getLast().y)/2);
		int adjustY = -2;
		int adjustX = 2;
		if((points.getFirst().x-points.getLast().x)*(points.getFirst().y - points.getLast().y)<0) {
			adjustY = 17;
			adjustX = 0;
		}
		shapeDescription+="<text x=\""+(midpoint.x+adjustX)+"\" y=\""+(midpoint.y+adjustY)+"\" fill=\"rgb(128,0,0)\">"+l+" "+LENGTH_UNITS_ABBR[unit]+"</text>\n";

		return new Question("<svg width=\""+(constrainingSquareLength+75)+"\" height=\""+(constrainingSquareLength+75)+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\">\n" +
				shapeDescription +
				"</svg>","image/svg+xml","What is the perimeter of this shape in "+LENGTH_UNITS[unit]+"?","text/plain",""+perimeter,dimensions,4);
	}
	
	private Question generatePlaceValueQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		int[] placeValues = new int[12];
		int place = RANDOM.nextInt(placeValues.length);
		int placeValue = (place==0)?(RANDOM.nextInt(9)+1):RANDOM.nextInt(10);
		String number = "";
		boolean pickValueForPlace = RANDOM.nextBoolean();
		for(int i=0;i<12;i++) {
			if(i==place) {
				placeValues[i] = placeValue;
			}
			else {
				if(i==0) {
					placeValues[i] = RANDOM.nextInt(9)+1;
				}
				else {
					placeValues[i] = RANDOM.nextInt(10);
				}
				if((!pickValueForPlace)&&(placeValues[i] == placeValue)) {
					if(placeValue==9) {
						placeValues[i]--;
					}
					else {
						placeValues[i]++;
					}
				}
			}
			number+=placeValues[i];
			if((i+1)%3 == 0) {
				if((i+1)/3 == 3) {
					number+=".";
				}
				else if((i+1)/3 < 3) {
					number+=",";
				}
			}
		}
		String[] placeArray = RANDOM.nextBoolean()?PLACE_VALUE_VOCABULARY:PLACE_VALUE_VOCABULARY_ABBR;
		
		if(pickValueForPlace) // Get the digit for the place value
			return new Question("You are given this number: "+number,"text/plain","What digit is in the "+placeArray[place]+" place?","text/plain",""+placeValue,dimensions,4);

		// Get the place value for the digit.
		ArrayList<Choice> choiceOptions = new ArrayList<>(12);
		for(int i=0;i<12;i++)
			choiceOptions.add(new Choice("text/plain", ""+placeArray[i], ""+i));
		Choice correctChoice = choiceOptions.remove(place);
		ArrayList<Choice> choices = new ArrayList<>(4);
		for(int i=0;i<3;i++)
			choices.add(choiceOptions.remove(RANDOM.nextInt(choiceOptions.size())));
		choices.add(RANDOM.nextInt(4), correctChoice);
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", "Which of the following words describes the place value of the digit "+placeValues[place]+"?", choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		
		return new Question("You are given this number: "+number,"text/plain",answerPrompt,"choice/radio",""+place,dimensions,4);
	}
	
	private Question generatePrimeNumberQuestion(){
		HashMap<String,Float> dimensions = new HashMap<>();
		int primePosition = RANDOM.nextInt(4);
		
		ArrayList<Choice> choices = new ArrayList<>(4);
		for(int i=0;i<4;i++) {
			if(i==primePosition) {
				choices.add(new Choice("text/plain", ""+PRIME_NUMBERS[RANDOM.nextInt(30)], ""+i));
				continue;
			}
			if(RANDOM.nextBoolean()) {
				choices.add(new Choice("text/plain", ""+(PRIME_NUMBERS[RANDOM.nextInt(6)]*PRIME_NUMBERS[RANDOM.nextInt(6)]*PRIME_NUMBERS[RANDOM.nextInt(6)]), ""+i));
				continue;
			}
			choices.add(new Choice("text/plain", ""+(PRIME_NUMBERS[RANDOM.nextInt(12)]*PRIME_NUMBERS[RANDOM.nextInt(12)]), ""+i));
		}
		MultipleChoice multipleChoice = new MultipleChoice("text/plain", "Which of these numbers is prime.", choices);
		String answerPrompt = JSONEncoder.encode(Marshal.marshal(multipleChoice));
		
		return new Question("Here is a list of numbers.","text/plain",answerPrompt,"choice/radio",""+primePosition,dimensions,4);
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
	
	/**
	 * Place value. (0.001 to 100,000,000)
	 * Rounding numbers
	 * Rounding numbers to solve story problems.
	 * 
	 * Addition (sums to under 100,000. Decimals to 0.001)
	 * Subtraction (to under 100,000.  Decimals to 0.001)
	 * Multiplication (Three decimal places to 100's * three decimal places to 100's.  Decimals to 0.01)
	 * Division (Up to five digits up to 1000's divided by two digits up to 10's.  Decimals to 0.01)
	 * Multistep expressions (up to 4 steps, possibly with a variable, which has a value defined separately)
	 *  - Order of operations: +, -, *, /, (), [], {}, exponents
	 *
	 * Comparing angles
	 * Vocabulary:
	 *  - ray
	 *  - angle
	 *  - vertex
	 *  - acute
	 *  - obtuse
	 *  - right
	 *  - straight
	 *  - perpendicular
	 *  - parallel
	 *  - isosceles triangle
	 *  - equilateral triangle
	 *  - right triangle
	 *  - parallelogram
	 *  - rectangle
	 *  - rhombus
	 *  - square
	 *  - trapezoid
	 * Recognizing rays by pair of vertices on ray line.
	 * Recognizing angles by sets of three vertices on lines.
	 * Angles in triangles and quadrilaterals.
	 *  - Triangles: 180 degrees total
	 *  - Quadrilaterals: 360 degrees total
	 * 
	 * Fraction multiplication
	 *  - integer and fraction
	 *  - fraction and fraction
	 * Fraction division (integer and fraction)
	 * Fractions as division
	 * Add/subtract fractions
	 * 
	 * Comparing decimals (to hundredths)
	 * Multiply/divide by powers of 10 (10^n or 1000. 0.001 to 100,000)
	 * 
	 * Variables in simple equations (one operator and an equals sign)
	 * Understanding 6t as an alternative to 6*t
	 * 
	 * Vocabulary:
	 *  - x-axis
	 *  - y-axis
	 *  - origin
	 *  - ordered pair
	 * Finding coordinate locations on a 2-D graph
	 * Graphing linear equations.
	 * 
	 * Vocabulary:
	 *  - perimeter
	 *  - area
	 *  - volume
	 *  - cube
	 *  - prism
	 *  - meter, m
	 *  - square meter, m^2
	 *  - cubic meter, m^3
	 *  - centimeter, cm
	 *  - square centimeter, cm^2
	 *  - cubic centimeter, cm^3
	 *  - feet, ft
	 *  - yard, yd
	 *  - millimeter, mm
	 * Perimeters of rectangles, squares.
	 * Perimeters of freeform shapes with given edge lengths.
	 * Surface area of cubes and prisms.
	 * Volume of cubes and prisms.
	 * Volume of composite shapes.
	 * Estimating area of irregular shapes with a grid overlayed.
	 * 
	 */
}
