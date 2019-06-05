package converter;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(MethodDefinition.class);
	private String signature;
	
	public MethodDefinition(Method method, String sourceCode) throws Exception {
		
		this.member = method;
		this.sourceCode = sourceCode;
		this.rawCode = findRawCode();
		this.annotations = findAnnotations();
		this.signature = setSignature();
	}
	
	private String setSignature() throws Exception {
		
		logger.info("Setting signature for the method " + member.getName());
		final String regex = String.format("(\\w.+\\b%s\\b.+\\))", member.getName());
		logger.info("Using REGEX " + regex);
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(sourceCode);

		if (matcher.find()) {
			
			String strSignature = matcher.group(1);
			return strSignature;
		}
		
		String message = "The signature of method " + member.getName() + " was not found. Check the source file or regex at \n\n" + sourceCode;
		logger.error(message);
		
		throw new Exception(message);
	}
	
	public String getSignature() {
		
		return signature;
	}
	
	private String findRawCode() throws Exception {
		
		String name = member.getName();
		String bodyMethod = null;
		
		logger.info("Finding definition for the method " + name);
		
		final String regex = "\\b"+name+"\\b\\s*\\(.*(\\{(\\s*(?!public|protected|default|private).|\\n)*?\\}\\s{2,})";
		logger.info("Using REGEX " + regex);
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		
		final Matcher matcher = pattern.matcher(sourceCode);
		
		if (matcher.find()) {
			bodyMethod = matcher.group(1);
		}
		
		if (bodyMethod == null) {
			String message = "The content of method "+ name +" was not found. Check the regex "+ regex +" or method name in the source.\n\n" + sourceCode;
			logger.error(message);
			throw new Exception(message);
		}
		
		return bodyMethod.trim();
		
	}

}
