package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import converter.errors.OJB2JPAPathNotFoundException;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class OJBJPATest {
	
	private final String basePath = System.getProperty("user.dir");
	private final String sourceFolderPath = basePath + "/src/test/java/converter/resources/classes";
	private final String pathToSave = System.getProperty("java.io.tmpdir") + "/ojb2jpa/convertedFiles";
	
	@Test
	public void sourceAndByteCodeFolderShouldExist() {
		
		File sourceFolder = new File(sourceFolderPath);
		assertTrue(sourceFolder.isDirectory());
		
	}
	
	@Test(expected = OJB2JPAPathNotFoundException.class)
	public void shouldThrowOJB2JPAPathNotFoundExceptionTest() throws OJB2JPAPathNotFoundException {
		
		new OJB2JPA("path/not/found");
		
	}
	
	@Test
	public void converterTest() throws Exception {
		
		OJB2JPA converter = new OJB2JPA(sourceFolderPath);
		
		converter.convert(pathToSave);
		assertEquals(4, converter.getJPAFiles().size());
		
		converter.write();
		
		for(ConvertedJPAFile jpaFile : converter.getJPAFiles()) {
			
			Path path = Paths.get(pathToSave, jpaFile.getClassName()+".java");
			
			if (!jpaFile.isSkippedClass()) {
				assertTrue(path.toFile().isFile());
			}
		}
	}
	
}