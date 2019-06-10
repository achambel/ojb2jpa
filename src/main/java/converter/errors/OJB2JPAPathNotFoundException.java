package converter.errors;

import java.io.IOException;

/**
 * 
 * @author Adriano Chambel <acmlima.softweb@gmail.com>
 *
 */
public class OJB2JPAPathNotFoundException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public OJB2JPAPathNotFoundException(String message) {
		super(message);
	}

}
