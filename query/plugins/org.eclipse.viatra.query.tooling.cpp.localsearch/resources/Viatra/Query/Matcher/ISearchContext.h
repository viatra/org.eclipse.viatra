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

#include "IClassHelper.h"

namespace Viatra {
namespace Query {
namespace Matcher {

/**
 * @brief Context of the search.
 *
 * An instance of this class represents the context of a search,
 * giving access to a collection of utility classes that can be
 * used while searching for a pattern.
 *
 * For now it only contains the Util::IClassHelper, but it might get extended later.
 */
class ISearchContext {
public:
    /**
     * Constructs an instance of ISearchContext wit the specified instance
     * of Util::IClassHelper.
     *
     * @param ch The pointer to the instance of an Util::IClassHelper.
     */
    ISearchContext(IClassHelper* ch) :
            _ch(ch) {
    }

    /**
     * Returns an instance of Util::IClassHelper.
     *
     * @return The instance of Util::IClassHelper.
     */
    IClassHelper& get_class_helper() const {
        return *_ch;
    }

private:
    IClassHelper* _ch;
};

}  /* namespace Matcher */
}  /* namespace Query */
}  /* namespace Viatra */
