package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import converter.errors.OJB2JPAPathNotFoundException;

public class OJBJPATest {
	
	private final String basePath = System.getProperty("user.dir");
	private final String sourceFolderPath = basePath + "/src/test/java/converter/resources/classes";
	private final String pathToSave = "/tmp/ojb2jpa/convertedFiles";
	
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
	public void shouldGetAListOfJavaSourceFiles() throws OJB2JPAPathNotFoundException {
		
		OJB2JPA converter = new OJB2JPA(sourceFolderPath);
		
		List<?> listOfJavaSourceFiles = converter.listSourceFiles();
		assertTrue(listOfJavaSourceFiles.size() > 0);
		
	}
	
	@Test
	public void converterTest() throws Exception {
		
		OJB2JPA converter = new OJB2JPA(sourceFolderPath);
		
		converter.convert(pathToSave);
		assertEquals(1, converter.getJPAFiles().size());
		
		converter.write();
		
		for(ConvertedJPAFile jpaFile : converter.getJPAFiles()) {
			
			Path path = Paths.get(pathToSave, jpaFile.getClassName()+".java");
			
			assertTrue(path.toFile().isFile());
		}
	}
	
}