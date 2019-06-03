package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OJBFieldDefinitionTest {
	
	@Test
	public void columnAnnotationWithNameTest() {
		
		String doclet = "/**\n" + 
				"     * @ojb.field column=\"document\"\n" + 
				"     *            jdbc-type=\"BLOB\"\n" + 
				"     */";
		
		OJBFieldDefinition field = new OJBFieldDefinition();
		field.parse2JPA(doclet);
		
		assertEquals(doclet, field.getDoclet());
		
		assertEquals(1, field.getImports().size());
		
		assertTrue(field.getImports().contains("import javax.persistence.Column;"));
		
		assertEquals(1, field.getJpaAnnotations().size());
		
		assertEquals("@Column(name = \"document\")", field.getJpaAnnotations().get(0));
	}
	
	@Test
	public void columnAnnotationWithNameAndLengthTest() {
		
		String doclet = "/**\n" + 
				"     * @ojb.field column=\"document_type\"\n" + 
				"     *            length=\"50\"\n" + 
				"     *            jdbc-type=\"VARCHAR\"\n" + 
				"     */";
		
		OJBFieldDefinition field = new OJBFieldDefinition();
		field.parse2JPA(doclet);
		
		assertEquals(doclet, field.getDoclet());
		
		assertEquals("@Column(name = \"document_type\", length = 50)", field.getJpaAnnotations().get(0));
	}
	
	@Test
	public void noDocletTest() {
		
		String doclet = "// just a single line comment";
		
		OJBFieldDefinition field = new OJBFieldDefinition();
		field.parse2JPA(doclet);
		
		assertTrue(field.getImports().isEmpty());
		assertTrue(field.getJpaAnnotations().isEmpty());
		
	}

}
