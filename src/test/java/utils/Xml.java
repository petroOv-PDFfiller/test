package utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.*;

/**
 * Created by Vladyslav on 08.07.2015.
 */
public class Xml {

    public Document doc;
    private String filePath = null;

    public Xml(String filePath) {
        this.filePath = filePath;
        init();
    }

    public Xml() {
        init();
    }

    private void init() {
        Logger.info("Initializing new xml object");
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.newDocument();
            Element root = doc.createElement("root");
            doc.appendChild(root);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                Logger.info("Parsing " + filePath);
                try {
                    doc = builder.parse(new InputSource(new InputStreamReader(
                            new FileInputStream(filePath))));
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * param String
     * elementName
     *
     * @return <code>Element</code> element
     */
    public Element addElement(String elementName) {
        Element newElement = null;
        Element mainElement = doc.getDocumentElement();
        newElement = doc.createElement(elementName);
        mainElement.appendChild(newElement);
        return newElement;
    }

    /**
     * param String elementXpath
     *
     * @return <code>Element</code> element
     */
    public Element findElement(String xpathValue) {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        NodeList node = null;
        try {
            XPathExpression expr = xpath.compile(xpathValue);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            node = (NodeList) result;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return (Element) node.item(0);
    }

    /**
     * @param xpathValue
     * @return <code>NodeList</code> nodeList
     */
    public NodeList findElements(String xpathValue) {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        NodeList node = null;
        try {
            XPathExpression expr = xpath.compile(xpathValue);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            node = (NodeList) result;
        } catch (XPathExpressionException e) {
            Logger.info("Wrong xpath " + xpathValue);
        }
        return node;
    }

    public void transform() {
        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult resultCreate = new StreamResult(filePath);
        try {
            transformer.transform(source, resultCreate);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void transformHtml(String xslStylesheetPath, String outputFilePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            Source xslDoc = new StreamSource(xslStylesheetPath);
            OutputStream htmlFile = new FileOutputStream(outputFilePath);
            transformer = transformerFactory.newTransformer(xslDoc);
            transformer.transform(source, new StreamResult(htmlFile));
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        File xmlFile = new File(filePath);
        if (xmlFile.exists())
            xmlFile.delete();
    }

    public String getAttribute(Element element, String atr) {
        try {
            return element.getAttribute(atr);
        } catch (Exception e) {
            Logger.info("Cannot get attribute '" + atr + "' from element");
            return null;
        }
    }
}