package converter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertedJPAFile {
	
	private String pathToSave;
	private String packageName;
	private String className;
	private String OJBFileContent;
	private List<Field> OJBFields = new ArrayList<>();
	private List<Method> OJBMethods = new ArrayList<>();
	private Path sourceFilePath;
	
	public ConvertedJPAFile(Path sourceFilePath, String pathToSave) throws ClassNotFoundException {
		
		this.sourceFilePath = sourceFilePath;
		this.pathToSave = pathToSave;
		readOJBFileContent();
	}
	
	private void readOJBFileContent() throws ClassNotFoundException {
		
		try {
			String fileContent = new String(Files.readAllBytes(sourceFilePath));
			this.OJBFileContent = fileContent;
			setClassInformation();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setClassInformation() throws ClassNotFoundException {
		
		setPackageName();
		setClassName();
		
		Class<?> clazz =  Class.forName(getPackageName() + "." + getClassName());
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			this.OJBFields.add(field);
		}
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			this.OJBMethods.add(method);
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


	public List<Field> getOJBFields() {
		return OJBFields;
	}


	public void setOJBFields(List<Field> oJBFields) {
		OJBFields = oJBFields;
	}


	public List<Method> getOJBMethods() {
		return OJBMethods;
	}


	public void setOJBMethods(List<Method> oJBMethods) {
		OJBMethods = oJBMethods;
	}
	
}
