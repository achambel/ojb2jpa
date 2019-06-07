package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OJBCollectionDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(OJBCollectionDefinition.class);
	
	public OJBCollectionDefinition(String doclet) {
		super(doclet, null);
	}

	@Override
	public void findDefinition() {

		List<String> values = new ArrayList<>();

		Pattern pattern = Pattern.compile("element-class-ref\\s*=\\s*\"(.+?)\"");
		Matcher matcher = pattern.matcher(doclet);
		
		if (matcher.find()) {
			values.add(String.format("targetEntity = %s.class", matcher.group(1)));
		}
		
		if (isAutoRetrieve()) {
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
			
			String jpaAnnotation = String.format("@OneToMany(%s)", String.join(", ", values));
			
			jpaImports.add("import javax.persistence.OneToMany;");
			jpaImports.add("import javax.persistence.FetchType;");
			
			jpaAnnotations.add(jpaAnnotation);
			
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
