package com.jecfbagsx.android.data;

public class AccountInfo {
	private String accountName;
	private String password;
	private String prefName;
	private AccountType accType;
	private boolean isDefault = false;

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public AccountInfo(AccountType accType, String prefName) {
		super();
		this.prefName = prefName;
		this.accType = accType;
	}

	public AccountType getAccType() {
		return accType;
	}

	public void setAccType(AccountType accType) {
		this.accType = accType;
	}

	public String getPrefName() {
		return prefName;
	}

	public void setPrefName(String prefName) {
		this.prefName = prefName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "AccountInfo [accountName=" + accountName + ", password="
				+ "*******" + ", prefName=" + prefName + ", accType=" + accType
				+ ", isDefault=" + isDefault + "]";
	}
}
