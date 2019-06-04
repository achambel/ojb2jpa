package converter;

import java.util.List;
import java.util.Set;

public abstract interface IOJBDefinition {

	void parse2JPA(String doclet) throws Exception;
	Set<String> getImports();
	List<String> getJpaAnnotations();
	
}
