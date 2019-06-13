package converter.resources.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John Doe
 * @version 1.0
 * @ojb.class
 *      table="tb_account"
 */
public class Account {
	
	/**
     * @ojb.collection element-class-ref="com.aliquantum.objects.AccountNote"
     *                 collection-class="org.apache.ojb.broker.util.collections.ManageableArrayList"
     *            	   foreignkey="accountIdentity"
     *   		  	   orderby="noteTimestamp=DESC"
     *   			   auto-delete="true"
     *   			   auto-insert="true"
     *   		  	   auto-update="true"
     * 				   auto-retrieve="true" proxy="true"
     */
	private List<AccountNote> notes = new ArrayList<>();

	public List<AccountNote> getNotes() {
		return notes;
	}

	public void setNotes(List<AccountNote> notes) {
		this.notes = notes;
	}
}
