package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class ConvertedJPAFileTest {

	private final String basePath = System.getProperty("user.dir");
	private final String sourceFilePath = basePath + "/src/test/java/converter/resources/classes/HelloWorld.java";
	String pathToSave = System.getProperty("java.io.tmpdir") + "/ojb2jpa/convertedFiles";
	
	@Test
	public void convertedJPAFileTest() throws Exception {

		ConvertedJPAFile jpaFile = new ConvertedJPAFile(Paths.get(sourceFilePath), pathToSave);

		assertEquals(10, jpaFile.getImports().size());

		assertEquals("package converter.resources.classes;", jpaFile.getPackageName());

		assertEquals("HelloWorld", jpaFile.getClassName());
		
		assertTrue(jpaFile.getImports().contains("import javax.persistence.Transient;"));
		
		String regex = "@Transient";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(jpaFile.printConvertedClass());
		
		int count = 0;
		while(matcher.find()) {
			count++;
		}
		
		assertEquals(4, count);
		
	}

	@Test
	public void readAllJavaFilesTest() throws Exception {

		String sourceFolder = "/home/adriano/git/aq-gameserver/src-common/com/aliquantum/objects";

		DirectoryStream<Path> directoryContent = Files.newDirectoryStream(Paths.get(sourceFolder),
				path -> path.toString().toLowerCase().endsWith(".java"));

		for (Path path : directoryContent) {
			ConvertedJPAFile jpaFile = new ConvertedJPAFile(path, pathToSave);
			
			Path destPath = Paths.get(jpaFile.getPathToSave(), jpaFile.getClassName()+".java");
			
			assertEquals(path.getFileName(), destPath.getFileName());
			
		}

	}
	
	@Test
	public void primitiveToWrapperTest() throws Exception {
		
		Path sourceFilePath = Paths.get(basePath, "/src/test/java/converter/resources/classes/AccountNote.java");
		
		ConvertedJPAFile jpaFile = new ConvertedJPAFile(sourceFilePath, pathToSave);
		
		System.out.println(jpaFile.printConvertedClass());
		
		Pattern pattern = Pattern.compile("private Long accountIdentity;");
		Matcher matcher = pattern.matcher(jpaFile.printConvertedClass());
		
		int count = 0;
		while(matcher.find()) {
			count++;
		}
		
		assertEquals(1, count);
		
	}

}
