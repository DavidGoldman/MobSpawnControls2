package com.mcf.davidee.msc.gui.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.mcf.davidee.msc.gui.MSCScreen;
import com.mcf.davidee.msc.gui.popup.GroupPopup;
import com.mcf.davidee.msc.gui.popup.PopupYesNo;
import com.mcf.davidee.msc.gui.popup.PopupYesNo.YesNoHandler;
import com.mcf.davidee.msc.packet.GroupsPacket;
import com.mcf.davidee.msc.packet.MSCPacket;
import com.mcf.davidee.msc.packet.MSCPacket.PacketType;

import cpw.mods.fml.common.network.PacketDispatcher;

public class GroupsMenu extends MSCScreen{


	private Container masterContainer, labelContainer;
	private Scrollbar scrollbar;
	private Button save, close;
	private Button add, remove, rename, edit;
	private Label title;

	private GroupsPacket packet;

	private Map<String,String> groupMap;
	private List<String> groups, log;

	public GroupsMenu(GroupsPacket packet, GuiScreen parent) {
		super(parent);

		this.packet = packet;
		groupMap = new HashMap<String,String>();
		groups = new ArrayList<String>();
		log = new ArrayList<String>();
	}

	@Override
	public void updateScreen() {
		setEnabled(labelContainer.hasFocusedWidget(), remove, rename, edit);
		super.updateScreen();
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(labelContainer.left(),labelContainer.top(),labelContainer.right()-10,labelContainer.bottom(),0x44444444);
	}

	@Override
	protected void revalidateGui() {
		scrollbar.setPosition(width/2+70,height/4-7);
		title.setPosition(width/2,height/4-30);
		save.setPosition(width/2-55,height/4+135);
		close.setPosition(width/2+5,height/4+135);
		add.setPosition(width/2-150,height/4);
		remove.setPosition(width/2-150,height/4+25);
		rename.setPosition(width/2+100,height/4);
		edit.setPosition(width/2+100,height/4+25);

		List<FocusableWidget> groups = labelContainer.getFocusableWidgets();
		for (int i = 0; i < groups.size(); ++i)
			groups.get(i).setPosition(width/2,height/4-5+i*14);


		labelContainer.revalidate(width/2-70,height/4-7,150,128);
		masterContainer.revalidate(0, 0, width, height);
	}

	@Override
	protected void createGui() {
		title = new Label(packet.mod + " Biome Groups");
		save = new ButtonVanilla(50,20,"Save",  new ButtonHandler(){
			public void buttonClicked(Button button) {
				String[] arr = new String[groups.size()];
				for (int i = 0; i < arr.length; ++i){
					String gName = groups.get(i);
					arr[i] = gName + '=' + groupMap.get(gName);
				}
				PacketDispatcher.sendPacketToServer(MSCPacket.getPacket(PacketType.GROUPS, 
						packet.mod, arr, log.toArray(new String[0])));
				close();
			}
		});
		close = new ButtonVanilla(50,20,"Cancel",new CloseHandler());
		add = new ButtonVanilla(50,20,"Add", this);
		remove = new ButtonVanilla(50,20,"Remove", this);
		rename = new ButtonVanilla(50,20,"Rename", this);
		edit = new ButtonVanilla(50,20,"Edit", this);
		save.setEnabled(false);
		setEnabled(packet.groups.length > 0, remove, rename, edit);

		scrollbar = new ScrollbarVanilla(10,128);
		labelContainer = new FocusedContainer(scrollbar,14,4);

		FocusableLabel[] labels = new FocusableLabel[packet.groups.length];
		for (int i = 0; i < labels.length; ++i){
			String group = packet.groups[i];
			int index = group.indexOf('=');
			String groupName = group.substring(0,index), groupDef = group.substring(index+1);
			groups.add(groupName);
			groupMap.put(groupName, groupDef);
			labels[i] = new FocusableLabel(width/2,height/4-5+i*14,groupName);
		}
		labelContainer.addWidgets(labels);

		masterContainer = new Container();
		masterContainer.addWidgets(title, add, remove, rename, edit, save, close);
		containers.add(labelContainer);
		containers.add(masterContainer);
		selectedContainer = labelContainer;
	}

	@Override
	public void buttonClicked(Button button) {
		if (button == add)
			mc.displayGuiScreen(new GroupPopup(this,groupMap.keySet()));
		if (button == remove){
			String group = ((FocusableLabel)labelContainer.getFocusedWidget()).getText();
			mc.displayGuiScreen(new PopupYesNo(this,"Yes","Cancel",new YesNoHandler(){
				public void handleExit(boolean yes) {
					if (yes){
						String s= ((FocusableLabel)labelContainer.deleteFocused()).getText();
						//LOG IT
						log.add("del," + s);
						removeReferences(s);
						setEnabled(labelContainer.hasFocusedWidget(), remove, rename, edit);
						save.setEnabled(true);
					}
				}
			},"",group,"","Are you sure you want to remove this group?","","Deleting a group is permanent!"));
		}
		if (button == rename)
			mc.displayGuiScreen(new GroupPopup(this,((FocusableLabel)labelContainer.getFocusedWidget()).getText(),
					groupMap.keySet()));
		if (button == edit){
			String group = ((FocusableLabel)labelContainer.getFocusedWidget()).getText();
			mc.displayGuiScreen(new EditGroupScreen(this, group, groupMap.get(group), getUnused(group)));
		}

	}

	private List<String> getUnused(String group) {
		List<String> list = new ArrayList<String>();
		//Add groups that come before this one
		for (int i = 0; i < groups.indexOf(group); ++i)
			list.add(groups.get(i));
		//Add all biomes
		for (String s : packet.biomeNames)
			list.add(s);
		//Now remove all groups/biomes in the definition
		String defString = groupMap.get(group);
		if (!defString.isEmpty()) {
			String[] def = defString.split(",");
			for (String s : def) 
				if (s.charAt(0) != '*')
					list.remove(s.substring(1));
		}

		return list;
	}

	private void removeReferences(String group) {
		groups.remove(group);
		groupMap.remove(group);

		for (Entry<String,String> entry : groupMap.entrySet()) { 
			String[] ops = entry.getValue().split(",");
			boolean hasPrev = false;
			StringBuilder sb = new StringBuilder();
			for (String s : ops) {
				if (!s.isEmpty()) { 
					char c = s.charAt(0);
					if (c != '*') {
						String gb = s.substring(1);
						if (!gb.equalsIgnoreCase(group)) {
							if(hasPrev)
								sb.append(',');
							sb.append(c + gb);
							hasPrev = true;
						}
					}
					else {
						hasPrev = true;
						sb.append('*');
					}
				}
			}
			entry.setValue(sb.toString());
		}
	}

	public void addGroup(String group) {
		//LOG IT
		log.add("add," + group);
		groups.add(group);
		groupMap.put(group, "");
		int i = labelContainer.getFocusableWidgets().size();
		labelContainer.addWidgets(new FocusableLabel(width/2,height/4-5+i*14,group));
		save.setEnabled(true);
	}
	
	public void updateDefinition(String group, String definition) {
		groupMap.put(group, definition);
		save.setEnabled(true);
	}

	public void renameGroup(String group, String newName) {
		//LOG IT
		log.add("ren," + group + "," + newName);
		//Remove map entry
		String def = groupMap.remove(group);

		//Update References
		for (Entry<String,String> entry : groupMap.entrySet()) { 
			String[] ops = entry.getValue().split(",");
			boolean hasPrev = false;
			StringBuilder sb = new StringBuilder();
			for (String s : ops) {
				if (!s.isEmpty()) {  // For each string
					char c = s.charAt(0);
					if (c != '*') {
						String gb = s.substring(1);
						if (gb.equalsIgnoreCase(group))
							gb = newName;
						if(hasPrev)
							sb.append(',');
						sb.append(c + gb);
						hasPrev = true;
					}
					else {
						hasPrev = true;
						sb.append('*');
					}
				}
			}
			entry.setValue(sb.toString());
		}
		//Update GUI Label
		for (FocusableWidget w : labelContainer.getFocusableWidgets()){
			FocusableLabel l = ((FocusableLabel)w);
			if (l.getText().equalsIgnoreCase(group)){
				l.setText(newName);
				break;
			}
		}
		//Add new group
		groups.set(groups.indexOf(group), newName);
		groupMap.put(newName, def);

		save.setEnabled(true);
	}

}
