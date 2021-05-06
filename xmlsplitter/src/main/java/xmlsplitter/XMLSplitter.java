package xmlsplitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XMLSplitter {
	
	public static void main(String args[]) throws IOException, DocumentException
	{
		
		XMLSplitter xmlSplitter=new XMLSplitter();
		xmlSplitter.splitXMLFile();
	}


public void splitXMLFile() throws IOException, DocumentException
{
	Properties configurationDetails=getConfigurationDetails();
	
	String inputFileDir=configurationDetails.getProperty("config.inputFilePath");
	String inputFileName=configurationDetails.getProperty("config.inputFileName");
	String splitConditionXPATH=configurationDetails.getProperty("config.splitConditionXPATH");
	String outputFileDir=configurationDetails.getProperty("config.outputFileDirectory");
	String outputFileName=configurationDetails.getProperty("config.outputFileName");
	
	File inputFile = new File(inputFileDir+"\\"+inputFileName);
    SAXReader reader = new SAXReader();
    Document document = reader.read( inputFile );

    System.out.println("Root element :" + document.getRootElement().getName());
    List<Node> nodes = document.selectNodes(splitConditionXPATH);
    System.out.println("----------------------------");
    System.out.println("No of node with xpath "+splitConditionXPATH+" is the file "+nodes.size());
    System.out.println("----------------------------");
    System.out.print("Enter Nodes per file:");
    Scanner sc=new Scanner(System.in);
    int nodesPerFile=Integer.parseInt(sc.nextLine());
    System.out.println("Nodes per file "+nodesPerFile);
    int counter=0;
    int fileCounter=1;
    String SplitFileName=null;
    List<Node> splitNodeList=new ArrayList<Node>();
    int remaningNodes=nodes.size();
   
    
    for(int i=0;i<nodes.size();i++)
    {
    	counter++;
    	splitNodeList.add(nodes.get(i));
    	
    	if(counter>=nodesPerFile)
    	{
    		SplitFileName=outputFileName.replaceAll("_XX", "_"+String.valueOf(fileCounter));
    		File createdSplitFile=createOutputFile(SplitFileName,outputFileDir);
    		addSplitNodesToNewFile(splitNodeList, document.getRootElement(), createdSplitFile);
    		remaningNodes=remaningNodes-counter;
    		splitNodeList=null;
    		splitNodeList=new ArrayList<Node>();
    		fileCounter++;
    		counter=0;
    	}
    	else if(i==nodes.size()-1 && remaningNodes <= nodesPerFile)
    	{
    		SplitFileName=outputFileName.replaceAll("_XX", "_"+String.valueOf(fileCounter));
    		File createdSplitFile=createOutputFile(SplitFileName,outputFileDir);
    		addSplitNodesToNewFile(splitNodeList, document.getRootElement(), createdSplitFile);
    		remaningNodes=remaningNodes-counter;
    		splitNodeList=null;
    		splitNodeList=new ArrayList<Node>();
    		fileCounter++;
    		counter=0;
    	}
    	
    }
}

public Properties getConfigurationDetails() throws IOException
{
	InputStream reader=XMLSplitter.class.getClassLoader().getResourceAsStream("configurations.properties");
			//new FileReader("configurations.properties");
	
	String path = "./resources/configurations.properties";
	FileInputStream file=new FileInputStream(path);;

	Properties configurationDetails=new Properties();
	configurationDetails.load(file);
	return configurationDetails;
}

public File createOutputFile(String fileName,String outputDir) throws IOException
{
	System.out.println("Creating file with name : "+outputDir+"\\"+fileName);
	File file = new File(outputDir+"\\"+fileName);
	file.createNewFile();
	return file;	
}

public void addSplitNodesToNewFile(List<Node> nodeList,Node rootNode,File splitFileName) throws DocumentException, IOException
{
	SAXReader reader = new SAXReader();
    Document document = DocumentHelper.createDocument();
    Element rootElement=document.addElement(rootNode.getName());
    
    for(Node splitNode:nodeList)
    {
    	rootElement.add((Node)splitNode.clone());
    }
    
    Writer writer = new OutputStreamWriter(new FileOutputStream(splitFileName), "UTF-8");//Convert file output stream to writer object
    document.write(writer);
    writer.close();
	
}

}