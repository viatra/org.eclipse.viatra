/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.emf.notification.integration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.viatra.cep.core.api.engine.CEPEngine;
import org.eclipse.viatra.cep.core.streams.EventStream;
import org.eclipse.viatra.cep.emf.notification.model.CepFactory;
import org.eclipse.viatra.cep.emf.notification.model.events.ADD_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.ADD_MANY_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.MOVE_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.REMOVE_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.REMOVE_MANY_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.RESOLVE_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.SET_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.UNSET_Event;
import org.eclipse.viatra.cep.emf.notification.model.traits.EObject;

/**
 * Extension library for processing Ecore {@link Notification} events.
 * 
 * @author Istvan David
 *
 */
public class EcoreNotificationAdapter {
    private static final String LOGGER_NAME = "org.eclipse.viatra.cep.emf.notification.integration";
    private static final String EVENT_OBSERVED_MSG = "%s event observed.";
    private Logger logger = Logger.getLogger(LOGGER_NAME);

    private Adapter adapter;
    private Notifier notifier;
    private EventStream eventStream;

    /**
     * Constructor for cases with a dedicated event stream for {@link Notification}s.
     * 
     * @param notifier
     *            root object of the model
     * @param cepEngine
     *            {@link CEPEngine} to work with
     */
    public EcoreNotificationAdapter(Notifier notifier, CEPEngine cepEngine) {
        this(notifier, cepEngine.getStreamManager().newEventStream());
    }

    /**
     * Constructor for cases with an event stream specified by the user.
     * 
     * @param notifier
     *            object of the model
     * @param eventStream
     *            {@link EventStream} to work with
     */
    public EcoreNotificationAdapter(Notifier notifier, EventStream eventStream) {
        this.notifier = notifier;
        this.eventStream = eventStream;
        setAdapter();
        setDebugLevel(Level.OFF);
    }

    private void setAdapter() {
        adapter = new AdapterImpl() {
            @Override
            public void notifyChanged(Notification notification) {
                switch (notification.getEventType()) {
                case Notification.ADD:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.ADD"));
                    ADD_Event addEvent = CepFactory.getInstance().createADD_Event();
                    initialize(addEvent, notification);
                    eventStream.push(addEvent);
                    break;
                case Notification.ADD_MANY:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.ADD_MANY"));
                    ADD_MANY_Event addManyEvent = CepFactory.getInstance().createADD_MANY_Event();
                    initialize(addManyEvent, notification);
                    eventStream.push(addManyEvent);
                    break;
                case Notification.MOVE:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.MOVE"));
                    MOVE_Event moveEvent = CepFactory.getInstance().createMOVE_Event();
                    initialize(moveEvent, notification);
                    eventStream.push(moveEvent);
                    break;
                case Notification.REMOVE:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.REMOVE"));
                    REMOVE_Event removeEvent = CepFactory.getInstance().createREMOVE_Event();
                    initialize(removeEvent, notification);
                    eventStream.push(removeEvent);
                    break;
                case Notification.REMOVE_MANY:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.REMOVE_MANY"));
                    REMOVE_MANY_Event removeManyEvent = CepFactory.getInstance().createREMOVE_MANY_Event();
                    initialize(removeManyEvent, notification);
                    eventStream.push(removeManyEvent);
                    break;
                case Notification.RESOLVE:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.RESOLVE"));
                    RESOLVE_Event resolveEvent = CepFactory.getInstance().createRESOLVE_Event();
                    initialize(resolveEvent, notification);
                    eventStream.push(resolveEvent);
                    break;
                case Notification.SET:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.SET"));
                    SET_Event setEvent = CepFactory.getInstance().createSET_Event();
                    initialize(setEvent, notification);
                    eventStream.push(setEvent);
                    break;
                case Notification.UNSET:
                    logger.debug(String.format(EVENT_OBSERVED_MSG, "Notification.UNSET"));
                    UNSET_Event unsetEvent = CepFactory.getInstance().createUNSET_Event();
                    initialize(unsetEvent, notification);
                    eventStream.push(unsetEvent);
                    break;
                default:
                    break;
                }
            }
        };
        notifier.eAdapters().add(this.adapter);
    }

    private void initialize(EObject eObject, Notification notification) {
        eObject.setNotifier(notification.getNotifier());
        eObject.setFeature(notification.getFeature());
        eObject.setOldValue(notification.getOldValue());
        eObject.setNewValue(notification.getNewValue());
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setDebugLevel(Level level) {
        logger.setLevel(level);
    }

    public void dispose() {
        notifier.eAdapters().remove(adapter);
        logger = null;
        adapter = null;
    }
}
