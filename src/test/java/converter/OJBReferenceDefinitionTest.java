package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OJBReferenceDefinitionTest {
	
	private String sourceCode = "/**\n" + 
			"     * @ojb.field column=\"user_id\"\n" + 
			"     *            jdbc-type=\"INTEGER\"\n" + 
			"     */\n" + 
			"    private long userIdentity;\n" + 
			"\n" + 
			"	/**\n" + 
			"     * @ojb.field column=\"name\"\n" + 
			"     *            length=\"50\"\n" + 
			"     *            jdbc-type=\"VARCHAR\"\n" + 
			"     */\n" + 
			"    private String name;\n" + 
			"\n" + 
			"    /**\n" + 
			"     * @ojb.field column=\"description\"\n" + 
			"     *            length=\"255\"\n" + 
			"     *            jdbc-type=\"VARCHAR\"\n" + 
			"     */\n" + 
			"    private String description;";
	
	@Test
	public void oneToOneAnnotationTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.reference     class-ref=\"com.aliquantum.objects.User\"\n" + 
				"     *                     foreignkey=\"userIdentity\"\n" + 
				"     *                      auto-update=\"false\"\n" + 
				"     *                      auto-retrieve=\"true\" proxy=\"true\"\n" + 
				"     */";
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition(doclet, sourceCode);
		
		assertEquals(doclet, reference.getDoclet());
		
		assertEquals(3, reference.getJPAImports().size());
		
		assertTrue(reference.getJPAImports().contains("import javax.persistence.OneToOne;"));
		assertTrue(reference.getJPAImports().contains("import javax.persistence.JoinColumn;"));
		assertTrue(reference.getJPAImports().contains("import javax.persistence.FetchType;"));
		
		String one2oneAnnotation = "@OneToOne(targetEntity = com.aliquantum.objects.User.class, fetch = FetchType.EAGER)";
		assertTrue(reference.getJPAAnnotations().contains(one2oneAnnotation));
		
		String joinColumnAnnotation = "@JoinColumn(name = \"user_id\")";
		assertTrue(reference.getJPAAnnotations().contains(joinColumnAnnotation));
		
		assertEquals(2, reference.getJPAAnnotations().size());
		
	}
	
	@Test
	public void oneToOneLazyAnnotationTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.reference     class-ref=\"com.aliquantum.objects.User\"\n" + 
				"     *                    foreignkey=\"userIdentity\"\n" + 
				"     *                    auto-update=\"false\"\n" +  
				"     */";
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition(doclet, sourceCode);
		
		assertEquals(doclet, reference.getDoclet());
		
		String one2oneAnnotation = "@OneToOne(targetEntity = com.aliquantum.objects.User.class, fetch = FetchType.LAZY)";
		assertTrue(reference.getJPAAnnotations().contains(one2oneAnnotation));
	}
	
	@Test
	public void oneToOneCascadeTypesAnnotationTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.reference     class-ref=\"com.aliquantum.objects.User\"\n" + 
				"     *                    foreignkey=\"userIdentity\"\n" + 
				"     *                    auto-update=\"true\"\n" +
				"     *                    auto-insert=\"true\"\n" +
				"     *                    auto-delete=\"true\"\n" +
				"     *                    auto-retrieve=\"true\"\n" +
				"     */";
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition(doclet, sourceCode);
		
		assertEquals(doclet, reference.getDoclet());
		
		String one2oneAnnotation = "@OneToOne(targetEntity = com.aliquantum.objects.User.class, fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })";
		assertTrue(reference.getJPAAnnotations().contains(one2oneAnnotation));
		
		assertTrue(reference.getJPAImports().contains("import javax.persistence.CascadeType;"));
		assertEquals(4, reference.getJPAImports().size());
	}
	
	@Test
	public void noDocletTest() throws Exception {
		
		String doclet = "// just a single line comment";
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition(doclet, "");
		
		assertTrue(reference.getJPAImports().isEmpty());
		assertTrue(reference.getJPAAnnotations().isEmpty());
		assertFalse(reference.isCandidateForConvertion());
		
	}

}
