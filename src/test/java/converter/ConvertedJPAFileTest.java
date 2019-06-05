package converter;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class ConvertedJPAFileTest {

	private final String basePath = System.getProperty("user.dir");
	private final String sourceFilePath = basePath + "/src/test/java/converter/resources/classes/BrandContentItem.java";
	String pathToSave = "/tmp/ojb2jpa/convertedFiles";
	
	
	public void convertedJPAFileTest() throws Exception {
		
		ConvertedJPAFile jpaFile = new ConvertedJPAFile(Paths.get(sourceFilePath), pathToSave);
		
//		assertEquals(13, jpaFile.getImports().size());
//		
//		assertEquals("converter.resources.classes", jpaFile.getPackageName());
//		
//		assertEquals("John Doe", jpaFile.getAuthor());
//		
//		assertEquals("1.0", jpaFile.getVersion());
//		
//		assertEquals("tb_hello_world", jpaFile.getTableName());
//		
//		assertEquals("HelloWorld", jpaFile.getClassName());
//		
//		List<String> fieldAnnotations = jpaFile.getOJBFields()
//			   .stream()
//			   .map(FieldDefinition::getAnnotations)
//			   .flatMap(Collection::stream)
//			   .collect(Collectors.toList());
//		
//		assertEquals(3, fieldAnnotations.size());
//			   
//		
//		assertEquals(14, jpaFile.getOJBFields().size());
//		
//		List<String> methodAnnotations = jpaFile.getOJBMethods()
//				   .stream()
//				   .map(MethodDefinition::getAnnotations)
//				   .flatMap(Collection::stream)
//				   .collect(Collectors.toList());
//			
//		assertEquals(1, methodAnnotations.size());
//		
//		assertEquals(9, jpaFile.getOJBMethods().size());
		
		System.out.println(jpaFile.printConvertedClass());
		
		
	}
	
	@Test
	public void readAllJavaFilesTest() throws Exception {
		
		String sourceFolder = "/home/adriano/git/aq-gameserver/src-common/com/aliquantum/objects";
		
		DirectoryStream<Path> directoryContent = Files.newDirectoryStream(Paths.get(sourceFolder),
				path -> path.toString().toLowerCase().endsWith(".java"));
		
		for (Path path : directoryContent) {
			ConvertedJPAFile jpaFile = new ConvertedJPAFile(path, pathToSave);
			System.out.println(jpaFile.printConvertedClass());
		}
		
		
	}
	
}
