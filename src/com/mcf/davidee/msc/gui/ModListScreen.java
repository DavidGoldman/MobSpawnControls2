package com.mcf.davidee.msc.gui;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.basic.FocusedContainer;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.focusable.FocusableLabel;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.ScrollbarVanilla;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.ModListPacket;

import cpw.mods.fml.common.network.PacketDispatcher;

public class ModListScreen extends MSCScreen{

	private ModListPacket packet;

	private Label title;
	private FocusableLabel[] labels;
	private Button close, controls, types, groups;
	private Scrollbar scrollbar;
	private Container labelContainer, masterContainer;
	
	
	public ModListScreen(ModListPacket packet, GuiScreen parent) {
		super(parent);

		this.packet = packet;
	}

	@Override
	protected void revalidateGui() {
		scrollbar.setPosition(width/2+50,height/4-7);
		controls.setPosition(width/2-50,height/4+106);
		groups.setPosition(width/2-155,height/4+106);
		types.setPosition(width/2+55,height/4+106);
		close.setPosition(width/2-50,height/4+132);
		title.setPosition(width/2,height/4-30);
		
		for (int i = 0; i < labels.length; ++i)
			labels[i].setPosition(width/2,height/4-5+i*14);
		
		labelContainer.revalidate(width/2-50,height/4-7,110,100);
		masterContainer.revalidate(0, 0, width, height);
	}
	
	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(labelContainer.left(),labelContainer.top(),labelContainer.right()-10,labelContainer.bottom(),0x44444444);
	}


	@Override
	protected void createGui() {
		title = new Label("Mod Select");
		controls = new ButtonVanilla(100,20,"Spawn Controls", this);
		groups = new ButtonVanilla(100,20,"Biome Groups", this);
		types = new ButtonVanilla(100,20,"Mob Types", this);
		
		close = new ButtonVanilla(100,20,"Back",new CloseHandler());
		
		labels = new FocusableLabel[packet.mods.length];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = new FocusableLabel(packet.mods[i]);

		scrollbar = new ScrollbarVanilla(10,100);
		labelContainer = new FocusedContainer(width/2-50,height/4-7,110,100, scrollbar,14,4);
		masterContainer = new Container(0,0,width,height);
		masterContainer.addWidgets(title, controls, groups, types, close);
		labelContainer.addWidgets(labels);
		containers.add(labelContainer);
		containers.add(masterContainer);
		selected = labelContainer;
	}
	
	@Override
	protected void reopenedGui(){
		setEnabled(true, controls, groups, types);
	}
	
	@Override
	public void buttonClicked(Button button) {
		setEnabled(false, controls, groups, types);
		String mod = ((FocusableLabel)labelContainer.getFocusedWidget()).getText();
		if (button == types)
			mc.displayGuiScreen(new MobTypesMenu(mod, this));
		if (button == controls)
			mc.displayGuiScreen(new SpawnControlMenu(mod, this));
		if (button == groups)
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.GROUPS, mod));
	}
	
}
