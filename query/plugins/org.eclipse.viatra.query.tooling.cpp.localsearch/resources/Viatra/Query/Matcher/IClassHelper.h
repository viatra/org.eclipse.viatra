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

#include "../Util/Defs.h"

namespace Viatra {
namespace Query {
namespace Matcher {

/**
 * @brief Interface of a class helper.
 *
 * This is an interface for different implementation of class helpers for inheritance checking.
 */
class IClassHelper {
public:
    virtual ~IClassHelper() {
    }

    /**
     * Checks whether parent is the super type of child.
     *
     * @param child The child type.
     * @param parent The super type.
     *
     * @return **True** if parent is super of child, **False** otherwise.
     */
    virtual bool is_super_type(const EClass& child, const EClass& parent) const = 0;
};

}  /* namespace Matcher */
}  /* namespace Query */
}  /* namespace Viatra */
