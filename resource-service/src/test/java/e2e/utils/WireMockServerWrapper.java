package e2e.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;


public class WireMockServerWrapper {

	private final WireMockServer wireMockServer;
	private final int port;

	public WireMockServerWrapper() {
		this.wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
		this.port = this.wireMockServer.port();
	}

	public WireMockServer getWireMockServer() {
		return wireMockServer;
	}

	public int getPort() {
		return port;
	}
}
