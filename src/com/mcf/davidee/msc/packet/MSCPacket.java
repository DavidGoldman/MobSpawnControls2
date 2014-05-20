package com.mcf.davidee.msc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import com.google.common.primitives.UnsignedBytes;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.network.AbstractPacket;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket;
import com.mcf.davidee.msc.packet.settings.EvaluatedBiomePacket;
import com.mcf.davidee.msc.packet.settings.EvaluatedGroupPacket;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;

import cpw.mods.fml.common.network.ByteBufUtils;

/*
 * See  cpw.mods.fml.common.network.FMLPacket
 * This class has a similar implementation, but
 * does not support multipart packets
 */
public abstract class MSCPacket extends AbstractPacket {
	
	public static final String PACKET_ID = "msc2";

	public enum PacketType{

		HANDSHAKE(HSPacket.class),
		ACCESS_DENIED(ADPacket.class),
		REQUEST(RequestPacket.class),
		MOD_LIST(ModListPacket.class),
		GROUPS(GroupsPacket.class),
		CREATURE_TYPE(CreatureTypePacket.class),
		BIOME_LIST(BiomeListPacket.class),
		BIOME_SETTING(BiomeSettingPacket.class),
		ENTITY_LIST(EntityListPacket.class),
		ENTITY_SETTING(EntitySettingPacket.class),
		SETTINGS(SettingsPacket.class),
		EVALUATED_BIOME(EvaluatedBiomePacket.class),
		EVALUATED_GROUP(EvaluatedGroupPacket.class),
		DEBUG(DebugPacket.class);

		private Class<? extends MSCPacket> packetType;

		private PacketType(Class<? extends MSCPacket> cls) {
			this.packetType = cls;
		}

		private MSCPacket make() {
			try {
				return packetType.newInstance();
			} catch (Exception e) {
				MobSpawnControls.getLogger().severe("Error during packet creation");
				throw new RuntimeException(e);
			}
		}
		
		public static int ordinalForClass(Class<? extends MSCPacket> cls) throws IllegalArgumentException {
			PacketType[] values = PacketType.values();
			for (int i = 0; i < values.length; ++i)
				if (values[i].packetType == cls)
					return i;
			throw new IllegalArgumentException("Unknown class " + cls);
		}
	}

	public abstract MSCPacket readData(Object... data);

	//Packet creation
	public static MSCPacket getRequestPacket(PacketType type) {
		return getPacket(PacketType.REQUEST, UnsignedBytes.checkedCast(type.ordinal()));
	}

	public static MSCPacket getRequestPacket(PacketType type, String data) {
		return getPacket(PacketType.REQUEST, UnsignedBytes.checkedCast(type.ordinal()), data);
	}

	public static MSCPacket getPacket(PacketType type, Object... data) {
		return type.make().readData(data);
	}
	
	//Helper methods for the PacketPipeline
	public static MSCPacket readPacket(ChannelHandlerContext ctx, ByteBuf payload) throws IOException {
		int type = UnsignedBytes.toInt(payload.readByte());
		PacketType pType = PacketType.values()[type];
		MSCPacket packet = pType.make();
		packet.decodeFrom(ctx, payload.slice());
		return packet;
	}
	
	public static void writePacket(MSCPacket pkt, ChannelHandlerContext ctx, ByteBuf to) throws IOException {
		to.writeByte(UnsignedBytes.checkedCast(PacketType.ordinalForClass(pkt.getClass())));
		pkt.encodeInto(ctx, to);
	}
	
	
	//String utility methods
	public static void writeString(String str, ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, str);
	}
	
	public static String readString(ByteBuf buffer) {
		return ByteBufUtils.readUTF8String(buffer);
	}

	public static void writeStringArray(String[] arr, ByteBuf to) {
		to.writeInt(arr.length);
		for (String s : arr)
			writeString(s, to);
	}

	public static String[] readStringArray(ByteBuf from) {
		String[] arr = new String[from.readInt()];
		for (int i = 0; i < arr.length; ++i)
			arr[i] = readString(from);
		return arr;
	}
}
