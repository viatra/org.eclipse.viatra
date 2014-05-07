/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.core.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utility methods for XML document handling.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public final class XmlDocumentHelper {

    private static DocumentBuilderFactory factory;
    
    private XmlDocumentHelper() {
        //Hiding constructor for utility class
    }

    public static DocumentBuilderFactory getDefaultFactory() {
        if (factory == null) {
            factory = DocumentBuilderFactory.newInstance();
            // Following settings required to maintain original lexical information
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(false);
            factory.setIgnoringComments(false);
            factory.setIgnoringElementContentWhitespace(false);
        }
        return factory;
    }

    public static Document loadDocument(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        return getDefaultFactory().newDocumentBuilder().parse(is);
    }

    public static InputStream saveDocument(Document document) throws TransformerException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        StreamResult result = new StreamResult(outStream);
        transformer.transform(new DOMSource(document), result);
        
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    public static Document getEmptyXmlDocument() throws ParserConfigurationException {
        return getDefaultFactory().newDocumentBuilder().newDocument();
    }
}
