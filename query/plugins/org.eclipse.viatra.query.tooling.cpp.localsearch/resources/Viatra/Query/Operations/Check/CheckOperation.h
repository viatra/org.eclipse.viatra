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

#include "../ISearchOperation.h"

namespace Viatra {
namespace Query {
namespace Operations {
namespace Check {

/**
 * @brief A check operation.
 *
 * This operation represents a check operation, meaning it will not bind any values in the
 * frame, it only checks if the already bound values satisfy a specific type of constraint.
 *
 * @tparam MatchingFrame Describes the structure of the *MatchingFrame* the operation is executed on.
 */
template<class MatchingFrame>
class CheckOperation: public ISearchOperation<MatchingFrame> {
public:

    /**
     * Creates a new CheckOperation.
     */
    CheckOperation() :
            _executed(false) {
    }

    /**
     * Destroys a CheckOperation instance.
     */
	virtual ~CheckOperation() = default;

    void on_initialize(MatchingFrame&, const Matcher::ISearchContext&) {
        _executed = false;
    }

    void on_backtrack(MatchingFrame&, const Matcher::ISearchContext&) {
        // nop
    }

    /**
     * Executes the check iff it wasn't executed already.
     *
     * @param frame The frame the operation is executed on.
     * @param context The context of the search.
     * @return
     */
    bool execute(MatchingFrame& frame, const Matcher::ISearchContext& context) {
        _executed = _executed ? false : check(frame, context);
        return _executed;
    }

protected:
    /**
     * Defines the execution of the check operation.
     *
     * @param frame The frame the operation is executed on.
     * @param context The context of the search.
     * @return **True** if the check was successful, **False** otherwise.
     */
    virtual bool check(MatchingFrame& frame, const Matcher::ISearchContext& context) = 0;

private:
    bool _executed; /** @var Indicates whether the check was executed already. **/
};

}  /* namespace Check */
}  /* namespace Operations */
}  /* namespace Query */
}  /* namespace Viatra */
