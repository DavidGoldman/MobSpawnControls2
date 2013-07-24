package com.mcf.davidee.msc.gui.edit;

import static com.mcf.davidee.msc.gui.edit.EditEntityScreen.DIS_COLOR;
import static com.mcf.davidee.msc.gui.edit.EditEntityScreen.DIS_FOCUS;
import static com.mcf.davidee.msc.gui.edit.EditEntityScreen.DIS_HOVER;
import static com.mcf.davidee.msc.gui.edit.EditEntityScreen.ENABLED_COLOR;
import static com.mcf.davidee.msc.gui.edit.EditEntityScreen.ENABLED_FOCUS;
import static com.mcf.davidee.msc.gui.edit.EditEntityScreen.ENABLED_HOVER;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Checkbox;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.basic.FocusedContainer;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.focusable.FocusableLabel;
import com.mcf.davidee.gui.focusable.FocusableWidget;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.CheckboxVanilla;
import com.mcf.davidee.gui.vanilla.ScrollbarVanilla;
import com.mcf.davidee.gui.vanilla.sliders.IntSlider;
import com.mcf.davidee.msc.gui.MSCScreen;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket;
import com.mcf.davidee.msc.packet.settings.BiomeSettingPacket.EntityEntry;
import com.mcf.davidee.msc.spawning.MobHelper;

import cpw.mods.fml.common.network.PacketDispatcher;

public class EditBiomeScreen extends MSCScreen {

	private Label title, subTitle;
	private Button save, close, monster, creature, ambient, water;
	private Scrollbar scrollbar;
	private Checkbox enabled;
	private IntSlider weight, min, max;

	private Container masterContainer, labelContainer;
	private FocusableWidget lastFocused;

	private BiomeSettingPacket packet;

	private EntityEntry[][] entries;
	private int creatureType;

	public EditBiomeScreen(BiomeSettingPacket packet, GuiScreen parent) {
		super(parent);

		this.packet = packet;
		entries = packet.getOrderedEntries();
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(labelContainer.left(),labelContainer.top(),labelContainer.right()-10,labelContainer.bottom(),0x44444444);
	}

	@Override
	public void updateScreen() {
		if (labelContainer.hasFocusedWidget()) {
			if (lastFocused == labelContainer.getFocusedWidget())
				updateEntrySettings();
			else
				updateGuiSettings();
		}
		else {
			lastFocused = null;
		}
		super.updateScreen();
	}
	
	@Override
	protected void unhandledKeyTyped(char c, int code) { 
		if (code == Keyboard.KEY_RETURN) {
			if (labelContainer.hasFocusedWidget() && labelContainer.getFocusedWidget() == lastFocused) {
				enabled.setChecked(!enabled.isChecked());
				updateEntrySettings();
			}
		}
		super.unhandledKeyTyped(c, code);
	}

	private void updateGuiSettings() {
		lastFocused = labelContainer.getFocusedWidget();
		FocusableLabel label = (FocusableLabel)lastFocused;
		EntityEntry e = (EntityEntry)label.getUserData();
		enabled.setChecked(e.weight > 0);
		weight.setIntValue(enabled.isChecked() ? e.weight : 4);
		min.setIntValue(e.min);
		max.setIntValue(e.max);
	}

	private void updateEntrySettings() {
		FocusableLabel label = (FocusableLabel)labelContainer.getFocusedWidget();
		EntityEntry e = (EntityEntry) label.getUserData();
		if (enabled.isChecked()) {
			e.weight = weight.getIntValue();
			e.min = min.getIntValue();
			e.max = max.getIntValue();
			label.setColor(ENABLED_COLOR);
			label.setHoverColor(ENABLED_HOVER);
			label.setFocusColor(ENABLED_FOCUS);
		}
		else {
			e.weight = 0;
			e.min = min.getIntValue();
			e.max = max.getIntValue();
			label.setColor(DIS_COLOR);
			label.setHoverColor(DIS_HOVER);
			label.setFocusColor(DIS_FOCUS);
		}
	}

	@Override
	protected void revalidateGui() {
		close.setPosition(width/2+6,height/4+135);
		save.setPosition(width/2-57, height/4+135);
		title.setPosition(width/2,height/4-48);
		subTitle.setPosition(width/2,height/4-32);

		enabled.setPosition(width/2 + 70, height/4+15);

		weight.setPosition(width/2 + 50, height/4 + 30);
		min.setPosition(width/2 + 50, height/4 + 55);
		max.setPosition(width/2 + 50, height/4 + 80);
		
		monster.setPosition(width/2-190, height/4+10);
		creature.setPosition(width/2-190, height/4+35);
		ambient.setPosition(width/2-190, height/4+60);
		water.setPosition(width/2-190, height/4+85);

		List<FocusableWidget> groups = labelContainer.getFocusableWidgets();
		for (int i = 0; i < groups.size(); ++i)
			groups.get(i).setPosition(width/2-50,height/4-11+i*14);

		scrollbar.setPosition(width/2+20,height/4-13);
		labelContainer.revalidate(width/2-120,height/4-13,150,143);
		masterContainer.revalidate(0, 0, width, height);
	}

	@Override
	protected void createGui() {
		masterContainer = new Container();
		title = new Label(packet.mod + " Controls");
		subTitle = new Label(packet.biome);
		close = new ButtonVanilla(50, 20, "Cancel", new CloseHandler());
		save = new ButtonVanilla(50, 20, "Save", this);
		enabled = new CheckboxVanilla("Enabled", true);

		weight = new IntSlider(100, 20, "Weight %d", 1, 1, 100);
		min = new IntSlider(100, 20, "Min %d", 1, 1, 10);
		max = new IntSlider(100, 20, "Max %d", 1, 1, 15);
		
		monster = new ButtonVanilla(60, 20, "Monster", this);
		creature = new ButtonVanilla(60, 20, "Creature", this);
		ambient = new ButtonVanilla(60, 20, "Ambient", this);
		water = new ButtonVanilla(60, 20, "Water", this);

		masterContainer.addWidgets(title, subTitle, enabled, weight, min, max, monster, creature, ambient, water, save, close);

		scrollbar = new ScrollbarVanilla(10,143);
		labelContainer = new FocusedContainer(scrollbar, 14, 4);

		creatureType = 0;
		EntityEntry[] monsterEntries = entries[creatureType];
		FocusableLabel[] labels = new FocusableLabel[monsterEntries.length];
		for (int i = 0; i < labels.length; ++i) {
			EntityEntry e = monsterEntries[i];
			if (e.weight != 0) //enabled
				labels[i] = new FocusableLabel(e.entity, ENABLED_COLOR, ENABLED_HOVER, ENABLED_FOCUS);
			else //disabled
				labels[i] = new FocusableLabel(e.entity, DIS_COLOR, DIS_HOVER, DIS_FOCUS);
			labels[i].setUserData(e);
		}

		labelContainer.addWidgets(labels);

		containers.add(labelContainer);
		containers.add(masterContainer);
		
		if (labelContainer.hasFocusedWidget())
			updateGuiSettings();

		selectedContainer = labelContainer;
	}


	@Override
	public void buttonClicked(Button button) {
		if (button == save) {
			save();
			close();
		}
		else {
			String type = button.getText().replace("Water", "WaterCreature");
			int index = MobHelper.typeOf(type).ordinal();
			if (index != creatureType) {
				EntityEntry[] curEntries = entries[index];
				FocusableLabel[] labels = new FocusableLabel[curEntries.length];
				labelContainer.removeFocusableWidgets();
				
				for (int i = 0; i < labels.length; ++i) {
					EntityEntry e = curEntries[i];
					if (e.weight != 0) //enabled
						labels[i] = new FocusableLabel(e.entity, ENABLED_COLOR, ENABLED_HOVER, ENABLED_FOCUS);
					else //disabled
						labels[i] = new FocusableLabel(e.entity, DIS_COLOR, DIS_HOVER, DIS_FOCUS);
					labels[i].setUserData(e);
					labels[i].setPosition(width/2 - 50, height/4 - 11 + i*14);
				}

				labelContainer.addWidgets(labels);
				creatureType = index;
				
				if (labelContainer.hasFocusedWidget())
					updateGuiSettings();
			}
		}
	}

	private void save() {
		EntityEntry[][] newEntries = new EntityEntry[4][];
		String[][] empty = new String[4][0];
		
		for (int index = 0; index < 4; ++index) {
			List<EntityEntry> entryList = new ArrayList<EntityEntry>();
			
			for (EntityEntry entry : entries[index])
				if (entry.weight > 0)
					entryList.add(entry);
			
			newEntries[index] = entryList.toArray(new EntityEntry[0]);
		}
		
		PacketDispatcher.sendPacketToServer(MSCPacket.getPacket(PacketType.BIOME_SETTING, packet.mod, packet.biome, 
				newEntries, empty));
	}


}
