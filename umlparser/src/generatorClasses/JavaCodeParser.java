package generatorClasses;

import java.io.File;
import java.io.FileInputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


public class JavaCodeParser {

	public static void main(String args[]) throws Exception
	{
		CompilationUnit compilationUnit = JavaParser.parse("class A { }");
		//ClassOrInterfaceDeclaration classA = compilationUnit.getClassByName("A");
		String basePath = new File("").getAbsolutePath();
		FileInputStream in = new FileInputStream(basePath+"/GenerateClassDiagram.java");

        // parse the file
        CompilationUnit cu = JavaParser.parse(in);
        System.out.println(cu.toString());
	}
}
