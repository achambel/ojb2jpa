package converter;

public abstract interface IOJBDefinition {

	void parse2JPA(String doclet);
	String getJPAAnnotationImport();
}
