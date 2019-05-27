package converter;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

public class ConvertedJPAFileTest {

	private final String basePath = System.getProperty("user.dir");
	private final String sourceFilePath = basePath + "/src/test/java/converter/resources/classes/HelloWorld.java";
	String pathToSave = "/tmp/ojb2jpa/convertedFiles";
	
	@Test
	public void convertedJPAFileTest() throws ClassNotFoundException {
		
		ConvertedJPAFile jpaFile = new ConvertedJPAFile(Paths.get(sourceFilePath), pathToSave);
		
		assertEquals("converter.resources.classes", jpaFile.getPackageName());
		
		assertEquals("HelloWorld", jpaFile.getClassName());
		
		assertEquals(2, jpaFile.getOJBFields().size());
		
		assertEquals(5, jpaFile.getOJBMethods().size());
		
	}
}
