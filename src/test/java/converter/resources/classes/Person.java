package converter.resources.classes;

//import org.apache.ojb.broker.PersistenceBroker;

/**
 * @author John Doe
 * @version 1.0
 * @ojb.class
 *      table="tb_person"
 */
public class Person extends BaseIdentifieableVO {

	/**
     * @ojb.field column="age"
     *            jdbc-type="INTEGER"
     */
    private long age;

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}
    
    
}
