package com.mcf.davidee.msc.gui.list;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.focusable.FocusableLabel;
import com.mcf.davidee.gui.focusable.FocusableWidget;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.ScrollbarVanilla;
import com.mcf.davidee.msc.gui.MSCScreen;
import com.mcf.davidee.msc.packet.EntityListPacket;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.spawning.MobHelper;

import cpw.mods.fml.common.network.PacketDispatcher;

public class EntityListScreen extends MSCScreen{

	private Label title, subTitle;
	private Button select, close, monster, creature, ambient, water;
	private Scrollbar scrollbar;
	private Container masterContainer, labelContainer;
	
	private EntityListPacket packet;
	private String curType;
	private boolean sent;
	
	public EntityListScreen(EntityListPacket packet, GuiScreen parent) {
		super(parent);
		
		this.packet = packet;
	}
	
	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(labelContainer.left(),labelContainer.top(),labelContainer.right()-10,labelContainer.bottom(),0x44444444);
	}
	
	@Override
	public void updateScreen() {
		select.setEnabled(!sent && labelContainer.hasFocusedWidget());
		super.updateScreen();
	}
	
	@Override
	protected void reopenedGui(){
		sent = false;
	}
	
	@Override
	protected void revalidateGui() {
		close.setPosition(width/2+6,height/4+135);
		select.setPosition(width/2-57, height/4+135);
		title.setPosition(width/2,height/4-45);
		subTitle.setPosition(width/2,height/4-30);
		
		monster.setPosition(width/2-130, height/4-10);
		creature.setPosition(width/2-65, height/4-10);
		ambient.setPosition(width/2, height/4-10);
		water.setPosition(width/2+65, height/4-10);
		
		scrollbar.setPosition(width/2+70,height/4+15);
		
		List<FocusableWidget> groups = labelContainer.getFocusableWidgets();
		for (int i = 0; i < groups.size(); ++i)
			groups.get(i).setPosition(width/2,height/4+17+i*14);
		
		labelContainer.revalidate(width/2-70,height/4+15,150,115);
		masterContainer.revalidate(0, 0, width, height);
	}

	@Override
	protected void createGui() {
		masterContainer = new Container();
		title = new Label(packet.mod + " Controls");
		subTitle = new Label("By Entity");
		close = new ButtonVanilla(50, 20, "Back", new CloseHandler());
		select = new ButtonVanilla(50, 20, "Edit", this);
		select.setEnabled(false);
		
		monster = new ButtonVanilla(60, 20, "Monster", this);
		creature = new ButtonVanilla(60, 20, "Creature", this);
		ambient = new ButtonVanilla(60, 20, "Ambient", this);
		water = new ButtonVanilla(60, 20, "Water", this);
		
		masterContainer.addWidgets(monster, creature, ambient, water, title, subTitle, select, close);
		
		scrollbar = new ScrollbarVanilla(10,115);
		labelContainer = new Container(scrollbar,14,4);
		
		containers.add(labelContainer);
		containers.add(masterContainer);
	}
	
	@Override
	public void buttonClicked(Button button) {
		if (button == select) {
			sent = true;
			String selectedObj = ((FocusableLabel)labelContainer.getFocusedWidget()).getText();
			PacketDispatcher.sendPacketToServer(MSCPacket.getRequestPacket(PacketType.ENTITY_SETTING, 
					packet.mod + ':' + selectedObj));
		}
		else { //CreatureType button
			String type = button.getText().replace("Water", "WaterCreature");
			if (!type.equalsIgnoreCase(curType)) {
				int index = MobHelper.typeOf(type).ordinal();
				String[] entities = packet.entities[index];
				FocusableLabel[] labels = new FocusableLabel[entities.length];
				labelContainer.removeFocusableWidgets();
				for (int i = 0; i < entities.length; ++i)
					labels[i] = new FocusableLabel(width/2,height/4+17+i*14, entities[i]);

				labelContainer.addWidgets(labels);
				curType = type;
			}
		}
	}

}
