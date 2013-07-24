package com.mcf.davidee.msc.gui.popup;

import com.mcf.davidee.gui.Button;
import com.mcf.davidee.gui.Container;
import com.mcf.davidee.gui.basic.BasicScreen;
import com.mcf.davidee.gui.basic.Label;
import com.mcf.davidee.gui.vanilla.ButtonVanilla;

public class PopupMessage extends MSCPopup {

	private String bText;
	private String[] labelStr;

	private Container container;
	private Label[] labels;
	private Button ok;

	public PopupMessage(BasicScreen bg, String buttonText, String... strings) {
		super(bg);

		this.bText = buttonText;
		this.labelStr = strings;
	}

	@Override
	protected void createGui() {
		container = new Container();
		ok = new ButtonVanilla(50, 20, bText, new CloseHandler());
		container.addWidgets(ok);

		labels = new Label[labelStr.length];
		for (int i = 0; i < labels.length; ++i) {
			labels[i] = new Label(labelStr[i], 0, 0);
			labels[i].setShadowedText(false);
		}
		container.addWidgets(labels);

		containers.add(container);
		selected = container;
	}

	@Override
	public void revalidateGui() {
		super.revalidateGui();
		int startY = (height - HEIGHT) / 2;
		ok.setPosition(width / 2 - 25, startY + 100);
		container.revalidate(0, 0, width, height);

		for (int i = 0; i < labels.length; ++i)
			labels[i].setPosition(width / 2, startY + i * 13);
	}

}
