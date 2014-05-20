package com.mcf.davidee.msc.gui.list;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.guilib.basic.FocusedContainer;
import com.mcf.davidee.guilib.basic.Label;
import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Container;
import com.mcf.davidee.guilib.core.Scrollbar;
import com.mcf.davidee.guilib.focusable.FocusableLabel;
import com.mcf.davidee.guilib.focusable.FocusableWidget;
import com.mcf.davidee.guilib.vanilla.ButtonVanilla;
import com.mcf.davidee.guilib.vanilla.ScrollbarVanilla;
import com.mcf.davidee.msc.MobSpawnControls;
import com.mcf.davidee.msc.gui.MSCScreen;
import com.mcf.davidee.msc.packet.BiomeListPacket;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

public class BiomeListScreen extends MSCScreen{

	private Label title, subTitle;
	private Button select, close, biomes, groups;
	private Scrollbar scrollbar;
	private Container masterContainer, labelContainer;

	private BiomeListPacket packet;

	public BiomeListScreen(BiomeListPacket packet, GuiScreen parent) {
		super(parent);

		this.packet = packet;
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(labelContainer.left(),labelContainer.top(),labelContainer.right()-10,labelContainer.bottom(),0x44444444);
	}

	@Override
	protected void reopenedGui(){
		select.setEnabled(true);
	}

	@Override
	protected void revalidateGui() {
		groups.setPosition(width/2+10, height/4-33);
		biomes.setPosition(width/2-60, height/4-33);
		
		close.setPosition(width/2+6,height/4+136);
		select.setPosition(width/2-57, height/4+136);
		
		title.setPosition(width/2,height/4-58);
		subTitle.setPosition(width/2,height/4-46);

		scrollbar.setPosition(width/2+70,height/4-10);

		List<FocusableWidget> groups = labelContainer.getFocusableWidgets();
		for (int i = 0; i < groups.size(); ++i)
			groups.get(i).setPosition(width/2,height/4-8+i*14);

		labelContainer.revalidate(width/2-70,height/4-10,150,143);
		masterContainer.revalidate(0, 0, width, height);
	}

	@Override
	protected void createGui() {
		masterContainer = new Container();
		title = new Label(packet.mod + " Controls");
		subTitle = new Label((packet.evalRequest ? "Evaluated Biomes" : "By Biome"));
		
		groups = new ButtonVanilla(50, 20, "Groups", this);
		groups.setEnabled(packet.groups.length > 0);
		biomes = new ButtonVanilla(50, 20, "Biomes", this);
		
		
		close = new ButtonVanilla(50, 20, "Back", new CloseHandler());
		select = new ButtonVanilla(50, 20, (packet.evalRequest ? "View" : "Edit"), this);

		masterContainer.addWidgets(title, subTitle, biomes, groups, select, close);

		scrollbar = new ScrollbarVanilla(10);
		labelContainer = new FocusedContainer(scrollbar, 14, 4);
		FocusableLabel[] labels = new FocusableLabel[packet.biomes.length];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = new FocusableLabel(packet.biomes[i]);
		labelContainer.addWidgets(labels);

		containers.add(labelContainer);
		containers.add(masterContainer);

		selectedContainer = labelContainer;
	}

	public void buttonClicked(Button button) {
		if (button == groups) {
			labelContainer.removeFocusableWidgets();
			FocusableLabel[] labels = new FocusableLabel[packet.groups.length];
			for (int i = 0; i < labels.length; ++i)
				labels[i] = new FocusableLabel(width/2,height/4-8+i*14,packet.groups[i]);
			labelContainer.addWidgets(labels);
			subTitle.setText((packet.evalRequest ? "Evaluated Groups" : "By Group"));
		}
		if (button == biomes) {
			labelContainer.removeFocusableWidgets();
			FocusableLabel[] labels = new FocusableLabel[packet.biomes.length];
			for (int i = 0; i < labels.length; ++i)
				labels[i] = new FocusableLabel(width/2,height/4-8+i*14,packet.biomes[i]);
			labelContainer.addWidgets(labels);
			subTitle.setText((packet.evalRequest ? "Evaluated Biomes" : "By Biome"));
		}
		if (button == select) {
			button.setEnabled(false);
			String selected = ((FocusableLabel)labelContainer.getFocusedWidget()).getText();
			
			if (packet.evalRequest) {
				if (selected.contains(".")) //Biome
					MobSpawnControls.DISPATCHER.sendToServer(MSCPacket.getRequestPacket(PacketType.EVALUATED_BIOME, packet.mod + ':' + selected));
				else  //Group
					MobSpawnControls.DISPATCHER.sendToServer(MSCPacket.getRequestPacket(PacketType.EVALUATED_GROUP, packet.mod + ':' + selected));
			}
			else
				MobSpawnControls.DISPATCHER.sendToServer(MSCPacket.getRequestPacket(PacketType.BIOME_SETTING, packet.mod + ':' + selected));
		}
	}

}