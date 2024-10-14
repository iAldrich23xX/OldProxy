package dev.waterdog.waterdogpe.event.defaults;

import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.ItemComponentPacket;
import dev.waterdog.waterdogpe.utils.exceptions.CancelSignalException;
import dev.waterdog.waterdogpe.utils.types.PacketHandler;

public class DownstreamPacketHandler extends PacketHandler {

    private boolean itemComponentPacketSent = false;

    public DownstreamPacketHandler(BedrockSession session) {
        super(session);
    }

    @Override
    public boolean handle(ItemComponentPacket packet) {
        if (itemComponentPacketSent) {
            throw CancelSignalException.CANCEL;
        }

        itemComponentPacketSent = true;
        return true;
    }
}
