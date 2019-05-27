package converter.errors;

import java.io.IOException;

public class OJB2JPAPathNotFoundException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public OJB2JPAPathNotFoundException(String message) {
		super(message);
	}

}
