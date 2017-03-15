package generatorClasses;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

public class GenerateClassDiagram {
private String yUMLWebLink;
private String intermediateGrammar;
private String fullWebURL;

public GenerateClassDiagram(String uri, String intGrammar)
{
	this.yUMLWebLink = uri;
	this.intermediateGrammar = intGrammar;
}
public void generateDiagram()
{
	fullWebURL = yUMLWebLink + intermediateGrammar;
	try{
		URL url = new URL(fullWebURL);
		HttpURLConnection hcon = (HttpURLConnection) url.openConnection();
		hcon.setRequestMethod("GET");
		String outputFilePath = "C:\\Users\\Haroon\\Desktop\\202-umlParser\\outputFiles\\classdiag.png";
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
public static void main(String args[])
{
	String sampleGrammar = "[C1|+test(a1:A1)]uses -.->[<<interface>>;A1],[<<interface>>;A1]^-.-[B1],[<<interface>>;A1]^-.-[B2],[P]^-[B1],[P]^-[B2],[<<interface>>;A2]^-.-[B2],[C2|+test(a2:A2)]uses -.->[<<interface>>;A2]";
	String link = "https://yuml.me/diagram/scruffy/class/";
    GenerateClassDiagram gen  = new GenerateClassDiagram(link,sampleGrammar);
    gen.generateDiagram();
}
}
