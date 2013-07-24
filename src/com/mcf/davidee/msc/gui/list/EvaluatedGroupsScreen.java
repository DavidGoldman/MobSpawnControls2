package com.mcf.davidee.msc.gui.list;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.basic.FocusedContainer;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.basic.BasicScreen.CloseHandler;
import com.mcf.davidee.gui.focusable.FocusableLabel;
import com.mcf.davidee.gui.focusable.FocusableWidget;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.ScrollbarVanilla;
import com.mcf.davidee.msc.gui.MSCScreen;
import com.mcf.davidee.msc.packet.settings.EvaluatedGroupPacket;

public class EvaluatedGroupsScreen extends MSCScreen {
	
	private Label title, subTitle;
	private Button close;
	private Scrollbar scrollbar;
	private Container masterContainer, labelContainer;
	
	
	private EvaluatedGroupPacket packet;

	public EvaluatedGroupsScreen(EvaluatedGroupPacket packet, GuiScreen parent) {
		super(parent);
		
		this.packet = packet;
	}
	
	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(labelContainer.left(), labelContainer.top(), labelContainer.right()-10, labelContainer.bottom(), 0x44444444);
	}

	@Override
	protected void revalidateGui() {
		title.setPosition(width/2,height/4-50);
		subTitle.setPosition(width/2,height/4-30);
		close.setPosition(width/2-50,height/4+136);
		
		scrollbar.setPosition(width/2+70,height/4-14);

		List<FocusableWidget> groups = labelContainer.getFocusableWidgets();
		for (int i = 0; i < groups.size(); ++i)
			groups.get(i).setPosition(width/2,height/4-12 + i*14);

		labelContainer.revalidate(width/2-70, height/4-14, 150, 143);
		masterContainer.revalidate(0, 0, width, height);
	}

	@Override
	protected void createGui() {
		masterContainer = new Container();
		title = new Label(packet.mod + " Controls");
		subTitle = new Label("Evaluated Group \"" + packet.group + "\"");
		close = new ButtonVanilla(100, 20, "Back", new CloseHandler());
		
		masterContainer.addWidgets(title, subTitle, close);
		
		scrollbar = new ScrollbarVanilla(10, 143);
		labelContainer = new FocusedContainer(scrollbar, 14, 4);
		FocusableLabel[] labels = new FocusableLabel[packet.biomes.length];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = new FocusableLabel(packet.biomes[i]);
		labelContainer.addWidgets(labels);
		
		containers.add(labelContainer);
		containers.add(masterContainer);

		selectedContainer = labelContainer;
	}

}
