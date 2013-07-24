package com.mcf.davidee.msc.gui;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.basic.FocusedContainer;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.basic.Tooltip;
import com.mcf.davidee.gui.focusable.FocusableLabel;
import com.mcf.davidee.gui.focusable.FocusableWidget;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.ScrollbarVanilla;
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
		title = new Label("MSC Debug", new Tooltip(MobSpawnControls.version));
		
		container = new Container();
		container.addWidgets(close,title);
		
		scrollbar = new ScrollbarVanilla(20, 142);
		logContainer = new FocusedContainer(scrollbar, 14, 4);
		
		FocusableLabel[] labels = new FocusableLabel[packet.log.length];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = new FocusableLabel(packet.log[i]);
		
		logContainer.addWidgets(labels);
		
		containers.add(logContainer);
		containers.add(container);
		
		selected = logContainer;
	}

}
