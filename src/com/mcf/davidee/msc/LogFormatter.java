package com.mcf.davidee.msc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		sb.append(dateFormat.format(Long.valueOf(record.getMillis())));
		sb.append(" [" + record.getLevel().getLocalizedName() + "] ");

		sb.append(record.getMessage());
		sb.append(System.getProperty("line.separator"));
		Throwable thr = record.getThrown();

		if (thr != null) {
			StringWriter thrDump = new StringWriter();
			thr.printStackTrace(new PrintWriter(thrDump));
			sb.append(thrDump.toString());
		}

		return sb.toString();
	}
}
