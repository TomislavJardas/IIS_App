package com.tjardas.iisapi.endpoint;

import com.tjardas.iisapi.xml.Countries;
import com.tjardas.iisapi.xml.SearchRequest;
import com.tjardas.iisapi.xml.SearchResponse;
import com.tjardas.iisapi.service.CsvService;
import com.tjardas.iisapi.service.XmlValidationService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import static com.ibm.wsdl.util.xml.DOM2Writer.nodeToString;


@Endpoint
@RequiredArgsConstructor
public class CountryEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(CountryEndpoint.class);
    private static final String NAMESPACE_URI = "http://example.com/countries";
    private final CsvService csvService;
    private final XmlValidationService xmlValidationService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SearchRequest")
    @ResponsePayload
    public SearchResponse searchCountries(@RequestPayload SearchRequest request) throws Exception {
        String searchTerm = request.getSearchTerm();

        if (searchTerm == null) {
            throw new IllegalArgumentException("Search term cannot be null");
        }
        Countries countries = csvService.getFilteredCountries(null, null, null);

        JAXBContext context = JAXBContext.newInstance(Countries.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(countries, writer);

        String xmlContent = writer.toString();

        String searchResult = searchXmlWithXpath(xmlContent, searchTerm);

        xmlValidationService.validateXml(searchResult, "SOAP-validation.xsd");

        if (searchResult.isEmpty()) {
            throw new Exception("Search result is empty");
        }
        SearchResponse response = new SearchResponse();
        response.setCountries(parseXmlToCountryList(searchResult));

        return response;
    }

    private String searchXmlWithXpath(String xml, String searchTerm) throws Exception {
        XPath xpath = createXPathWithNamespace();
        XPathExpression expr = compileXPathExpression(xpath, searchTerm);
        NodeList nodes = evaluateXPathExpression(expr, xml);
        return buildSearchResultXml(nodes);
    }

    private XPath createXPathWithNamespace() {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("ns2".equals(prefix)) {
                    return "http://example.com/countries";
                }
                return XMLConstants.NULL_NS_URI;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                if ("http://example.com/countries".equals(namespaceURI)) {
                    return "ns2";
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null;
            }
        });
        return xpath;
    }

    private XPathExpression compileXPathExpression(XPath xpath, String searchTerm) throws Exception {
        String xpathExpression = "//*[local-name()='Country']/*[local-name()='name' and contains(text(), '" + searchTerm + "')]";
        logger.debug("XPath expression: {}", xpathExpression);
        return xpath.compile(xpathExpression);
    }

    private NodeList evaluateXPathExpression(XPathExpression expr, String xml) throws Exception {
        InputSource inputSource = new InputSource(new StringReader(xml));
        return (NodeList) expr.evaluate(inputSource, XPathConstants.NODESET);
    }

    private String buildSearchResultXml(NodeList nodes) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("<Countries xmlns=\"http://example.com/countries\">");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i).getParentNode();
            resultBuilder.append(nodeToString(node));
        }
        resultBuilder.append("</Countries>");
        return resultBuilder.toString();
    }

    private List<Countries.Country> parseXmlToCountryList(String xml) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Countries.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        Countries countries = (Countries) unmarshaller.unmarshal(reader);
        return countries.getCountries();
    }

}