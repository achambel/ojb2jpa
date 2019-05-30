package converter;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldDefinition extends BaseDefinition {
		
	private String doclet;
	private String type;

	public FieldDefinition(Field field, String sourceCode) throws Exception {
		
		this.member = field;
		this.type = field.getType().getSimpleName();
		this.sourceCode = sourceCode;
		this.rawCode = findRawCode();
		this.annotations = findAnnotations();
		this.doclet = findDoclet();
	}
	
	public String getDoclet() {
		return doclet;
	}
	
	private String findDoclet() {
		
		String doc = null;
		
		final String regex = String.format("(?:\\/\\*(?:[^\\*]|(?:\\*+[^\\*\\/]))*\\*+\\/(?=\\s.*))(?=\\s*(?:@\\w.+\\s*)*.+%s\\s+%s)", Pattern.quote(type), member.getName());
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

}
