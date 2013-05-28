package distributed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import communication.Message;

public class CustomMarshaller {
	private static CustomMarshaller singleton;

	public static CustomMarshaller getCustomMarshaller() {

		if (singleton == null)
			singleton = new CustomMarshaller();

		return singleton;
	}

	public synchronized String marshal(Message o) throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(o.getClass());

		final Marshaller marshaller = context.createMarshaller();

		final StringWriter stringWriter = new StringWriter();

		marshaller.marshal(o, stringWriter);
		return stringWriter.toString();
	}

	public synchronized Message unmarshal(String string) throws JAXBException,
	
	ParserConfigurationException, SAXException, IOException,
			ClassNotFoundException, DOMException {
		if(string==null||string==""){return null;}
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(string
				.getBytes()));
		doc.getDocumentElement().normalize();
		doc.getElementById("type");
		communication.Message received;
		received = (communication.Message) JAXB.unmarshal(new StringReader(
				string), Class.forName(doc.getElementsByTagName("type").item(0)
				.getTextContent()));

		Class.forName(received.type).cast(received);
		return received;

	}
}
