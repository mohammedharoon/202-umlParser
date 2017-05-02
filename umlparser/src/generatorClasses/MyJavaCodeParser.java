package generatorClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


public class MyJavaCodeParser {
	
    private String yUMLWebLink;
    private String fullWebURL;
	private String relationships = "";
    private String resultantIntermediateString = "";
    private HashMap<String,String> classInterfaceMap;
    private Set<String> getterSetterVariables;
    
    public MyJavaCodeParser()
    {
    	this.yUMLWebLink = "https://yuml.me/diagram/scruffy/class/";
        this.getterSetterVariables = new  HashSet<String>();
    }
  
    public void generateDiagram(String outputFileName)
    {
    	fullWebURL = yUMLWebLink + resultantIntermediateString;
    	try{
    		URL url = new URL(fullWebURL);
    		HttpURLConnection hcon = (HttpURLConnection) url.openConnection();
    		hcon.setRequestMethod("GET");
    		String outputFilePath = "C:\\Users\\Haroon\\Desktop\\202-umlParser\\outputFiles\\"+outputFileName+".png";
    		FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
    		System.out.println(hcon.getResponseCode());
    		int i;
    		while((i = hcon.getInputStream().read())!=-1)
    			fos.write(i);
    		fos.close();
    	}
    	catch(MalformedURLException e){
    		e.printStackTrace();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}	
    }
    
    /**
     * This is the main method
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception
	{  
		try{
			//String basePath = "C:\\Users\\Haroon\\Desktop\\202-umlParser\\ClassDiagramsTestCases\\class-diagram-test-4";
			String basePath = args[0];
			String outputFileName = args[1];
			ArrayList<CompilationUnit> compilationUnits;
	        MyJavaCodeParser myJavaCodeParser = new MyJavaCodeParser();
	        compilationUnits = myJavaCodeParser.compileTestFolder(basePath);
	        myJavaCodeParser.createClassInterfaceMap(compilationUnits);
	        String result = myJavaCodeParser.parser(compilationUnits);
	        String modifiedResult = myJavaCodeParser.changeAttributesAccessModifierToPublic(result);
	        System.out.println(modifiedResult);
	        myJavaCodeParser.generateDiagram(outputFileName); 
		}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("One or more parameters missing");
			System.out.println("Please enter parameters as <SourceFolder> <OutputFileName>");
		}
       
    }
    
	public void createClassInterfaceMap(ArrayList<CompilationUnit> compilationUnits)
	{
		classInterfaceMap = new HashMap<String,String>();
		for(CompilationUnit compilationUnit : compilationUnits)
		{
			List<TypeDeclaration> typeDec = compilationUnit.getTypes();
			for(Node node: typeDec)
			{
				ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) node;
				if(coid.isInterface())
				    classInterfaceMap.put(coid.getName(), "interface");
				else
					classInterfaceMap.put(coid.getName(), "class");
			}
		}
	}
	
	
	/** This method reads the files in the test folder for creating UML diagram.
	 * If the file is a java file then it creates a compilationUnit for it. 
	 * @param testFolderPath
	 * @return compilationUnits
	 */
	public ArrayList<CompilationUnit> compileTestFolder(String testFolderPath) 
	{
		ArrayList<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
		File testFolder = new File(testFolderPath);
		for(File file : testFolder.listFiles())
		{
			if(file.getName().endsWith(".java") && file.isFile())
			{
				try
				{
					FileInputStream  inpFile = new FileInputStream(file);
			        CompilationUnit compilationUnit = JavaParser.parse(inpFile);
			        compilationUnits.add(compilationUnit);
			        inpFile.close();
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return compilationUnits;
	}
	
    /** This method converts the access modifier of a variable
     *  into equivalent UML symbol
     * @param varAccessModifier
     * @return
     */
	public String convertAccessModifiedToSymbol(String varAccessModifier)
	{
		if(varAccessModifier.equals("private"))
			return "-";
		else if(varAccessModifier.equals("public"))
			return "+";
		else
			return "";
		
	}
	
	/** This method changes the access modifiers from private to public
	 *  of those attributes that have setter and getter methods
	 * @param intermediateGrammer
	 * @return intermediateGrammer
	 */
	private String changeAttributesAccessModifierToPublic(String intermediateGrammer)
	{
		StringBuilder sb = new StringBuilder(intermediateGrammer);
     for(String varName : getterSetterVariables)
     {
    	 if(intermediateGrammer.contains(varName))
    	     {
    		 int indexOfVariable = intermediateGrammer.indexOf(varName);
    		 if(sb.charAt(indexOfVariable-1) == '-')
    		     sb.setCharAt(indexOfVariable-1, '+');
    	     }
     }
     return sb.toString();
	}
	
	public String parser(ArrayList<CompilationUnit> compUnits){
		
		for(CompilationUnit compUnit : compUnits)
		{
		List<TypeDeclaration> c1 = compUnit.getTypes();
		Node node = c1.get(0);
		//System.out.println("comp Unit ->"+compUnit);
		String classNameString = getClassName(compUnit);
		String variablesString = getVariableCompartment(node);
		String methodsString = getMethodCompartment(node);
		resultantIntermediateString += getResultString(classNameString,variablesString,methodsString);
		}
		Set<String> hashSet = new HashSet<String>(Arrays.asList(relationships.split(",")));
		relationships = String.join(",", hashSet);		
		resultantIntermediateString += relationships;
		return resultantIntermediateString;
	}
	
	/**
	 * This method generates the intermediate grammar by combining
	 * the className, variablesString and methodsString
	 * @param classNames
	 * @param variablesString
	 * @param methodsString
	 * @return
	 */
    private String getResultString(String classNames, String variablesString, String methodsString)
    {
    	String result;
    	if(variablesString.length() == 0 && methodsString.length() == 0)
    	    result = "["+ classNames  + "],";
    	else if(variablesString.length() == 0)
    		result = "["+ classNames + "|" + methodsString + "],";
    	else if(methodsString.length() == 0)
    		result = "["+ classNames + "|" + variablesString + "],";
    	else
    		result = "["+ classNames + "|" + variablesString + "|" + methodsString + "],";
    	return result;
    }
    
    /**
     * This method gets the class name from the compilation unit
     * @param cu
     * @return
     */
	private String getClassName(CompilationUnit cu){
		List<TypeDeclaration> c1 = cu.getTypes();
		for(Node codeBlock : c1)
		{
			ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) codeBlock;
			if(coid.getExtends() != null)
			{
				relationships += "[" + coid.getName() +"]"+ "-^" + coid.getExtends() +",";
			}
			if(coid.getImplements() != null)
			{
				List<ClassOrInterfaceType> implementsInterfaceList = (List<ClassOrInterfaceType>) coid.getImplements();
				for(ClassOrInterfaceType implementsInterface : implementsInterfaceList)
					relationships += "[" + coid.getName() + "]" +"-.-^" + "[<<interface>>;" + implementsInterface + "],";

			}
			if(coid.isInterface())
				return "<<interface>>;" + coid.getName();
			else
				return coid.getName();
		}
		
		return "";
	}
	
	/**
	 * This method returns the Method Compartment of a class/interface
	 * @param node
	 * @return
	 */
	private String getMethodCompartment(Node node) {
		boolean nextMethod = false;
		String methodString = "";
		ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) node;
		String className = coid.getName();
		boolean isInterface = coid.isInterface();
        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
            if (bd instanceof ConstructorDeclaration) {
                ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
                if (cd.getDeclarationAsString().startsWith("public")) {
                    if (nextMethod)
                    	methodString += ";";
                    methodString += "+ " + cd.getName() + "(";
                    for (Object childNodeObj : cd.getChildrenNodes()) 
                    {
                    	
                        if (childNodeObj instanceof Parameter) 
                        {
                            Parameter parameter = (Parameter) childNodeObj;
                            String parameterType = parameter.getType().toString();
                            String parameterName = parameter.getChildrenNodes().get(0).toString();
                            methodString += parameterName + " : " + parameterType;
                            if (classInterfaceMap.containsKey(parameterType)) 
                            {
                                if (classInterfaceMap.get(parameterType).equals("interface") && !isInterface)
                                {
                                	relationships += "[" + className + "] uses -.->";
                                	relationships += "[<<interface>>;" + parameterType + "]";
                                }
                                relationships += ",";
                            }
                        }
                    }
                    methodString += ")";
                    nextMethod = true;
                }//if const is public
            }//if bd is of type constructor
            
            //get public methods
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = ((MethodDeclaration) bd);
                // Get only public methods
                if (md.getDeclarationAsString().startsWith("public")){
                        //&& !coid.isInterface()) {
                        if (nextMethod)
                        	methodString += ";";
                        if(md.getName().startsWith("set") || md.getName().startsWith("get"))
                        {
                        	String variableName = md.getName().substring(3);
                        	StringBuilder sb = new StringBuilder(variableName);
                        	sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
                        	String modifiedVariableName = sb.toString();
                        	getterSetterVariables.add(modifiedVariableName);
                        	continue;
                        }
                        methodString += "+ " + md.getName() + "(";
                        for (Object childNodeObj : md.getChildrenNodes()) {
                            if (childNodeObj instanceof Parameter) 
                                {
                                 Parameter parameter = (Parameter) childNodeObj;
                                 String parameterType = parameter.getType().toString();
                                 String parameterName = parameter.getChildrenNodes().get(0).toString();
                                 methodString += parameterName + " : " + parameterType;
                                 
                                 if (classInterfaceMap.containsKey(parameterType)) 
                                   {
                                        if (classInterfaceMap.get(parameterType).equals("interface") && !isInterface)
                                       {
                                    	relationships += "[" + className+ "]uses -.->";
                                    	relationships += "[<<interface>>;"+ parameterType + "]";	
                                        }
                                        /*
                                        else if(classInterfaceMap.get(parameterType).equals("interface") && isInterface)
                                        {
                                    	 relationships += "[<<interface>>;" + className + "] uses -.->";
                                    	 relationships += "[<<interface>>;" + parameterType + "]";                                	
                                        }*/
                                     }
                                    relationships += ",";
                                 }
                            else if(childNodeObj instanceof BlockStmt)
                            {
                            	BlockStmt bst = (BlockStmt) childNodeObj;
                            	for(Object stmtChildNodeObj : bst.getChildrenNodes())
                            		
                            	{
                            		if(stmtChildNodeObj instanceof ExpressionStmt)
                            		{
                            			ExpressionStmt expStatement = (ExpressionStmt) stmtChildNodeObj;
                            			Expression ex = expStatement.getExpression();
                            			if(ex instanceof VariableDeclarationExpr)
                            			    {
                            				VariableDeclarationExpr var = (VariableDeclarationExpr) expStatement.getExpression();
                            			    String localVariable = var.getType().toString();
                                            if (classInterfaceMap.containsKey(localVariable) && !isInterface) 
                                            {
                                            	relationships += "[" + className+ "]uses-.->";
                                                 if (classInterfaceMap.get(localVariable).equals("interface"))
                                                {
                                             	relationships += "[<<interface>>;"+ localVariable + "]";	
                                                 }
                                                 else if(classInterfaceMap.get(localVariable).equals("class"))
                                                 {
                                             	 relationships += "[" + localVariable + "]";                                	
                                                 }
                                              }
                                             relationships += ",";
                            			    }
                            		}
                            	}
                            }
                        }
                        methodString += ") : " + md.getType();
                        nextMethod = true;
                }
            }// if bd is type of method
        }// for loop of body dec
		return methodString;
	}

	/**
	 * This method return the variable compartment of the class
	 * @param node
	 * @return
	 */
	private String getVariableCompartment(Node node) {
		boolean nextVariable = false;
		String variableString = "";
		ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) node;
		String className = coid.getName();
        for (BodyDeclaration bd : ((TypeDeclaration)node).getMembers()) {
        	
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = ((FieldDeclaration) bd);
                //[String, message = "hello"]
                String variableAccessModifier = bd.toStringWithoutComments().substring(0,
                                bd.toStringWithoutComments().indexOf(" "));
                variableAccessModifier = convertAccessModifiedToSymbol(variableAccessModifier);
                String variableType =  fd.getType().toString();
                String variableName = fd.getChildrenNodes().get(1).toString();
                if(variableType.contains("["))
                {
                	variableType = variableType.replace("[", "(*");
                	variableType = variableType.replace("]", ")");
                }
                if(variableName.contains("="))
                	variableName = variableName.substring(0,variableName.indexOf("="));

                // for uses relationship
                if(classInterfaceMap.containsKey(variableType))
                {
                	if(classInterfaceMap.get(variableType).equals("class"))
                	    relationships += "[" + className + "]-[" + variableType+"],";
                	if(classInterfaceMap.get(variableType).equals("interface"))
                	    relationships += "[" + className + "]-[<<interface>>;" + variableType+"],";
                }
                //for collection of class objects
                if(variableType.contains("<"))
                {//private Collection<Observer> observers
                	//use association
                	String collectionType = variableType.substring(variableType.indexOf("<")+1, variableType.indexOf(">"));
                	if(classInterfaceMap.containsKey(collectionType))
                	{
                		if(classInterfaceMap.get(collectionType).equals("class"))
                			relationships += "[" + className + "]-*" + "["+collectionType+"],";
                		else
                		    relationships += "[" + className + "]-*[<<interface>>;" + collectionType+"],";
                	}
                	continue;
                }
                if(variableAccessModifier.equals("-") || variableAccessModifier.equals("+"))
                {
                	if(nextVariable)
                	    variableString += ";";
                    variableString += variableAccessModifier + variableName +":"+variableType;
                    nextVariable = true;
                }
            }   
	    }
		return variableString;
	}
}