/**
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.sdk.dmf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.hawkbit.dmf.amqp.api.EventTopic;
import org.eclipse.hawkbit.dmf.json.model.DmfDownloadAndUpdateRequest;
import org.eclipse.hawkbit.dmf.json.model.DmfUpdateMode;
import org.eclipse.hawkbit.sdk.Controller;
import org.eclipse.hawkbit.sdk.Tenant;
import org.eclipse.hawkbit.sdk.dmf.amqp.DmfSender;

/**
 * Class representing DMF device twin connecting to hawkBit via DMF.
 */
@Slf4j
@Getter
public class DmfController {

    private static final String LOG_PREFIX = "[{}:{}] ";

    private static final String DEPLOYMENT_BASE_LINK = "deploymentBase";
    private static final String CONFIRMATION_BASE_LINK = "confirmationBase";

    private final Tenant tenant;
    private final Controller controller;
    private final UpdateHandler updateHandler;
    private final DmfSender dmfSender;

    @Getter
    private final Map<String, String> attributes = new HashMap<>();

    @Setter
    private volatile Long currentActionId;

    private volatile Long lastActionId;

    @SuppressWarnings("java:S3077") // volatile used only for the reference as expected
    private volatile ScheduledExecutorService executorService;

    /**
     * Creates a new device instance.
     *
     * @param tenant the tenant of the device belongs to
     * @param controller the controller
     */
    DmfController(
            final Tenant tenant, final Controller controller,
            final UpdateHandler updateHandler,
            final DmfSender dmfSender) {
        this.tenant = tenant;
        this.controller = controller;
        this.updateHandler = updateHandler == null ? UpdateHandler.SKIP : updateHandler;
        this.dmfSender = dmfSender;
    }

    public String getTenantId() {
        return tenant.getTenantId();
    }

    public String getControllerId() {
        return controller.getControllerId();
    }

    public void start(ScheduledExecutorService executorService) {
        stop();
        this.executorService = executorService;
        log.debug(LOG_PREFIX + "Connecting/Polling ...", getTenantId(), getControllerId());
        dmfSender.createOrUpdateThing(getTenantId(), getControllerId());
        log.debug(LOG_PREFIX + "Done. Create thing sent.", getTenantId(), getControllerId());
    }

    public void unregisterThing() {
        log.debug(LOG_PREFIX + "Removing Controller...", getTenantId(), getControllerId());
        dmfSender.removeThing(getTenantId(), getControllerId());
        log.debug(LOG_PREFIX + "Done. Remove thing sent.", getTenantId(), getControllerId());
    }

    public void stop() {
        if (executorService != null) {
            executorService.shutdown();
        }
        executorService = null;
        lastActionId = null;
        currentActionId = null;
    }

    public void processUpdate(final EventTopic actionType, final DmfDownloadAndUpdateRequest updateRequest) {
        log.info(LOG_PREFIX + "Processing update for action {} .", getTenantId(), getControllerId(), updateRequest.getActionId());
        executorService.submit(updateHandler.getUpdateProcessor(this, actionType, updateRequest));
    }

    public void sendFeedback(final UpdateStatus updateStatus) {
        log.info(LOG_PREFIX + "Sending UPDATE_ACTION_STATUS for action : {}", getTenantId(), getControllerId(), currentActionId);
        dmfSender.sendFeedback(getTenantId(), currentActionId, updateStatus);
    }

    public void sendUpdateAttributes() {
        log.info(LOG_PREFIX + "Sending UPDATE_ATTRIBUTES", getTenantId(), getControllerId());
        dmfSender.updateAttributes(getTenantId(), getControllerId(), DmfUpdateMode.MERGE, attributes);
    }

    public void setAttribute(final String key, final String value) {
        this.attributes.put(key, value);
    }

    public void removeAttribute(final String key) {
        this.attributes.remove(key);
    }

    public void thingCreated() {
        log.debug(LOG_PREFIX + "Thing created.", getTenantId(), getControllerId());
        dmfSender.createOrUpdateThing(getTenantId(), getControllerId());
    }
}