package converter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConvertedJPAFile {
	
	private String pathToSave;
	private Set<String> imports = new HashSet<String>();
	private String packageName;
	private String className;
	private String OJBFileContent;
	private List<FieldDefinition> OJBFields = new ArrayList<>();
	private List<MethodDefinition> OJBMethods = new ArrayList<>();
	private Path sourceFilePath;
	private String convertedCode;
	private Class<?> clazz;
	private String classDefinitionName;
	private String author = "";
	private String version = "";
	
	private String tableName;
	
	public ConvertedJPAFile(Path sourceFilePath, String pathToSave) throws Exception {
		
		this.sourceFilePath = sourceFilePath;
		this.pathToSave = pathToSave;
		readOJBFileContent();
	}
	
	private void readOJBFileContent() throws Exception {
		
		try {
			String fileContent = new String(Files.readAllBytes(sourceFilePath));
			this.OJBFileContent = fileContent;
			setClassInformation();
			this.convertedCode = assembleClass();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String assembleClass() {
		
		StringBuilder code = new StringBuilder();
		
		code.append("\n\n/**\n");
		code.append(String.format(" * @author %s\n", getAuthor()));
		code.append(String.format(" * @version %s\n", getVersion()));
		code.append(" * Class converted from OJB to JPA. May it needs to double check.\n");
		code.append(" */\n");
		
		code.append("\n");
		code.append("@Entity\n");
		code.append(String.format("@Table(name=\"%s\")%n", getTableName()));
		code.append(getClassDefinitionName()+newLine(2));
		
		for (FieldDefinition field : OJBFields) {
			
			List<String> annotations = new ArrayList<>();
			
			if (field.hasAnnotations()) annotations.addAll(field.getAnnotations());
			
			if (field.hasOJBDefinition()) { 
				annotations.addAll(field.getOJBDefinition().getJpaAnnotations());
				imports.addAll(field.getOJBDefinition().getImports());
			}

			annotations.forEach(a -> {
				code.append(String.format("\t%s\n", a));
			});
			
			code.append("\t"+field.rawCode+newLine(2));
		}
		
		for (MethodDefinition method : OJBMethods) {
			
			method.getAnnotations().forEach(a -> {
				code.append(String.format("\t%s\n", a));
			});
			
			code.append("\t"+method.getSignature());
			code.append(" "+method.rawCode+newLine(2));
		}
		
		code.append("}");
		
		String imports = String.join(newLine(1), getImports());
		
		code.insert(0, imports);
		code.insert(0, getPackageName()+";"+newLine(2));
		
		return code.toString();
	}
	
	private String newLine(int amount) {
		
		String lines = "";
		
		for (int i = 1; i <= amount; i++) {
			lines = lines.concat(System.lineSeparator());
		}
		
		return lines;
	}
	
	public String printConvertedClass( ) {
		
		return this.convertedCode;
	}
	
	private void setClassInformation() throws Exception {
		
		setPackageName();
		setImports();
		setClassName();
		
		clazz =  Class.forName(getPackageName() + "." + getClassName());
		
		setAuthor();
		setVersion();
		setTableName();
		setClassDefinitionName();
		setFields();
		setMethods();
		
	}
	
	public String getTableName() {
		
		return tableName;
	}
	
	private void setTableName() throws Exception {
		
		final String regex = "@ojb\\.class(?:.+|\\s*.+)table\\s*=\\s*\"(.+)\"";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(OJBFileContent);
		
		if (matcher.find()) {
			
			tableName = matcher.group(1);
			
			imports.add("import javax.persistence.Entity;");
			imports.add("import javax.persistence.Table;");
		}
		else {
			throw new Exception("Table name not found in class "+getClassName()+". Check the source code or regex");
		}
		
	}
	
	public String getVersion() {
		return version;
	}
	
	private void setVersion() {
		
		final Pattern pattern = Pattern.compile("@version(.+)");
		Matcher matcher = pattern.matcher(OJBFileContent);

		if (matcher.find()) {
			version = matcher.group(1).trim();
		}
	}
	
	public String getAuthor() {
		
		return author;
	}
	
	private void setAuthor() {
		
		final Pattern pattern = Pattern.compile("@author(.+)");
		Matcher matcher = pattern.matcher(OJBFileContent);

		if (matcher.find()) {
			author = matcher.group(1).trim();
		}
	}
	
	public String getClassDefinitionName() {
		
		return classDefinitionName;
	}
	
	private void setClassDefinitionName() throws Exception {
		
		final String regex = String.format(".+class\\s+\\b%s\\b.+\\{", clazz.getSimpleName());
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(OJBFileContent);
		
		if (matcher.find()) {
			classDefinitionName = matcher.group(0);
		}
		else {
			throw new Exception("Class definition not found for class name "+clazz.getSimpleName()+". Check the source code or regex.");
		}
	}
	
	private void setFields() throws Exception {
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			
			FieldDefinition fieldDefinition = new FieldDefinition(field, OJBFileContent);
			OJBFields.add(fieldDefinition);
		}
		
	}
	
	private void setMethods() throws Exception {
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			
			MethodDefinition methodDefinition = new MethodDefinition(method, OJBFileContent);
			OJBMethods.add(methodDefinition);
		}
		
	}
	

	private void setImports() {
		
		final String regex = "import\\s+[\\w.]+;";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(this.OJBFileContent);
		
		while (matcher.find()) {
			imports.add(matcher.group(0));
		}
		
	}

	private void setPackageName() {
		
		String regex = "(?:package\\s+)(.+);";
		Pattern pattern = Pattern.compile(regex);
		
		Matcher matcher = pattern.matcher(this.OJBFileContent);
		
		if (matcher.find()) {
			this.packageName = matcher.group(1);
		}
	}
	
	private void setClassName() {
		
		this.className = this.sourceFilePath
							 .getFileName()
							 .toString()
							 .replaceFirst("[.][^.]+$", "");
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


	public List<FieldDefinition> getOJBFields() {
		return OJBFields;
	}


	public List<MethodDefinition> getOJBMethods() {
		return OJBMethods;
	}
	
}
