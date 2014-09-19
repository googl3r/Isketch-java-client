package is.controller.net;

import java.text.ParseException;
import java.util.List;

public class Response extends Command<EResponse> {

	public Response(String commandName, List<String> arguments)
			throws ParseException {
		super(commandName, unsecureSpecialCharacters(arguments),
				EResponse.class);
	}

	private static List<String> unsecureSpecialCharacters(List<String> arguments) {
		for (int i = 0; i < arguments.size(); i++) {
			arguments.set(i,
					arguments.get(i).replace("\\\\", "\\").replace("\\/", "/"));
		}

		return arguments;
	}

}
