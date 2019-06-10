package converter;

import static org.junit.Assert.assertEquals;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

		assertEquals(9, jpaFile.getImports().size());

		assertEquals("package converter.resources.classes;", jpaFile.getPackageName());

		assertEquals("HelloWorld", jpaFile.getClassName());

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

}
