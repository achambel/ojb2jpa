package converter.resources.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author John Doe
 * @version 1.0
 * @ojb.class
 *      table="tb_hello_world"
 */
public class HelloWorld {
	
	/**
     * @ojb.field column="first_field"
     *            length="50"
     *            jdbc-type="VARCHAR"
     */
	private String firstField = "A text as default value";
	private int secondField;
	String thirdField;
	
	private List<String> list = new ArrayList<>();
	
	private static final long serialVersionUID = 1;
	
	public static final String STATUS_IN_USE = "I";
	public static final String STATUS_LOST = "L";
	public static final String STATUS_BLOCKED = "B";
	private static final String STATUS_UNASSIGNED = "U";
	
	@Resource(name = "foo", description = "bar")
	public static final String DEFAULT_STATUS = STATUS_UNASSIGNED;
		
	@Deprecated
	@Resource(name = "foo", description = "bar")
	public static HashMap<String, String> STATUSES = new HashMap<String, String>() {{
		
		put(STATUS_IN_USE, "In use");
		put(STATUS_LOST, "Lost");
		put(STATUS_BLOCKED, "Blocked");
		put(STATUS_UNASSIGNED, "Unassigned");
	}};
	
	/**
     * @ojb.collection element-class-ref="com.aliquantum.objects.UserDocumentUser"
     *                 collection-class="org.apache.ojb.broker.util.collections.RemovalAwareCollection"
     *            	   foreignkey="userDocumentIdentity"
     *   		  	   auto-delete="true"
     *   		  	   auto-insert="true"
     *   		  	   auto-update="true"
     * 				   auto-retrieve="true" proxy="true"
     */
	private List<String> users;
	
	/**
     * @ojb.reference     class-ref="com.aliquantum.objects.User"
     *                     foreignkey="userIdentity"
     *                      auto-update="false"
     *                      auto-retrieve="true" proxy="true"
     */
    private Object user;

    /**
     * @ojb.field column="user_id"
     *            jdbc-type="INTEGER"
     */
    private long userIdentity;
	
	public String getFirstField() {
		return firstField;
	}
	
	@Deprecated
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

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
	
	

}
