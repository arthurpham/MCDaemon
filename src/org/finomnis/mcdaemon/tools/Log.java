package org.finomnis.mcdaemon.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	public static void out(String msg) {

		System.out.println(format(msg));

	}

	public static void debug(String msg) {

		// System.out.println(format("[d] " + msg));

	}

	public static void err(String msg) {

		System.err.println(format(msg));

	}

	public static void warn(Exception e) {

		System.err.println(format("WARNING: " + e.toString()));
		for (StackTraceElement tr : e.getStackTrace()) {
			System.err.println("   " + tr);
		}
	}

	public static void err(Exception e) {

		System.err.println(format("ERROR: " + e.toString()));
		for (StackTraceElement tr : e.getStackTrace()) {
			System.err.println("   " + tr);
		}
	}

	private static String format(String msg) {

		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
		String erg = ft.format(new Date()) + " ["
				+ Thread.currentThread().getId() + "]  " + msg;
		return erg;

	}

	public static void serverMessage(String msg) {
		/*
		if (msg.matches("\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}\\:\\d{2}\\:\\d{2} .*\\n?")) {
			try {
				msg = msg.substring(20);
			} catch (Exception e) {
				warn(e);
			}
		}
		System.err.println(format("[SERVER] " + msg));
		*/
	}

}
