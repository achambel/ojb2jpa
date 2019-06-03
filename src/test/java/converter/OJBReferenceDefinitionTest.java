package converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OJBReferenceDefinitionTest {
	
	@Test
	public void oneToOneAnnotationTest() {
		
		String doclet = "/**\n" + 
				"     * @ojb.reference     class-ref=\"com.aliquantum.objects.User\"\n" + 
				"     *                     foreignkey=\"userIdentity\"\n" + 
				"     *                      auto-update=\"false\"\n" + 
				"     *                      auto-retrieve=\"true\" proxy=\"true\"\n" + 
				"     */";
		
		OJBReferenceDefinition reference = new OJBReferenceDefinition();
		reference.parse2JPA(doclet);
		assertEquals(doclet, reference.getDoclet());
		
		// TODO
//		assertEquals(N, reference.getImports().size());
		
	}

}
