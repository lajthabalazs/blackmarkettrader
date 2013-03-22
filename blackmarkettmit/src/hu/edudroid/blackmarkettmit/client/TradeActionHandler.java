package hu.edudroid.blackmarkettmit.client;

import hu.edudroid.blackmarkettmit.shared.Player;

public interface TradeActionHandler {
	void cooperate(Player player);
	void screw(Player player);
	void reject(Player player);
}