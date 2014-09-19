package is.controller.net;

import java.text.ParseException;
import java.util.List;

public abstract class Command<T extends Enum<T>> {

	private List<String> arguments;
	private T commandName;

	protected Command(String commandName, List<String> arguments, Class<T> c)
			throws ParseException {
		this.commandName = coerceCommand(commandName, c);
		this.arguments = arguments;
	}

	protected Command(T command, List<String> arguments) {
		this.commandName = command;
		this.arguments = arguments;
	}

	private T coerceCommand(String str, Class<T> c) throws ParseException {
		try {
			return Enum.valueOf(c, str);
		} catch (IllegalArgumentException e) {
			throw new ParseException("Unknown protocol command : " + str, 0);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(commandName + "/");
		for (String arg : arguments)
			sb.append(arg.replace("\\", "\\\\").replace("/", "\\/").toString()
					+ "/");

		return sb.toString();
	}

	public T getCommand() {
		return commandName;
	}

	public List<String> getArguments() {
		return arguments;
	}
}
