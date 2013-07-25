package com.mcf.davidee.msc.gui;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.guilib.basic.Label;
import com.mcf.davidee.guilib.basic.Tooltip;
import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Container;
import com.mcf.davidee.guilib.vanilla.ButtonVanilla;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

import cpw.mods.fml.common.network.PacketDispatcher;

public class MobTypesMenu extends MSCScreen{
	
	private String mod;
	
	private Container container;
	private Button close, monster, creature, ambient, water, none;
	private Label title;
	

	public MobTypesMenu(String mod, GuiScreen parent) {
		super(parent);

		this.mod = mod;
	}

	@Override
	protected void revalidateGui() {
		title.setPosition(width/2,height/4-30);
		close.setPosition(width/2-75,height/4+107);
		
		monster.setPosition(width/2-102,height/4+8);
		creature.setPosition(width/2+2,height/4+8);
		ambient.setPosition(width/2-102,height/4+33);
		water.setPosition(width/2+2,height/4+33);
		none.setPosition(width/2-50,height/4+58);
		
		container.revalidate(0,0,width,height);
	}
	
	@Override
	public void buttonClicked(Button b) {
		PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.CREATURE_TYPE, 
				mod + ":" + ((ButtonVanilla)b).getText()));
		setEnabled(false, monster, creature, ambient, water, none);
	}
	
	protected void reopenedGui(){
		setEnabled(true, monster, creature, ambient, water, none);
	}

	@Override
	protected void createGui() {
		close = new ButtonVanilla(150,20,"Back",new CloseHandler());
		
		monster = new ButtonVanilla(100, 20, "Monster", this);
		creature = new ButtonVanilla(100, 20, "Creature", this);
		ambient = new ButtonVanilla(100, 20, "Ambient", this);
		water = new ButtonVanilla(100, 20, "WaterCreature", this);
		none = new ButtonVanilla(100, 20, "None/Unknown", this);

		title = new Label(mod + " Mob Types", new Tooltip("Select a Mob Type"));

		container = new Container();
		container.addWidgets(close, monster, creature, ambient, water, none, title);
		containers.add(container);
	}

}
