package com.mcf.davidee.msc.packet;

import java.util.Arrays;

import net.minecraft.network.packet.Packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.network.MSCPacketHandler;
import com.mcf.davidee.msc.packet.settings.EntitySettingPacket;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/*
 * See  cpw.mods.fml.common.network.FMLPacket
 * This class has a similar implementation, but
 * does not support multipart packets
 */
public abstract class MSCPacket {
	
	public static final String PACKET_ID = "msc2";

	public enum PacketType{

		HANDSHAKE(HSPacket.class),
		ACCESS_DENIED(ADPacket.class),
		REQUEST(RequestPacket.class),
		MOD_LIST(ModListPacket.class),
		GROUPS(GroupsPacket.class),
		CREATURE_TYPE(CreatureTypePacket.class),
		BIOME_LIST(BiomeListPacket.class),
		ENTITY_LIST(EntityListPacket.class),
		ENTITY_SETTING(EntitySettingPacket.class),
		SETTINGS(SettingsPacket.class),
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
	}

	public abstract MSCPacket readData(Object... data);
	public abstract byte[] generatePacket();
	public abstract MSCPacket readPacket(ByteArrayDataInput pkt);
	public abstract void execute(MSCPacketHandler handler, Player player);


	public static byte[] makePacket(PacketType type, Object... data) {
		byte[] packetData = type.make().readData(data).generatePacket();
		return Bytes.concat(new byte[] { UnsignedBytes.checkedCast(type.ordinal()) }, packetData);
	}

	public static Packet getRequestPacket(PacketType type) {
		return getPacket(PacketType.REQUEST, UnsignedBytes.checkedCast(type.ordinal()));
	}

	public static Packet getRequestPacket(PacketType type, String data) {
		return getPacket(PacketType.REQUEST, UnsignedBytes.checkedCast(type.ordinal()), data);
	}

	public static Packet getPacket(PacketType type, Object... data) {
		return PacketDispatcher.getPacket(PACKET_ID, makePacket(type, data));
	}

	public static MSCPacket readPacket(byte[] payload) {
		int type = UnsignedBytes.toInt(payload[0]);
		PacketType pType = PacketType.values()[type];
		return pType.make().readPacket(ByteStreams.newDataInput(Arrays.copyOfRange(payload, 1, payload.length)));
	}

	public static void writeStringArray(String[] arr, ByteArrayDataOutput output) {
		output.writeInt(arr.length);
		for (String s : arr)
			output.writeUTF(s);
	}

	public static String[] readStringArray(ByteArrayDataInput input) {
		String[] arr = new String[input.readInt()];
		for (int i = 0; i < arr.length; ++i)
			arr[i] = input.readUTF();
		return arr;
	}
}
