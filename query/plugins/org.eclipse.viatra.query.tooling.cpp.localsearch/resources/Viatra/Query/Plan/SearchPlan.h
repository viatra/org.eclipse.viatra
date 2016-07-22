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

#include <algorithm>
#include <memory>
#include <vector>

#include "../Operations/ISearchOperation.h"

namespace Viatra {
namespace Query {
namespace Plan {

/**
 * @brief A search plan.
 *
 * This class represents a search plan consisting of Operations::ISearchOperation.
 *
 * @tparam MatchingFrame The frame the operations will be executed on.
 */
template<class MatchingFrame>
class SearchPlan {
public:

    /**
     * Adds a new operation to the search plan. This takes ownership of the operation.
     *
     * @param operation The search operation to add.
     */
    void add_operation(Operations::ISearchOperation<MatchingFrame>* operation);

    /**
     * Adds a collection of new operations to the search plan. This takes ownership of the operations.
     *
     * @param operation The vector of operations to add.
     */
    void add_operation(std::vector<Operations::ISearchOperation<MatchingFrame>*> operations);

    /**
     * Returns the vector of operations contained in the plan.
     *
     * @return The std::vector of instances of Operations::ISearchOperation.
     */
    const std::vector<std::shared_ptr<Operations::ISearchOperation<MatchingFrame> > >& get_operations() const;
private:
    std::vector<std::shared_ptr<Operations::ISearchOperation<MatchingFrame> > > _operations;
};

template<class MatchingFrame>
inline void SearchPlan<MatchingFrame>::add_operation(Operations::ISearchOperation<MatchingFrame>* operation) {
	_operations.push_back(std::shared_ptr<Operations::ISearchOperation<MatchingFrame> >(operation));
}

template<class MatchingFrame>
inline void SearchPlan<MatchingFrame>::add_operation(std::vector<Operations::ISearchOperation<MatchingFrame> *> operations) {
	std::transform(std::begin(operations), std::end(operations), std::back_inserter(_operations),
		[](Operations::ISearchOperation<MatchingFrame> * op) {return std::shared_ptr<::Query::Operations::ISearchOperation<MatchingFrame> >(op); }
	);
}

template<class MatchingFrame>
inline const std::vector<std::shared_ptr<Operations::ISearchOperation<MatchingFrame> > >& Query::Plan::SearchPlan<MatchingFrame>::get_operations() const {
	return _operations;
}

} /* namespace Plan */
} /* namespace Query */
} /* namespace Viatra */

