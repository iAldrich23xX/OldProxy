/*
 * Copyright 2021 WaterdogTEAM
 * Licensed under the GNU General Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.waterdog.waterdogpe.event.defaults;

import dev.waterdog.waterdogpe.event.AsyncEvent;
import dev.waterdog.waterdogpe.network.session.DownstreamClient;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

/**
 * Called when the transfer from one server to the next is completed.
 * At this point, the player is already logged in and registered to the new downstream target and the old downstream is already
 * disconnected.
 */
@AsyncEvent
public class TransferCompleteEvent extends PlayerEvent {

    private final DownstreamClient oldClient;
    private final DownstreamClient newClient;
    private boolean effectsAllowed = true;

    public TransferCompleteEvent(DownstreamClient oldClient, DownstreamClient newClient, ProxiedPlayer player) {
        super(player);
        this.oldClient = oldClient;
        this.newClient = newClient;
    }

    public DownstreamClient getOldClient() {
        return this.oldClient;
    }

    public DownstreamClient getNewClient() {
        return this.newClient;
    }

    public boolean isEffectsAllowed()
    {
        return this.effectsAllowed;
    }

    public void setEffectsAllowed(boolean effectsAllowed)
    {
        this.effectsAllowed = effectsAllowed;
    }
}

