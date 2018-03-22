package fr.leborgne.converter.jsonldconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import fr.leborgne.converter.jsonldconverter.parser.MyParser;

/**
 * App
 *
 */
public class App 
{
	private static final Logger logger = Logger.getLogger("Converter");
	public static void main( String[] args )
	{
		
		try {
			if(args.length > 0)
				for(String arg : args)
				{
					convert(arg);
				}
			else
				logger.error("Tu devrais penser à y mettre un fichier... MERCI!!!");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			logger.fatal(e.getMessage(), e);
		}
	}
	private static void convert(String arg) throws FileNotFoundException, UnsupportedEncodingException {
		File tempFile = new File(arg);
		if(tempFile.isDirectory())
		{
			for(String path : tempFile.list())
			{
				if(Paths.get(path).toString().endsWith(".owl"))
					parse(arg+"/"+Paths.get(path).toString());
			}
		}
		else
		{
			parse(arg);
		}
	}
	private static void parse(String arg) throws FileNotFoundException, UnsupportedEncodingException {
		InputStream input = new FileInputStream(arg);
		MyParser parser = new MyParser(input);

		PrintWriter writer = new PrintWriter(arg.replace(".owl", ".tsv"), "UTF-8");
		writer.print(parser.parse());
		writer.close();

		if(logger.isInfoEnabled())
			logger.info("arg");
	}
}
