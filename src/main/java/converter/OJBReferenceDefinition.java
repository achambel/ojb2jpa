package converter;

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
public class OJBReferenceDefinition extends BaseDefinition {

	static final Logger logger = LoggerFactory.getLogger(OJBReferenceDefinition.class);

	public OJBReferenceDefinition(String doclet, String sourceCode) {
		super(doclet, sourceCode);
	}

	@Override
	public void findDefinition() {

		List<String> one2oneOptions = new ArrayList<>();

		String targetEntity = extractFirstGroup("class-ref\\s*=\\s*\"(.+?)\"", doclet);
		if (!targetEntity.isEmpty()) {
			one2oneOptions.add(String.format("targetEntity = %s.class", targetEntity));
		}

		if (isAutoRetrieve()) {
			one2oneOptions.add("fetch = FetchType.EAGER");
		} else if (one2oneOptions.size() > 0) {
			one2oneOptions.add("fetch = FetchType.LAZY");
		}

		List<String> cascadeTypes = new ArrayList<>();

		if (isAutoDelete()) {
			cascadeTypes.add("CascadeType.REMOVE");
		}

		if (isAutoUpdateOrInsert()) {
			cascadeTypes.add("CascadeType.PERSIST");
		}

		if (cascadeTypes.size() > 0) {
			one2oneOptions.add(String.format("cascade = { %s }", String.join(", ", cascadeTypes)));
			jpaImports.add("import javax.persistence.CascadeType;");
		}

		if (one2oneOptions.size() > 0) {
			String one2oneAnnotation = String.format("@OneToOne(%s)", String.join(", ", one2oneOptions));
			jpaAnnotations.add(one2oneAnnotation);
			jpaImports.add("import javax.persistence.OneToOne;");
			jpaImports.add("import javax.persistence.FetchType;");

			String fk = extractFirstGroup("foreignkey\\s*=\\s*\\\"(\\w+)\\\"", doclet);
			String joinColumnDocLet = findDocletForField(fk);
			String joinColumnName = getJoinColumnName(joinColumnDocLet);

			jpaAnnotations.add(String.format("@JoinColumn(name = \"%s\")", joinColumnName));
			jpaImports.add("import javax.persistence.JoinColumn;");

			logger.info("Annotations mapped...\n" + String.join("\n", jpaAnnotations));
		}
	}

	private String findDocletForField(String field) {

		final String regex = String.format(
				"(?:\\/\\*(?:[^\\*]|(?:\\*+[^\\*\\/]))*\\*+\\/(?=\\s.*))(?=\\s*(?:@\\w.+\\s*)*.+\\b%s\\b)", field);
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(getSourceCode());

		if (matcher.find()) {
			return matcher.group(0);
		}

		logger.warn("Doclet not found for field " + field + ". Check the regex or source code.");

		return "";
	}

	private String getJoinColumnName(String doclet) {

		return extractFirstGroup("column\\s*=\\s*\\\"(\\w+)\\\"", doclet);

	}

	@Override
	public boolean isCandidateForConvertion() {

		return jpaAnnotations.size() > 0;
	}

}
