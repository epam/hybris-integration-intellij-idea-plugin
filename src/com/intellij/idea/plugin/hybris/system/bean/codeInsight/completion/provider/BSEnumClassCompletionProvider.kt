/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019-2023 EPAM Systems <hybrisideaplugin@epam.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.system.bean.codeInsight.completion.provider

import com.intellij.idea.plugin.hybris.system.bean.meta.model.BSMetaType
import com.intellij.openapi.application.ApplicationManager
import java.util.*

class BSEnumClassCompletionProvider : BSClassCompletionProvider(EnumSet.of(BSMetaType.META_ENUM)) {
    companion object {
        val instance: BSEnumClassCompletionProvider = ApplicationManager.getApplication().getService(BSEnumClassCompletionProvider::class.java)
    }
}