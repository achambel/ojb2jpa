package converter;

import java.util.ArrayList;
import java.util.List;

public abstract class OJBDefinition {
	
	private String doclet;
	private List<String> jpaAnnotations = new ArrayList<>();
	private List<String> imports = new ArrayList<>();;
	
	public String getDoclet() {
		return doclet;
	}
	public void setDoclet(String doclet) {
		this.doclet = doclet;
	}
	public List<String> getJpaAnnotations() {
		return jpaAnnotations;
	}
	public void setJpaAnnotations(List<String> jpaAnnotation) {
		this.jpaAnnotations = jpaAnnotation;
	}
	
	public void setImports(List<String> imports) {
		this.imports = imports;
	}
	
	public List<String> getImports() {
		return imports;
	}
	
}
