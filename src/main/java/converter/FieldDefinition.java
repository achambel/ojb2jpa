package converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldDefinition {
	
	private Field field;
	private String rawCode;
	private List<String> annotations = new ArrayList<>();
	private String sourceCode;
	
	public FieldDefinition(Field field, String sourceCode) throws Exception {
		this.field = field;
		this.sourceCode = sourceCode;
		this.rawCode = findRawCode();
		this.annotations = findAnnotations();
	}
	
	public Field getField() {
		return field;
	}
	
	public String getRawCode() {
		return rawCode;
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	public boolean hasAnnotations() {
		return annotations.size() > 0;
	}
	
	private String findRawCode() throws Exception {
		
		String fieldName = field.getName();
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
		
		return rawCode;
	}
	
	private List<String> findAnnotations() throws Exception {
		
		final String regex = String.format("(@.+(?>@.+|[\\s])*?).+(?=%s)", Pattern.quote(rawCode.trim()));
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(sourceCode);
		
		List<String> annotations = new ArrayList<>();
		
		if (matcher.find()) {
			String allAnnotations = matcher.group(1);
			
			final String annotationRegex = "@.+";
			final Pattern annotationPattern = Pattern.compile(annotationRegex);
			final Matcher annotationMatcher = annotationPattern.matcher(allAnnotations);
			
			while (annotationMatcher.find()) {
				annotations.add(annotationMatcher.group(0));
			}
			
		}
		
		
		if (annotations.size() != field.getDeclaredAnnotations().length) {
			throw new Exception("Incorrect number of annotations for the field " + field.getName() + "Check the source code or regex used for capture them.");
		}
		
		for (Annotation a : field.getDeclaredAnnotations()) {
			
			String name = "@"+a.annotationType().getSimpleName();
			boolean isCaptured = annotations.stream().anyMatch(str -> str.startsWith(name));
			
			if (!isCaptured) {
				throw new Exception("Annotation " + name + " was not found. It means that annotation exists in the source code but not in the parsed code.");
			}
		}
		
		return annotations;
	}
	

}
