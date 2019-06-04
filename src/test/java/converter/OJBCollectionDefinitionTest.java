package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OJBCollectionDefinitionTest {
	
	@Test
	public void oneToManyAnnotationTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.collection element-class-ref=\"com.aliquantum.objects.UserDocumentUser\"\n" + 
				"     *                 collection-class=\"org.apache.ojb.broker.util.collections.RemovalAwareCollection\"\n" + 
				"     *            	    foreignkey=\"userDocumentIdentity\"\n" + 
				"     *   		  	    auto-delete=\"true\"\n" + 
				"     *   		  	    auto-insert=\"true\"\n" + 
				"     *   		  	    auto-update=\"true\"\n" + 
				"     * 				auto-retrieve=\"true\" proxy=\"true\"\n" + 
				"     */";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition("");
		collection.parse2JPA(doclet);
		
		assertEquals(doclet, collection.getDoclet());
		
		assertEquals(3, collection.getImports().size());
		
		assertTrue(collection.getImports().contains("import javax.persistence.OneToMany;"));
		assertTrue(collection.getImports().contains("import javax.persistence.CascadeType;"));
		assertTrue(collection.getImports().contains("import javax.persistence.FetchType;"));
		
		String annotation = "@OneToMany(targetEntity = com.aliquantum.objects.UserDocumentUser.class, fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })";
		assertEquals(annotation, collection.getJpaAnnotations().get(0));
		
		assertEquals(1, collection.getJpaAnnotations().size());
		
	}
	
	@Test
	public void oneToManyWithLazyFetchTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.collection element-class-ref=\"com.aliquantum.objects.UserDocumentUser\"\n" + 
				"     *                 collection-class=\"org.apache.ojb.broker.util.collections.RemovalAwareCollection\"\n" + 
				"     *            	    foreignkey=\"userDocumentIdentity\"\n" + 
				"     * 				auto-retrieve=\"false\" proxy=\"true\"\n" + 
				"     */";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition("");
		collection.parse2JPA(doclet);
		
		assertEquals(doclet, collection.getDoclet());
		
		assertEquals(2, collection.getImports().size());
		
		assertTrue(collection.getImports().contains("import javax.persistence.OneToMany;"));
		assertTrue(collection.getImports().contains("import javax.persistence.FetchType;"));
		
		String annotation = "@OneToMany(targetEntity = com.aliquantum.objects.UserDocumentUser.class, fetch = FetchType.LAZY)";
		assertEquals(annotation, collection.getJpaAnnotations().get(0));
		
		
	}
	
	@Test
	public void noDocletTest() throws Exception {
		
		String doclet = "// just a single line comment";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition("");
		collection.parse2JPA(doclet);
		
		assertTrue(collection.getImports().isEmpty());
		assertTrue(collection.getJpaAnnotations().isEmpty());
		
	}
	
	@Test
	public void oneToManyWithOrderByTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.collection element-class-ref=\"com.aliquantum.objects.AccountFavouriteGame\"\n" + 
				"     *                 collection-class=\"org.apache.ojb.broker.util.collections.ManageableArrayList\"\n" + 
				"     *            	    foreignkey=\"accountIdentity\"\n" + 
				"     *            	    orderby=\"displaySequence=ASC\"\n" + 
				"     *   		  	    auto-update=\"true\"\n" + 
				"     * 				auto-retrieve=\"true\" proxy=\"true\"\n" + 
				"     */";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition("");
		collection.parse2JPA(doclet);
		
		assertTrue(collection.getImports().contains("import javax.persistence.OrderBy;"));
		assertTrue(collection.getJpaAnnotations().contains("@OrderBy(\"displaySequence ASC\")"));
		
	}

}
