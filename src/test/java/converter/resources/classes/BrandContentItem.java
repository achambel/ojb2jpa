package converter.resources.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aliquantum.objects.BaseIdentifieableVO;
import com.aliquantum.objects.Brand;
import com.aliquantum.objects.BrandContentItemForLanguage;
import com.aliquantum.objects.EmailTemplateForLanguage;


/**
 * @author Andy Jones
 * @version 1.0
 *
 * Game 
 * @ojb.class
 *      table="tb_brand_content_item"
 *
 */
public class BrandContentItem extends BaseIdentifieableVO{

	private static final long serialVersionUID = 1;
	public static final String ONE_OFF_ITEM = "OneOI";
	
    /**
     * @ojb.reference     class-ref="com.aliquantum.objects.Brand"
     *                     foreignkey="brandIdentity"
     *                      auto-update="false"
     *                      auto-retrieve="true" proxy="true"
     */
    private Brand brand;

    /**
     * @ojb.field column="brand_id"
     *            jdbc-type="INTEGER"
     */
    private long brandIdentity;
   
	/**
     * @ojb.field column="category"
     *            length="5"
     *            jdbc-type="VARCHAR"
     */
    private String category = "";

	/**
     * @ojb.field column="name"
     *            length="20"
     *            jdbc-type="VARCHAR"
     */
    private String name = "";

    /**
     * @ojb.field column="description"
     *            length="255"
     *            jdbc-type="VARCHAR"
     */
    private String description = "";

    /**
     * @ojb.field column="created_timestamp"
     *  		  jdbc-type="TIMESTAMP"
     *			  conversion="org.apache.ojb.broker.accesslayer.conversions.JavaDate2SqlDateFieldConversion"
     */
    private Date createdTimestamp;

    /**
     * @ojb.field column="display_from_timestamp"
     *  		  jdbc-type="TIMESTAMP"
     *			  conversion="org.apache.ojb.broker.accesslayer.conversions.JavaDate2SqlDateFieldConversion"
     */
    private Date displayFromTimestamp;

    /**
     * @ojb.field column="display_to_timestamp"
     *  		  jdbc-type="TIMESTAMP"
     *			  conversion="org.apache.ojb.broker.accesslayer.conversions.JavaDate2SqlDateFieldConversion"
     */
    private Date displayToTimestamp;

    /**
     * @ojb.field column="priority"
     *            jdbc-type="INTEGER"
     *   		  conversion="org.apache.ojb.broker.accesslayer.conversions.Boolean2IntFieldConversion"
     */
	private boolean priority;

    /**
     * @ojb.collection element-class-ref="com.aliquantum.objects.BrandContentItemForLanguage"
     *                 collection-class="org.apache.ojb.broker.util.collections.ManageableArrayList"
     *            	   foreignkey="brandContentItemIdentity"
     *   		  	   auto-update="true"
     *   		  	   auto-delete="true"
     * 				   auto-retrieve="true"
     * 					proxy="true"
     */
	private List brandContentItemForLanguage = new ArrayList<>(); 

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeader(String languageCode) {
		
		for (int i = 0;i<brandContentItemForLanguage.size();i++){
			EmailTemplateForLanguage etfl = (EmailTemplateForLanguage) brandContentItemForLanguage.get(i);
			if (languageCode.equals(etfl.getBrandAllowableLanguage().getLanguage().getCode())){
				if (etfl.getSubject().startsWith("%")){
					return getHeader(etfl.getSubject().substring(1));
				}
				else{
					return etfl.getSubject();
				}
			}
		}
	
		return "";

	}
	

	public String getContent(String languageCode) {

		for (int i = 0;i<brandContentItemForLanguage.size();i++){
			BrandContentItemForLanguage etfl = (BrandContentItemForLanguage) brandContentItemForLanguage.get(i);
			if (languageCode.equals(etfl.getBrandAllowableLanguage().getLanguage().getCode())){
				if (etfl.getContent().startsWith("%")){
					return getContent(etfl.getContent().substring(1));
				}
				else{
					return etfl.getContent();
				}
			}
		}
	
		return "";
	}

	public long getBrandIdentity() {
		return brandIdentity;
	}

	public void setBrandIdentity(long brandIdentity) {
		this.brandIdentity = brandIdentity;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public List getBrandContentItemForLanguage() {
		return brandContentItemForLanguage;
	}

	public void setBrandContentItemForLanguage(List brandNewsItemForLanguage) {
		this.brandContentItemForLanguage = brandNewsItemForLanguage;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Date getDisplayFromTimestamp() {
		return displayFromTimestamp;
	}

	public void setDisplayFromTimestamp(Date displayFromTimestamp) {
		this.displayFromTimestamp = displayFromTimestamp;
	}

	public Date getDisplayToTimestamp() {
		return displayToTimestamp;
	}

	public void setDisplayToTimestamp(Date displayToTimestamp) {
		this.displayToTimestamp = displayToTimestamp;
	}

	public boolean isPriority() {
		return priority;
	}

	public void setPriority(boolean priority) {
		this.priority = priority;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String type) {
		this.category = type;
	}

	
}