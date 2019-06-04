package converter;

import static org.junit.Assert.assertEquals;
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
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition(sourceCode);
		reference.parse2JPA(doclet);
		
		assertEquals(doclet, reference.getDoclet());
		
		assertEquals(3, reference.getImports().size());
		
		assertTrue(reference.getImports().contains("import javax.persistence.OneToOne;"));
		assertTrue(reference.getImports().contains("import javax.persistence.JoinColumn;"));
		assertTrue(reference.getImports().contains("import javax.persistence.FetchType;"));
		
		String one2oneAnnotation = "@OneToOne(targetEntity = com.aliquantum.objects.User.class, fetch = FetchType.EAGER)";
		assertTrue(reference.getJpaAnnotations().contains(one2oneAnnotation));
		
		String joinColumnAnnotation = "@JoinColumn(name = \"user_id\")";
		assertTrue(reference.getJpaAnnotations().contains(joinColumnAnnotation));
		
		assertEquals(2, reference.getJpaAnnotations().size());
		
	}
	
	@Test
	public void oneToOneLazyAnnotationTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.reference     class-ref=\"com.aliquantum.objects.User\"\n" + 
				"     *                    foreignkey=\"userIdentity\"\n" + 
				"     *                    auto-update=\"false\"\n" +  
				"     */";
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition(sourceCode);
		reference.parse2JPA(doclet);
		
		assertEquals(doclet, reference.getDoclet());
		
		String one2oneAnnotation = "@OneToOne(targetEntity = com.aliquantum.objects.User.class, fetch = FetchType.LAZY)";
		assertTrue(reference.getJpaAnnotations().contains(one2oneAnnotation));
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
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition(sourceCode);
		reference.parse2JPA(doclet);
		
		assertEquals(doclet, reference.getDoclet());
		
		String one2oneAnnotation = "@OneToOne(targetEntity = com.aliquantum.objects.User.class, fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })";
		assertTrue(reference.getJpaAnnotations().contains(one2oneAnnotation));
		
		assertTrue(reference.getImports().contains("import javax.persistence.CascadeType;"));
		assertEquals(4, reference.getImports().size());
	}
	
	@Test
	public void noDocletTest() throws Exception {
		
		String doclet = "// just a single line comment";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition("");
		collection.parse2JPA(doclet);
		
		assertTrue(collection.getImports().isEmpty());
		assertTrue(collection.getJpaAnnotations().isEmpty());
		
	}

}
