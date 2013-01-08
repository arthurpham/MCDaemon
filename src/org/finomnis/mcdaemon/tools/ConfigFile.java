package org.finomnis.mcdaemon.tools;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public abstract class ConfigFile {

	private Map<String, String> values = new TreeMap<String, String>();
	private Map<String, String> defaultValues = new HashMap<String, String>();
	private String fileName = "";

	public ConfigFile() {
		values.clear();
		defaultValues.clear();

		setDefaultValues(defaultValues);
		values.putAll(defaultValues);

		fileName = getFileName();

		readFromFile();
		writeToFile();
	}

	protected boolean isValid(String configName, String value) {
		String validValues[] = getValidValues(configName);

		if (validValues == null)
			return true;

		// sort out special values
		if (validValues.length == 1) {
			switch (validValues[0]) {
			case ":int:":
				try {
					Integer.parseInt(value);
					return true;
				} catch (Exception e) {
					return false;
				}
			case ":bool:":
				if (value.equals("false") || value.equals("true"))
					return true;
				return false;
			case ":path:":
				if(value.length() < 2)
					return false;
				if (!value.startsWith("\"") || !value.endsWith("\""))
					return false;
				return true;
			default:
				break;
			}
		}

		// check values
		for (String validValue : validValues) {
			if (validValue.equals(value)) {
				return true;
			}
		}
		return false;

	}

	public String getConfig(String configName) {

		String res = values.get(configName);
		if (res == null)
			throw new RuntimeException("Key '" + configName + "' not found!");

		// Check if value is valid, otherwise replace with default value
		if (!isValid(configName, res)) {
			res = defaultValues.get(configName);
			if (res == null)
				throw new RuntimeException("Key '" + configName
						+ "' not found!");
			values.put(configName, res);
			writeToFile();
		}

		return res;
	}

	public void setConfig(String configName, String value) {
		if (!values.containsKey(configName))
			throw new RuntimeException("Key '" + configName + "' not found!");
		values.put(configName, value);
		writeToFile();
	}

	
	
	private void readFromFile() {
		Scanner scanner;

		try {
			scanner = new Scanner(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			Log.out("Config file '" + fileName + "' not found.");
			return;
		}

		try {

			while (scanner.hasNextLine()) {

				try {

					String nextLine = scanner.nextLine().trim();

					if (nextLine.startsWith("#"))
						continue;

					int splitPos = nextLine.indexOf('=');
					if (splitPos == -1)
						continue;

					String key = nextLine.substring(0, splitPos);
					String value = nextLine.substring(splitPos + 1);

					if (!values.containsKey(key))
						continue;
					
					// Check if value is valid, otherwise replace with default value
					if (!isValid(key, value)) {
						value = defaultValues.get(key);
						if (value == null)
							throw new RuntimeException("Key '" + key
									+ "' not found!");
					}
					
					values.put(key, value);

				} catch (Exception e) {
					Log.warn(e);
				}
			}

		} finally {
			scanner.close();
		}

	}

	private void writeToFile() {

		FileWriter f;

		try {
			f = FileTools.openFileWriteText(fileName, false);
		} catch (IOException e) {
			Log.err(e);
			Log.err("Unable to write config to file!");
			return;
		}

		try {

			for (Entry<String, String> e : values.entrySet()) {
				String configDescription = getConfigDescription(e.getKey());
				if (configDescription == null)
					configDescription = "";

				String[] validValues = getValidValues(e.getKey());
				
				boolean printValidValues = true;
				if(validValues.length == 1)
					if(validValues[0].startsWith(":") && validValues[0].endsWith(":"))
						printValidValues = false;
				
				if (validValues != null && printValidValues) {
					if (!configDescription.equals(""))
						configDescription += "\n";
					configDescription += "  Valid values: ";
					for (int i = 0; i < validValues.length; i++) {
						if (i != 0)
							configDescription += ", ";
						configDescription += "'" + validValues[i] + "'";
					}
				}

				for (String str : configDescription.split("\\r?\\n")) {
					f.write("# " + str + "\r\n");
				}
				f.write(e.getKey() + "=" + e.getValue() + "\r\n\r\n");
			}

		} catch (Exception e) {
			Log.err(e);
		} finally {
			try {
				f.close();
			} catch (IOException e) {
				Log.warn(e);
			}
		}

	}

	protected abstract void setDefaultValues(Map<String, String> configs);

	protected abstract String getFileName();

	protected abstract String getConfigDescription(String config);

	protected abstract String[] getValidValues(String config);

}
