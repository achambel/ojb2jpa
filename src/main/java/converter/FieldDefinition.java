package converter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldDefinition extends BaseDefinition {
		
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
			IOJBDefinition ojbDef = clazz.newInstance();
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
		
		String fieldName = member.getName();
		String rawCode = null;
		
		final String regex = String.format("(.+\\b%s\\b.+\\{(\\n?.|\\n)*?\\};\\s{2,})|(.+\\b%s\\b.*;$)", fieldName, fieldName);
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(sourceCode);
		
		if (matcher.find()) {
			rawCode = matcher.group(0);
		}
		
		if (rawCode == null) {
			throw new Exception("Field "+fieldName +" was not found. Check the source file or field name");
		}
		
		return rawCode.trim();
	}
	
	public boolean hasOJBDefinition() {
		return ojbDefinition != null;
	}

}
