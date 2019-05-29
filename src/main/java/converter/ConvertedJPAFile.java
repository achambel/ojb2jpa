package converter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertedJPAFile {
	
	private String pathToSave;
	private List<String> imports = new ArrayList<>();
	private String packageName;
	private String className;
	private String OJBFileContent;
	private List<FieldDefinition> OJBFields = new ArrayList<>();
	private HashMap<Method, Object> OJBMethods = new HashMap<>();
	private Path sourceFilePath;
	
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setClassInformation() throws Exception {
		
		setPackageName();
		setImports();
		setClassName();
		
		Class<?> clazz =  Class.forName(getPackageName() + "." + getClassName());

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			
			FieldDefinition fieldDefinition = new FieldDefinition(field, this.OJBFileContent);
			this.OJBFields.add(fieldDefinition);
		}
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			String bodyMethod = findBodyMethod(method.getName());
			this.OJBMethods.put(method, bodyMethod);
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

	private String findBodyMethod(String methodName) throws Exception {
		
		String bodyMethod = null;
		
		final String regex = "\\b"+methodName+"\\b\\s*\\(.*(\\{(\\n?(?!public|protected|default|private).|\\n)*?\\}\\s{2,})";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		
		final Matcher matcher = pattern.matcher(this.OJBFileContent);
		
		if (matcher.find()) {
			bodyMethod = matcher.group(1);
		}
		
		if (bodyMethod == null) {
			throw new Exception("The content of method "+methodName +" was not found. Check the source file or method name");
		}
		
		return bodyMethod.trim();
		
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
		return imports;
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


	public HashMap<Method, Object> getOJBMethods() {
		return OJBMethods;
	}
	
}
