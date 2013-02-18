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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * This class is responsible for querying the active target platform data for registered GenModels 
 * and EPackages.
 */
public final class TargetPlatformMetamodelsIndex implements ITargetPlatformMetamodelLoader{

	private static final String EP_GENPACKAGE = "org.eclipse.emf.ecore.generated_package";
	private static final String PACKAGE = "package";
	private static final String ATTR_URI = "uri";
	private static final String ATTR_GENMODEL = "genModel";
	
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
		List<TargetPlatformMetamodel> metamodels = new LinkedList<TargetPlatformMetamodelsIndex.TargetPlatformMetamodel>();
		for(IPluginExtension extension : base.getExtensions()){
			if (EP_GENPACKAGE.equals(extension.getPoint())){
				for(IPluginObject po : extension.getChildren()){
                    if (po instanceof IPluginElement && PACKAGE.equals(po.getName())) {
                        IPluginAttribute uriAttrib = ((IPluginElement) po).getAttribute(ATTR_URI);
                        IPluginAttribute genAttrib = ((IPluginElement) po).getAttribute(ATTR_GENMODEL);
                        if (uriAttrib != null && genAttrib != null) {
                            String uri = uriAttrib.getValue();
                            String genModel = genAttrib.getValue();
                            if (!genModel.startsWith("/"))
                                genModel = "/" + genModel;
                            metamodels.add(new TargetPlatformMetamodel(URI.createURI(resolvePluginResource(
                                    base.getPluginModel(), genModel)), uri));
                        }
                    }
				}
			}
		}
		return metamodels;
	}
	
	public static class TargetPlatformMetamodel{
		
		private final URI genModel;
		private final String packageURI;
		
		/**
		 * 
		 */
		private TargetPlatformMetamodel(URI genModel, String packageURI) {
			this.genModel = genModel;
			this.packageURI = packageURI;
		}
		
		/**
		 * @return the packageURI
		 */
		public String getPackageURI() {
			return packageURI;
		}
		
		public GenModel loadGenModel(ResourceSet resourceset){
			try{
				Resource genModel = resourceset.getResource(this.genModel, true);
				for(EObject eo : genModel.getContents()){
					if (eo instanceof GenModel){
						return (GenModel)eo;
					}
				}
				return null;
			}catch(Exception e){
				//Exception
				return null;
			}
		}
		
		public GenPackage loadGenPackage(ResourceSet resourceset){
			GenModel genModel = loadGenModel(resourceset);
			try{
				for(GenPackage genpack : genModel.getAllGenPackagesWithClassifiers()){
					EPackage epack = genpack.getEcorePackage();
					if (this.packageURI.equals(epack.getNsURI())){
						return genpack;
					}
				}
			}catch(NullPointerException e){
				// genModel.getAllGenPackagesWithClassifiers() can throw NullPointerException
			}
			return null;
		}
		
		public EPackage loadPackage(ResourceSet resourceset){
			GenPackage genPack = loadGenPackage(resourceset);
			if (genPack != null) return genPack.getEcorePackage();
			return null;
		}
		
	}
	
	private static String resolvePluginResource(IPluginModelBase modelbase, String path){
        IResource res = modelbase.getUnderlyingResource();
        if (res != null) {
            IProject project = res.getProject();
            URI platformUri = URI.createPlatformResourceURI(project.findMember(path).getFullPath().toString(),
                    false);
            return platformUri.toString();
        }
		String location = modelbase.getInstallLocation();
		if (location.endsWith(".jar")) {
			return "jar:file:" + location + "!" + path;
		} else {
			return "file:" + modelbase.getInstallLocation() + path;
		}
	}

	private Iterable<TargetPlatformMetamodel> load(){
		update();
		return entries.values();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.tooling.core.targetPlatform.ITargetPlatformMetamodelLoader#listEPackages()
	 */
	@Override
	public Iterable<String> listEPackages() {
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
