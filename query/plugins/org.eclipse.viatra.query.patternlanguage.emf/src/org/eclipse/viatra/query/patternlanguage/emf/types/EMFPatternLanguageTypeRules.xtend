/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue
import org.eclipse.viatra.query.patternlanguage.typing.PatternLanguageTypeRules
import org.eclipse.viatra.query.patternlanguage.typing.TypeInformation
import org.eclipse.viatra.query.patternlanguage.typing.judgements.TypeJudgement
import org.eclipse.viatra.query.patternlanguage.typing.BottomTypeKey

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 */
class EMFPatternLanguageTypeRules extends PatternLanguageTypeRules {
   
   @Inject EMFTypeSystem typeSystem
   
   def dispatch void inferTypes(EClassifierConstraint constraint, TypeInformation information) {
        val type = if (typeSystem.isValidType(constraint.type)) {
            typeSystem.extractTypeDescriptor(constraint.type)
        } else {
            BottomTypeKey.INSTANCE
        }
        information.provideType(new TypeJudgement(constraint.^var, type))
    }
   
   def dispatch void inferTypes(EnumValue reference, TypeInformation information) {
       val type = if (reference.enumeration === null) {
           // A previous resolution error will prevent type inference to work - it will be reported elsewhere
           BottomTypeKey.INSTANCE
       } else {
           typeSystem.classifierToInputKey(reference.literal.EEnum)
       }
       information.provideType(new TypeJudgement(reference, type))
   }
}