package com.mcf.davidee.msc.gui.edit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Button.ButtonHandler;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.basic.FocusedContainer;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.focusable.FocusableLabel;
import com.mcf.davidee.gui.focusable.FocusableWidget;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.ScrollbarVanilla;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.gui.MSCScreen;
import com.mcf.davidee.msc.packet.CreatureTypePacket;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

import cpw.mods.fml.common.network.PacketDispatcher;

public class EditCreatureTypeScreen extends MSCScreen{

	private CreatureTypePacket packet;
	
	private Label title, arrow;
	private Button close, save, move;
	private Scrollbar scrollbar;
	
	private Container labelContainer, masterContainer, creatureContainer;
	
	private List<String> modifications;
	
	private boolean sent;
	
	public EditCreatureTypeScreen(CreatureTypePacket packet, GuiScreen parent) {
		super(parent);
		
		this.packet = packet;
		modifications = new ArrayList<String>();
	}
	
	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(labelContainer.left(),labelContainer.top(),labelContainer.right()-15,labelContainer.bottom(),0x44444444);
		drawRect(creatureContainer.left(),creatureContainer.top(),creatureContainer.right(),creatureContainer.bottom(),0x44444444);
	}
	
	@Override
	public void updateScreen() {
		move.setEnabled(!sent && labelContainer.hasFocusedWidget());
		super.updateScreen();
	}
	
	@Override
	protected void revalidateGui() {
		scrollbar.setPosition(width/2+30,height/4-25);
		arrow.setPosition(width/2+58,height/4+47);
		title.setPosition(width/2-30,height/4-45);
		close.setPosition(width/2+5,height/4+130);
		save.setPosition(width/2-55,height/4+130);
		move.setPosition(width/2+120-25, height/4);
		
		List<FocusableWidget> entities = labelContainer.getFocusableWidgets();
		for (int i = 0; i < entities.size(); ++i) 
			entities.get(i).setPosition(width/2-30,height/4-23+i*14);
		
		List<FocusableWidget> types = creatureContainer.getFocusableWidgets();
		for (int i =0; i < types.size(); ++i)
			types.get(i).setPosition(width/2+120,height/4+27+i*14);
		
		creatureContainer.revalidate(width/2+80,height/4+25,80,58);
		labelContainer.revalidate(width/2-90,height/4-25,135,142);
		masterContainer.revalidate(0, 0, width, height);
	}

	@Override
	protected void createGui() {
		arrow = new Label("-->");
		title = new Label(packet.mod + " " + packet.creatureType +"s");
		save = new ButtonVanilla(50,20,"Save",new ButtonHandler(){
			public void buttonClicked(Button button) {
				setEnabled(false,save,move);
				sent = true;
				PacketDispatcher.sendPacketToServer(MSCPacket.getPacket(PacketType.CREATURE_TYPE, 
						packet.mod, packet.creatureType, modifications.toArray(new String[0])));
				close();
			}
		});
		save.setEnabled(false);
		move = new ButtonVanilla(50, 20, "Move To", new ButtonHandler(){
			public void buttonClicked(Button button) {
				String s = ((FocusableLabel)labelContainer.deleteFocused()).getText() + ":" +
						((FocusableLabel)creatureContainer.getFocusedWidget()).getText();
				modifications.add(s);
				button.setEnabled(labelContainer.hasFocusedWidget());
				save.setEnabled(true);
			}
		});
		move.setEnabled(packet.mobs.length > 0);
		close = new ButtonVanilla(50,20,"Cancel",new CloseHandler());
		
		masterContainer = new Container(0,0,width,height);
		masterContainer.addWidgets(title, arrow, move, save, close);
		
		FocusableLabel[] labels = new FocusableLabel[packet.mobs.length];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = new FocusableLabel(packet.mobs[i]);
		
		scrollbar = new ScrollbarVanilla(15,142);
		labelContainer = new FocusedContainer(width/2-90,height/4-25,135,142, scrollbar, 14, 4);
		labelContainer.addWidgets(labels);
		
		
		List<String> creatures = Utils.asList("Monster","Creature","Ambient","WaterCreature","None/Unknown");
		creatures.remove(packet.creatureType);
		String[] creatureNames = creatures.toArray(new String[0]);
		FocusableLabel[] creatureLabels = new FocusableLabel[creatureNames.length];
		for (int i = 0; i < creatureLabels.length; ++i)
			creatureLabels[i] = new FocusableLabel(creatureNames[i]);
		
		creatureContainer = new FocusedContainer(width/2+80,height/4+25,80,58);
		creatureContainer.addWidgets(creatureLabels);
		
		containers.add(labelContainer);
		containers.add(creatureContainer);
		containers.add(masterContainer);
		selected = labelContainer;
	}

}
