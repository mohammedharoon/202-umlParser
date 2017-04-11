package generatorClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class MyJavaCodeParser {
	
	private static String variables;
	private String methods;
	private String className;
	private String relationships = "";
    private String resultantIntermediateString;
    private HashMap<String,String> classInterfaceMap;
    private String yUMLWebLink;
    private String fullWebURL;
    
    public MyJavaCodeParser()
    {
    	this.yUMLWebLink = "https://yuml.me/diagram/scruffy/class/";

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
    
	public static void main(String args[]) throws Exception
	{
      
		String basePath = "C:\\Users\\Haroon\\Desktop\\202-umlParser\\ClassDiagramsTestCases\\class-diagram-test-1";
		ArrayList<CompilationUnit> compilationUnits;
        MyJavaCodeParser myJavaCodeParser = new MyJavaCodeParser();

        //Read the folder and create compilationUnits for the java files
        compilationUnits = myJavaCodeParser.compileTestFolder(basePath);
        myJavaCodeParser.createClassInterfaceMap(compilationUnits);
        //Parse the compilationUnits
        //myJavaCodeParser.printClassInterfaceMap();
        String result = myJavaCodeParser.parser(compilationUnits);
        System.out.println(result);
        myJavaCodeParser.generateDiagram("test2");
		//String basePath = "C:\\Users\\Haroon\\Desktop\\202-umlParser\\umlparser\\src\\generatorClasses";
		//FileInputStream in = new FileInputStream(basePath+"/GenerateClassDiagram.java");        
        //List<TypeDeclaration<?>> c1 = compilationUnit.getTypes();
        //List<TypeDeclaration> c1 = compilationUnit.getTypes();
        //System.out.println(c1.size());
        /*
        for(Node n: c1)
        {		ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration)n;
                if(coid.isInterface())
                	System.out.println("yes");
                else
                	System.out.println("No");
                System.out.println(coid.getMembers());
        	//n.get(0);
        }
        */
        //TypeDeclaration<?> node = c1.get(0);
        
        
        //BodyDeclaration bd = ((TypeDeclaration) node).getMembers();
    }
	
    private void printClassInterfaceMap() {
        System.out.println("Map:");
        Set<String> keys = classInterfaceMap.keySet(); // get all keys
        for (String i : keys) {
            System.out.println(i + "->" + classInterfaceMap.get(i));
        }
        System.out.println("---");
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
	
	public String parser(ArrayList<CompilationUnit> compUnits){
		
        
		for(CompilationUnit compUnit : compUnits)
		{
		List<TypeDeclaration> c1 = compUnit.getTypes();
		Node node = c1.get(0);
		System.out.println("comp Unit ->"+compUnit);
		String classNameString = getClassName(compUnit);
		String variablesString = getVariableCompartment(node);
		//String methodsString = getMethodCompartment(node);
		String methodsString = "";
		resultantIntermediateString += getResultString(classNameString,variablesString,methodsString);
		}
		resultantIntermediateString += relationships;
		return resultantIntermediateString;
	}
	
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
	
	private String getMethodCompartment(Node node) {
		// TODO Auto-generated method stub
		boolean nextMethod = false;
		String methodString = "";
		ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) node;
		String className = coid.getName();
        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
            // Get public constructors
        	System.out.println("bd ->" + bd);
            if (bd instanceof ConstructorDeclaration) {
                ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
                if (cd.getDeclarationAsString().startsWith("public")) {
                    if (nextMethod)
                    	methodString += ";";
                    methodString += "+ " + cd.getName() + "(";
                    System.out.println("getChildrenNodes ->"+cd.getChildrenNodes());
                    for (Object childNodeObj : cd.getChildrenNodes()) 
                    {
                    	
                        if (childNodeObj instanceof Parameter) 
                        {
                            Parameter parameter = (Parameter) childNodeObj;
                            String parameterType = parameter.getType().toString();
                            System.out.println("par type: " + parameterType);
                            String parameterName = parameter.getChildrenNodes()
                                    .get(0).toString();
                            System.out.println(parameterName);
                            methodString += parameterName + " : " + parameterType;
                            if (classInterfaceMap.containsKey(parameterType)) 
                            {
                            	relationships += "[" + className + "] uses -.->";
                                if (classInterfaceMap.get(parameterType).equals("interface"))
                                	relationships += "[<<interface>>;" + parameterType + "]";
                                else
                                	relationships += "[" + parameterType + "]";
                                relationships += ",";
                            }
                            //relationships += ",";
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
                if (md.getDeclarationAsString().startsWith("public")
                        && !coid.isInterface()) {

                        if (nextMethod)
                        	methodString += ";";
                        methodString += "+ " + md.getName() + "(";
                        for (Object childNodeObj : md.getChildrenNodes()) {
                            if (childNodeObj instanceof Parameter) {
                                Parameter parameter = (Parameter) childNodeObj;
                                String parameterType = parameter.getType()
                                        .toString();
                                String parameterName = parameter.getChildrenNodes()
                                        .get(0).toString();
                                methodString += parameterName + " : " + parameterType;
                                if (classInterfaceMap.containsKey(parameterType)) {
                                	relationships += "[" + className
                                            + "]uses -.->";
                                    if (classInterfaceMap.get(parameterType).equals("interface"))
                                    	relationships += "[<<interface>>;"
                                                + parameterType + "]";
                                    else
                                    	relationships += "[" + parameterType + "]";
                                }
                                relationships += ",";
                            }
                        }
                        methodString += ") : " + md.getType();
                        nextMethod = true;
                }
            }// if bd is type of method
        }// for loop of body dec
		return methodString;
	}

	private String getVariableCompartment(Node node) {
		// TODO Auto-generated method stub
		boolean nextVariable = false;
		String variableString = "";
		ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) node;
		String className = coid.getName();
        for (BodyDeclaration bd : ((TypeDeclaration)node).getMembers()) {
        	
            if (bd instanceof FieldDeclaration) {

            	//System.out.println("bd:"+bd);
                FieldDeclaration fd = ((FieldDeclaration) bd);
                System.out.println("bd ->"+bd.toString());
                System.out.println(fd.getChildrenNodes());
                //[String, message = "hello"]
                String variableAccessModifier = bd.toStringWithoutComments().substring(0,
                                bd.toStringWithoutComments().indexOf(" "));
                variableAccessModifier = convertAccessModifiedToSymbol(variableAccessModifier);
                //System.out.println(variableAccessModifier);
                String variableType = fd.getType().toString();
                // getChildrenNodes returns [String, yUMLWebLink]
                //System.out.println(variableType);
                String variableName = fd.getChildrenNodes().get(1).toString();
                if(variableType.contains("["))
                {
                	variableType = variableType.replace("[", "(");
                	variableType = variableType.replace("]", ")");
                }
                if(variableAccessModifier.equals("-") || variableAccessModifier.equals("+"))
                {
                	if(nextVariable)
                	    variableString += ";";
                    variableString += variableAccessModifier + variableName +":"+variableType;
                    nextVariable = true;
                }
                // for uses relationship
                if(classInterfaceMap.containsKey(variableType))
                {
                	if(classInterfaceMap.get(variableType).equals("class"))
                	    relationships += "[" + className + "]-" + variableType;
                }
                if(variableType.contains("<"))
                {
                	String collectionType = variableType.substring(variableType.indexOf("<")+1, variableType.indexOf(">"));
                	if(classInterfaceMap.containsKey(collectionType))
                		System.out.println(classInterfaceMap.get(collectionType));
                	System.out.println(collectionType);
                }
                
            }   
	    }
		return variableString;
	}
}




