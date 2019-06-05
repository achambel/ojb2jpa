package converter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(FieldDefinition.class);
	
	private String doclet;
	private String type;
	private IOJBDefinition ojbDefinition;
	
	@SuppressWarnings("serial")
	private Map<String, Class<? extends IOJBDefinition>> ojbClassMapping = new HashMap<String, Class<? extends IOJBDefinition>>() {{
		
		put("field", OJBFieldDefinition.class);
		put("collection", OJBCollectionDefinition.class);
		put("reference", OJBReferenceDefinition.class);
		
	}};

	public FieldDefinition(Field field, String sourceCode) throws Exception {
		
		this.member = field;
		this.type = field.getType().getSimpleName();
		this.sourceCode = sourceCode;
		this.rawCode = findRawCode();
		this.annotations = findAnnotations();
		this.doclet = findDoclet();
		this.ojbDefinition = findOJBDefinition();
	}
	
	public IOJBDefinition getOJBDefinition() {
		return ojbDefinition;
	}
	
	private IOJBDefinition findOJBDefinition() throws Exception {
		
		if (doclet == null) return null;
		
		final String regex = "(?:.+@ojb\\.(\\w+))";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			
			String type = matcher.group(1);
			
			Class<? extends IOJBDefinition> clazz = ojbClassMapping.get(type);
			IOJBDefinition ojbDef = clazz.getDeclaredConstructor(String.class).newInstance(sourceCode);
			ojbDef.parse2JPA(doclet);
			
			return ojbDef;
			
		}
		
		throw new Exception("OJB definition not found for doclet:\n" + doclet);
	}
	
	public String getDoclet() {
		return doclet;
	}
	
	private String findDoclet() {
		
		String doc = null;
		
		final String regex = String.format("(?:\\/\\*(?:[^\\*]|(?:\\*+[^\\*\\/]))*\\*+\\/(?=\\s.*))(?=\\s*(?:@\\w.+\\s*)*.+%s(?:.*)\\s+%s)", Pattern.quote(type), member.getName());
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(sourceCode);

		if (matcher.find()) {
			doc = matcher.group(0);
		}

		return doc;
		
	}
	
	private String findRawCode() throws Exception {
		
		logger.info("Finding definition for field " + member.getName());
		String fieldName = member.getName();
		String rawCode = null;
		
		final String regex = String.format("((\\w+\\s+)+\\b[A-Z]\\w+(?>\\[\\]){0,1}|(\\w+\\s+)+(boolean|byte\\[?\\]?|char\\[?\\]?|short|int|long|float|double))\\s+%s\\b(.+(\\s*\\{(\\n*.)*\\};)|\\s*;|.+;)", fieldName);
		logger.info("Using REGEX " + regex);
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(sourceCode);
		
		if (matcher.find()) {
			rawCode = matcher.group(0);
		}
		
		if (rawCode == null) {
			
			String message = "Field "+fieldName +" was not found. Check regex or field name at source code\n\n"+sourceCode;
			logger.error(message);
			throw new Exception(message);
		}
		
		return rawCode.trim();
	}
	
	public boolean hasOJBDefinition() {
		return ojbDefinition != null;
	}

}
