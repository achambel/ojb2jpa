package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class OJBCollectionDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(OJBCollectionDefinition.class);
	
	public OJBCollectionDefinition(String doclet, String sourceCode, Path sourceFilePath) {
		super(doclet, sourceCode, sourceFilePath, null);
	}

	@Override
	public void findDefinition() {

		List<String> values = new ArrayList<>();

		Pattern pattern = Pattern.compile("element-class-ref\\s*=\\s*\"(.+?)\"");
		Matcher matcher = pattern.matcher(doclet);
		String joinColumnName = null;
		
		if (matcher.find()) {
			
			String classRef = matcher.group(1);
			String classRefName = classRef.replaceAll("(?<!.).+\\.", "");
			
			try {
				Path targetClassPath = Paths.get(this.getSourceFilePath().getParent().toString(), classRefName + ".java");
				String targetClassContent = new String(Files.readAllBytes(targetClassPath));
				String foreignkey = this.extractFirstGroup("foreignkey\\s*=\\s*\"(\\w+)\"", doclet);
				String fkDoclet = OJB2JPA.findDoclet(foreignkey, targetClassContent);
				joinColumnName = this.extractFirstGroup("column\\s*=\\s*\"(\\w+)\"", fkDoclet);
				if (joinColumnName.isEmpty()) {
					
					logger.error("Oh no, where's the join column name for this collection?");
					logger.error("Check which field is pointing to the collection at Entity " + classRef);
				}
				else {
					values.add(String.format("targetEntity = %s.class", classRef));
				}
				
			} catch (IOException e) {
				logger.error("Unable to read " + classRefName + " class.", e);
			}
				
		}
		
		if (isProxy()) {
			values.add("fetch = FetchType.LAZY");
		}
		else if (isAutoRetrieve() && !isProxy()) {
			values.add("fetch = FetchType.EAGER");
		}
		else if (values.size() > 0) {
			values.add("fetch = FetchType.LAZY");
		}
		
		List<String> cascadeTypes = new ArrayList<>(); 
				
		if (isAutoDelete()) {
			cascadeTypes.add("CascadeType.REMOVE");
		}
			
		if (isAutoUpdateOrInsert()) {
			cascadeTypes.add("CascadeType.PERSIST");
		}
		
		if (cascadeTypes.size() > 0) {
			values.add(String.format("cascade = { %s }", String.join(", ", cascadeTypes)));
			jpaImports.add("import javax.persistence.CascadeType;");
		}
		
		if (!values.isEmpty()) {
			
			String one2many = String.format("@OneToMany(%s)", String.join(", ", values));
			jpaAnnotations.add(one2many);
			jpaImports.add("import javax.persistence.OneToMany;");
			jpaImports.add("import javax.persistence.FetchType;");
			
			String joinColumn = String.format("@JoinColumn(name = \"%s\")", joinColumnName);
			jpaAnnotations.add(joinColumn);
			jpaImports.add("import javax.persistence.JoinColumn;");
			
			if (hasOrderBy()) {
				jpaImports.add("import javax.persistence.OrderBy;");
				String orderBy = extractFirstGroup("orderby\\s*=\\s*\"(.+?)\"", doclet);
				orderBy = orderBy.replaceAll("=", " ");
				String annotation = String.format("@OrderBy(\"%s\")", orderBy);
				jpaAnnotations.add(annotation);
			}
			
			logger.info("Annotations mapped...\n" + String.join("\n", jpaAnnotations));
			
		}
		
	}
	
	private boolean hasOrderBy() {
		
		final String regex = "orderby\\s*=\\s*\"(.+?)\"";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(getDoclet());
		
		return matcher.find();
		
	}

	@Override
	public boolean isCandidateForConvertion() {

		return jpaAnnotations.size() > 0;
	}

}
