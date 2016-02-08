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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utility methods for XML document handling.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public final class XmlDocumentHelper {

    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    {
        // Following settings required to maintain original lexical information
        FACTORY.setCoalescing(false);
        FACTORY.setExpandEntityReferences(false);
        FACTORY.setIgnoringComments(false);
        FACTORY.setIgnoringElementContentWhitespace(false);
    }

    private static Transformer serializingTransformer;

    private XmlDocumentHelper() {
        // Hiding constructor for utility class
    }

    public static DocumentBuilderFactory getDefaultFactory() {
        return FACTORY;
    }

    public static Document loadDocument(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        return getDefaultFactory().newDocumentBuilder().parse(is);
    }

    public static InputStream saveDocument(Document document) throws TransformerException, IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outStream);
        getXmlSerializerTransformer().transform(new DOMSource(document), result);

        return new ByteArrayInputStream(outStream.toByteArray());
    }

    public static Document getEmptyXmlDocument() throws ParserConfigurationException {
        return getDefaultFactory().newDocumentBuilder().newDocument();
    }

    private static Transformer getXmlSerializerTransformer() throws TransformerConfigurationException, IOException {
        if (serializingTransformer == null) {
            TransformerFactory tf = TransformerFactory.newInstance();
            serializingTransformer = tf.newTransformer(new StreamSource(FileLocator.openStream(
                    FrameworkUtil.getBundle(XmlDocumentHelper.class), new Path("/formatter.xslt"), false)));
        }
        return serializingTransformer;
    }
}
