package converter.resources.classes;

/**
 * @author John Doe
 * @version 1.0
 * @ojb.class
 *      table="tb_account_note"
 */
public class AccountNote {

	/**
     * @ojb.reference     class-ref="com.aliquantum.objects.Account"
     *                     foreignkey="accountIdentity"
     *                      auto-update="false"
     *                      auto-retrieve="true" proxy="true"
     */
    private Account account;

    /**
     * @ojb.field column="ACCOUNT_ID"
     *            jdbc-type="INTEGER"
     */
    private long accountIdentity;
    
    /**
     * @ojb.field column="activated"
     *            jdbc-type="INTEGER"
     */
    private boolean activated;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public long getAccountIdentity() {
		return accountIdentity;
	}

	public void setAccountIdentity(long accountIdentity) {
		this.accountIdentity = accountIdentity;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
}
