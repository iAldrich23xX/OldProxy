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

package dev.waterdog.waterdogpe.network.downstream;

import com.nukkitx.protocol.bedrock.packet.*;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.FastTransferRequestEvent;
import dev.waterdog.waterdogpe.event.defaults.PostTransferCompleteEvent;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.network.rewrite.types.RewriteData;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.network.session.DownstreamClient;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.exceptions.CancelSignalException;
import dev.waterdog.waterdogpe.utils.types.TranslationContainer;

import static dev.waterdog.waterdogpe.player.PlayerRewriteUtils.injectEntityImmobile;

public class ConnectedDownstreamHandler extends AbstractDownstreamHandler {

    public ConnectedDownstreamHandler(ProxiedPlayer player, DownstreamClient client) {
        super(player, client);
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        if (!this.player.acceptPlayStatus() || packet.getStatus() != PlayStatusPacket.Status.PLAYER_SPAWN) {
            return false;
        }

        this.player.setAcceptPlayStatus(false);
        RewriteData rewriteData = this.player.getRewriteData();
        if (!rewriteData.hasImmobileFlag()) {
            injectEntityImmobile(this.player.getUpstream(), rewriteData.getEntityId(), false);
        }

        SetLocalPlayerAsInitializedPacket initializedPacket = new SetLocalPlayerAsInitializedPacket();
        initializedPacket.setRuntimeEntityId(rewriteData.getEntityId());
        this.client.sendPacket(initializedPacket);

        PostTransferCompleteEvent event = new PostTransferCompleteEvent(this.client, this.player);
        this.player.getProxy().getEventManager().callEvent(event);
        return false;
    }

    @Override
    public boolean handle(TransferPacket packet) {
        if (!this.player.getProxy().getConfiguration().useFastTransfer()) {
            return false;
        }

        ServerInfo serverInfo = this.player.getProxy().getServerInfo(packet.getAddress());
        if (serverInfo == null) {
            serverInfo = this.player.getProxy().getServerInfo(packet.getAddress(), packet.getPort());
        }

        FastTransferRequestEvent event = new FastTransferRequestEvent(serverInfo, this.player, packet.getAddress(), packet.getPort());
        this.player.getProxy().getEventManager().callEvent(event);

        if (!event.isCancelled() && event.getServerInfo() != null) {
            this.player.connect(event.getServerInfo());
            throw CancelSignalException.CANCEL;
        }
        return false;
    }

    @Override
    public final boolean handle(DisconnectPacket packet) {
        if (this.player.sendToFallback(this.client.getServerInfo(), packet.getKickMessage())) {
            throw CancelSignalException.CANCEL;
        }

        //this.player.sendMessage(Color.RED + "Error, contact https://discord.clovercube.net");
        ProxyServer.getInstance().getLogger().info(Color.RED + "Error disconnect packet ConnectedDownstreamHandler " + packet.getKickMessage());
        this.player.getDownstream().close();

        this.player.connect(ProxyServer.getInstance().getServerInfo("lobby"));
        throw CancelSignalException.CANCEL;
    }
}
