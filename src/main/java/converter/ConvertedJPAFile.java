package converter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class ConvertedJPAFile {

	private static final Logger logger = LoggerFactory.getLogger(ConvertedJPAFile.class);

	private String pathToSave;
	private Set<String> imports = new HashSet<String>();
	private String packageName;
	private String className;
	private String OJBFileContent;
	private Path sourceFilePath;
	private Class<?> clazz;

	private List<String> skippedClasses = Arrays.asList("BaseIdentifieableVO", "BaseVO", "BaseIdentifiableVO");

	private String target;

	public ConvertedJPAFile(Path sourceFilePath, String pathToSave) throws Exception {

		logger.info("Converting class " + sourceFilePath);

		this.sourceFilePath = sourceFilePath;
		this.pathToSave = pathToSave;
		readOJBFileContent();
	}

	private void readOJBFileContent() throws Exception {

		try {
			String fileContent = new String(Files.readAllBytes(sourceFilePath));
			this.OJBFileContent = fileContent;
			this.target = String.copyValueOf(OJBFileContent.toCharArray());
			setClassName();

			if (isSkippedClass()) {
				logger.warn(
						"Skipping conversion due to class " + getClassName() + " was added in skipped classes list.");
			} else {
				setClassInformation();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isSkippedClass() {
		return this.skippedClasses.contains(getClassName());
	}

	public String printConvertedClass() {

		return this.target;
	}

	private void setClassInformation() throws Exception {

		setEntity();

		logger.info("Looking for package name...");
		setPackageName();

		final String regex = "package\\s+|;$";
		String name = getPackageName().replaceAll(regex, "") + "." + getClassName();

		logger.info("Getting new instance for " + name);
		clazz = Class.forName(name);

		setFields();
		replaceOJBCollections();

		List<String> printableImports = getImports();
		printableImports.add(0, getPackageName() + "\n");

		target = target.replace(getPackageName(), String.join("\n", printableImports));
		
		String oldPkg = "import org.apache.ojb.broker.PersistenceBroker;";
		String newPkg = "import com.aliquantum.jpa.mock.PersistenceBroker;";
		target = target.replace(oldPkg, newPkg);
		
		String oldParent = "(extends)(\\s+)(BaseIdentifieableVO)";
		target = target.replaceFirst(oldParent, "$1$2BaseIdentifiableVO");

		logger.info("Parser finished for file " + sourceFilePath);

	}

	private void replaceOJBCollections() {

		final String importRegex = "(?m)^import org.apache\\.ojb\\.broker\\.util\\.collections\\.(ManageableArrayList|RemovalAwareCollection|RemovalAwareList);$";
		Pattern pattern = Pattern.compile(importRegex);
		Matcher matcher = pattern.matcher(target);

		if (matcher.find()) {

			final String regex = "new\\s+(ManageableArrayList|RemovalAwareCollection|RemovalAwareList)(\\(|<).+;";
			target = target.replaceAll(regex, "new ArrayList<>();");

			target = target.replaceAll(importRegex, "");
			target = target.replaceAll("(?m)^import\\s+java\\.util\\.ArrayList;$", "");

			imports.add("import java.util.ArrayList;");
		}

	}

	private void setFields() {

		logger.info("Getting declared fields...");

		Field[] fields = clazz.getDeclaredFields();
		logger.info("Total of fields in this class: " + fields.length);

		for (Field field : fields) {

			logger.info("Found field " + field.getName());

			String docletForField = OJB2JPA.findDoclet(field.getName(), OJBFileContent);

			if (docletForField.isEmpty()) {
				logger.warn("No doclet has been found for the field " + field.getName());
				
				String modifier = Modifier.toString(field.getModifiers());
				
				if (modifier.contains("static")) {
					logger.warn("Static field found. JPA will not use this as a persistent field. Don't need to annotate it.");
				}
				else {
					logger.warn("So generating @Transient annotation. This field will not be persisted.");
					String fieldStr =  modifier + " " + field.getType().getSimpleName();
					final String regex = String.format(".%s.+\\b%s\\b.*", Pattern.quote(fieldStr.trim()), field.getName());
					target = target.replaceAll(regex, "\t@Transient\n$0");
					imports.add("import javax.persistence.Transient;");
				}
			} else {
				FieldDefinition fieldDefinition = new FieldDefinition(docletForField, OJBFileContent, this.sourceFilePath, field.getName());
				if (fieldDefinition.isCandidateForConvertion()) {

					target = target.replace(docletForField, String.join("\n\t", fieldDefinition.getJPAAnnotations()));
					imports.addAll(fieldDefinition.getJPAImports());
				}
				if (fieldDefinition.isPrimitiveToWrapper()) {
					
					logger.info("Replacing long primitive type by Long wrapper class...");
					
					final String regex = String.format("(long)(\\s+%s\\s*)(=\\s*\\d+)?(;)", field.getName());
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(target);
					
					if (matcher.find()) {
						if (matcher.group(3) != null) {
							target = target.replaceAll(regex, "Long$2$3l$4");
						}
						else {
							target = target.replaceAll(regex, "Long$2$4");
						}
					}
					
				}
			}

		}

		logger.info("No more field to find any definition in this class.");

	}

	private void setEntity() throws Exception {

		String surround = String.format("class\\s+%s", getClassName());
		String docletForEntity = OJB2JPA.findDoclet(surround, OJBFileContent);

		ClassDefinition classDefinition = new ClassDefinition(docletForEntity);

		if (classDefinition.isCandidateForConvertion()) {
			target = target.replace(docletForEntity, String.join("\n", classDefinition.getJPAAnnotations()));
			imports.addAll(classDefinition.getJPAImports());
		} else {

			String message = "Entity not found in class " + getClassName()
					+ ". Check the regex, make sure this class is an Entity or if not, include it to skip classes method.\n\n"
					+ OJBFileContent;
			logger.error(message);
			throw new Exception(message);
		}

	}

	private void setPackageName() {

		String regex = "^package\\s+.+;";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(this.OJBFileContent);

		if (matcher.find()) {
			this.packageName = matcher.group(0);
		}
	}

	private void setClassName() {

		this.className = this.sourceFilePath.getFileName().toString().replaceFirst("[.][^.]+$", "");
	}

	public String getPackageName() {
		return packageName;
	}

	public List<String> getImports() {
		return imports.stream().sorted().collect(Collectors.toList());
	}

	public Path getSourceFilePath() {
		return sourceFilePath;
	}

	public String getPathToSave() {
		return pathToSave;
	}

	public String getClassName() {
		return className;
	}

	public String getOJBFileContent() {
		return OJBFileContent;
	}

	public void setOJBFileContent(String oJBFileContent) {
		OJBFileContent = oJBFileContent;
	}

}
