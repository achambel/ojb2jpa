package converter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class OJBFieldDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(OJBFieldDefinition.class);
	
	public OJBFieldDefinition(String doclet, String sourceCode, Path sourceFilePath, String fieldName) {
		super(doclet, sourceCode, sourceFilePath, fieldName);
	}

	@Override
	public void findDefinition() {
		
		List<String> values = new ArrayList<>();

		Pattern pattern = Pattern.compile("column=\"(.\\w+)\"");
		Matcher matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			values.add(String.format("name = \"%s\"", matcher.group(1)));
		}
		
		if (isForeignkey(fieldName)) {
			// Entity has already a column with the same name annotated
			values.add("insertable = false, updatable = false");
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

	private boolean isForeignkey(String fieldName) {

		final String regex = String.format("foreignkey\\s*=\\s*\"%s\"", fieldName);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sourceCode);
		
		boolean result = matcher.find();
		
		return result;
	}

	@Override
	public boolean isCandidateForConvertion() {
		
		return jpaAnnotations.size() > 0;
	}

}
