package com.mcf.davidee.msc.gui;

import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Container;
import com.mcf.davidee.guilib.core.Button.ButtonHandler;
import com.mcf.davidee.guilib.basic.Label;
import com.mcf.davidee.guilib.basic.Tooltip;
import com.mcf.davidee.guilib.vanilla.ButtonVanilla;

public class AccessDeniedScreen extends MSCScreen {
	
	private Container container;
	private Label title, message;
	private Button close;

	public AccessDeniedScreen() {
		super(null);
	}
	
	@Override
	protected void createGui() {
		title = new Label("Mob Spawn Controls", new Tooltip("by Davidee"));
		message = new Label("You do not have permission to use MSC", new Tooltip("Try contacting a Server OP"));
		close = new ButtonVanilla("Close",new CloseHandler());

		container = new Container();
		container.addWidgets(title,message,close);
		containers.add(container);
	}

	@Override
	protected void revalidateGui() {
		title.setPosition(width/2, height/4-30);
		close.setPosition(width/2-100, height/4 + 132);
		message.setPosition(width/2,height/4+45);
		
		container.revalidate(0, 0, width, height);
	}


}
