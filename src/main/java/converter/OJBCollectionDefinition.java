package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OJBCollectionDefinition extends OJBDefinition implements IOJBDefinition {

	public OJBCollectionDefinition(String sourceCode) {

		super(sourceCode);
	}

	@Override
	public void parse2JPA(String doclet) {

		this.setDoclet(doclet);
		
		List<String> groups = new ArrayList<>();

		Pattern pattern = Pattern.compile("element-class-ref\\s*=\\s*\"(.+)\"");
		Matcher matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			groups.add(String.format("targetEntity = %s.class", matcher.group(1)));
		}
		
		if (isAutoRetrieve()) {
			groups.add("fetch = FetchType.EAGER");
		}
		else if (groups.size() > 0) {
			groups.add("fetch = FetchType.LAZY");
		}
		
		List<String> cascadeTypes = new ArrayList<>(); 
				
		if (isAutoDelete()) {
			cascadeTypes.add("CascadeType.REMOVE");
		}
			
		if (isAutoUpdateOrInsert()) {
			cascadeTypes.add("CascadeType.PERSIST");
		}
		
		if (cascadeTypes.size() > 0) {
			groups.add(String.format("cascade = { %s }", String.join(", ", cascadeTypes)));
			this.getImports().add("import javax.persistence.CascadeType;");
		}
		
		if (!groups.isEmpty()) {
			
			String jpaAnnotation = String.format("@OneToMany(%s)", String.join(", ", groups));
			
			this.getImports().add("import javax.persistence.OneToMany;");
			this.getImports().add("import javax.persistence.FetchType;");
			
			this.getJpaAnnotations().add(jpaAnnotation);
			
		}
		
	}

}
