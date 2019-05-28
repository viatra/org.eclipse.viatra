/*******************************************************************************
 * Copyright (c) 2010-2019, Geza Kulcsar, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.testing.core

import com.google.common.collect.Maps
import java.util.ArrayList
import java.util.Date
import java.util.Map
import org.eclipse.emf.common.util.Enumerator
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.core.api.JavaObjectAccess
import org.eclipse.viatra.query.testing.snapshot.BooleanSubstitution
import org.eclipse.viatra.query.testing.snapshot.DateSubstitution
import org.eclipse.viatra.query.testing.snapshot.DoubleSubstitution
import org.eclipse.viatra.query.testing.snapshot.EMFSubstitution
import org.eclipse.viatra.query.testing.snapshot.EnumSubstitution
import org.eclipse.viatra.query.testing.snapshot.FloatSubstitution
import org.eclipse.viatra.query.testing.snapshot.InputSpecification
import org.eclipse.viatra.query.testing.snapshot.IntSubstitution
import org.eclipse.viatra.query.testing.snapshot.LongSubstitution
import org.eclipse.viatra.query.testing.snapshot.MatchRecord
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord
import org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord
import org.eclipse.viatra.query.testing.snapshot.MiscellaneousSubstitution
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot
import org.eclipse.viatra.query.testing.snapshot.SerializedJavaObjectSubstitution
import org.eclipse.viatra.query.testing.snapshot.SnapshotFactory
import org.eclipse.viatra.query.testing.snapshot.StringSubstitution
import org.eclipse.viatra.query.testing.snapshot.CustomEMFSubstitution
import java.util.function.Function
import org.eclipse.emf.ecore.EClass

/**
 * Helper methods for dealing with snapshots and match set records.
 */
class SnapshotHelper {
    
    final Map<String, JavaObjectAccess> accessMap;
    
    final Map<EClass, Function<EObject,String>> customEObjectSerializerMap;
    
    new(){
        this(Maps.newHashMap)
    }
    
    /**
     * Initializes a {@link SnapshotHelper} with a map containing {@link JavaObjectAccess} objects for 
     * serialization and deserialization of plain Java types.
     * 
     * @since 1.6
     * 
     * @deprecated 
     * Use @link #SnapshotHelper(Map<String, JavaObjectAccess>,  Map<EClass, Function<EObject,String>>) instead
     */
    @Deprecated
    new(Map<String, JavaObjectAccess> accessMap){
        this.accessMap = accessMap
        this.customEObjectSerializerMap = Maps.newHashMap
    }
    
    /**
     * @since 2.2
     */
    new(Map<String, JavaObjectAccess> accessMap,  Map<EClass, Function<EObject,String>> customMap) {
        this.accessMap = accessMap
        this.customEObjectSerializerMap = customMap
    }
    
    /**
     * Returns the actual value of the substitution based on its type
     */
    def derivedValue(MatchSubstitutionRecord substitution){
        switch substitution{
            BooleanSubstitution: substitution.value
            DateSubstitution: substitution.value
            DoubleSubstitution: substitution.value
            EMFSubstitution: substitution.value
            EnumSubstitution: substitution.valueLiteral
            FloatSubstitution: substitution.value
            IntSubstitution: substitution.value
            LongSubstitution: substitution.value
            MiscellaneousSubstitution: substitution.value
            StringSubstitution: substitution.value
            SerializedJavaObjectSubstitution: substitution
            /* TODO we don't check if the type attributes match */
            CustomEMFSubstitution: substitution.value
        }
    }
    
    /**
     * Returns the EMF root that was used by the matchers recorded into the given snapshot,
     *  based on the input specification and the model roots.
     */
    def getEMFRootForSnapshot(QuerySnapshot snapshot){
        if(snapshot.inputSpecification == InputSpecification::EOBJECT){
            if(snapshot.modelRoots.size > 0){
                snapshot.modelRoots.get(0)
            }
        } else if(snapshot.inputSpecification == InputSpecification::RESOURCE){
            if(snapshot.modelRoots.size > 0){
                snapshot.modelRoots.get(0).eResource
            }
        } else if(snapshot.inputSpecification == InputSpecification::RESOURCE_SET){
            snapshot.eResource.resourceSet
        }
    }

    /**
     * Returns the model roots that were used by the given ViatraQueryEngine.
     */
    def getModelRootsForEngine(ViatraQueryEngine engine){
        switch scope : engine.scope {
            EMFScope: {
                scope.scopeRoots.map[
                    switch it {
                        EObject: #[it]
                        Resource: contents
                        ResourceSet: resources.map[contents].flatten.toList
                    }
                ].flatten.toList
               }
               default: #[]
           }
    }

    /**
     * Returns the input specification for the given matcher.
     */
    def getInputSpecificationForMatcher(ViatraQueryMatcher<?> matcher){
        switch scope : matcher.engine.scope {
            EMFScope: {
                switch scope.scopeRoots.head {
                    EObject: InputSpecification::EOBJECT
                    Resource: InputSpecification::RESOURCE
                    ResourceSet: InputSpecification::RESOURCE_SET 
                }
            }
            default: InputSpecification::UNSET
        }
    }
    
    /**
     * Saves the matches of the given matcher (using the partial match) into the given snapshot.
     * If the input specification is not yet filled, it is now filled based on the engine of the matcher.
     */
    def <MATCH extends IPatternMatch> saveMatchesToSnapshot(ViatraQueryMatcher<MATCH> matcher, MATCH partialMatch, QuerySnapshot snapshot){
        val patternFQN = matcher.patternName
        val actualRecord = SnapshotFactory::eINSTANCE.createMatchSetRecord
        actualRecord.patternQualifiedName = patternFQN
        // 1. put actual match set record in the same model with the expected
        snapshot.matchSetRecords.add(actualRecord)
        // 2. store model roots
        if(snapshot.inputSpecification == InputSpecification::UNSET){
            snapshot.modelRoots.addAll(matcher.engine.modelRootsForEngine)
            snapshot.modelRoots.remove(snapshot)
            snapshot.inputSpecification = matcher.inputSpecificationForMatcher
        }
        actualRecord.filter = partialMatch.createMatchRecordForMatch

        // 3. create match set records
        matcher.forEachMatch(partialMatch)[match |
            actualRecord.matches.add(match.createMatchRecordForMatch)
        ]
        return actualRecord
    }

    /**
     * Creates a match record that corresponds to the given match.
     *  Each parameter with a value is saved as a substitution.
     */
    def createMatchRecordForMatch(IPatternMatch match){
        val matchRecord = SnapshotFactory::eINSTANCE.createMatchRecord
        match.parameterNames.forEach()[param |
            if (match.get(param) !== null) {
                matchRecord.substitutions.add(param.createSubstitution(match.get(param)))
            }
        ]
        return matchRecord
    }
    
    /**
     * Creates a match set record which holds the snapshot of a single matcher instance. It is also possible to enter
     * a filter for the matcher.
     */
    def <Match extends IPatternMatch> MatchSetRecord createMatchSetRecordForMatcher(ViatraQueryMatcher<Match> matcher, Match filter){
        val matchSetRecord = SnapshotFactory::eINSTANCE.createMatchSetRecord
        matcher.forEachMatch(filter, [ match |
            matchSetRecord.matches.add(createMatchRecordForMatch(match))
        ] );
        return matchSetRecord
    }

    /**
     * Creates a partial match that corresponds to the given match record.
     *  Each substitution is used as a value for the parameter with the same name.
     */
    def <MATCH extends IPatternMatch> MATCH createMatchForMatchRecord(IQuerySpecification<? extends ViatraQueryMatcher<MATCH>> querySpecification, MatchRecord matchRecord){
        val match = querySpecification.newEmptyMatch as MATCH
        matchRecord.substitutions.forEach()[
            var target = derivedValue
            match.set(parameterName,target)
        ]
        return match
    }

    
    /**
     * Saves all matches of the given matcher into the given snapshot.
     * If the input specification is not yet filled, it is now filled based on the engine of the matcher.
     */
    def <MATCH extends IPatternMatch> saveMatchesToSnapshot(ViatraQueryMatcher<MATCH> matcher, QuerySnapshot snapshot){
        matcher.saveMatchesToSnapshot(matcher.newEmptyMatch, snapshot)
    }

    /**
     * Returns the match set record for the given pattern FQN from the snapshot,
     *  if there is only one such record.
     */
    def getMatchSetRecordForPattern(QuerySnapshot snapshot, String patternFQN){
        val matchsetrecord = snapshot.matchSetRecords.filter[patternQualifiedName.equals(patternFQN)]
        if(matchsetrecord.size == 1){
            return matchsetrecord.iterator.next
        }
    }

    /**
     * Returns the match set records for the given pattern FQN from the snapshot.
     */
    def getMatchSetRecordsForPattern(QuerySnapshot snapshot, String patternFQN){
        val matchSetRecords = new ArrayList<MatchSetRecord>
        matchSetRecords.addAll(snapshot.matchSetRecords.filter[patternQualifiedName.equals(patternFQN)])
        return matchSetRecords
    }

    /**
     * Creates a substitution for the given parameter name using the given value.
     *  The type of the substitution is decided based on the type of the value.
     */
    def createSubstitution(String parameterName, Object value){
        return switch(value) {
            Enumerator: {
                val sub = SnapshotFactory::eINSTANCE.createEnumSubstitution
                sub.setValueLiteral(value.literal)
                sub.setParameterName(parameterName)
                sub
            }
            EObject : {

                var obj = getMostSpecificSerializationRule(value.eClass)
                
                if (obj !== null) {
                    
                    val sub = SnapshotFactory::eINSTANCE.createCustomEMFSubstitution
                    sub.setValue(obj.apply(value))
                    sub.setType(value.eClass())
                    sub.setParameterName(parameterName)
                    sub
                    
                } else {
                    
                    val sub = SnapshotFactory::eINSTANCE.createEMFSubstitution
                    sub.setValue(value)
                    sub.setParameterName(parameterName)
                    sub
                
                } 
            }
            Integer : {
                val sub = SnapshotFactory::eINSTANCE.createIntSubstitution
                sub.setValue(value)
                sub.setParameterName(parameterName)
                sub
            }
            Long : {
                val sub = SnapshotFactory::eINSTANCE.createLongSubstitution
                sub.setValue(value)
                sub.setParameterName(parameterName)
                sub
            }
            Double : {
                val sub = SnapshotFactory::eINSTANCE.createDoubleSubstitution
                sub.setValue(value)
                sub.setParameterName(parameterName)
                sub				
            }
            Float : {
                val sub = SnapshotFactory::eINSTANCE.createFloatSubstitution
                sub.setValue(value)
                sub.setParameterName(parameterName)
                sub	
            }
            Boolean : {
                val sub = SnapshotFactory::eINSTANCE.createBooleanSubstitution
                sub.setValue(value)
                sub.setParameterName(parameterName)
                sub
            }
            String : {
                val sub = SnapshotFactory::eINSTANCE.createStringSubstitution
                sub.setValue(value)
                sub.setParameterName(parameterName)
                sub	
            }
            Date : {
                val sub = SnapshotFactory::eINSTANCE.createDateSubstitution
                sub.setValue(value)
                sub.setParameterName(parameterName)
                sub				
            }
            default : {
                val obj = accessMap.get(value.class.name)
                if(obj !== null){
                   val sub = obj.toSubstitution(value)
                   sub.parameterName = parameterName
                   sub
                }else{
                   val sub = SnapshotFactory::eINSTANCE.createMiscellaneousSubstitution
                   sub.setValue(value)
                   sub.setParameterName(parameterName)
                   sub  
                }
            }
        }
    }
    
    private def Function<EObject,String> getMostSpecificSerializationRule(EClass cls) {
        
                var obj = customEObjectSerializerMap.get(cls)
                
                 /* In case of multiple inheritance, the order of traversal is unspecified
                  * Keep in mind when multiple parents provide separate rules
                  */
            
                if (obj !== null) { 
                    return obj
                } else {    
                    for (type : cls.ESuperTypes) {
                        if (customEObjectSerializerMap.get(type) !== null) { return customEObjectSerializerMap.get(type) }
                    }
                    for (type : cls.ESuperTypes) {
                        return getMostSpecificSerializationRule(type)
                    }
                }
    }
    
    /**
     * Retrieve a human-readable string denoting the given record
     */
    def dispatch String prettyPrint(MatchRecord record){
        '''«FOR substitution : record.substitutions SEPARATOR ", "»«substitution.parameterName» = «substitution.derivedValue?.prettyPrint»«ENDFOR»'''
    }
    
    def dispatch String prettyPrint(EObject obj) {
        val eClass = obj.eClass
        '''«eClass.name» («FOR attr : eClass.EAllAttributes» «attr.name» = «obj.eGet(attr)?.prettyPrint» «ENDFOR»)'''
    }
    
    def dispatch String prettyPrint(Object obj) {
        return obj.toString
    }

}