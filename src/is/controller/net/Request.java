package is.controller.net;

import java.util.List;

public class Request extends Command<ERequest> {

	public Request(ERequest command, List<String> arguments) {
		super(command, secureSpecialCharacters(arguments));
	}

	private static List<String> secureSpecialCharacters(List<String> arguments) {
		for (int i = 0; i < arguments.size(); i++) {
			arguments.set(i, arguments.get(i).replaceAll("\\\\", "\\\\")
					.replaceAll("/", "\\/"));
		}
		return arguments;
	}
}
