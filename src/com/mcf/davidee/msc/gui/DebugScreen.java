package com.mcf.davidee.msc.gui;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.guilib.basic.FocusedContainer;
import com.mcf.davidee.guilib.basic.Label;
import com.mcf.davidee.guilib.basic.Tooltip;
import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Container;
import com.mcf.davidee.guilib.core.Scrollbar;
import com.mcf.davidee.guilib.focusable.FocusableLabel;
import com.mcf.davidee.guilib.focusable.FocusableWidget;
import com.mcf.davidee.guilib.vanilla.ButtonVanilla;
import com.mcf.davidee.guilib.vanilla.ScrollbarVanilla;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.packet.DebugPacket;

public class DebugScreen extends MSCScreen{
	
	private DebugPacket packet;
	
	private Container container, logContainer;
	private Scrollbar scrollbar;
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
		
		scrollbar.setPosition(width - 20, height/4 - 20);
		List<FocusableWidget> groups = logContainer.getFocusableWidgets();
		for (int i = 0; i < groups.size(); ++i)
			groups.get(i).setPosition(width/2, height/4 - 18 + i*14);
		
		logContainer.revalidate(20, height/4 - 20, width - 20, 142);
		container.revalidate(0,0,width,height);
	}
	
	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(logContainer.left(), logContainer.top(), logContainer.right() - 20, logContainer.bottom(), 0x44444444);
	}

	@Override
	protected void createGui() {
		close = new ButtonVanilla(150,20,"Back",new CloseHandler());
		title = new Label("MSC Debug", new Tooltip(MobSpawnControls.VERSION));
		
		container = new Container();
		container.addWidgets(close,title);
		
		scrollbar = new ScrollbarVanilla(20);
		logContainer = new FocusedContainer(scrollbar, 14, 4);
		
		FocusableLabel[] labels = new FocusableLabel[packet.log.length];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = new FocusableLabel(packet.log[i]);
		
		logContainer.addWidgets(labels);
		
		containers.add(logContainer);
		containers.add(container);
		
		selectedContainer = logContainer;
	}

}
