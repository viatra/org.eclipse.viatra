/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.testing.core

import java.util.ArrayList
import java.util.Date
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.snapshot.BooleanSubstitution
import org.eclipse.viatra.query.testing.snapshot.DateSubstitution
import org.eclipse.viatra.query.testing.snapshot.DoubleSubstitution
import org.eclipse.viatra.query.testing.snapshot.EMFSubstitution
import org.eclipse.viatra.query.testing.snapshot.EnumSubstitution
import org.eclipse.viatra.query.testing.snapshot.FloatSubstitution
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot
import org.eclipse.viatra.query.testing.snapshot.InputSpecification
import org.eclipse.viatra.query.testing.snapshot.IntSubstitution
import org.eclipse.viatra.query.testing.snapshot.LongSubstitution
import org.eclipse.viatra.query.testing.snapshot.MatchRecord
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord
import org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord
import org.eclipse.viatra.query.testing.snapshot.MiscellaneousSubstitution
import org.eclipse.viatra.query.testing.snapshot.SnapshotFactory
import org.eclipse.viatra.query.testing.snapshot.StringSubstitution

/**
 * Helper methods for dealing with snapshots and match set records.
 */
class SnapshotHelper {
	
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
	def getInputSpecificationForMatcher(ViatraQueryMatcher matcher){
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
	def saveMatchesToSnapshot(ViatraQueryMatcher matcher, IPatternMatch partialMatch, QuerySnapshot snapshot){
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
			if(match.get(param) != null){
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
		matcher.forEachMatch(filter,[ match |
			matchSetRecord.matches.add(createMatchRecordForMatch(match))
		] );
		return matchSetRecord
	}

	/**
	 * Creates a partial match that corresponds to the given match record.
	 *  Each substitution is used as a value for the parameter with the same name.
	 * @deprecated use createMatchForMatchRecord(IQuerySpecification, MatchRecord) instead
	 */
	@Deprecated
	def createMatchForMachRecord(ViatraQueryMatcher matcher, MatchRecord matchRecord){
		val match = matcher.newEmptyMatch
		matchRecord.substitutions.forEach()[
			var target = derivedValue
			/*if(target instanceof EObject){
				var etarget = target as EObject
				if(etarget.eIsProxy){
					target = EcoreUtil::resolve(etarget, matchRecord)
				}
			}*/
			match.set(parameterName,target)
		]
		return match
	}

	/**
	 * Creates a partial match that corresponds to the given match record.
	 *  Each substitution is used as a value for the parameter with the same name.
	 */
	def <Match extends IPatternMatch> Match createMatchForMatchRecord(IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, MatchRecord matchRecord){
		val match = querySpecification.newEmptyMatch as Match
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
	def saveMatchesToSnapshot(ViatraQueryMatcher matcher, QuerySnapshot snapshot){
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
			EEnumLiteral: {
				val sub = SnapshotFactory::eINSTANCE.createEnumSubstitution
				sub.setValueLiteral((value as EEnumLiteral).literal)
				sub.setEnumType((value as EEnumLiteral).EEnum)
				sub.setParameterName(parameterName)
				sub
			}
			EObject : {
				val sub = SnapshotFactory::eINSTANCE.createEMFSubstitution
				sub.setValue(value as EObject)
				sub.setParameterName(parameterName)
				sub
			}
			Integer : {
				val sub = SnapshotFactory::eINSTANCE.createIntSubstitution
				sub.setValue(value as Integer)
				sub.setParameterName(parameterName)
				sub
			}
			Long : {
				val sub = SnapshotFactory::eINSTANCE.createLongSubstitution
				sub.setValue(value as Long)
				sub.setParameterName(parameterName)
				sub
			}
			Double : {
				val sub = SnapshotFactory::eINSTANCE.createDoubleSubstitution
				sub.setValue(value as Double)
				sub.setParameterName(parameterName)
				sub				
			}
			Float : {
				val sub = SnapshotFactory::eINSTANCE.createFloatSubstitution
				sub.setValue(value as Float)
				sub.setParameterName(parameterName)
				sub	
			}
			Boolean : {
				val sub = SnapshotFactory::eINSTANCE.createBooleanSubstitution
				sub.setValue(value as Boolean)
				sub.setParameterName(parameterName)
				sub
			}
			String : {
				val sub = SnapshotFactory::eINSTANCE.createStringSubstitution
				sub.setValue(value as String)
				sub.setParameterName(parameterName)
				sub	
			}
			Date : {
				val sub = SnapshotFactory::eINSTANCE.createDateSubstitution
				sub.setValue(value as Date)
				sub.setParameterName(parameterName)
				sub				
			}
			default : {
				val sub = SnapshotFactory::eINSTANCE.createMiscellaneousSubstitution
				sub.setValue(value)
				sub.setParameterName(parameterName)
				sub	
			}
		}
	}
	
	/**
	 * Retrieve a human-readable string denoting the given record
	 */
	def String prettyPrint(MatchRecord record){
		val sb = new StringBuilder()
		val first = #[true]
		record.substitutions.forEach[
			if (first.get(0)){
				first.set(0, false);
			}else{
				sb.append(", ");
			}
			sb.append(it.parameterName)
			sb.append(" = ");
			sb.append(it.derivedValue)
		]
		return sb.toString
	}

}