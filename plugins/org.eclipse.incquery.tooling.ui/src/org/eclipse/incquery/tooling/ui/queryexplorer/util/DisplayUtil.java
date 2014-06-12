/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.incquery.databinding.runtime.adapter.DatabindingAdapter;
import org.eclipse.incquery.databinding.runtime.util.DatabindingUtil;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.emf.specification.GenericQuerySpecification;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The util contains several useful methods for the query displaying operations.
 */
@Singleton
public class DisplayUtil {


    private static Map<URI, AdapterFactoryLabelProvider> registeredItemProviders = Maps.newHashMap();
    private static Map<URI, IConfigurationElement> uriConfElementMap = null;
    private static ILog logger = IncQueryGUIPlugin.getDefault().getLog();
    private static Map<String, IMarker> orderByPatternMarkers = Maps.newHashMap();


    public static final String PATTERNUI_ANNOTATION = "PatternUI";
    public static final String ORDERBY_ANNOTATION = "OrderBy";


    @Inject
    private IResourceSetProvider resSetProvider;
    private Map<IProject, ResourceSet> resourceSetMap = new WeakHashMap<IProject, ResourceSet>();

    /**
     * Creates a marker with a warning for the given pattern. The marker's message will be set to the given message
     * parameter.
     *
     * @param patternFqn
     *            the fully qualified name of the pattern
     * @param message
     *            the warning message for the marker
     */
    public static void addOrderByPatternWarning(String patternFqn, String message) {
        if (orderByPatternMarkers.get(patternFqn) == null) {
            IQuerySpecification<?> specification = QueryExplorerPatternRegistry.getInstance().getPatternByFqn(patternFqn);
            if (specification instanceof GenericQuerySpecification) {
                Pattern pattern = ((GenericQuerySpecification) specification).getPattern();
                URI uri = pattern.eResource().getURI();
                String platformString = uri.toPlatformString(true);
                IResource markerLoc = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
                try {
                    IMarker marker = markerLoc.createMarker(EValidator.MARKER);
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                    marker.setAttribute(IMarker.TRANSIENT, true);
                    marker.setAttribute(IMarker.LOCATION, pattern.getName());
                    marker.setAttribute(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI(pattern).toString());
                    marker.setAttribute(IMarker.MESSAGE, message);
                    orderByPatternMarkers.put(patternFqn, marker);
                } catch (CoreException e) {
                    logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID,
                            "Marker could not be created for pattern: " + patternFqn, e));
                }
            }
        }
    }


    /**
     * Removes the marker for the given pattern if it is present.
     *
     * @param patternFqn
     *            the fully qualified name of the pattern
     */
    public static void removeOrderByPatternWarning(String patternFqn) {
        IMarker marker = orderByPatternMarkers.remove(patternFqn);
        if (marker != null) {
            try {
                marker.delete();
            } catch (CoreException e) {
                logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Marker could not be deleted: "
                        + marker.toString(), e));
            }
        }
    }

    /**
     * Returns the {@link AdapterFactoryLabelProvider} instance for the given uri.
     *
     * @param uri
     *            the uri
     * @return the {@link AdapterFactoryLabelProvider} instance
     */
    public synchronized static AdapterFactoryLabelProvider getAdapterFactoryLabelProvider(URI uri) {
        if (uriConfElementMap == null) {
            uriConfElementMap = collectItemProviders();
        }
        AdapterFactoryLabelProvider af = registeredItemProviders.get(uri);
        if (af != null) {
            return af;
        } else {
            IConfigurationElement ce = uriConfElementMap.get(uri);
            try {
                if (ce != null) {
                    Object obj = ce.createExecutableExtension("class");
                    AdapterFactoryLabelProvider lp = new AdapterFactoryLabelProvider((AdapterFactory) obj);
                    registeredItemProviders.put(uri, lp);
                    return lp;
                }
            } catch (CoreException e) {
                logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID,
                        "AdapterFactory could not be created for uri: " + uri.toString(), e));
            }
            return null;
        }
    }

    private static Map<URI, IConfigurationElement> collectItemProviders() {
        Map<URI, IConfigurationElement> result = new HashMap<URI, IConfigurationElement>();
        try {
            IExtensionRegistry reg = Platform.getExtensionRegistry();
            IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.emf.edit.itemProviderAdapterFactories");
            for (IExtension e : ep.getExtensions()) {
                for (IConfigurationElement ce : e.getConfigurationElements()) {
                    if (ce.getName().matches("factory")) {
                        URI uri = URI.createURI(ce.getAttribute("uri"));
                        result.put(uri, ce);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Collecting item providers failed.", e));
        }
        return result;
    }

    /**
     * Returns a text message for a generated, not filtered matcher about the current match size.
     *
     * @param matcher
     * @param matchesSize
     * @param patternFqn
     * @return
     */
    public static String getMessage(IncQueryMatcher<? extends IPatternMatch> matcher, int matchesSize, String patternFqn) {
        return getMessage(matcher, matchesSize, patternFqn, false, false, null);
    }

    /**
     * Returns a text message about the matches size for the given matcher.
     *
     * @param matcher
     *            the {@link IncQueryMatcher} instance
     * @param matchesSize
     *            the size of the matchset
     * @param patternFqn
     *            the pattern fqn
     * @param isGenerated
     *            true, if the matcher is generated, false if generic
     * @param isFiltered
     *            true, if the matcher is filtered, false otherwise
     * @return the label associated to the matcher
     */
    public static String getMessage(IncQueryMatcher<? extends IPatternMatch> matcher, int matchesSize,
            String patternFqn, boolean isGenerated, boolean isFiltered, String exceptionMessage) {
        if (matcher == null) {
        	if (exceptionMessage != null)
        		return String.format("%s - %s", patternFqn, exceptionMessage);
        	else
        		return String.format("%s - See mouseover text for query loading errors", patternFqn, exceptionMessage);
        } else {
            String matchString;
            switch (matchesSize) {
            case 0:
                matchString = "No matches";
                break;
            case 1:
                matchString = "1 match";
                break;
            default:
                matchString = String.format("%d matches", matchesSize);
                break;
            }

            String isFilteredString = isFiltered ? " - Filtered" : "";
            String isGeneratedString = isGenerated ? " (Generated)" : " (Runtime)";

            return String.format("%s - %s %s %s", matcher.getPatternName(), matchString, isFilteredString, isGeneratedString);
        }
    }

    /**
     * Get the value of the PatternUI annotation's message attribute for the pattern which name is patternName.
     *
     * @param patternName
     *            the name of the pattern
     * @return the content of the message attribute
     */
    public static String getMessage(IPatternMatch match)//, boolean generatedMatcher)
    {
//        if (generatedMatcher) {
//            return DatabindingUtil.getDatabindingMessageForGeneratedMatcher(match);
//        } else {
            return getMessageForMatch(match);
//      }
    }



    private static String getMessageForMatch(IPatternMatch match) {
        String patternName = match.patternName();
        IQuerySpecification<?> pattern = null;

        // find PatternUI annotation
        for (IQuerySpecification<?> p : QueryExplorerPatternRegistry.getInstance().getActivePatterns()) {
            if (p.getFullyQualifiedName().matches(patternName)) {
                pattern = p;

                PAnnotation annotation = p.getFirstAnnotationByName(IExtensions.QUERY_EXPLORER_ANNOTATION);
                if (annotation == null) {
                    // Try with deprecated PatternUI annotation
                    annotation = p.getFirstAnnotationByName(PATTERNUI_ANNOTATION);
                }
                if (annotation != null) {
                    return (String)annotation.getFirstValue("message");
                }
            }
        }

        // No formatting annotation found
        if (pattern != null) {
            StringBuilder message = new StringBuilder();
            if (pattern.getParameterNames().size() == 0) {
                message.append("(Match)");
            } else {
                int i = 0;
                for (String v : pattern.getParameterNames()) {
                    if (i > 0) {
                        message.append(", ");
                    }
                    // message += v.getName()+"=$"+v.getName()+"$";
                    message.append(String.format("%s=$%s$", v, v));
                    i++;
                }
            }
            return message.toString();
        }

        return null;
    }

    /**
     * Get the DatabindingAdapter generated for the pattern whose name is patternName
     *
     * @param patternName
     *            the name of the pattern
     * @return an instance of the DatabindingAdapter class generated for the pattern
     * TODO move into {@link DatabindingUtil} once Pattern Registry refactoring is done
     */
    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(String patternName)//, boolean generatedMatcher)
    {
        IQuerySpecification<?> pattern = QueryExplorerPatternRegistry.getInstance().getPatternByFqn(patternName);
//        if (generatedMatcher) {
//            return DatabindingUtil.getDatabindingAdapterForGeneratedMatcher(pattern);
//        } else {
            return DatabindingUtil.getDatabindingAdapter(pattern);
//        }
    }



    public PatternModel extractPatternModelFromResource(Resource resource) {
    	if (resource != null) {
            if (resource.getErrors().size() > 0) {
                return null;
            }
            if (resource.getContents().size() >= 1) {
                EObject topElement = resource.getContents().get(0);
                return topElement instanceof PatternModel ? (PatternModel) topElement : null;
            }
        }
        return null;
    }



    /**
     * Parses the given .eiq file into a {@link PatternModel}.
     *
     * @param file
     *            the .eiq file instance
     * @return the parsed pattern model or null in case of error
     */
    public PatternModel parseEPM(IFile file) {
        if (file == null) {
            return null;
        }
        ResourceSet resourceSet = null;
        IProject project = file.getProject();
		if (resourceSetMap.containsKey(project)) {
        	resourceSet = resourceSetMap.get(project);
        } else {
        	resourceSet = resSetProvider.get(project);
        	resourceSetMap.put(project, resourceSet);
        }
        URI fileURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
        Resource resource = resourceSet.getResource(fileURI, false);
        try {
        	if (resource == null) {
        		resource = resourceSet.createResource(fileURI);
        	} 
        	else if (resource.isLoaded()) {
        		// TODO remove this kludgy, side effect-laden code from here
        		TreeIterator<EObject> it = resource.getAllContents();

        		QueryExplorerPatternRegistry queryRegistry = QueryExplorerPatternRegistry.getInstance();
				QueryExplorer queryExplorer = QueryExplorer.getInstance();
        		while (it.hasNext()) {
        			EObject next = it.next();
        			if (next instanceof Pattern) {
						Pattern oldPattern = (Pattern) next;


						String fqn = CorePatternLanguageHelper.getFullyQualifiedName(oldPattern);
                        queryExplorer.getPatternsViewerRoot().getGenericPatternsRoot().removeComponent(fqn);
						queryExplorer.getPatternsViewerRoot().getGenericPatternsRoot().purge();
						//queryExplorer.getPatternsViewer().setInput(queryExplorer.getPatternsViewerInput());

						queryRegistry.removeActivePattern(fqn);
						it.prune();
        			}
        		}
				queryExplorer.getPatternsViewer().setInput(queryExplorer.getPatternsViewerRoot());
        		resource.unload();
        	}
			resource.load(null);
		} catch (IOException e) {
			return null;
		}

        return extractPatternModelFromResource(resource);
    }
}
