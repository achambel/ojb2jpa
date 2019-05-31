package converter;

public abstract class OJBDefinition {
	
	private String doclet;
	private String jpaAnnotation;
	
	public String getDoclet() {
		return doclet;
	}
	public void setDoclet(String doclet) {
		this.doclet = doclet;
	}
	public String getJpaAnnotation() {
		return jpaAnnotation;
	}
	public void setJpaAnnotation(String jpaAnnotation) {
		this.jpaAnnotation = jpaAnnotation;
	}
	
	

}
