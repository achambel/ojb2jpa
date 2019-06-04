package converter;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class ConvertedJPAFileTest {

	private final String basePath = System.getProperty("user.dir");
	private final String sourceFilePath = basePath + "/src/test/java/converter/resources/classes/HelloWorld.java";
	String pathToSave = "/tmp/ojb2jpa/convertedFiles";
	
	@Test
	public void convertedJPAFileTest() throws Exception {
		
		ConvertedJPAFile jpaFile = new ConvertedJPAFile(Paths.get(sourceFilePath), pathToSave);
		
		assertEquals(12, jpaFile.getImports().size());
		
		assertEquals("converter.resources.classes", jpaFile.getPackageName());
		
		assertEquals("John Doe", jpaFile.getAuthor());
		
		assertEquals("1.0", jpaFile.getVersion());
		
		assertEquals("tb_hello_world", jpaFile.getTableName());
		
		assertEquals("HelloWorld", jpaFile.getClassName());
		
		List<String> fieldAnnotations = jpaFile.getOJBFields()
			   .stream()
			   .map(FieldDefinition::getAnnotations)
			   .flatMap(Collection::stream)
			   .collect(Collectors.toList());
		
		assertEquals(3, fieldAnnotations.size());
			   
		
		assertEquals(14, jpaFile.getOJBFields().size());
		
		List<String> methodAnnotations = jpaFile.getOJBMethods()
				   .stream()
				   .map(MethodDefinition::getAnnotations)
				   .flatMap(Collection::stream)
				   .collect(Collectors.toList());
			
		assertEquals(1, methodAnnotations.size());
		
		assertEquals(9, jpaFile.getOJBMethods().size());
		
		System.out.println(jpaFile.printConvertedClass());
		
		
	}
}
