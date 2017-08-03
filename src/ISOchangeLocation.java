import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ISOchangeLocation {

	HashMap<String, String> countryCode = new HashMap();

	public void ReplaceLocationISO(File file_metadata, File new_file_entry)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		int country_list_length;
		File metadata_file = file_metadata;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(metadata_file);
		doc.getDocumentElement().normalize();

		NodeList country_node_parent = doc.getElementsByTagName("country");
		Element country_element = (Element) country_node_parent.item(0);
		NodeList country_values = country_element.getElementsByTagName("option-child");

		for (int i = 0; i < country_values.getLength(); i++) {

			// String iso_country_code =
			// isoCodePlease(country_values.item(i).getAttributes().getNamedItem("name").getNodeValue().toString());
			// System.out.println(iso_country_code);

			// System.out.println(country_values.item(i).getAttributes().getNamedItem("name").getNodeValue());
			
			String countryName = country_values.item(i).getAttributes().getNamedItem("name").getNodeValue();
			String countryCode = isoCodePlease(countryName);
			
			
			
			
			// System.out.println(countryCode);
			// ApplyChangeToMetadata(countryName,countryCode, new_file_entry);
			

		}
		
		ApplyChangeToMetadata(new_file_entry);

	}
	
	//public void ApplyChangeToMetadata(String countryName, String countryCode, File final_csv_location) throws SAXException, IOException, ParserConfigurationException, TransformerException
	public void ApplyChangeToMetadata(File final_csv_location) throws SAXException, IOException, ParserConfigurationException, TransformerException
	{
		File processed_xml = final_csv_location;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(processed_xml);
		doc.getDocumentElement().normalize();

		NodeList country_node_parent = doc.getElementsByTagName("country");
		Element country_element = (Element) country_node_parent.item(0);
		NodeList country_values = country_element.getElementsByTagName("option-child");
		
		
		
		
		for (int i = 0; i < country_values.getLength(); i++) 
		{
			
			String countrybuffer = country_values.item(i).getAttributes().getNamedItem("name").getNodeValue();
			
			
			
				country_values.item(i).getAttributes().getNamedItem("name").setNodeValue(isoCodePlease(countrybuffer));
				country_values.item(i).getAttributes().getNamedItem("value").setNodeValue(isoCodePlease(countrybuffer));
				System.out.println(" &&&&&&&&&&&&&  Country Code Value Updated   &&&&&&&&&&&&");
				
				
		}
			
			
			
		TransformerFactory transformerFactory =  TransformerFactory.newInstance();
		Transformer transformer=  transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result=new StreamResult(processed_xml);
		transformer.transform(source, result);

		//For console Output.
		StreamResult consoleResult = new StreamResult(System.out);
		transformer.transform(source, consoleResult);
		
		
			
		}
		
		

	public String isoCodePlease(String country_name) {

		try {

			String countryName = country_name;
			String country_code = countryCode.get(countryName).toString();
			return country_code;

		}

		catch (NullPointerException e) {
			return null;
		}

	}

	public void csvtoHash(File csvfile) throws IOException {
		File csvFileValue = csvfile;
		String line = "";
		String cvsSplitBy = ";";

		BufferedReader br = new BufferedReader(new FileReader(csvFileValue));

		while ((line = br.readLine()) != null) {

			String[] country = line.split(cvsSplitBy);
			countryCode.put(country[0], country[1]);
		}

	}

	public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException, TransformerException

	{

		//String new_file_location = "/Users/sathya/Desktop/updated-flex-metadatadefinition-synclocation.xml";
		String new_file_location = args[2];
		File new_file = new File(new_file_location);

		//String xml_file_location = "/Users/sathya/Desktop/flex-metadatadefinition-synclocation.xml";
		String xml_file_location = args[0];
		File file1_metadata = new File(xml_file_location);

		//String csv_file_location = "/Users/sathya/Desktop/Countries-ISO-3166-1-alpha-2-Flex.csv";
		String csv_file_location = args[1];
		File iso_csv_file = new File(csv_file_location);

		// File file2_isofile = new File(args[1]);
		ISOchangeLocation iso = new ISOchangeLocation();
		iso.csvtoHash(iso_csv_file);

		InputStream is = new FileInputStream(xml_file_location);
		OutputStream os = new FileOutputStream(new_file_location);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}

		iso.ReplaceLocationISO(file1_metadata, new_file);

	}

}
