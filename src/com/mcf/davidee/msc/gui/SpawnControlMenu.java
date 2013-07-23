package com.mcf.davidee.msc.gui;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.basic.Tooltip;
import com.mcf.davidee.gui.basic.BasicScreen.CloseHandler;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.gui.GuiScreen;

public class SpawnControlMenu extends MSCScreen{
	
	private Container container;
	private Button close, master, biomes, entities, eval;
	private Label title;
	
	private String mod;

	public SpawnControlMenu(String mod, GuiScreen parent) {
		super(parent);
		
		this.mod = mod;
	}

	@Override
	protected void reopenedGui(){
		setEnabled(true, master, biomes, entities, eval);
	}
	

	@Override
	public void buttonClicked(Button button){
		if (button == master)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.BIOME_SETTING, mod + ":Master"));
		if (button == biomes)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.BIOME_LIST, "0" + mod));
		if (button == entities)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.ENTITY_LIST, mod));
		if (button == eval)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.BIOME_LIST, "1" + mod));
		
		setEnabled(false, master, biomes, entities, eval);
	}
	
	@Override
	protected void revalidateGui(){
		title.setPosition(width/2, height/4-30);
		close.setPosition(width/2-75, height/4 + 107);
		
		master.setPosition(width/2-50, height/4-5);
		biomes.setPosition(width/2-50,  height/4+20);
		entities.setPosition(width/2-50,height/4+45);
		eval.setPosition(width/2-50, height/4+70);
		
		container.revalidate(0,0,width,height);
	}

	@Override
	protected void createGui() {
		close = new ButtonVanilla(150,20,"Back",new CloseHandler());
		master = new ButtonVanilla(100, 20, "Master Settings", this);
		biomes = new ButtonVanilla(100,20,"By Biome", this);
		entities = new ButtonVanilla(100,20,"By Entity",this);
		eval = new ButtonVanilla(100,20,"Evaluated",this);

		title = new Label(mod + " Spawn Controls");

		container = new Container(0,0,width,height);
		container.addWidgets(close, master, biomes, entities, eval, title);
		containers.add(container);
	}

}
