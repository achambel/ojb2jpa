package converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class FieldDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(FieldDefinition.class);
	
	public FieldDefinition(String docletForField, String sourceCode) {
		super(docletForField, sourceCode);
	}

	@Override
	public void findDefinition() {

		logger.info("Finding definition at doclet\n" + doclet);
		final String regex = "(?:.+@ojb\\.(\\w+))";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(doclet);
		
		logger.info("Using REGEX " + regex);
		
		if (matcher.find()) {
			
			String type = matcher.group(1);
			if (type.equals("field")) {
				OJBFieldDefinition field = new OJBFieldDefinition(doclet);
				jpaAnnotations.addAll(field.getJPAAnnotations());
				jpaImports.addAll(field.getJPAImports());
			} 
			else if (type.equals("collection")) {
				OJBCollectionDefinition collection = new OJBCollectionDefinition(doclet);
				jpaAnnotations.addAll(collection.getJPAAnnotations());
				jpaImports.addAll(collection.getJPAImports());
			}
			else if (type.equals("reference")) {
				OJBReferenceDefinition reference = new OJBReferenceDefinition(doclet, getSourceCode());
				jpaAnnotations.addAll(reference.getJPAAnnotations());
				jpaImports.addAll(reference.getJPAImports());
			}
			else {
				logger.error("OJB definition was detected, however no class to map it. Check your missing implementation.");
			}
		}
		else {
			logger.warn("This field is not candidate for convertion. Check regex or source code if necessary.");
		}
		
	}

	@Override
	public boolean isCandidateForConvertion() {
		
		return getJPAAnnotations().size() > 0;
	}
	
	
}
