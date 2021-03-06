/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue
import org.eclipse.viatra.query.patternlanguage.emf.types.PatternLanguageTypeRules
import org.eclipse.viatra.query.patternlanguage.emf.types.TypeInformation
import org.eclipse.viatra.query.patternlanguage.emf.types.judgements.TypeJudgement
import org.eclipse.viatra.query.patternlanguage.emf.types.BottomTypeKey
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 */
class EMFPatternLanguageTypeRules extends PatternLanguageTypeRules {
   
   @Inject EMFTypeSystem typeSystem
   
   /**
    * @since 2.0
    */
   def dispatch void inferTypes(EClassifierConstraint constraint, TypeInformation information) {
       if (PatternLanguageHelper.isNonSimpleConstraint(constraint)) {
           return;
       }
        val type = if (typeSystem.isValidType(constraint.type)) {
            typeSystem.extractTypeDescriptor(constraint.type)
        } else {
            BottomTypeKey.INSTANCE
        }
        information.provideType(new TypeJudgement(constraint.^var, type))
    }
   
   /**
    * @since 2.0
    */
   def dispatch void inferTypes(EnumValue reference, TypeInformation information) {
       val type = if (reference.enumeration === null || reference.literal === null || reference.literal.EEnum === null) {
           // A previous resolution error will prevent type inference to work - it will be reported elsewhere
           BottomTypeKey.INSTANCE
       } else {
           typeSystem.classifierToInputKey(reference.literal.EEnum)
       }
       information.provideType(new TypeJudgement(reference, type))
   }
}