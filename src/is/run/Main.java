package is.run;

import is.controller.Controller;
import is.model.ModelFacade;
import is.vue.ViewFacade;

public class Main {

	public static boolean DEBUG = false;

	public static void main(String[] args) {
		DEBUG = true;
		ModelFacade model = new ModelFacade();
		ViewFacade vue = new ViewFacade(model);
		@SuppressWarnings("unused")
		Controller cont = new Controller(model, vue);
	}

}
