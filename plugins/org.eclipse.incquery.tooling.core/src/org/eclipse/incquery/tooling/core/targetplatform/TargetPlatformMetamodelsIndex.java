/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Balazs Grill - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.targetplatform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.pde.core.plugin.IPluginAttribute;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.core.plugin.PluginRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * This class is responsible for querying the active target platform data for registered GenModels 
 * and EPackages.
 */
public final class TargetPlatformMetamodelsIndex implements ITargetPlatformMetamodelLoader{

	private static final String EP_GENPACKAGE = "org.eclipse.emf.ecore.generated_package";
	private static final String PACKAGE = "package";
	private static final String ATTR_URI = "uri";
	private static final String ATTR_GENMODEL = "genModel";
	
    @Inject
    Logger logger;

	private static final Multimap<String, TargetPlatformMetamodel> entries = ArrayListMultimap.create();
	
	private void update(){
		IPluginModelBase[] plugins = PluginRegistry.getActiveModels();
		Set<String> workspacePlugins = new HashSet<String>();
		Map<String, IPluginBase> pluginset = new HashMap<String, IPluginBase>();
		for(IPluginModelBase mbase : plugins){
			IPluginBase base = mbase.createPluginBase();
			String ID = mbase.getInstallLocation();
			pluginset.put(ID, base);
			if (mbase.getUnderlyingResource() != null){
				workspacePlugins.add(ID);
			}
		}
		
		/* Remove entries that disappeared */
		Set<String> remove = new HashSet<String>(entries.keySet());
		remove.removeAll(pluginset.keySet());
		for(String id : remove){
			entries.removeAll(id);
		}
		/* Always reload workspace plugins */
		for(String id : workspacePlugins){
			entries.removeAll(id);
		}
		
		/* Add new entries */
		Set<String> added = new HashSet<String>(pluginset.keySet());
		added.removeAll(entries.keySet());
		for(String id : added){
			IPluginBase base = pluginset.get(id);
			entries.putAll(id, load(base));
		}
	}
	
	private List<TargetPlatformMetamodel> load(IPluginBase base){
		List<TargetPlatformMetamodel> metamodels = new LinkedList<TargetPlatformMetamodel>();
		for(IPluginExtension extension : base.getExtensions()){
			if (EP_GENPACKAGE.equals(extension.getPoint())){
				for(IPluginObject po : extension.getChildren()){
                    if (po instanceof IPluginElement && PACKAGE.equals(po.getName())) {
                        IPluginAttribute uriAttrib = ((IPluginElement) po).getAttribute(ATTR_URI);
                        IPluginAttribute genAttrib = ((IPluginElement) po).getAttribute(ATTR_GENMODEL);
                        TargetPlatformMetamodel metamodel = loadMetamodelSpecification(base, uriAttrib, genAttrib);
                        if (metamodel != null) {
                            metamodels.add(metamodel);
                        }
                    }
				}
			}
		}
		return metamodels;
	}

    private TargetPlatformMetamodel loadMetamodelSpecification(IPluginBase base, IPluginAttribute uriAttrib,
            IPluginAttribute genAttrib) {
        TargetPlatformMetamodel metamodel = null;
        if (uriAttrib != null && genAttrib != null) {
            String nsUri = uriAttrib.getValue();
            URI genmodelURI = null;
            String genModel = genAttrib.getValue();
            genmodelURI = URI.createURI(genModel);
            if (genmodelURI.isRelative()) {
                genmodelURI = resolvePluginResource(base.getPluginModel(), "/" + genModel);
            }
            metamodel = new TargetPlatformMetamodel(nsUri, genmodelURI, logger);
        }
        return metamodel;
    }
	
    public static final class TargetPlatformMetamodel {
		
		private final URI genModelUri;
		private final String packageURI;
        private Logger logger;

        private final static String GENMODEL_LOAD_ERROR = "Error while loading genmodel '%s' for EPackage '%s'. Check corresponding plugin.xml declaration.";
		
        private TargetPlatformMetamodel(String packageURI, URI genModel, Logger logger) {
            this.logger = logger;
            Preconditions.checkArgument(packageURI != null && !packageURI.isEmpty(), "EPackage nsURI must be set");
            Preconditions.checkArgument(genModel != null, "Genmodel URI must not be null");
			this.genModelUri = genModel;
			this.packageURI = packageURI;
		}
		
		/**
		 * @return the packageURI
		 */
		public String getPackageURI() {
			return packageURI;
		}
		
        /**
         * Loads and returns the genmodel into the selected {@link ResourceSet}.
         * 
         * @param resourceset
         * @return the loaded genmodel, or null if {@link #genModelUri} contains no {@link GenModel}. During the loading
         *         of the genmodel IO-related runtime exceptions might be thrown.
         */
        private GenModel loadGenModel(ResourceSet resourceset) {
            Resource genModel = resourceset.getResource(this.genModelUri, true);
            for (EObject eo : genModel.getContents()) {
                if (eo instanceof GenModel) {
                    return (GenModel) eo;
				}
			}

            return null;
		}
		
		public GenPackage loadGenPackage(ResourceSet resourceset){
            try {
                GenModel genModel = loadGenModel(resourceset);
                if (genModel != null) {
                    for (GenPackage genpack : genModel.getAllGenPackagesWithClassifiers()) {
                        EPackage epack = genpack.getEcorePackage();
                        if (this.packageURI.equals(epack.getNsURI())) {
                            return genpack;
                        }
                    }
                } else {
                    logger.warn(String.format(GENMODEL_LOAD_ERROR, this.genModelUri, packageURI));
                }
            } catch (Exception e) {
                logger.warn(String.format(GENMODEL_LOAD_ERROR, this.genModelUri, packageURI), e);
            }
			return null;
		}
		
		public EPackage loadPackage(ResourceSet resourceset){
            GenPackage genPack = loadGenPackage(resourceset);
            if (genPack != null) {
                return genPack.getEcorePackage();
            }
			return null;
		}
		
	}
	
    private URI resolvePluginResource(IPluginModelBase modelbase, String path) {
        // File exist check removed as it does not work with classpath-based resource paths
        URI platformUri;
        String pathString = new Path(modelbase.getPluginBase().getId()).append(path)
                .toString();
        if (modelbase.getUnderlyingResource() != null) {
            platformUri = URI.createPlatformResourceURI(pathString, false);
        } else {
            platformUri = URI.createPlatformPluginURI(pathString, false);
        }
        return platformUri;
	}

	private Iterable<TargetPlatformMetamodel> load(){
	    // FIXME we need to ensure that only one caller modifies entries at any given time
		synchronized (TargetPlatformMetamodelsIndex.class) {
		    update();
		    return new ArrayList<TargetPlatformMetamodel>(entries.values());
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.tooling.core.targetPlatform.ITargetPlatformMetamodelLoader#listEPackages()
	 */
	@Override
	public List<String> listEPackages() {
		List<String> packageURIs = new LinkedList<String>();
		for(TargetPlatformMetamodel entry: load()){
			packageURIs.add(entry.getPackageURI());
		}
		return packageURIs;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.tooling.core.targetPlatform.ITargetPlatformMetamodelLoader#loadPackage(org.eclipse.emf.ecore.resource.ResourceSet, java.lang.String)
	 */
	@Override
	public EPackage loadPackage(ResourceSet resourceSet, String nsURI) {
		for(TargetPlatformMetamodel mm : load()){
			if (nsURI.equals(mm.packageURI)){
				return mm.loadPackage(resourceSet);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.tooling.core.targetPlatform.ITargetPlatformMetamodelLoader#loadGenPackage(org.eclipse.emf.ecore.resource.ResourceSet, java.lang.String)
	 */
	@Override
	public GenPackage loadGenPackage(ResourceSet resourceSet, String nsURI) {
		for(TargetPlatformMetamodel mm : load()){
			if (nsURI.equals(mm.packageURI)){
				return mm.loadGenPackage(resourceSet);
			}
		}
		return null;
	}
	
}
