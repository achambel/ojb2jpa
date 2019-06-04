package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OJBReferenceDefinition extends OJBDefinition implements IOJBDefinition {

	
	public OJBReferenceDefinition(String sourceCode) {
		super(sourceCode);
	}

	@Override
	public void parse2JPA(String doclet) throws Exception {

		this.setDoclet(doclet);
		
		List<String> one2oneOptions = new ArrayList<>();
		
		String targetEntity = extractFirstGroup("class-ref\\s*=\\s*\"(.+)\"", doclet);
		one2oneOptions.add(String.format("targetEntity = %s.class", targetEntity));
		
		if (isAutoRetrieve()) {
			one2oneOptions.add("fetch = FetchType.EAGER");
		} else if (one2oneOptions.size() > 0) {
			one2oneOptions.add("fetch = FetchType.LAZY");
		}
		
		List<String> cascadeTypes = new ArrayList<>(); 
		
		if (isAutoDelete()) {
			cascadeTypes.add("CascadeType.REMOVE");
		}
			
		if (isAutoUpdateOrInsert()) {
			cascadeTypes.add("CascadeType.PERSIST");
		}
		
		if (cascadeTypes.size() > 0) {
			one2oneOptions.add(String.format("cascade = { %s }", String.join(", ", cascadeTypes)));
			this.getImports().add("import javax.persistence.CascadeType;");
		}

		String one2oneAnnotation = String.format("@OneToOne(%s)", String.join(", ", one2oneOptions));
		this.getJpaAnnotations().add(one2oneAnnotation);
		this.getImports().add("import javax.persistence.OneToOne;");
		this.getImports().add("import javax.persistence.FetchType;");
		
		String fk = extractFirstGroup("foreignkey\\s*=\\s*\"(.+)\"", doclet);
		String joinColumnDocLet = findDocletForField(fk);
		String joinColumnName = getJoinColumnName(joinColumnDocLet);
		
		this.getJpaAnnotations().add(String.format("@JoinColumn(name = \"%s\")", joinColumnName));
		this.getImports().add("import javax.persistence.JoinColumn;");
		
	}
	
	private String findDocletForField(String field) throws Exception {
		
		final String regex = String.format("(?:\\/\\*(?:[^\\*]|(?:\\*+[^\\*\\/]))*\\*+\\/(?=\\s.*))(?=\\s*(?:@\\w.+\\s*)*.+\\b%s\\b)", field);
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(getSourceCode());
		
		if (matcher.find()) {
			return matcher.group(0);
		}
		
		throw new Exception("Doclet not found for field " + field + ". Check the regex or source code.");
	}
	
	private String getJoinColumnName(String doclet) throws Exception {
		
		return extractFirstGroup("column\\s*=\\s*\"(.+)\"", doclet);	
		
	}
	
	private String extractFirstGroup(String regex, String str) throws Exception {
		
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(str);
		
		if (matcher.find()) {
			
			return matcher.group(1);
		}
		
		throw new Exception("First group not found at regex " + regex + " in " + str);
	}

}
