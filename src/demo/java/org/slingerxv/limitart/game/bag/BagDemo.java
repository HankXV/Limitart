package org.slingerxv.limitart.game.bag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.game.bag.Bag;
import org.slingerxv.limitart.game.bag.exception.BagFullException;
import org.slingerxv.limitart.game.bag.exception.BagGridOcuppiedException;
import org.slingerxv.limitart.game.bag.exception.ItemNotExistException;
import org.slingerxv.limitart.game.bag.exception.ItemZeroNumException;
import org.slingerxv.limitart.game.item.AbstractItem;


public class BagDemo extends Bag {
	private Logger log = LogManager.getLogger();
	private ConcurrentHashMap<Integer, AbstractItem> bag = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		BagDemo bag = new BagDemo();
		for (int i = 0; i < bag.capacity(); ++i) {
			ItemDemo item = new ItemDemo();
			item.setNum(23);
			try {
				bag.addItem(item);
			} catch (BagFullException | BagGridOcuppiedException | ItemZeroNumException | ItemNotExistException e) {
				e.printStackTrace();
			}
		}
		bag.makeUp();
	}

	@Override
	public int capacity() {
		return 1000;
	}

	@Override
	protected void onItemAdded(int gridId, AbstractItem item) {
		log.info("onItemAdded gridId:" + gridId);
	}

	@Override
	protected void onItemRemoved(int gridId, AbstractItem item) {
		log.info("onItemRemoved gridId:" + gridId);
	}

	@Override
	public void onItemChanged(AbstractItem item) {
		log.info("onItemChanged");
	}

	@Override
	protected Map<Integer, AbstractItem> bag() {
		return bag;
	}

}
