package com.example.bank.simulator1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bank")
public class BankProperties {

	private String name;
	private String payeeId;
	private Security security = new Security();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPayeeId() {
		return payeeId;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public static class Security {

		private String checksumKey;
		private boolean encryptionEnabled;

		public String getChecksumKey() {
			return checksumKey;
		}

		public void setChecksumKey(String checksumKey) {
			this.checksumKey = checksumKey;
		}

		public boolean isEncryptionEnabled() {
			return encryptionEnabled;
		}

		public void setEncryptionEnabled(boolean encryptionEnabled) {
			this.encryptionEnabled = encryptionEnabled;
		}
	}
}