package converter;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public abstract class BaseDefinition implements IDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseDefinition.class);
	
	protected String doclet;
	protected Set<String> jpaAnnotations = new HashSet<String>();
	protected Set<String> jpaImports = new HashSet<String>();
	protected String sourceCode;
	protected Path sourceFilePath;
	protected String fieldName;
	protected boolean primitiveToWrapper;
	
	public BaseDefinition(String doclet, String sourceCode, Path sourceFilePath, String fieldName) {
		
		this.doclet = doclet;
		this.sourceCode = sourceCode;
		this.sourceFilePath = sourceFilePath;
		this.fieldName = fieldName;
		
		findDefinition();
	}
	
	public boolean isPrimitiveToWrapper() {
		return primitiveToWrapper;
	}
	
	public void setPrimitiveToWrapper(boolean primitive) {
		this.primitiveToWrapper = primitive;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public Path getSourceFilePath() {
		return sourceFilePath;
	}
	
	public String getDoclet() {
		return doclet;
	}
	
	public void setDoclet(String doclet) {
		this.doclet = doclet;
	}
	
	public Set<String> getJPAAnnotations() {
		return jpaAnnotations;
	}
	
	public void setJPAAnnotations(Set<String> jpaAnnotations) {
		this.jpaAnnotations = jpaAnnotations;
	}
	
	public Set<String> getJPAImports() {
		return jpaImports;
	}
	
	public void setJPAImports(Set<String> jpaImports) {
		this.jpaImports = jpaImports;
	}
	
	public boolean isAutoRetrieve() {
		
		return matches("auto-retrieve=\"true\"");
	}
	
	public boolean isAutoDelete() {
		
		return matches("auto-delete=\"(object|true)\"");
	}
	
	public boolean isAutoUpdateOrInsert() {
		
		return matches("auto-(?:update|insert)=\"(object|true)\"");
	}
	
	public boolean isProxy() {
		return matches("proxy=\"true\"");
	}
	
	protected boolean matches(String regex) {
		
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(getDoclet());
		
		return matcher.find();
	}
	
	protected String extractFirstGroup(String regex, String str) {
		
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(str);
		
		if (matcher.find()) {
			
			return matcher.group(1);
		}
		
		logger.warn("First group not found at regex " + regex + " in " + str);
		
		return "";
	}
	
	public String getSourceCode() {
		
		return sourceCode;
	}

}
