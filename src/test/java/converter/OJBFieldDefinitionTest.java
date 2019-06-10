package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class OJBFieldDefinitionTest {
	
	@Test
	public void columnAnnotationWithNameTest() {
		
		String doclet = "/**\n" + 
				"     * @ojb.field column=\"document\"\n" + 
				"     *            jdbc-type=\"BLOB\"\n" + 
				"     */";
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet);
		
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
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet);
		assertEquals(doclet, field.getDoclet());
		
		assertTrue(field.getJPAAnnotations().contains("@Column(name = \"document_type\", length = 50)"));
	}
	
	@Test
	public void noDocletTest() {
		
		String doclet = "// just a single line comment";
		
		OJBFieldDefinition field = new OJBFieldDefinition(doclet);
		
		assertTrue(field.getJPAImports().isEmpty());
		assertTrue(field.getJPAAnnotations().isEmpty());
		assertFalse(field.isCandidateForConvertion());
		
	}

}
