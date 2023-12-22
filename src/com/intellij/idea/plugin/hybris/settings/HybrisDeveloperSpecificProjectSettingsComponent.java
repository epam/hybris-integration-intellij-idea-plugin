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

package com.intellij.idea.plugin.hybris.settings;

import com.intellij.idea.plugin.hybris.common.HybrisConstants;
import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService;
import com.intellij.idea.plugin.hybris.properties.PropertiesService;
import com.intellij.idea.plugin.hybris.settings.HybrisRemoteConnectionSettings.Type;
import com.intellij.lang.properties.IProperty;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.idea.plugin.hybris.common.HybrisConstants.STORAGE_HYBRIS_DEVELOPER_SPECIFIC_PROJECT_SETTINGS;

@State(name = "HybrisDeveloperSpecificProjectSettings", storages = {@Storage(value = STORAGE_HYBRIS_DEVELOPER_SPECIFIC_PROJECT_SETTINGS, roamingType = RoamingType.DISABLED)})
@Service(Service.Level.PROJECT)
public final class HybrisDeveloperSpecificProjectSettingsComponent implements PersistentStateComponent<HybrisDeveloperSpecificProjectSettings> {

    private final MessageBus myMessageBus;
    private final HybrisDeveloperSpecificProjectSettings state = new HybrisDeveloperSpecificProjectSettings();


    public HybrisDeveloperSpecificProjectSettingsComponent(final Project project) {
        myMessageBus = project.getMessageBus();
    }

    public static HybrisDeveloperSpecificProjectSettingsComponent getInstance(@NotNull final Project project) {
        return project.getService(HybrisDeveloperSpecificProjectSettingsComponent.class);
    }

    @NotNull
    @Override
    public HybrisDeveloperSpecificProjectSettings getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull final HybrisDeveloperSpecificProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public List<HybrisRemoteConnectionSettings> getHacRemoteConnectionSettings() {
        return getHybrisRemoteConnectionSettings(Type.Hybris);
    }

    public List<HybrisRemoteConnectionSettings> getSolrRemoteConnectionSettings() {
        return getHybrisRemoteConnectionSettings(Type.SOLR);
    }

    private List<HybrisRemoteConnectionSettings> getHybrisRemoteConnectionSettings(
        final Type type
    ) {
        if (getState() == null) return Collections.emptyList();
        return getState().getRemoteConnectionSettingsList().stream()
            .filter(it -> it.getType() == type)
            .collect(Collectors.toList());
    }

    public HybrisRemoteConnectionSettings getActiveHacRemoteConnectionSettings(final Project project) {
        final HybrisDeveloperSpecificProjectSettings state = getState();
        if (state == null) {
            return getDefaultHacRemoteConnectionSettings(project);
        }
        final var instances = getHacRemoteConnectionSettings();
        if (instances.isEmpty()) {
            return getDefaultHacRemoteConnectionSettings(project);
        }
        final String id = state.getActiveRemoteConnectionID();

        return instances.stream()
            .filter(e -> Objects.equals(id, e.getUuid()))
            .findFirst()
            .orElseGet(() -> instances.get(0));
    }

    public HybrisRemoteConnectionSettings getActiveSolrRemoteConnectionSettings(final Project project) {
        final HybrisDeveloperSpecificProjectSettings state = getState();
        if (state == null) {
            return getDefaultSolrRemoteConnectionSettings(project);
        }
        final var instances = getSolrRemoteConnectionSettings();
        if (instances.isEmpty()) {
            return getDefaultSolrRemoteConnectionSettings(project);
        }
        final String id = state.getActiveSolrConnectionID();

        return instances.stream()
            .filter(e -> Objects.equals(id, e.getUuid()))
            .findFirst()
            .orElseGet(() -> instances.get(0));
    }

    @NotNull
    public HybrisRemoteConnectionSettings getDefaultHacRemoteConnectionSettings(final Project project) {
        final HybrisRemoteConnectionSettings item = new HybrisRemoteConnectionSettings();
        item.setType(HybrisRemoteConnectionSettings.Type.Hybris);
        item.setUuid(UUID.randomUUID().toString());
        item.setHostIP(HybrisConstants.DEFAULT_HOST_URL);
        item.setPort(getPropertyOrDefault(project, HybrisConstants.PROPERTY_TOMCAT_SSL_PORT, "9002"));
        item.setHacWebroot(getPropertyOrDefault(project, HybrisConstants.PROPERTY_HAC_WEBROOT, ""));
        item.setHacLogin("admin");
        item.setHacPassword("nimda");
        item.setSsl(true);
        item.setSslProtocol(HybrisConstants.DEFAULT_SSL_PROTOCOL);
        item.setGeneratedURL(CommonIdeaService.getInstance().getHostHacUrl(project, item));
        return item;
    }

    @NotNull
    public HybrisRemoteConnectionSettings getDefaultSolrRemoteConnectionSettings(final Project project) {
        final HybrisRemoteConnectionSettings item = new HybrisRemoteConnectionSettings();
        item.setType(HybrisRemoteConnectionSettings.Type.SOLR);
        item.setUuid(UUID.randomUUID().toString());
        item.setHostIP(HybrisConstants.DEFAULT_HOST_URL);

        item.setPort(getPropertyOrDefault(project, HybrisConstants.PROPERTY_SOLR_DEFAULT_PORT, "8983"));
        item.setSolrWebroot("solr");
        item.setAdminLogin(getPropertyOrDefault(project, HybrisConstants.PROPERTY_SOLR_DEFAULT_USER, "solrserver"));
        item.setAdminPassword(getPropertyOrDefault(project, HybrisConstants.PROPERTY_SOLR_DEFAULT_PASSWORD, "server123"));
        item.setSsl(true);
        item.setGeneratedURL(CommonIdeaService.getInstance().getSolrUrl(project, item));
        return item;
    }

    public void setActiveHacRemoteConnectionSettings(final HybrisRemoteConnectionSettings settings) {
        if (settings == null || getState() == null) {
            return;
        }
        getState().setActiveRemoteConnectionID(settings.getUuid());
        myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).hacActiveConnectionSettingsChanged();
    }

    public void setActiveSolrRemoteConnectionSettings(final HybrisRemoteConnectionSettings settings) {
        if (settings == null || getState() == null) {
            return;
        }
        getState().setActiveSolrConnectionID(settings.getUuid());
        myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).solrActiveConnectionSettingsChanged();
    }

    public void saveRemoteConnectionSettingsList(
        final Type type,
        final List<HybrisRemoteConnectionSettings> instances
    ) {
        if (getState() == null) {
            return;
        }
        final var newInstances = new ArrayList<>(instances);
        getState().getRemoteConnectionSettingsList().stream()
            .filter(it -> it.getType() != type)
            .forEach(newInstances::add);
        getState().setRemoteConnectionSettingsList(newInstances);
        switch (type) {
            case Hybris -> myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).hacConnectionSettingsChanged();
            case SOLR -> myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).solrConnectionSettingsChanged();
        }
    }

    private static String getPropertyOrDefault(final Project project, final String key, final String fallback) {
        return Optional.ofNullable(PropertiesService.getInstance(project))
            .map(it -> it.findProperty(key))
            .orElse(fallback);
    }
}
