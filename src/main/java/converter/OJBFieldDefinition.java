package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OJBFieldDefinition extends OJBDefinition implements IOJBDefinition {
	
	@Override
	public void parse2JPA(String doclet) {

		this.setDoclet(doclet);
		
		List<String> groups = new ArrayList<>();

		Pattern pattern = Pattern.compile("column=\"(.\\w+)\"");
		Matcher matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			groups.add(String.format("name = \"%s\"", matcher.group(1)));
		}
		
		pattern = Pattern.compile("length=\"(\\d+)\"");
		matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			groups.add(String.format("length = %s", matcher.group(1)));
		}
		
		if (!groups.isEmpty()) {
			
			String jpaAnnotation = String.format("@Column(%s)", String.join(", ", groups));
			
			this.getImports().add("import javax.persistence.Column;");
			this.getJpaAnnotations().add(jpaAnnotation);
			
		}
		
	}
	

}
