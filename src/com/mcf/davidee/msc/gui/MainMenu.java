package com.mcf.davidee.msc.gui;

import net.minecraft.util.ChatAllowedCharacters;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Button.ButtonHandler;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.TextField.CharacterFilter;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.basic.Tooltip;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

import cpw.mods.fml.common.network.PacketDispatcher;

public class MainMenu extends MSCScreen{

	private Container container;

	private Button close, mods, settings, debug;
	private Label title;

	public MainMenu() {
		super(null);
	}

	public void createGui() {
		close = new ButtonVanilla(150,20,"Close",new CloseHandler());
		mods = new ButtonVanilla(100,20,"Configs", this);
		settings = new ButtonVanilla(100,20,"Settings",this);
		debug = new ButtonVanilla(100,20,"Debug",this);

		title = new Label("Mob Spawn Controls",new Tooltip(MobSpawnControls.version));

		container = new Container();
		container.addWidgets(close, mods, settings, debug, title);
		containers.add(container);
		
		selectedContainer = container;
	}

	@Override
	public void buttonClicked(Button button){
		if (button == mods)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.MOD_LIST));
		if (button == settings)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.SETTINGS));
		if (button == debug)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.DEBUG));
		setEnabled(false,mods,settings,debug);
	}
	
	@Override
	protected void reopenedGui(){
		setEnabled(true,mods,settings,debug);
	}
	
	@Override
	protected void revalidateGui(){
		title.setPosition(width/2, height/4-30);
		close.setPosition(width/2-75, height/4 + 107);
		
		mods.setPosition(width/2-50, height/4+8);
		settings.setPosition(width/2-50, height/4+33);
		debug.setPosition(width/2-50, height/4+58);
		
		container.revalidate(0,0,width,height);
	}

}
