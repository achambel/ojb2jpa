package converter;

import java.util.List;

public abstract interface IOJBDefinition {

	void parse2JPA(String doclet);
	List<String> getImports();
	List<String> getJpaAnnotations();
	
}
