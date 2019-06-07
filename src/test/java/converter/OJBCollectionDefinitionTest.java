package converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet);
		
		assertEquals(doclet, collection.getDoclet());
		
		assertEquals(3, collection.getJPAImports().size());
		
		assertTrue(collection.getJPAImports().contains("import javax.persistence.OneToMany;"));
		assertTrue(collection.getJPAImports().contains("import javax.persistence.CascadeType;"));
		assertTrue(collection.getJPAImports().contains("import javax.persistence.FetchType;"));
		
		String annotation = "@OneToMany(targetEntity = com.aliquantum.objects.UserDocumentUser.class, fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })";
		assertTrue(collection.getJPAAnnotations().contains(annotation));
		
		assertEquals(1, collection.getJPAAnnotations().size());
		assertTrue(collection.isCandidateForConvertion());
		
	}
	
	@Test
	public void oneToManyWithLazyFetchTest() throws Exception {
		
		String doclet = "/**\n" + 
				"     * @ojb.collection element-class-ref=\"com.aliquantum.objects.UserDocumentUser\"\n" + 
				"     *                 collection-class=\"org.apache.ojb.broker.util.collections.RemovalAwareCollection\"\n" + 
				"     *            	    foreignkey=\"userDocumentIdentity\"\n" + 
				"     * 				auto-retrieve=\"false\" proxy=\"true\"\n" + 
				"     */";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet);
		
		assertEquals(doclet, collection.getDoclet());
		
		assertEquals(2, collection.getJPAImports().size());
		
		assertTrue(collection.getJPAImports().contains("import javax.persistence.OneToMany;"));
		assertTrue(collection.getJPAImports().contains("import javax.persistence.FetchType;"));
		
		String annotation = "@OneToMany(targetEntity = com.aliquantum.objects.UserDocumentUser.class, fetch = FetchType.LAZY)";
		assertTrue(collection.getJPAAnnotations().contains(annotation));
		
	}
	
	@Test
	public void noDocletTest() throws Exception {
		
		String doclet = "// just a single line comment";
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet);
		
		assertTrue(collection.getJPAImports().isEmpty());
		assertTrue(collection.getJPAAnnotations().isEmpty());
		assertFalse(collection.isCandidateForConvertion());
		
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
		
		OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet);
		
		assertTrue(collection.getJPAImports().contains("import javax.persistence.OrderBy;"));
		assertTrue(collection.getJPAAnnotations().contains("@OrderBy(\"displaySequence ASC\")"));
		
	}

}
