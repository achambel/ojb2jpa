package converter.resources.classes;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld {
	
	private String firstField;
	private int secondField;
	
	public String getFirstField() {
		return firstField;
	}
	
	public void setFirstField(String firstField) {
		this.firstField = firstField;
	}
	
	public int getSecondField() {
		return secondField;
	}
	
	public void setSecondField(int secondField) {
		this.secondField = secondField;
	}
	
	public List<String> nonGetterMethod() {
		
		return new ArrayList<>();
	}

}
