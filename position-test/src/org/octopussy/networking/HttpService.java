package org.octopussy.networking;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author octopussy
 */
public class HttpService {
	private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;

	public void request(String urlString, WebRequestHandler handler) {
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			handler.parse(connection.getInputStream());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
