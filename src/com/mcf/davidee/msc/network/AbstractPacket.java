package com.mcf.davidee.msc.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;


/**
 * AbstractPacket class. Should be the parent of all packets wishing to use the PacketPipeline.
 * @author sirgingalot
 */
public abstract class AbstractPacket {

    /**
     * Encode the packet data into the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
     *
     * @param ctx    channel context
     * @param buffer the buffer to encode into
     */
    public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf to) throws IOException;

    /**
     * Decode the packet data from the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
     *
     * @param ctx    channel context
     * @param buffer the buffer to decode from
     */
    public abstract void decodeFrom(ChannelHandlerContext ctx, ByteBuf from) throws IOException;

    public abstract void execute(MSCPacketHandler handler, EntityPlayer player);
    
    public void sendMessageToPlayer(EntityPlayer player, String msg) {
    	player.addChatMessage(new ChatComponentText(msg));
    }
}