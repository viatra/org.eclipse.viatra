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

namespace Viatra {
namespace Query {
namespace Util {

/**
* @brief Nullptr checker utility.
*
* This helper struct allows the checking for a nullptr regardless if the type is a pointer. Returns false for
* non pointer values (as they can never be nullptr).
*
* @tparam T Type of the parameter.
*/
template<class T>
struct IsNull {
	static bool check(const T) {
		return false;
	}
};

template<class T>
struct IsNull<T*> {
	static bool check(const T* val) {
		return val == nullptr;
	}
};

}  /* namespace Util*/
}  /* namespace Query*/
}  /* namespace Viatra */
