# 202-umlParser
This is a parser which converts Java source code into UML diagram.
It uses JavaParser for parsing the source code and converts it into
Abstract Syntax tree(intermediate code) and yUML tool to convert the
intermediate code into UML Class Diagram.

Libraries and tools used for creating parser

1.	Javaparser – Javaparser is a collection of tools to analyze and parse your java code and create an Abstract Syntax Tree (AST). This AST is a simple representation of the java code and can be used to generate UML diagram with available UML Diagram generation tools like yUML, plantuml etc.
Using the javaparser we can parse the java code file by file. It creates a CompilationUnit for each file. Each CompilationUnit can have one or more classes or interface. This unit is again composed of ClassOrInterfaceDeclaration, BodyDeclaration, ConstructorDeclaration, MethodDeclaration, FieldDeclaration etc which corresponds to the class or interface name, the body of the class, the constructor of the class, the methods of the class, the attributes of the class respectively. The MethodDeclaration and Constructor declaration in-turns consists of parameters, parameter types, return type (not for constructor) and body. Using the above units, we can generate an AST of our java code. This AST can be fed to yUML which will generate the class diagram for the code.

2.	yUML – The yUML is a tool for generating UML diagrams like class diagram, activity diagram and use case diagram. It takes as input the AST which can be generated from parsing tools and creates the required UML diagram. The requisite for yUML is an active internet connection so that we can connect to the yUML service and leverage their library to generate the UML diagrams.

3.	AspectJ – AspectJ is an extension for java programming language which provides an aspect oriented programming capability to our java programs. It consists of terminologies like join point, point cut, aspect, advise and introduction. AspectJ is used to do runtime analysis of the java program inorder to generate the sequence diagram. Using this we can obtain the participants involved in the code and the messages that are sent between them as part of the function calls.

4.	Plantuml – Plantuml is a tool for generating UML diagrams. Using this tool, we can generate variety of diagrams like class, sequence, activity, use case etc. The dynamic analysis done by the aspectj will generate the messages and participants which will be fed as input to the plantuml for generating sequence diagram.
