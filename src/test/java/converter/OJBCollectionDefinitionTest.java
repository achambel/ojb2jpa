package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class OJBCollectionDefinitionTest {
	
	private final String basePath = System.getProperty("user.dir");
	
	@Test
	public void oneToManyAnnotationTest() throws Exception {
		
		Path sourceFilePath = Paths.get(basePath, "/src/test/java/converter/resources/classes/Account.java");
		String fileContent = new String(Files.readAllBytes(sourceFilePath));
		
		String doclet = OJB2JPA.findDoclet("notes", fileContent);
		
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet, fileContent, sourceFilePath);
		
		assertEquals(doclet, collection.getDoclet());
		
		assertEquals(5, collection.getJPAImports().size());
		
		assertTrue(collection.getJPAImports().contains("import javax.persistence.OneToMany;"));
		assertTrue(collection.getJPAImports().contains("import javax.persistence.JoinColumn;"));
		assertTrue(collection.getJPAImports().contains("import javax.persistence.CascadeType;"));
		assertTrue(collection.getJPAImports().contains("import javax.persistence.FetchType;"));
		assertTrue(collection.getJPAImports().contains("import javax.persistence.OrderBy;"));
		
		String one2many = "@OneToMany(targetEntity = com.aliquantum.objects.AccountNote.class, fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })";
		assertTrue(collection.getJPAAnnotations().contains(one2many));
		
		String orderBy = "@OrderBy(\"noteTimestamp DESC\")";
		assertTrue(collection.getJPAAnnotations().contains(orderBy));
		
		String joinColumn = "@JoinColumn(name = \"ACCOUNT_ID\")";
		assertTrue(collection.getJPAAnnotations().contains(joinColumn));
		
		assertEquals(3, collection.getJPAAnnotations().size());
		assertTrue(collection.isCandidateForConvertion());
		
	}
	
	@Test
	public void oneToManyWithEagerFetchTest() throws Exception {
		
		Path sourceFilePath = Paths.get(basePath, "/src/test/java/converter/resources/classes/Account.java");
		String fileContent = new String(Files.readAllBytes(sourceFilePath));
		
		String doclet = OJB2JPA.findDoclet("notes", fileContent);
		
		doclet = doclet.replaceAll("proxy=\"true\"", "proxy=\"false\"");
		
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet, fileContent, sourceFilePath);
		
		String annotation = "@OneToMany(targetEntity = com.aliquantum.objects.AccountNote.class, fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })";
		assertTrue(collection.getJPAAnnotations().contains(annotation));
		
	}
	
	@Test
	public void noDocletTest() throws Exception {
		
		Path sourceFilePath = Paths.get(basePath, "/src/test/java/converter/resources/classes/Account.java");
		String fileContent = new String(Files.readAllBytes(sourceFilePath));
		
		String doclet = "// just a single line comment";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet, fileContent, sourceFilePath);
		
		assertTrue(collection.getJPAImports().isEmpty());
		assertTrue(collection.getJPAAnnotations().isEmpty());
		assertFalse(collection.isCandidateForConvertion());
		
	}

}
