/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
#pragma once

#include "CheckOperation.h"

namespace Viatra {
namespace Query {
namespace Operations {
namespace Check {

/**
 * @brief Expression check for running simple expressions.
 *
 * This class handles running 'check(...expression...)' constraints on the model. This is done
 * via helper check classes containing the expression itself.
 *
 * @tparam Check The type of the helper check class.
 * @tparam MatchingFrame Describes the structure of the *MatchingFrame* the operation is executed on.
 */
template<class Check, class MatchingFrame>
class CheckExpression: public CheckOperation<MatchingFrame> {
public:
    /**
     * Creates an instance of an expression check using the specified check.
     *
     * @param check An instance of the class containing the proper check.
     */
    CheckExpression(Check check);

    bool check(MatchingFrame& frame, const Matcher::ISearchContext& context);

private:
    Check _check;
};

template<class Check, class MatchingFrame>
inline CheckExpression<Check, MatchingFrame>::CheckExpression(Check check) :
        _check(check) {
}

template<class Check, class MatchingFrame>
inline bool CheckExpression<Check, MatchingFrame>::check(MatchingFrame& frame, const Matcher::ISearchContext& context) {
    return _check(frame);
}

}  /* namespace Check */
}  /* namespace Operations */
}  /* namespace Query */
}  /* namespace Viatra */
