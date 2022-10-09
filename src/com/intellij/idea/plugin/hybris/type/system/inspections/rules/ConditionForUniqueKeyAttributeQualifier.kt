/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019 EPAM Systems <hybrisideaplugin@epam.com>
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
package com.intellij.idea.plugin.hybris.type.system.inspections.rules

import com.intellij.idea.plugin.hybris.type.system.inspections.TypeSystemInspection

class ConditionForUniqueKeyAttributeQualifier : TypeSystemInspection() {
    override fun getSelectionQuery(): String = "//itemtype/attributes/attribute[concat('\"',@qualifier,'\"')=//itemtype/custom-properties/property[@name='uniqueKeyAttributeQualifier']/value/text()]"

    override fun getTestQuery(): String = "./modifiers[@optional='false' and @unique='true']"

    override fun getNameQuery(): String = "./@qualifier"

}