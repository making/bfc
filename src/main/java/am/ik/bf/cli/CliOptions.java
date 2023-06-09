package am.ik.bf.cli;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CliOptions {

	private static final Set<String> noValueKeys = Set.of("-h", "--help", //
			"-v", "--version", //
			"--optimize");

	private final Map<String, String> options;

	private static final String NOKEY = "__";

	public CliOptions(Map<String, String> options) {
		this.options = Collections.unmodifiableMap(options);
	}

	public boolean isEmpty() {
		return this.options.isEmpty();
	}

	public String get(String key) {
		return this.options.get(key);
	}

	public boolean contains(String key) {
		return this.options.containsKey(key);
	}

	public String getNokey() {
		return this.get(NOKEY);
	}

	public boolean containsNoKey() {
		return this.contains(NOKEY);
	}

	public static CliOptions build(String[] args) {
		final Map<String, String> options = new LinkedHashMap<>();
		String key = null;
		for (String arg : args) {
			if (key == null) {
				if (arg.equals("-") || !arg.startsWith("-")) {
					options.put(NOKEY, arg);
				}
				else {
					key = arg;
					if (noValueKeys.contains(key)) {
						options.put(key, "");
						key = null;
					}
				}
			}
			else {
				options.put(key, arg);
				key = null;
			}
		}
		return new CliOptions(options);
	}

	@Override
	public String toString() {
		return "CliOptions{" + "options=" + options + '}';
	}

}
