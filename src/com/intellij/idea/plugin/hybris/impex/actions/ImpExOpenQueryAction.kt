/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2023 EPAM Systems <hybrisideaplugin@epam.com> and contributors
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
package com.intellij.idea.plugin.hybris.impex.actions

import com.intellij.idea.plugin.hybris.actions.CopyFileToHybrisConsoleUtils
import com.intellij.idea.plugin.hybris.common.HybrisConstants
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils.message
import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons
import com.intellij.idea.plugin.hybris.impex.file.ImpexFileType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.SingleRootFileViewProvider

class ImpExOpenQueryAction : AnAction() {

    init {
        with (templatePresentation) {
            text = message("hybris.impex.actions.open_query")
            description = message("hybris.impex.actions.open_query.description")
            icon = HybrisIcons.CONSOLE_OPEN
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val query = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
            ?.firstOrNull()
            ?.takeIf { it.fileType is ImpexFileType }
            ?.takeUnless { SingleRootFileViewProvider.isTooLargeForIntelligence(it) }
            ?.let { FileDocumentManager.getInstance().getDocument(it) }?.text
            ?: return

        CopyFileToHybrisConsoleUtils.copyQueryToConsole(
            project,
            HybrisConstants.IMPEX_CONSOLE_TITLE,
            query
        )
    }

}