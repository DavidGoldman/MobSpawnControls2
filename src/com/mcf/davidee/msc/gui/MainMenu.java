package com.mcf.davidee.msc.gui;

import com.mcf.davidee.guilib.basic.Label;
import com.mcf.davidee.guilib.basic.Tooltip;
import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Container;
import com.mcf.davidee.guilib.vanilla.ButtonVanilla;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

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

		title = new Label("Mob Spawn Controls",new Tooltip(MobSpawnControls.VERSION));

		container = new Container();
		container.addWidgets(close, mods, settings, debug, title);
		containers.add(container);
		
		selectedContainer = container;
	}

	@Override
	public void buttonClicked(Button button){
		if (button == mods)
			MobSpawnControls.DISPATCHER.sendToServer(MSCPacket.getRequestPacket(PacketType.MOD_LIST));
		if (button == settings)
			MobSpawnControls.DISPATCHER.sendToServer(MSCPacket.getRequestPacket(PacketType.SETTINGS));
		if (button == debug)
			MobSpawnControls.DISPATCHER.sendToServer(MSCPacket.getRequestPacket(PacketType.DEBUG));
		setEnabled(false,mods,settings,debug);
	}
	
	@Override
	protected void reopenedGui(){
		setEnabled(true, mods, settings, debug);
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
