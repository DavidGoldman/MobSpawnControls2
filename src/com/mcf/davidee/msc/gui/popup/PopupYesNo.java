package com.mcf.davidee.msc.gui.popup;

import com.mcf.davidee.guilib.basic.BasicScreen;
import com.mcf.davidee.guilib.basic.Label;
import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Button.ButtonHandler;
import com.mcf.davidee.guilib.core.Container;
import com.mcf.davidee.guilib.vanilla.ButtonVanilla;

public class PopupYesNo extends MSCPopup{

	public interface YesNoHandler{
		public void handleExit(boolean yes);
	}

	private String yesText, noText;
	private YesNoHandler handler;
	private String[] labelStr;
	private Label[] labels;

	private Container container;
	private Button yes, no;

	public PopupYesNo(BasicScreen bg, String yesText, String noText, YesNoHandler handler, String... labelStr) {
		super(bg);

		this.yesText = yesText;
		this.noText = noText;
		this.handler = handler;
		this.labelStr = labelStr;
	}

	public void close() {
		handler.handleExit(false);
		super.close();
	}

	@Override
	protected void revalidateGui() {
		super.revalidateGui();
		int startY = (height-HEIGHT)/2;
		yes.setPosition(width/2-60,startY+100);
		no.setPosition(width/2+10,startY+100);
		container.revalidate(0,0,width,height);

		for (int i = 0; i < labels.length; ++i)
			labels[i].setPosition(width/2,startY+i*13);
	}

	@Override
	protected void createGui() {
		container = new Container();
		ButtonHandler h = new YesNoButtonHandler();
		yes = new ButtonVanilla(50,20,yesText,h);
		no = new ButtonVanilla(50,20,noText,h);
		container.addWidgets(yes, no);

		labels = new Label[labelStr.length];
		for (int i = 0; i < labels.length; ++i) {
			labels[i] = new Label(labelStr[i], 0, 0);
			labels[i].setShadowedText(false);
		}
		container.addWidgets(labels);

		containers.add(container);
		selectedContainer = container;
	}

	private class YesNoButtonHandler implements ButtonHandler{
		public void buttonClicked(Button button) {
			handler.handleExit(button == yes);
			PopupYesNo.super.close();
		}
	}

}
