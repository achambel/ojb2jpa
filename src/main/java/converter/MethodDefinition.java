package converter;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodDefinition extends BaseDefinition {

	private String signature;
	
	public MethodDefinition(Method method, String sourceCode) throws Exception {
		
		this.member = method;
		this.sourceCode = sourceCode;
		this.rawCode = findRawCode();
		this.annotations = findAnnotations();
		this.signature = setSignature();
	}
	
	private String setSignature() throws Exception {
		
		final String regex = String.format("(\\w.+\\b%s\\b.+\\))", member.getName());
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(sourceCode);

		if (matcher.find()) {
			
			String strSignature = matcher.group(1);
			return strSignature;
		}
		
		throw new Exception("The signature of method " + member.getName() + " was not found. Check the source file or regex.");
	}
	
	public String getSignature() {
		
		return signature;
	}
	
	private String findRawCode() throws Exception {
		
		String name = member.getName();
		String bodyMethod = null;
		
		final String regex = "\\b"+name+"\\b\\s*\\(.*(\\{(\\s*(?!public|protected|default|private).|\\n)*?\\}\\s{2,})";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		
		final Matcher matcher = pattern.matcher(sourceCode);
		
		if (matcher.find()) {
			bodyMethod = matcher.group(1);
		}
		
		if (bodyMethod == null) {
			throw new Exception("The content of method "+name +" was not found. Check the source file or method name");
		}
		
		return bodyMethod.trim();
		
	}

}
