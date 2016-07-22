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

#include <tuple>

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
 * @tparam MatchingFrame Describes the structure of the *MatchingFrame* the operation is executed on.
 * @tparam TequiredMatcher The matcher the nac call has to call.
 * @tparam Mp The arbitrary list of member pointers to access the proper field of the frame (to pass them to the matcher).
*/
template<class MatchingFrame, class RequiredMatcher, class ...Mp>
class NACOperation : public CheckOperation<MatchingFrame> {
public:
	NACOperation(const RequiredMatcher& matcher, Mp... memberPointers);

protected:
	bool check(MatchingFrame& frame, const Matcher::ISearchContext& context);

private:

	template<unsigned int... index>
	bool invoke_helper(MatchingFrame& frame, std::index_sequence<index...>);

	const RequiredMatcher _matcher;
	std::tuple<Mp...> _memberPointers;
};

template<class MatchingFrame, class RequiredMatcher, class ...Mp>
inline NACOperation<MatchingFrame, RequiredMatcher, Mp...>::NACOperation(const RequiredMatcher& matcher, Mp ...memberPointers) :
	_matcher(matcher), _memberPointers(memberPointers...) {
}

template<class MatchingFrame, class RequiredMatcher, class ...Mp>
inline bool NACOperation<MatchingFrame, RequiredMatcher, Mp...>::check(MatchingFrame & frame, const Matcher::ISearchContext & context) {
	constexpr auto Size = std::tuple_size<typename std::decay<std::tuple<Mp...>>::type>::value;
	return invoke_helper(frame, std::make_index_sequence<Size>{});
}

template<class MatchingFrame, class RequiredMatcher, class ...Mp>
NACOperation<MatchingFrame, RequiredMatcher, Mp...>* create_NACOperation(const RequiredMatcher& matcher, Mp... memberPointers) {
	return new NACOperation<MatchingFrame, RequiredMatcher, Mp...>(matcher, memberPointers...);
}

template<class MatchingFrame, class RequiredMatcher, class ...Mp>
template<unsigned int ...index>
inline bool NACOperation<MatchingFrame, RequiredMatcher, Mp...>::invoke_helper(MatchingFrame& frame, std::index_sequence<index...>) {
	auto matches = _matcher.matches((frame.*std::get<index>(std::forward<std::tuple<Mp...>>(_memberPointers)))...);
	return matches.size() == 0;
}

}  /* namespace Check */
}  /* namespace Util */
}  /* namespace Query */
}  /* namespace Viatra */
