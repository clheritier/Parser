package fr.leborgne.converter.jsonldconverter.parser;

import java.io.InputStream;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonString;
import org.apache.jena.atlas.json.JsonValue;

public class MyParser {

	private static final String DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
	private static final String TYPE = "@type";
	private static final String ID = "@id";
	JsonValue jsonVal;
	private static final String OBJECT_PROPERTY = "ObjectProperty";
	private static final String DATA_PROPERTY = "DatatypeProperty";
	private static final String NAMED_INDIVIDUAL = "NamedIndividual";

	public MyParser(InputStream input) {
		//		parser = new JSON_Parser(input);
		jsonVal = JSON.parseAny(input);
	}

	public String parse()
	{
		StringBuilder result = new StringBuilder();
		JsonArray array = jsonVal.getAsArray();
		parseObjectProperties(result, array);
		parseDataProperties(result, array);
		parseIndividuals(result, array);
		return result.toString();
	}

	private void parseIndividuals(StringBuilder result, JsonArray array) {
		StringBuilder uncomplete = new StringBuilder();
		for(int i=0; i<array.size(); i++)
		{
			JsonObject jsonObj = array.get(i).getAsObject();
			JsonArray types = jsonObj.get(TYPE).getAsArray();
			for(JsonValue type : types)
			{
				String tString = type.getAsString().value();
				if(tString.endsWith(NAMED_INDIVIDUAL))
				{
					String id = jsonObj.get(ID).getAsString().value();
					jsonObj.forEach((key, value) -> {
						if(!(key.equals(ID) || key.equals(TYPE)))
						{
							JsonArray jArray = value.getAsArray();
							JsonObject jObj = jArray.get(0).getAsObject();
							JsonValue jsonValue = jObj.get(ID);
							if(jsonValue != null)
							{
								JsonString asString = jsonValue.getAsString();
								String val = asString.value();
								result.append("<" + id.substring(id.lastIndexOf("#")+1) + ">\t");
								result.append("<" + key.substring(key.lastIndexOf("#")+1) + ">\t");
								result.append("<" + val.substring(val.lastIndexOf("#")+1) + ">\n");
							}
							else
								uncomplete.append("<" + id.substring(id.lastIndexOf("#")+1) + ">\n");
						}
					});
				}
			}
		}
		result.append("\n");
		result.append(uncomplete);
	}

	private void parseDataProperties(StringBuilder result, JsonArray array) {
		for(int i=0; i<array.size(); i++)
		{
			JsonObject jsonObj = array.get(i).getAsObject();
			JsonArray types = jsonObj.get(TYPE).getAsArray();
			for(JsonValue type : types)
			{
				String tString = type.getAsString().value();
				if(tString.endsWith(DATA_PROPERTY))
				{
					String id = jsonObj.get(ID).getAsString().value();
					result.append("<" + id.substring(id.lastIndexOf("#")+1) + ">\t");
					result.append("<" + DOMAIN + ">\t");
					String id2 = jsonObj.get(DOMAIN).getAsArray().get(0).getAsObject().get(ID).getAsString().value();
					result.append("<" + id2.substring(id2.lastIndexOf("#")+1) + ">\n");
				}
			}
		}
		result.append("\n");
	}

	private void parseObjectProperties(StringBuilder result, JsonArray array) {
		for(int i=0; i<array.size(); i++)
		{
			JsonObject jsonObj = array.get(i).getAsObject();
			JsonArray types = jsonObj.get(TYPE).getAsArray();
			for(JsonValue type : types)
			{
				String tString = type.getAsString().value();
				if(tString.endsWith(OBJECT_PROPERTY))
				{
					String id = jsonObj.get(ID).getAsString().value();
					result.append("<" + id.substring(id.lastIndexOf("#")+1) + ">\t");
					result.append("<" + DOMAIN + ">\t");
					String id2 = jsonObj.get(DOMAIN).getAsArray().get(0).getAsObject().get(ID).getAsString().value();
					result.append("<" + id2.substring(id2.lastIndexOf("#")+1) + ">\n");
				}
			}
		}
		result.append("\n");
	}
}
