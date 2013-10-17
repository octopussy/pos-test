package org.octopussy.scene.impl;

import org.octopussy.geo.ENUCoordinateSystem;
import org.octopussy.geo.LatLng;
import org.octopussy.networking.HttpService;
import org.octopussy.networking.WebRequestHandler;
import org.octopussy.scene.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
<osm version="0.6" generator="CGImap 0.3.0 (5470 thorn-02.openstreetmap.org)" copyright="OpenStreetMap and contributors" attribution="http://www.openstreetmap.org/copyright" license="http://opendatacommons.org/licenses/odbl/1-0/">
 <bounds minlat="55.7448630" minlon="37.9641500" maxlat="55.7499850" maxlon="37.9756200"/>
 <node id="994810682" visible="true" version="2" changeset="7294444" timestamp="2011-02-15T12:28:36Z" user="Ilyin19" uid="356391" lat="55.7465659" lon="37.9734163">
  <tag k="amenity" v="pharmacy"/>
  <tag k="opening_hours" v="Mo-Su 08:00-22:00"/>
 </node>
 <node id="995837265" visible="true" version="2" changeset="7158526" timestamp="2011-02-01T20:00:47Z" user="Ilyin19" uid="356391" lat="55.7478000" lon="37.9718688"/>
 <node id="995837270" visible="true" version="2" changeset="7158526" timestamp="2011-02-01T20:00:55Z" user="Ilyin19" uid="356391" lat="55.7480713" lon="37.9721811"/>
 <node id="995837268" visible="true" version="2" changeset="7158526" timestamp="2011-02-01T20:01:18Z" user="Ilyin19" uid="356391" lat="55.7479044" lon="37.9721795"/>
 <node id="995837267" visible="true" version="2" changeset="7158526" timestamp="2011-02-01T20:01:24Z" user="Ilyin19" uid="356391" lat="55.7479054" lon="37.9718698"/>
 <node id="995837298" visible="true" version="2" changeset="6695251" timestamp="2010-12-18T12:36:04Z" user="LexIgnatov" uid="316868" lat="55.7487979" lon="37.9719351"/>
 <way id="23453876" visible="true" version="19" changeset="15906682" timestamp="2013-04-29T10:44:04Z" user="Ilyin19" uid="356391">
  <nd ref="248733968"/>
  <nd ref="1783789702"/>
  <nd ref="1783789700"/>
  <nd ref="1783789698"/>
  <nd ref="1775343264"/>
  <nd ref="1564788876"/>
  <nd ref="254004149"/>
  </way>*/

/**
 * @author octopussy
 */
public class OSMSceneObjectsProviderImpl implements SceneObjectsProvider {
	private final HttpService mService;
	private SceneObjectsListener mListener;
	private final ENUCoordinateSystem mENUCoordinateSystem;

	// center 55.746829 37.969279
	private static String URL = "http://api.openstreetmap.org/api/0.6/map?bbox=37.96415,55.744863,37.97562,55.749985";

	public OSMSceneObjectsProviderImpl() {
		mService = new HttpService();
		LatLng centerLatLng = new LatLng(55.746829, 37.969279);
		mENUCoordinateSystem = new ENUCoordinateSystem(centerLatLng);
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

	private Scene createSceneObjects(OsmNodeHandler handler) {
		Scene objects = new Scene();
		for(OsmNodeHandler.WayElement way: handler.getWayElements().values()){
			if (way.tags.containsKey("building")){
				objects.addBuilding(createBuilding(way));
			}
		}
		return objects;
	}

	private Building createBuilding(OsmNodeHandler.WayElement way) {
		return new Building(createGeometry(way.nodes));
	}

	private Geometry createGeometry(List<OsmNodeHandler.NodeElement> nodes) {
		Geometry geom = new Geometry();
		for (OsmNodeHandler.NodeElement node : nodes){
			geom.addPoint(mENUCoordinateSystem.fromLatLngToLocal(node.lat, node.lon));
		}
		return geom;
	}

	private static class OsmNodeHandler extends DefaultHandler {
		private OsmElement mCurrentElement;

		private static enum ElementType{
			WAY, NODE
		}

		private Map<String, NodeElement> mNodeElements;
		private Map<String, WayElement> mWayElements;
		private ElementType mCurrentElementType;

		@Override
		public void startDocument() throws SAXException {
			mNodeElements = new HashMap<String, NodeElement>();
			mWayElements = new HashMap<String, WayElement>();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("node")){
				mCurrentElementType = ElementType.NODE;
				double lat = Double.parseDouble(attributes.getValue("lat"));
				double lon = Double.parseDouble(attributes.getValue("lon"));
				mCurrentElement = new NodeElement(parseAttributes(attributes), lat, lon);
				mNodeElements.put(mCurrentElement.attributes.id, (NodeElement)mCurrentElement);
			}else if (qName.equals("way")){
				mCurrentElementType = ElementType.WAY;
				mCurrentElement = new WayElement(parseAttributes(attributes));
				mWayElements.put(mCurrentElement.attributes.id, (WayElement)mCurrentElement);
			}else if (qName.equals("tag")){
				String k = attributes.getValue("k");
				String v = attributes.getValue("v");
				mCurrentElement.addTag(k, v);
			}else if (qName.equals("nd")){
				if (mCurrentElementType != ElementType.WAY){
					throw new IllegalStateException("Parse <nd>: Element type must be 'way'.");
				}
				String nodeRef = attributes.getValue("ref");
				NodeElement node = mNodeElements.get(nodeRef);
				if (node == null){
					throw new RuntimeException("Parse <nd>: cannot find node with id '" + nodeRef + "'");
				}

				((WayElement)mCurrentElement).addNode(node);
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

		public Map<String, WayElement> getWayElements() {
			return mWayElements;
		}

		private OsmAttributes parseAttributes(Attributes attributes) {
			boolean visible = attributes.getValue("visible").equals("true");
			String id = attributes.getValue("id");
			return new OsmAttributes(id, visible);
		}

		static class OsmAttributes {
			final String id;
			final boolean visible;

			OsmAttributes(String id, boolean visible) {
				this.id = id;
				this.visible = visible;
			}
		}

		static class OsmElement {
			final OsmAttributes attributes;
			final Map<String, String> tags;
			public OsmElement(OsmAttributes attributes) {
				this.attributes = attributes;
				tags = new HashMap<String, String>();
			}

			void addTag(String key, String value){
				tags.put(key, value);
			}
		}

		static class NodeElement extends OsmElement{
			final double lat;
			final double lon;
			public NodeElement(OsmAttributes attributes, double lat, double lon) {
				super(attributes);
				this.lat = lat;
				this.lon = lon;
			}

			@Override
			public String toString() {
				return "OsmNode: [" + lat + ", " + lon + "]";
			}
		}

		static class WayElement extends OsmElement {
			final List<NodeElement> nodes;
			public WayElement(OsmAttributes osmAttributes) {
				super(osmAttributes);
				nodes = new ArrayList<NodeElement>();
			}

			public void addNode(NodeElement node) {
				nodes.add(node);
			}
		}
	}
}
