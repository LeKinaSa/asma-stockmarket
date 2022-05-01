package stockmarket;

import jade.core.Agent;
import stockmarket.utils.Utils;

public class Oracle extends Agent {
	public void setup() {
		Utils.log(this, "Ready");
	}
}
