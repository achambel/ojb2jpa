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
public class ClassDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(ClassDefinition.class);
	private String entityName;

	public ClassDefinition(String docletForEntity) {
		super(docletForEntity, null);
	}
	
	
	@Override
	public void findDefinition() {
		
		logger.info("Finding Entity at doclet\n" + getDoclet());
		
		final String regex = "@ojb\\.class(?:.+|\\s*.+)table\\s*=\\s*\"(.+)\"";
		logger.info("Using REGEX " + regex);
		
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(getDoclet());
		
		if (matcher.find()) {
			
			this.entityName = matcher.group(1);
			String table = String.format("@Table(name = \"%s\")", entityName);
			
			jpaAnnotations.add("@Entity");
			jpaAnnotations.add(table);
			
			jpaImports.add("import javax.persistence.Entity;");
			jpaImports.add("import javax.persistence.Table;");
			
			logger.info("Annotations mapped...\n" + String.join("\n", jpaAnnotations));
		}
		else {
			logger.warn("Entity not found. Check the source or regex.");
		}
		
	}
	
	@Override
	public boolean isCandidateForConvertion() {
		
		return entityName != null;
	}
	
	public String getEntityName() {
		
		return entityName;
	}
	
}
