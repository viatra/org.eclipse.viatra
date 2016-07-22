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

#include "../../Matcher/ISearchContext.h"
#include "../../Util/Defs.h"
#include "../../Matcher/IClassHelper.h"
#include "CheckOperation.h"

namespace Viatra {
namespace Query {
namespace Operations {
namespace Check {

/**
 * @brief InstanceOf check.
 *
 * This type of check examines whether an instance is of the specified type.
 *
 * @tparam CheckedType The type of the checked object.
 * @tparam MatchingFrame Describes the structure of the *MatchingFrame* the operation is executed on.
 */
template<class CheckedType, class MatchingFrame>
class InstanceOfCheck: public CheckOperation<MatchingFrame> {
    typedef CheckedType MatchingFrame::* CheckedMember;
public:
    InstanceOfCheck(CheckedMember checked, EClass clazz);

    bool check(MatchingFrame& frame, const Matcher::ISearchContext& context);

private:
    CheckedMember _checked;
    int _clazz;
};

template<class CheckedType, class MatchingFrame>
inline InstanceOfCheck<CheckedType, MatchingFrame>::InstanceOfCheck(CheckedMember checked, EClass clazz) :
        _checked(checked), _clazz(clazz) {
}

template<class CheckedType, class MatchingFrame>
inline bool InstanceOfCheck<CheckedType, MatchingFrame>::check(MatchingFrame& frame, const Matcher::ISearchContext& context) {
    auto& ch = context.get_class_helper();
    const auto checkedObject = frame.*_checked;
    return ch.is_super_type(checkedObject->get_type_id(), _clazz);
}

template<class CheckedType, class MatchingFrame>
inline InstanceOfCheck<CheckedType, MatchingFrame>* create_InstanceOfCheck(CheckedType MatchingFrame::* checked, EClass clazz) {
	return new InstanceOfCheck<CheckedType, MatchingFrame>(checked, clazz);
}


}  /* namespace Check */
}  /* namespace Util */
}  /* namespace Query */
}  /* namespace Viatra */
