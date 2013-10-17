package org.octopussy.networking;

import java.io.InputStream;

/**
 * @author octopussy
 */
public abstract class WebRequestHandler {
	public abstract void parse(InputStream inputStream);
}
