package converter;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Paths;

import org.junit.Test;

public class ConvertedJPAFileTest {

	private final String basePath = System.getProperty("user.dir");
	private final String sourceFilePath = basePath + "/src/test/java/converter/resources/classes/HelloWorld.java";
	String pathToSave = "/tmp/ojb2jpa/convertedFiles";
	
	@Test
	public void convertedJPAFileTest() throws Exception {
		
		ConvertedJPAFile jpaFile = new ConvertedJPAFile(Paths.get(sourceFilePath), pathToSave);
		
		assertEquals(4, jpaFile.getImports().size());
		
		assertEquals("converter.resources.classes", jpaFile.getPackageName());
		
		assertEquals("HelloWorld", jpaFile.getClassName());
		
		assertEquals(11, jpaFile.getOJBFields().size());
		
		assertEquals(7, jpaFile.getOJBMethods().size());
		
		
		for (FieldDefinition fieldDefinition : jpaFile.getOJBFields()) {
			
			Field field = fieldDefinition.getField();
			
			System.out.println("Getting field definition for " + field.getName());
			System.out.println(fieldDefinition.getRawCode());
			
			for (Annotation annotation : field.getDeclaredAnnotations()) {
				
				System.out.println("Annotation name: " + annotation.annotationType().getSimpleName());
				
			}
		}
		
		
	}
}
