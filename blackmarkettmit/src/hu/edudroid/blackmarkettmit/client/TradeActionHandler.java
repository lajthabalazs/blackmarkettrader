package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Contact;

public interface TradeActionHandler {
	void cooperate(Contact player);
	void screw(Contact player);
	void reject(Contact player);
}