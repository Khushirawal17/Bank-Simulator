package com.example.bank.simulator1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

	private String bankName;
	private int port = 8080;
	private long defaultDelayMs = 0;
	private long maxDelayMs = 30000;
	private boolean callbackEnabled = true;

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getDefaultDelayMs() {
		return defaultDelayMs;
	}

	public void setDefaultDelayMs(long defaultDelayMs) {
		this.defaultDelayMs = defaultDelayMs;
	}

	public long getMaxDelayMs() {
		return maxDelayMs;
	}

	public void setMaxDelayMs(long maxDelayMs) {
		this.maxDelayMs = maxDelayMs;
	}

	public boolean isCallbackEnabled() {
		return callbackEnabled;
	}

	public void setCallbackEnabled(boolean callbackEnabled) {
		this.callbackEnabled = callbackEnabled;
	}
}