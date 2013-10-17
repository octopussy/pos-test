package org.octopussy.positioning.impl;

import org.octopussy.networking.HttpService;
import org.octopussy.networking.WebRequestHandler;
import org.octopussy.positioning.SceneObjects;
import org.octopussy.positioning.SceneObjectsListener;
import org.octopussy.positioning.SceneObjectsProvider;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author octopussy
 */
public class OSMSceneObjectsProviderImpl implements SceneObjectsProvider {
	private final HttpService mService;
	private SceneObjectsListener mListener;

	// center 55.746829 37.969279
	private static String URL = "http://api.openstreetmap.org/api/0.6/map?bbox=37.96415,55.744863,37.97562,55.749985";

	public OSMSceneObjectsProviderImpl() {
		mService = new HttpService();
	}

	@Override
	public void request(SceneObjectsListener listener) {
		mListener = listener;
		mService.request(URL, new WebRequestHandler() {
			@Override
			public void parse(InputStream inputStream) {
				parseOsmRawObjects(inputStream);
			}
		});
	}

	private void parseOsmRawObjects(InputStream inputStream) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		try {
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			OsmNodeHandler handler = new OsmNodeHandler();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(inputStream));

			if (mListener != null){
				mListener.onResult(createSceneObjects(handler));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private SceneObjects createSceneObjects(OsmNodeHandler handler) {
		return new SceneObjects();
	}

	private static class OsmNodeHandler extends DefaultHandler {
		private Map<String, NodeElement> mNodeElements;

		@Override
		public void startDocument() throws SAXException {
			mNodeElements = new HashMap<String, NodeElement>();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("node")){
				boolean visible = attributes.getValue("visible").equals("true");
				String id = attributes.getValue("id");
				NodeElement el = new NodeElement(visible);
				mNodeElements.put(id, el);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			super.endElement(uri, localName, qName);
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}

		static class NodeElement {
			final boolean visible;

			public NodeElement(boolean visible) {
				this.visible = visible;
			}
		}
	}
}
