package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OJBFieldDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(OJBFieldDefinition.class);
	
	public OJBFieldDefinition(String doclet) {
		super(doclet, null);
	}

	@Override
	public void findDefinition() {
		
		List<String> values = new ArrayList<>();

		Pattern pattern = Pattern.compile("column=\"(.\\w+)\"");
		Matcher matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			values.add(String.format("name = \"%s\"", matcher.group(1)));
		}
		
		pattern = Pattern.compile("length=\"(\\d+)\"");
		matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			values.add(String.format("length = %s", matcher.group(1)));
		}
		
		if (!values.isEmpty()) {
			
			String jpaAnnotation = String.format("@Column(%s)", String.join(", ", values));
			
			jpaImports.add("import javax.persistence.Column;");
			jpaAnnotations.add(jpaAnnotation);
			
			logger.info("Annotations mapped...\n" + String.join("\n", jpaAnnotations));
			
		}
		
	}

	@Override
	public boolean isCandidateForConvertion() {
		
		return jpaAnnotations.size() > 0;
	}

}
