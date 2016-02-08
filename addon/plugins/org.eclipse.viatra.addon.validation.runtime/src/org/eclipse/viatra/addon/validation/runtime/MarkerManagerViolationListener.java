/*******************************************************************************
 * Copyright (c) 2010-2012, Balint Lorand, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Balint Lorand - initial API and implementation
 *   Abel Hegedus - minor changes
 *******************************************************************************/
package org.eclipse.viatra.addon.validation.runtime;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.addon.validation.core.api.IEntry;
import org.eclipse.viatra.addon.validation.core.api.IViolation;
import org.eclipse.viatra.addon.validation.core.listeners.ConstraintListener;
import org.eclipse.viatra.addon.validation.core.listeners.ViolationListener;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;

/**
 * @author Balint Lorand
 *
 */
public class MarkerManagerViolationListener implements ConstraintListener, ViolationListener {

    private Logger logger;
    private ConstraintAdapter adapter;

    public MarkerManagerViolationListener(Logger logger, ConstraintAdapter adapter) {
        super();
        this.logger = logger;
        this.adapter = adapter;
    }

    @Override
    public void violationAppeared(IViolation violation) {
        List<String> keyNames = violation.getConstraint().getSpecification().getKeyNames();
        Map<String, Object> keyObjects = violation.getKeyObjects();
        for (String keyName : keyNames) {
            Object keyObject = keyObjects.get(keyName);
            if (keyObject instanceof EObject) {
                EObject location = (EObject) keyObject;
                if (location.eResource() != null) {
                    IResource markerLoc = adapter.getResourceForEditor();
                    if (markerLoc == null) {
                        URI uri = location.eResource().getURI();
                        String platformString = uri.toPlatformString(true);
                        if (platformString == null) {
                            logger.error("Marker location for " + location.toString() + " is invalid!");
                            return;
                        }
                        markerLoc = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
                        if (markerLoc == null) {
                            logger.error("Marker location " + platformString + " for " + location
                                    + "is not in workspace!");
                            return;
                        }
                    }
                    try {
                        IMarker marker = markerLoc.createMarker(EValidator.MARKER);
                        marker.setAttribute(IMarker.SEVERITY, violation.getConstraint().getSpecification()
                                .getSeverity().ordinal());
                        marker.setAttribute(IMarker.TRANSIENT, true);
                        StringBuilder locationSB = new StringBuilder();
                        StringBuilder relatedUriSB = new StringBuilder();
                        for (Entry<String, Object> entry : keyObjects.entrySet()) {
                            if (locationSB.length() > 0) {
                                locationSB.append(", ");
                            }
                            String locationString = String.format("%1$s: %2$s", entry.getKey(),
                                    BasePatternMatch.prettyPrintValue(entry.getValue()));
                            locationSB.append(locationString);

                            if (!entry.getKey().equals(keyName)) {
                                if (relatedUriSB.length() > 0) {
                                    relatedUriSB.append(" ");
                                }
                                if (entry.getValue() instanceof EObject) {
                                    URI targetUri = EcoreUtil.getURI((EObject) entry.getValue());
                                    relatedUriSB.append(targetUri);
                                }
                            }
                        }
                        marker.setAttribute(IMarker.LOCATION, locationSB.toString());
                        if (relatedUriSB.length() > 0) {
                            marker.setAttribute(EValidator.RELATED_URIS_ATTRIBUTE, relatedUriSB.toString());
                        }
                        marker.setAttribute(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI(location).toString());
                        marker.setAttribute(IMarker.MESSAGE, violation.getMessage());
                        adapter.addMarker(violation, marker);
                        violation.addListener(this);
                    } catch (CoreException e) {
                        logger.error("Error during marker initialization!", e);
                    }
                }
            }
            break;
        }
    }

    @Override
    public void violationDisappeared(IViolation violation) {
        IMarker marker = adapter.removeMarker(violation);
        if (marker != null) {
            try {
                marker.delete();
            } catch (CoreException e) {
                logger.error("Could not delete marker!", e);
            }
        }

    }

    @Override
    public void violationEntryAppeared(IViolation violation, IEntry entry) {
        // entries not handled in markers currently
    }

    @Override
    public void violationMessageUpdated(IViolation violation) {
        IMarker marker = adapter.getMarker(violation);
        if (marker != null) {
            try {
                marker.setAttribute(IMarker.MESSAGE, violation.getMessage());
            } catch (CoreException e) {
                logger.error("Error during marker update!", e);
            }
        }
    }

    @Override
    public void violationEntryDisappeared(IViolation violation, IEntry entry) {
        // entries not handled in markers currently
    }

}
