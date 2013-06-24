package com.mcf.davidee.msc.gui;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.basic.Tooltip;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.msc.packet.DebugPacket;

public class DebugScreen extends MSCScreen{
	
	private DebugPacket packet;
	
	private Container container;
	private Button close;
	private Label title;

	public DebugScreen(DebugPacket packet, GuiScreen parent) {
		super(parent);
		
		this.packet = packet;
	}

	@Override
	protected void revalidateGui() {
		title.setPosition(width/2, height/4-40);
		close.setPosition(width/2-75, height/4+132);
		
		container.revalidate(0,0,width,height);
	}

	@Override
	protected void createGui() {
		close = new ButtonVanilla(150,20,"Back",new CloseHandler());
		title = new Label("MSC Debug", new Tooltip("TODO"));
		
		container = new Container(0,0,width,height);
		container.addWidgets(close,title);
		containers.add(container);
		selected = container;
	}

}
