/*******************************************************************************
 * Copyright (c) 2010-2016, Peter Lunk and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.ArrayList;
import java.util.List;

public class AdaptableEVMFactory {
    private static AdaptableEVMFactory instance;
    private List<AdaptableEVM> adaptableEVMInstances;
    private List<IAdaptableEVMFactoryListener> listeners;
    
    public static AdaptableEVMFactory getInstance() {
        if(instance == null){
            instance = new AdaptableEVMFactory();
        }
        return instance;
    }
    
    protected AdaptableEVMFactory(){
        adaptableEVMInstances = new ArrayList<>();
        listeners = new ArrayList<>();
    }
    
    public List<AdaptableEVM> getAdaptableEVMInstances() {
        return adaptableEVMInstances;
    }
    
    public AdaptableEVM getAdaptableEVMInstance(String id) {
        for (AdaptableEVM adaptableEVM : adaptableEVMInstances) {
            if(adaptableEVM.getIdentifier().equals(id)){
                return adaptableEVM;
            }
        }
        return null;
    }

    public AdaptableEVM createAdaptableEVM(){
        return createAdaptableEVM("AdaptableEVM_"+System.currentTimeMillis());
    }
    
    public void disposeAdaptableEVM(AdaptableEVM evm){
        adaptableEVMInstances.remove(evm);
        notifyListeners();
    }
    
    public AdaptableEVM createAdaptableEVM(String id){
        AdaptableEVM adaptableEVM = new AdaptableEVM(id);
        adaptableEVMInstances.add(adaptableEVM);
        notifyListeners();
        return adaptableEVM;
    }
    
    public void registerListener(IAdaptableEVMFactoryListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
            notifyListeners();
        }
    }
    protected void notifyListeners() {
        for (IAdaptableEVMFactoryListener adaptableEVMFactoryListener : listeners) {
            adaptableEVMFactoryListener.adaptableEVMPoolChanged(new ArrayList<>(adaptableEVMInstances));
        }
    }
    
    public void unRegisterListener(IAdaptableEVMFactoryListener listener){
        if(listeners.contains(listener)){
            listeners.remove(listener);
        }
    }
    
}
