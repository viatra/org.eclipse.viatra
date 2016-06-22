/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryregistry.index

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import com.google.inject.Inject
import java.util.Map
import java.util.WeakHashMap
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IResourceChangeEvent
import org.eclipse.core.resources.IResourceChangeListener
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider
import org.eclipse.viatra.query.runtime.registry.IConnectorListener
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry
import org.eclipse.viatra.query.runtime.registry.connector.AbstractRegistrySourceConnector
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import org.eclipse.xtext.resource.DerivedStateAwareResource
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.resource.IResourceDescription
import org.eclipse.xtext.resource.IResourceDescription.Delta
import org.eclipse.xtext.resource.IResourceDescription.Event
import org.eclipse.xtext.resource.IResourceDescription.Event.Listener
import org.eclipse.xtext.resource.IResourceDescriptions
import org.eclipse.xtext.ui.notification.IStateChangeEventBroker
import org.eclipse.xtext.ui.resource.IResourceSetProvider

/**
 * @author Abel Hegedus
 *
 */
class XtextIndexBasedRegistryUpdater {
    
    public static final String DYNAMIC_CONNECTOR_ID_PREFIX = "dynamic:"
    
    private final IStateChangeEventBroker source
    private final IResourceDescriptions descriptions
    private final IResourceSetProvider resourceSetProvider
    
    private final QueryRegistryUpdaterListener listener
    private final Map<String,PatternDescriptionBasedSourceConnector> connectorMap
    private final WorkspaceBuildCompletedListener workspaceListener
    private IQuerySpecificationRegistry connectedRegistry
    private Map<IProject, ResourceSet> resourceSetMap = new WeakHashMap<IProject, ResourceSet>();
    
    @Inject
    new(IStateChangeEventBroker source, IResourceDescriptions descriptions, IResourceSetProvider resSetProvider) {
        super()
        this.source = source
        this.descriptions = descriptions
        this.resourceSetProvider = resSetProvider
        this.workspaceListener = new WorkspaceBuildCompletedListener(this)
        this.listener = new QueryRegistryUpdaterListener(this)
        this.connectorMap = Maps.newTreeMap
    }
    
    def connectIndexToRegistry(IQuerySpecificationRegistry registry) {
        if(connectedRegistry == null){
            connectedRegistry = registry
            descriptions.allResourceDescriptions.forEach[resourceDesc |
                if (!resourceDesc.URI.isPlatformResource) {
                    // only care about platform resources
                    return
                }
                val patternObjects = resourceDesc.getExportedObjectsByType(PatternLanguagePackage.Literals.PATTERN)
                if (patternObjects.empty) {
                    // only care if there are patterns
                    return
                }
                // create connector based on URI
                val uri = resourceDesc.URI.toString
                val projectName = resourceDesc.URI.segment(1)
                val projectExists = ResourcesPlugin.workspace.root.getProject(projectName).exists
                if(!projectExists){
                    // only care about workspace projects
                    return
                }
                val connectorId = DYNAMIC_CONNECTOR_ID_PREFIX + projectName
                var connector = connectorMap.get(connectorId)
                if(connector == null) {
                    connector = new PatternDescriptionBasedSourceConnector(connectorId)
                    connectorMap.put(connectorId, connector)
                }
                val conn = connector
                val resourceSet = createResourceSet(projectName)
                // create specification providers based on patterns
                patternObjects.forEach[
                    val provider = new PatternDescriptionBasedSpecificationProvider(this, resourceDesc, it, resourceSet)
                    conn.addProvider(uri, provider)
                ]
            ]
            connectorMap.values.forEach[ connector |
                registry.addSource(connector)
            ]
            source.addListener(listener)
            ResourcesPlugin.getWorkspace().addResourceChangeListener(workspaceListener)
        }
    }
    
    def disconnectIndexFromRegistry() {
        if(connectedRegistry != null){
            connectorMap.values.forEach[
                connectedRegistry.removeSource(it)
            ]
            source.removeListener(listener)
            connectorMap.clear
            connectedRegistry = null
        }
    }
    
    def createResourceSet(String projectName) {
        val root = ResourcesPlugin.getWorkspace().getRoot();
        val project = root.getProject(projectName);
        val resourceSet = resourceSetProvider.get(project);
        return resourceSet
    }
    
    @FinalFieldsConstructor
    private static final class QueryRegistryUpdaterListener implements Listener{
        
        final XtextIndexBasedRegistryUpdater updater
        
        override descriptionsChanged(Event event) {
            event.deltas.forEach[ delta |
                val oldDesc = delta.old
                val newDesc = delta.getNew
                val uri = delta.uri.toString
                if (!delta.uri.isPlatformResource) {
                    // only care about platform resources
                    return
                }
                val projectName = delta.uri.segment(1)
                val projectExists = ResourcesPlugin.workspace.root.getProject(projectName).exists
                if(!projectExists){
                    // only care about workspace projects
                    return
                }
                val connectorId = DYNAMIC_CONNECTOR_ID_PREFIX + projectName
                
                try {
                    if (oldDesc != null) {
                        if(newDesc == null) {
                            // delete
                            val connector = updater.connectorMap.get(connectorId)
                            if(connector != null){
                                connector.clearProviders(uri)
                                if(connector.descriptionToProvider.empty){
                                    // remove source connector
                                    updater.connectedRegistry.removeSource(connector)
                                    updater.connectorMap.remove(connectorId)
                                }
                            }
                        } else {
                            // update
                            delta.processResourceDescription(newDesc, connectorId, projectName)
                        }
                    } else if(newDesc != null && !newDesc.getExportedObjectsByType(PatternLanguagePackage.Literals.PATTERN).empty) {
                        // create connector based on URI or update if project connector already exists
                        delta.processResourceDescription(newDesc, connectorId, projectName)
                    }
                } catch (Exception ex) {
                    val logger = ViatraQueryLoggingUtil.getLogger(XtextIndexBasedRegistryUpdater)
                    logger.error('''Could not update registry based on Xtext index for «uri»''', ex)
                }
            ]
        }
        
        def processResourceDescription(Delta delta, IResourceDescription desc, String connectorId, String projectName) {
            if(updater.connectorMap.containsKey(connectorId)){
                updater.workspaceListener.connectorsToUpdate.put(desc.URI, desc)
            } else {
                if(!desc.getExportedObjectsByType(PatternLanguagePackage.Literals.PATTERN).empty) {
                    // create connector based on URI
                    val connector = new PatternDescriptionBasedSourceConnector(connectorId)
                    updater.connectorMap.put(connectorId, connector)
                    if(delta.haveEObjectDescriptionsChanged) {
                        val resourceSet = updater.createResourceSet(projectName)
                        desc.getExportedObjectsByType(PatternLanguagePackage.Literals.PATTERN).forEach[
                            val provider = new PatternDescriptionBasedSpecificationProvider(updater, desc, it, resourceSet)
                            connector.addProvider(desc.URI.toString, provider)
                        ]
                    }
                    updater.connectedRegistry.addSource(connector)
                }
            }
        }
        
    }
    
    @FinalFieldsConstructor
    private static final class PatternDescriptionBasedSpecificationProvider implements IPatternBasedSpecificationProvider {
        
        final XtextIndexBasedRegistryUpdater updater
        final IResourceDescription resourceDesc
        final IEObjectDescription description
        final ResourceSet resourceSet
        IQuerySpecification<?> specification
        
        override getFullyQualifiedName() {
            return description.qualifiedName.toString
        }
        
        override get() {
            if(specification == null){
                val pattern = findPatternForDescription
                val builder = new SpecificationBuilder();
                specification = builder.getOrCreateSpecification(pattern)
            }
            return specification
        }
        
        override getSpecification(SpecificationBuilder builder) {
            val pattern = findPatternForDescription
            val spec = builder.getOrCreateSpecification(pattern)
            return spec
        }
        
        def Pattern findPatternForDescription() {
            var pattern = description.EObjectOrProxy
            if(pattern.eIsProxy){
                pattern = EcoreUtil.resolve(pattern, resourceSet)
            }
            if(pattern.eIsProxy){
                throw new IllegalStateException('''Cannot load specification «fullyQualifiedName» from Xtext index''')
            }
            return pattern as Pattern
        }
        
        override getSourceProjectName() {
            resourceDesc.URI.segment(1)
        }
        
        override getSpecificationSourceURI() {
            description.EObjectURI
        }
        
    }
    
    private static final class PatternDescriptionBasedSourceConnector extends AbstractRegistrySourceConnector {
        
        final Multimap<String,IQuerySpecificationProvider> descriptionToProvider; 
        
        new(String identifier) {
            super(identifier, false)
            this.descriptionToProvider = HashMultimap.create
        }
        
        def addProvider(String resourceUri, PatternDescriptionBasedSpecificationProvider provider) {
            descriptionToProvider.put(resourceUri, provider)
            listeners.forEach[
                querySpecificationAdded(this, provider)
            ]
        }
        
        def clearProviders(String resourceUri) {
            descriptionToProvider.get(resourceUri).forEach[ provider |
                listeners.forEach[
                    querySpecificationRemoved(this, provider)
                ]
            ]
            descriptionToProvider.removeAll(resourceUri)
        }
        
        override protected sendQuerySpecificationsToListener(IConnectorListener listener) {
            descriptionToProvider.values.forEach[
                listener.querySpecificationAdded(this, it)
            ]
        }
    
    }
    
    @FinalFieldsConstructor
    private static final class WorkspaceBuildCompletedListener implements IResourceChangeListener {
        
        final Map<URI, IResourceDescription> connectorsToUpdate = newHashMap
        final XtextIndexBasedRegistryUpdater updater
        
        override resourceChanged(IResourceChangeEvent event) {
            val type = event.type
            if(type == IResourceChangeEvent.POST_CHANGE){
                if(connectorsToUpdate.empty){
                    return
                }
                val update = ImmutableMap.copyOf(connectorsToUpdate)
                update.forEach[ uri, descr | 
                    try{
                        connectorsToUpdate.remove(uri)
                        val projectName = uri.segment(1)
                        val connectorId = DYNAMIC_CONNECTOR_ID_PREFIX + projectName
                        val connector = updater.connectorMap.get(connectorId)
                        connector.clearProviders(uri.toString)
                        val resourceSet = updater.createResourceSet(projectName)
                        val patternObjects = descr.getExportedObjectsByType(PatternLanguagePackage.Literals.PATTERN)
                        patternObjects.forEach[
                            val provider = new PatternDescriptionBasedSpecificationProvider(updater, descr, it, resourceSet)
                            connector.addProvider(uri.toString, provider)
                        ]
                    } catch (Exception ex) {
                        val logger = ViatraQueryLoggingUtil.getLogger(XtextIndexBasedRegistryUpdater)
                        logger.error('''Could not update registry based on Xtext index for «uri»''', ex)
                    }
                ]
            }
        }
        
    }
}