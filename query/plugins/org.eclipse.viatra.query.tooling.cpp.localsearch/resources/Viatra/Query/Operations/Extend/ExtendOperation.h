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

#include <stddef.h>
#include <list>

#include "../ISearchOperation.h"
#include "../../Util/Defs.h"

namespace Viatra {
namespace Query {
namespace Operations {
namespace Extend {

/**
 * @brief An extend operation.
 *
 * This operation represents an extend operation, meaning it will try to bind a value
 * to the specified position in the frame.
 *
 * @tparam SrcType The type of the variable to be bound.
 * @tparam Container The type of the container that has the instances possible to bound.
 * @tparam MatchingFrame Describes the structure of the *MatchingFrame* the operation is executed on.
 */
template<class SrcType, class Container, class MatchingFrame>
class ExtendOperation: public ISearchOperation<MatchingFrame> {
    typedef SrcType MatchingFrame::* MemberToBind; /** @typedef The type of the member pointer used to bind a value in a frame */

public:

    /**
     * Creates a new instance of ExtendOperation.
     * @param bind The function used to bind a value in a frame.
     */
    ExtendOperation(MemberToBind member) :
            _bind(member) {
    }

    void on_backtrack(MatchingFrame&, const Matcher::ISearchContext&) {
        //_binder(frame, NULL);
    }

    /**
     * Binds the next possible value in the frame if it exists.
     *
     * @param frame The frame the value will be bound in.
     *
     * @return **True** if the binding was successful, **False** if there are no more values to bound.
     */
    bool execute(MatchingFrame& frame, const Matcher::ISearchContext&) {
        if (_it != _end) {
            const SrcType next = *_it;
            _it++;
            frame.*_bind = next;
            return true;
        } else {
            return false;
        }
    }

protected:

    /**
     * Sets the collection of bindable values.
     * @param begin The iterator pointing to the beginning of the collection.
     * @param end The iterator pointing to the end of the collection.
     */
    void set_data(typename Container::const_iterator begin, typename Container::const_iterator end) {
        _it = begin;
        _end = end;
    }

    /**
     * @var The pointer to the function used to bind the value in the frame.
     */
    MemberToBind _bind;

private:
    typename Container::const_iterator _it;
    typename Container::const_iterator _end;
};

}  /* namespace Extend */
}  /* namespace Operations */
}  /* namespace Query */
}  /* namespace Viatra */
