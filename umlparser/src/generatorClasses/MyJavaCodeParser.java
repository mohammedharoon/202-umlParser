package generatorClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class MyJavaCodeParser {
	
	private static String variables;
	private String methods;
	private String className;
    private String resultantIntermediateString;
    private HashMap<String,String> classInterfaceMap;
	public static void main(String args[]) throws Exception
	{
      
		String basePath = "C:\\Users\\Haroon\\Desktop\\202-umlParser\\ClassDiagramsTestCases\\class-diagram-test-1";
		ArrayList<CompilationUnit> compilationUnits;
        MyJavaCodeParser myJavaCodeParser = new MyJavaCodeParser();

        //Read the folder and create compilationUnits for the java files
        compilationUnits = myJavaCodeParser.compileTestFolder(basePath);
        myJavaCodeParser.createClassInterfaceMap(compilationUnits);
        //Parse the compilationUnits
        String result = myJavaCodeParser.parser(compilationUnits);
        System.out.println(result);
        
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
		if(varAccessModifier == "private")
			return "-";
		else if(varAccessModifier == "protected")
			return "#";
		else if(varAccessModifier == "public")
			return "+";
		else
			return "+";
		
	}
	
	public String parser(ArrayList<CompilationUnit> compUnits){
		
        
		for(CompilationUnit compUnit : compUnits)
		{
		List<TypeDeclaration> c1 = compUnit.getTypes();
		Node node = c1.get(0);
		String classNameString = getClassName(compUnit);
		String variablesString = getVariableCompartment(node);
		String methodsString = getMethodCompartment(node);
		resultantIntermediateString += getResultString(classNameString,variablesString,methodsString);
		}
		return resultantIntermediateString;
	}
	
    public String getResultString(String classNames, String variablesString, String methodsString)
    {
    	String result;
    	result = "["+ classNames + "|" + variablesString + "|" + methodsString + "]";
    	return result;
    }
    
	public String getClassName(CompilationUnit cu){
		List<TypeDeclaration> c1 = cu.getTypes();
		for(Node codeBlock : c1)
		{
			ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration) codeBlock;
			if(coid.isInterface())
				return "<<interface>>";
			else
				return coid.getName();
		}
		
		return "";
	}
	
	public String getMethodCompartment(Node node) {
		// TODO Auto-generated method stub

		return null;
	}

	public String getVariableCompartment(Node node) {
		// TODO Auto-generated method stub
		
        for (BodyDeclaration bd : ((TypeDeclaration)node).getMembers()) {
        	
            if (bd instanceof FieldDeclaration) {
            	//System.out.println("bd:"+bd);
                FieldDeclaration fd = ((FieldDeclaration) bd);
                //System.out.println(bd.toString());
                String variableAccessModifier = bd.toStringWithoutComments().substring(0,
                                bd.toStringWithoutComments().indexOf(" "));
                variableAccessModifier = convertAccessModifiedToSymbol(variableAccessModifier);
                System.out.println(variableAccessModifier);
                String variableType = fd.getType().toString();
                // getChildrenNodes returns [String, yUMLWebLink]
                System.out.println(variableType);
                String variableName = fd.getChildrenNodes().get(1).toString();
                System.out.println(variableName);
                
                boolean dependencyExist = false;
                if(variableName.contains("<"))
                {
                	String dependency = variableName.substring(variableName.indexOf("<")+1, variableName.indexOf(""));
                	dependencyExist = true;
                }
            }   
	    }
		return null;
	}
}




