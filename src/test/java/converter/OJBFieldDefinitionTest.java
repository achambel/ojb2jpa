package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class OJBFieldDefinitionTest {
	
	private final String basePath = System.getProperty("user.dir");
	
	@Test
	public void columnAnnotationWithNameTest() {
		
		String doclet = "/**\n" + 
				"     * @ojb.field column=\"document\"\n" + 
				"     *            jdbc-type=\"BLOB\"\n" + 
				"     */";
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet, "", null, "");
		
		assertEquals(doclet, field.getDoclet());
		
		assertEquals(1, field.getJPAImports().size());
		
		assertTrue(field.getJPAImports().contains("import javax.persistence.Column;"));
		
		assertEquals(1, field.getJPAAnnotations().size());
		
		assertTrue(field.getJPAAnnotations().contains("@Column(name = \"document\")"));
		
		assertTrue(field.isCandidateForConvertion());
	}
	
	@Test
	public void columnAnnotationWithNameAndLengthTest() {
		
		String doclet = "/**\n" + 
				"     * @ojb.field column=\"document_type\"\n" + 
				"     *            length=\"50\"\n" + 
				"     *            jdbc-type=\"VARCHAR\"\n" + 
				"     */";
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet, "", null, "");
		assertEquals(doclet, field.getDoclet());
		
		assertTrue(field.getJPAAnnotations().contains("@Column(name = \"document_type\", length = 50)"));
	}
	
	@Test
	public void noDocletTest() {
		
		String doclet = "// just a single line comment";
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet, "", null, "");
		
		assertTrue(field.getJPAImports().isEmpty());
		assertTrue(field.getJPAAnnotations().isEmpty());
		assertFalse(field.isCandidateForConvertion());
		
	}
	
	@Test
	public void columnAnnotationNoInsertableOrUpdatable() throws IOException {
		
		Path sourceFilePath = Paths.get(basePath, "/src/test/java/converter/resources/classes/AccountNote.java");
		String fileContent = new String(Files.readAllBytes(sourceFilePath));
		
		String fieldName = "accountIdentity";
		String doclet = OJB2JPA.findDoclet(fieldName, fileContent);
		
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet, fileContent, sourceFilePath, fieldName);
		
		assertEquals(doclet, field.getDoclet());
		
		String columnAnnotation = "@Column(name = \"ACCOUNT_ID\", insertable = false, updatable = false)";
		assertTrue(field.jpaAnnotations.contains(columnAnnotation));
	}
	
	@Test
	public void columnAnnotation() throws IOException {
		
		Path sourceFilePath = Paths.get(basePath, "/src/test/java/converter/resources/classes/AccountNote.java");
		String fileContent = new String(Files.readAllBytes(sourceFilePath));
		
		String fieldName = "activated";
		String doclet = OJB2JPA.findDoclet(fieldName, fileContent);
		
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet, fileContent, sourceFilePath, fieldName);
		
		assertEquals(doclet, field.getDoclet());
		
		String columnAnnotation = "@Column(name = \"activated\")";
		assertTrue(field.jpaAnnotations.contains(columnAnnotation));
	}

}
