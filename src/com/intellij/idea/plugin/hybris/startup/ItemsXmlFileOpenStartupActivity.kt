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
package com.intellij.idea.plugin.hybris.startup

import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils
import com.intellij.idea.plugin.hybris.common.utils.HybrisItemsXmlFileType
import com.intellij.idea.plugin.hybris.notifications.Notifications
import com.intellij.idea.plugin.hybris.system.type.validation.impl.DefaultItemsFileValidation
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope


class ItemsXmlFileOpenStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        if (!ApplicationManager.getApplication().getService(CommonIdeaService::class.java).isHybrisProject(project)) {
            return
        }

        DumbService.getInstance(project).runReadActionInSmartMode {
            val validation = DefaultItemsFileValidation(project)
            val isOutdated = FileTypeIndex.getFiles(
                HybrisItemsXmlFileType.INSTANCE,
                GlobalSearchScope.projectScope(project)
            )
                .any { file -> validation.isFileOutOfDate(file) }
            if (isOutdated) {
                Notifications.create(
                    NotificationType.WARNING,
                    HybrisI18NBundleUtils.message("hybris.notification.ts.validation.title"),
                    HybrisI18NBundleUtils.message("hybris.notification.ts.validation.content")
                )
                    .important(true)
                    .hideAfter(10)
                    .notify(project)
            }
        }
    }
}