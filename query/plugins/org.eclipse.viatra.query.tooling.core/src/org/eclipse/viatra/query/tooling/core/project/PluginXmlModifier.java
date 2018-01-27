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
package org.eclipse.viatra.query.tooling.core.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.viatra.query.tooling.core.generator.ExtensionData;
import org.eclipse.viatra.query.tooling.core.generator.ViatraQueryGeneratorPlugin;
import org.eclipse.xtext.xbase.lib.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeBasedTable;

/**
 * Stateful helper class for updating the extension definitions in a plugin.xml file of a PDE plug-in project.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class PluginXmlModifier {

    Table<String, String, List<ExtensionData>> extensionTable = HashBasedTable.create();
    private Document document;
    private IFile pluginXml;

    /**
     * Loads the plugin.xml file from the selected project. The project is assumed to be a PDE plug-in project for this
     * to work.
     * 
     * @param project
     * @throws CoreException
     */
    public void loadPluginXml(IProject project) throws CoreException {
        try {
            pluginXml = project.getFile("plugin.xml");
            if (pluginXml.exists()) {
                pluginXml.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
                document = XmlDocumentHelper.loadDocument(pluginXml.getContents());
            } else {
                document = XmlDocumentHelper.getEmptyXmlDocument();
                final Element root = document.createElement("plugin");
                document.appendChild(root);
            }
            loadExtensionData(document);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw wrapException(e);
        }
    }

    /**
     * Saves the changes to the plugin.xml file loaded previously.
     * 
     * @throws CoreException
     */
    public void savePluginXml() throws CoreException {
        try (InputStream stream = XmlDocumentHelper.saveDocument(document)) {
            if (pluginXml.exists()) {
                pluginXml.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
                pluginXml.setContents(stream, false, true, new NullProgressMonitor());
            } else {
                pluginXml.create(stream, false, new NullProgressMonitor());
            }
        } catch (TransformerException e) {
            throw wrapException(e);
        } catch (IOException e) {
            wrapException(e);
        }
    }

    /**
     * Adds a collection of extensions to the current xml model. If previous extensions with the same name are already
     * available, it updates them instead.
     */
    public void addExtensions(Iterable<ExtensionData> contributedExtensions) {
        Table<String, String, List<ExtensionData>> table = TreeBasedTable.create();
        for (ExtensionData data : contributedExtensions) {
            addExtensionToMap(data, table);
        }
        for (Cell<String, String, List<ExtensionData>> cell : table.cellSet()) {
            if (extensionTable.contains(cell.getRowKey(), cell.getColumnKey())) {
                // Updating existing items; using its original location
                final List<ExtensionData> oldList = extensionTable.get(cell.getRowKey(), cell.getColumnKey());
                final ExtensionData oldData = oldList.get(0);
                for (ExtensionData data : cell.getValue()) {
                    document.adoptNode(data.getNode());
                    Node parentNode = oldData.getNode().getParentNode();
                    if (parentNode != null) {
                        parentNode.insertBefore(data.getNode(), oldData.getNode());
                    } else {
                        document.getDocumentElement().appendChild(data.getNode());
                    }
                }
                // Removing old items
                for (ExtensionData data : oldList) {
                    removeNode(data.getNode());
                }
            } else {
                // New items, adding nodes in lexicographical order
                for (ExtensionData data : cell.getValue()) {
                    document.adoptNode(data.getNode());
                    final Node root = document.getDocumentElement();
                    Node nextIdChild = findExtensionFollowingId(cell.getRowKey(), root);
                    if(nextIdChild != null){
                        root.insertBefore(data.getNode(), nextIdChild);
                    } else {
                        // no following id, insert to end
                        root.appendChild(data.getNode());
                    }
                }
            }
            extensionTable.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }

    /**
     * @param extensionId
     *            the id that should be lexicographically preceding the returned node
     * @return true if there is at least a single extension with the given identifier
     * @since 1.7
     */
    public boolean hasExtensionFollowingId(String extensionId) {
        return extensionTable.containsColumn(extensionId);
    }
    
    /**
     * Find the extension node in the document node that has an id lexicographically following the given extensionId.
     * 
     * @param extensionId
     *            the id that should be lexicographically preceding the returned node
     * @param root
     *            the node whose children are explored
     * @return the node that is named "extension" and has attribute "id" with value lexicographically following the
     *         given extensionId.
     */
    private Node findExtensionFollowingId(String extensionId, final Node root) {
        Node child = root.getFirstChild();
        Node nextIdChild = null;
        while(nextIdChild == null && child != null){
            if("extension".equals(child.getNodeName())){
                Node namedItem = child.getAttributes().getNamedItem("id");
                if(namedItem != null && namedItem.getNodeValue().compareTo(extensionId) > 0) {
                    nextIdChild = child;
                }
            }
            child = child.getNextSibling();
        }
        return nextIdChild;
    }

    /**
     * Removes a collection of extensions from the current xml model. The removed extensions are described as
     * «id»-«extension point id» pairs. If multiple extensions are available with the same metadata, all of them are
     * removed.
     * 
     * @param removedExtensions
     */
    public void removeExtensions(Iterable<Pair<String, String>> removedExtensions) {
        for (Pair<String, String> ex : removedExtensions) {
            final String id = ex.getKey();
            final String point = ex.getValue();
            if (Strings.isNullOrEmpty(id)) {
                if (extensionTable.containsColumn(point)) {
                    for (List<ExtensionData> elements : extensionTable.columnMap().get(point).values()) {
                        for (ExtensionData data : elements) {
                            removeNode(data.getNode());
                        }
                    }
                }
            } else {
                if (extensionTable.contains(id, point)) {
                    for (ExtensionData data : extensionTable.get(id, point)) {
                        removeNode(data.getNode());
                    }
                    extensionTable.remove(id, point);
                }
            }
        }
    }

    private CoreException wrapException(Exception e) {
        return new CoreException(new Status(IStatus.ERROR, ViatraQueryGeneratorPlugin.BUNDLE_ID,
                "Error while processing plugin.xml", e));
    }

    private void loadExtensionData(Node n) {
        int type = n.getNodeType();
        if (type == Node.ELEMENT_NODE && "extension".equals(n.getNodeName())) {
            ExtensionData data = new ExtensionData(n);
            if (!Strings.isNullOrEmpty(data.getId())) {
                addExtensionToMap(data, extensionTable);
            }
            return;
        }
        for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
            loadExtensionData(child);
        }
    }

    private void addExtensionToMap(ExtensionData data, Table<String, String, List<ExtensionData>> table) {
        String id = data.getId();
        String point = data.getPoint();
        if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(point)) {
            return;
        }
        List<ExtensionData> extensions = null;
        if (table.contains(id, point)) {
            extensions = table.get(id, point);
        } else {
            extensions = Lists.newArrayList();
            table.put(id, point, extensions);
        }
        extensions.add(data);
    }

    private void removeNode(Node nodeToRemove) {
        if (nodeToRemove.getParentNode() != null) {
            nodeToRemove.getParentNode().removeChild(nodeToRemove);   		
        }
    }
}
