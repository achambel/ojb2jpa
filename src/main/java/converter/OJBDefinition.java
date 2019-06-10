package converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public abstract class OJBDefinition {
	
	private String doclet;
	private List<String> jpaAnnotations = new ArrayList<>();
	private Set<String> imports = new HashSet<String>();
	private String sourceCode;
	
	public OJBDefinition(String sourceCode) {
		this.sourceCode = sourceCode;
	}
	
	public String getSourceCode() {
		return sourceCode;
	}
	
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
	
	public void setImports(Set<String> imports) {
		this.imports = imports;
	}
	
	public Set<String> getImports() {
		return imports;
	}
	
	public boolean isAutoRetrieve() {
		
		return matches("auto-retrieve=\"true\"");
	}
	
	public boolean isAutoDelete() {
		
		return matches("auto-delete=\"(object|true)\"");
	}
	
	public boolean isAutoUpdateOrInsert() {
		
		return matches("auto-(?:update|insert)=\"(object|true)\"");
	}
	
	protected boolean matches(String regex) {
		
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(getDoclet());
		
		return matcher.find();
	}
	
	protected String extractFirstGroup(String regex, String str) throws Exception {
		
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(str);
		
		if (matcher.find()) {
			
			return matcher.group(1);
		}
		
		throw new Exception("First group not found at regex " + regex + " in " + str);
	}
	
}
