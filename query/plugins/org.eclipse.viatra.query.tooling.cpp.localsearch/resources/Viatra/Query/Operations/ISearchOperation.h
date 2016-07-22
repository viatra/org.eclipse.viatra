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

#include <list>

#include "../Matcher/ISearchContext.h"

namespace Viatra {
namespace Query {
namespace Operations {

/**
 * @brief The interface of a search operation.
 *
 * This interface defines the structure of a search operation used
 * in the search plan.
 *
 *  @tparam MatchingFrame Describes the structure of the *MatchingFrame* the operation is executed on.
 */
template<class MatchingFrame>
class ISearchOperation {
public:
    /**
     * Destroys the operation instance.
     */
    virtual ~ISearchOperation() {
    }


    /**
     * Initializes the operation.
     * @param frame The frame the operation is executed on.
     * @param context The context of the search.
     */
    virtual void on_initialize(MatchingFrame& frame, const Matcher::ISearchContext& context) = 0;

    /**
     * Defines what to do if backtracking is necessary.
     *
     * @param frame The frame the operation is executed on.
     * @param context The context of the search.
     */
    virtual void on_backtrack(MatchingFrame& frame, const Matcher::ISearchContext& context) = 0;

    /**
     * Defines the execution of the operation.
     *
     * @param frame The frame the operation is executed on.
     * @param context The context of the search.
     *
     * @return **True** if the execution completed successfully,
     * **False** if the execution failed and backtracking is necessary.
     */
    virtual bool execute(MatchingFrame& frame, const Matcher::ISearchContext& context) = 0;

private:
};

}  /* namespace Operations */
}  /* namespace Localsearch */
}  /* namespace Viatra */
