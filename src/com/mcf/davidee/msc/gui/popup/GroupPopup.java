package com.mcf.davidee.msc.gui.popup;

import java.util.Set;

import com.google.common.base.Strings;
import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Button.ButtonHandler;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.TextField;
import com.mcf.davidee.gui.TextField.CharacterFilter;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;
import com.mcf.davidee.gui.vanilla.TextFieldVanilla;
import com.mcf.davidee.msc.gui.edit.GroupsMenu;

public class GroupPopup extends MSCPopup {

	private Container container;

	private Label title, group;
	private boolean add;
	private Button submit, close;
	private TextField nameField;
	private String name;

	private Set<String> groups;
	
	public GroupPopup(GroupsMenu menu, String name, Set<String> groups) {
		super(menu);

		this.name = name;
		this.groups = groups;
		this.add = false;
	}
	
	public GroupPopup(GroupsMenu menu, Set<String> groups) {
		super(menu);

		this.groups = groups;
		this.add = true;
	}
	
	private boolean validInput() {
		String text = nameField.getText();
		if (Strings.isNullOrEmpty(text) || text.equalsIgnoreCase("master") || !add && name.equalsIgnoreCase(text))
			return false;
		for (String s : groups)
			if (s.equalsIgnoreCase(text))
				return false;
		return true;
	}
	
	@Override
	public void updateScreen() {
		submit.setEnabled(validInput());
		super.updateScreen();
	}
	
	@Override
	protected void revalidateGui() {
		super.revalidateGui();
		int startY = (height-HEIGHT)/2;

		title.setPosition(width/2, startY+5);
		group.setPosition(width/2, startY + 40);
		nameField.setPosition(width/2-75,startY + 55);
		submit.setPosition(width/2-60,startY+100);
		close.setPosition(width/2+10,startY+100);

		container.revalidate(0, 0, width, height);
	}

	@Override
	protected void createGui() {
		container = new Container();

		title = new Label((add) ? "Add Group" : "Rename Group", 0, 0);
		group = new Label("Group Name", 0, 0);
		title.setShadowedText(false);
		group.setShadowedText(false);
		submit = new ButtonVanilla(50,20,(add) ? "Add" : "Rename", new ButtonHandler(){
			public void buttonClicked(Button button) {
				if (add)
					((GroupsMenu)bg).addGroup(nameField.getText());
				else
					((GroupsMenu)bg).renameGroup(name,nameField.getText());
				close();
			}
		});
		submit.setEnabled(false);
		close = new ButtonVanilla(50,20,"Cancel", new CloseHandler());

		nameField = new TextFieldVanilla(150, 20, 0xffa09172, 0xff373737, new CharacterFilter(){
			public String filter(String s) {
				StringBuilder sb = new StringBuilder();
				for (char c : s.toCharArray())
					if (isAllowedCharacter(c))
						sb.append(c);
				return sb.toString();
			}
			public boolean isAllowedCharacter(char c) {
				return Character.isLetterOrDigit(c);
			}
		});
		
		if (!add)
			nameField.setText(name);
		nameField.setMaxLength(23);

		container.addWidgets(title, group, nameField, submit, close);

		containers.add(container);
		selected = container;
	}

}
