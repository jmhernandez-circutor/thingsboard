/**
 * Copyright © 2016-2018 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.rule.engine.util;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.alarm.AlarmId;
import org.thingsboard.server.common.data.id.*;

public class EntitiesTenantIdAsyncLoader {

    public static ListenableFuture<TenantId> findEntityIdAsync(TbContext ctx, EntityId original) {

        switch (original.getEntityType()) {
            case TENANT:
                return Futures.immediateFuture((TenantId) original);
            case CUSTOMER:
                return getTenantAsync(ctx.getCustomerService().findCustomerByIdAsync((CustomerId) original));
            case USER:
                return getTenantAsync(ctx.getUserService().findUserByIdAsync((UserId) original));
            case PLUGIN:
                return getTenantAsync(ctx.getPluginService().findPluginByIdAsync((PluginId) original));
            case ASSET:
                return getTenantAsync(ctx.getAssetService().findAssetByIdAsync((AssetId) original));
            case DEVICE:
                return getTenantAsync(ctx.getDeviceService().findDeviceByIdAsync((DeviceId) original));
            case ALARM:
                return getTenantAsync(ctx.getAlarmService().findAlarmByIdAsync((AlarmId) original));
            case RULE_CHAIN:
                return getTenantAsync(ctx.getRuleChainService().findRuleChainByIdAsync((RuleChainId) original));
            default:
                return Futures.immediateFailedFuture(new TbNodeException("Unexpected original EntityType " + original));
        }
    }

    private static <T extends HasTenantId> ListenableFuture<TenantId> getTenantAsync(ListenableFuture<T> future) {
        return Futures.transform(future, (AsyncFunction<HasTenantId, TenantId>) in -> {
            return in != null ? Futures.immediateFuture(in.getTenantId())
                    : Futures.immediateFuture(null);});
    }
}
