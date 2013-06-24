package com.mcf.davidee.msc.gui;

import net.minecraft.client.gui.GuiScreen;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Button.ButtonHandler;
import com.mcf.davidee.gui.Checkbox;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.TextField;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.basic.Tooltip;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.CheckboxVanilla;
import com.mcf.davidee.gui.vanilla.TextFieldVanilla;
import com.mcf.davidee.gui.vanilla.TextFieldVanilla.NumberFilter;
import com.mcf.davidee.gui.vanilla.sliders.IntSlider;
import com.mcf.davidee.msc.Utils;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.settings.SettingsPacket;

import cpw.mods.fml.common.network.PacketDispatcher;

public class SettingsScreen extends MSCScreen {
	
	private Container container;
	private Button close, save;
	private Label title, cap, freqLabel;
	private IntSlider mon, cre, amb, wat;
	private Checkbox readOnly;
	private TextField creatureFreq;
	
	private final SettingsPacket packet;
	
	public SettingsScreen(SettingsPacket packet, GuiScreen parent) {
		super(parent);
		
		this.packet = packet;
	}

	@Override
	protected void revalidateGui() {
		save.setPosition(width/2-55,height/4+132);
		close.setPosition(width/2+5, height/4+132);
		title.setPosition(width/2, height/4-40);
		readOnly.setPosition(width/2+19, height/4+50);
		freqLabel.setPosition(width/2+50, height/4-10);
		creatureFreq.setPosition(width/2+35, height/4+5);
		
		cap.setPosition(width/2-55, height/4-10);
		mon.setPosition(width/2-105,height/4+10);
		cre.setPosition(width/2-105,height/4+35);
		amb.setPosition(width/2-105,height/4+60);
		wat.setPosition(width/2-105,height/4+85);
		
		container.revalidate(0,0,width,height);
	}

	@Override
	protected void createGui() {
		save = new ButtonVanilla(50,20,"Save",new ButtonHandler(){
			@Override
			public void buttonClicked(Button button) {
				button.setEnabled(false);
				save();
				close();
			}
		});
		close = new ButtonVanilla(50,20,"Cancel",new CloseHandler());
		title = new Label("MSC Settings");
		readOnly = new CheckboxVanilla("Read Only", packet.readOnly);
		freqLabel = new Label("Creature Freq", new Tooltip("Gap between creature spawns"));
		creatureFreq = new TextFieldVanilla(30, 20, new NumberFilter());
		creatureFreq.setMaxLength(3);
		creatureFreq.setText("" + packet.creatureFreq);
		
		cap = new Label("Spawn Caps");
		mon = new IntSlider(100,20,"Monster %d",packet.caps[0],1,200);
		cre = new IntSlider(100,20,"Creature %d",packet.caps[1],1,200);
		amb = new IntSlider(100,20,"Ambient %d",packet.caps[2],1,200);
		wat = new IntSlider(100,20,"Water %d",packet.caps[3],1,200);
		
		container = new Container(0,0,width,height);
		container.addWidgets(close, save, mon, cre, amb, wat, readOnly, creatureFreq, freqLabel, title, cap);
		containers.add(container);
		selected = container;
	}

	private void save() {
		int[] caps = new int[] { mon.getIntValue(), cre.getIntValue(), amb.getIntValue(), wat.getIntValue() };
		int cFreq = Utils.parseIntDMinMax(creatureFreq.getText(), packet.creatureFreq, 1, 400);
		PacketDispatcher.sendPacketToServer(MSCPacket.getPacket(PacketType.SETTINGS, readOnly.isChecked(), caps, cFreq));
	}
}
