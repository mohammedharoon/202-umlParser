package generatorClasses;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class JavaCodeParser {
	
	private static String variables;
	private String methods;
	private String className;
    private String result;
    
	public static void main(String args[]) throws Exception
	{

		String basePath = "C:\\Users\\Haroon\\Desktop\\202-umlParser\\umlparser\\src\\generatorClasses";
		FileInputStream in = new FileInputStream(basePath+"/GenerateClassDiagram.java");
        // parse the file
        CompilationUnit compilationUnit = JavaParser.parse(in);
        JavaCodeParser javaCodeParser = new JavaCodeParser();
        javaCodeParser.parser(compilationUnit);
        
        //List<TypeDeclaration<?>> c1 = compilationUnit.getTypes();
        List<TypeDeclaration> c1 = compilationUnit.getTypes();
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
        Node node = c1.get(0);
        //BodyDeclaration bd = ((TypeDeclaration) node).getMembers();
        
        for (BodyDeclaration bd : ((TypeDeclaration)node).getMembers()) {
        	
            if (bd instanceof FieldDeclaration) {
            	//System.out.println("bd:"+bd);
                FieldDeclaration fd = ((FieldDeclaration) bd);
                //System.out.println(bd.toString());
                String variableScope = bd.toStringWithoutComments().substring(0,
                                bd.toStringWithoutComments().indexOf(" "));
                String variableType = fd.getType().toString();
                // getChildrenNodes returns [String, yUMLWebLink]
                String variableName = fd.getChildrenNodes().get(1).toString();
            }   
	    }
    }
	

	
	public String parser(CompilationUnit compUnit){
		className = getClassName(compUnit);
		variables = getVariableCompartment(compUnit);
		methods = getMethodCompartment(compUnit);
		result = "["+ className + "|" + variables + "|" + methods + "]";
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
	
	public String getMethodCompartment(CompilationUnit cu) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVariableCompartment(CompilationUnit cu) {
		// TODO Auto-generated method stub
		return null;
	}
}




