package converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class Converter {

	private static final Logger logger = LoggerFactory.getLogger(Converter.class);
	
	public static void main(String[] args) {

		try {
			
			String sourceFolderPath = args[0];
			String pathToSave = args[1];

			OJB2JPA converter = new OJB2JPA(sourceFolderPath);

			converter.convert(pathToSave);
			converter.write();
			
			logger.info("Done.");
			
		} 
		catch (ArrayIndexOutOfBoundsException e) {
			String msg = "Ops, you need to pass <sourceFolderPath> and <pathToSave> as arguments.";
			logger.error(msg, e);
			System.out.println(msg);
		} 
		catch (Exception e) {

		}
		
		System.out.println("Check the logs for more information");

	}

}
