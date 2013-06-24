package com.mcf.davidee.msc.gui;

import java.util.List;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Button.ButtonHandler;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.Scrollbar;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.focusable.FocusableLabel;
import com.mcf.davidee.gui.focusable.FocusableSpecialLabel;
import com.mcf.davidee.gui.focusable.FocusableWidget;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.ScrollbarVanilla;
import com.mcf.davidee.msc.gui.popup.PopupMessage;

public class EditGroupScreen extends MSCScreen{

	private Container master, group, biomes;

	private Button save, close;
	private Button add, subtract, remove, inspect;
	private Label title, gLabel, bLabel, left, right;

	private String groupName;
	private String groupDef;
	private List<String> unused;

	private Scrollbar groupBar, biomeBar;

	public EditGroupScreen(GroupsMenu parent, String groupName, String groupDef, List<String> unused) {
		super(parent);

		this.groupName = groupName;
		this.groupDef = groupDef;
		this.unused = unused;
	}

	@Override
	public void updateScreen() {
		setEnabled(biomes.hasFocusedWidget(), add, subtract);
		remove.setEnabled(group.hasFocusedWidget());
		inspect.setEnabled(group.hasFocusedWidget() || biomes.hasFocusedWidget());
		super.updateScreen();
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		drawRect(group.left()+10,group.top(),group.right(),group.bottom(),0x44444444);
		drawRect(biomes.left(),biomes.top(),biomes.right()-10,biomes.bottom(),0x44444444);
	}

	@Override
	protected void revalidateGui() {
		master.revalidate(0, 0, width, height);
		title.setPosition(width/2,height/4-37);
		gLabel.setPosition(width/2-125, height/4-22);
		bLabel.setPosition(width/2+125, height/4-22);
		left.setPosition(width/2, height/4-10);
		right.setPosition(width/2, height/4+105);

		save.setPosition(width/2-55, height/4+155);
		close.setPosition(width/2+5, height/4+155);
		add.setPosition(width/2-25, height/4+5);
		subtract.setPosition(width/2-25, height/4+30);
		inspect.setPosition(width/2-25,  height/4+70);
		remove.setPosition(width/2-25, height/4+120);

		List<FocusableWidget> groupWidgets = group.getFocusableWidgets();
		for (int i = 0; i < groupWidgets.size(); ++i)
			groupWidgets.get(i).setPosition(width/2-125, height/4-8 + 14*i);

		groupBar.setPosition(width/2-210, height/4-10);
		group.revalidate(width/2-210,height/4-10,160,156);

		List<FocusableWidget> biomeWidgets = biomes.getFocusableWidgets();
		for (int i = 0; i < biomeWidgets.size(); ++i)
			biomeWidgets.get(i).setPosition(width/2+125, height/4-8 + 14*i);

		biomeBar.setPosition(width/2+200, height/4-10);
		biomes.revalidate(width/2+50,height/4-10,160,156);
	}

	@Override
	protected void createGui() {
		master = new Container(0, 0, width, height);
		title = new Label("Group \"" + groupName + "\"");
		gLabel = new Label("Group Definition");
		bLabel = new Label("Unused Biomes/Groups");
		left = new Label("<-----");
		right = new Label("----->");

		save = new ButtonVanilla(50, 20, "Submit", this);
		close = new ButtonVanilla(50, 20, "Cancel", new CloseHandler());
		add = new ButtonVanilla(50, 20, "Add", this);
		subtract =  new ButtonVanilla(50, 20, "Subtract", this);
		inspect = new ButtonVanilla(50, 20, "Inspect", this);
		remove = new ButtonVanilla(50, 20, "Remove", this);
		setEnabled(false, save, add, subtract, inspect, remove);
		master.addWidgets(title, gLabel, bLabel, left, right, add, subtract, inspect, remove, save, close);

		groupBar = new ScrollbarVanilla(10, 156);
		group = new Container(width/2-210,height/4-10,160,156, groupBar, 14, 4);

		biomeBar = new ScrollbarVanilla(10, 156);
		biomes = new Container(width/2+50,height/4-10,160,156, biomeBar, 14, 4);

		if (!groupDef.isEmpty()) {
			String[] arr = groupDef.split(",");
			FocusableLabel[] def = new FocusableLabel[arr.length];
			for (int i = 0; i < arr.length; ++i) 
				def[i] = new FocusableSpecialLabel(getSimpleName(arr[i]), arr[i]);
			group.addWidgets(def);
		}

		FocusableLabel[] others = new FocusableLabel[unused.size()];
		for (int i = 0; i < others.length; ++i) {
			String full = unused.get(i);
			others[i] = new FocusableSpecialLabel(getSimpleName(full), full);
		}
		biomes.addWidgets(others);

		containers.add(group);
		containers.add(biomes);
		containers.add(master);
	}

	private String getSimpleName(String str) {
		if (str.contains(".")) {
			String prefix = str.startsWith("+") ? "+" : str.startsWith("-") ? "-" : "";
			return prefix + str.substring(str.lastIndexOf('.')+1);
		}
		return str;
	}

	@Override
	public void buttonClicked(Button button) {
		if (button == save) {
			List<FocusableWidget> widgets = group.getFocusableWidgets();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < widgets.size(); ++i) {
				if (i > 0)
					sb.append(',');
				sb.append(((FocusableSpecialLabel)widgets.get(i)).getSpecialText());
			}
			((GroupsMenu)getParent()).updateDefinition(groupName, sb.toString());
			close();
		}
		if (button == add) 
			addGroupDefinition('+' + ((FocusableSpecialLabel)biomes.deleteFocused()).getSpecialText());
		if (button == subtract) 
			addGroupDefinition('-' + ((FocusableSpecialLabel)biomes.deleteFocused()).getSpecialText());
		if (button == remove)
			addUnused(((FocusableSpecialLabel)group.deleteFocused()).getSpecialText());
		if (button == inspect){
			FocusableSpecialLabel lb = (group.hasFocusedWidget()) ? (FocusableSpecialLabel)group.getFocusedWidget() :
				(FocusableSpecialLabel)biomes.getFocusedWidget();
			if (lb.getSpecialText().contains("."))
				showBiomePopup(lb.getText(), lb.getSpecialText());
			else
				showGroupPopup(lb.getSpecialText());
			setEnabled(false, add, subtract, inspect, remove);
		}
	}

	private void showBiomePopup(String shown, String actual) {
		if (shown.charAt(0) == '+' || shown.charAt(0) == '-') {
			shown = shown.substring(1);
			actual = actual.substring(1);
		}
		String s2 = "";
		if (actual.length() > 45) {
			s2 = actual.substring(45);
			actual = actual.substring(0,45);
		}
		mc.displayGuiScreen(new PopupMessage(this, "OK", "", "Biome \"" + shown + "\"", "", "", actual, s2));
	}
	private void showGroupPopup(String group) {
		if (group.equals("*"))
			mc.displayGuiScreen(new PopupMessage(this, "OK", "", "", "* (All Biomes)"));
		else{
			if (group.startsWith("+") || group.startsWith("-"))
				group = group.substring(1);
			mc.displayGuiScreen(new PopupMessage(this, "OK", "", "",  "Group \"" + group + "\""));
		}
	}

	private void addGroupDefinition(String defLine) {
		int i = group.getFocusableWidgets().size();
		group.addWidgets(new FocusableSpecialLabel(width/2-125,height/4-8+i*14,getSimpleName(defLine),defLine));
		setEnabled(false, add, subtract);
		save.setEnabled(true);
	}

	private void addUnused(String line) {
		if (!line.equals("*")) {
			int i = biomes.getFocusableWidgets().size();
			String toUse = line.substring(1);
			biomes.addWidgets(new FocusableSpecialLabel(width/2+125, height/4-8 + 14*i, getSimpleName(toUse), toUse));
		}
		remove.setEnabled(false);
		save.setEnabled(true);
	}


}
