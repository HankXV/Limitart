package org.slingerxv.limitart.game.poker.texas;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.game.poker.Poker;
import org.slingerxv.limitart.util.MathUtil;
import org.slingerxv.limitart.util.NumberUtil;

public class TXCardsCalculatorTest {
	private TXCardsCalculator txCardsCalculator;
	private List<List<Byte>> tableCards;
	private List<List<Byte>> handCards;
	private List<List<Byte>> fiveCards;

	@Before
	public void setUp() throws Exception {
		tableCards = new ArrayList<>();
		handCards = new ArrayList<>();
		fiveCards = new ArrayList<>();
		byte[] pokers = Poker.createPoker();
		List<Byte> bytes = new ArrayList<>();
		for (byte poker : pokers) {
			bytes.add(poker);
			if (bytes.size() >= pokers.length / 2) {
				break;
			}
		}
		List<List<Byte>> cnSeven = MathUtil.CNM(bytes, 7);
		for (int i = 0; i < cnSeven.size(); ++i) {
			List<Byte> temp = cnSeven.get(i);
			List<Byte> tCards = new ArrayList<>();
			List<Byte> hCards = new ArrayList<>();
			for (int j = 0; j < temp.size(); ++j) {
				if (j < 5) {
					tCards.add(temp.get(j));
				} else {
					hCards.add(temp.get(j));
				}
			}
			tableCards.add(tCards);
			handCards.add(hCards);
		}
		fiveCards = MathUtil.CNM(bytes, 5);
	}

	@After
	public void tearDown() throws Exception {
		txCardsCalculator = null;
	}

	@Test
	public void testCalBestCards() {
		for (int i = 0; i < tableCards.size(); ++i) {
			txCardsCalculator = TXCardsCalculator.calBestCards(NumberUtil.toByteArray(tableCards.get(i)),
					NumberUtil.toByteArray(handCards.get(i)));
			Assert.assertFalse(txCardsCalculator.getRank() < TXCardsCalculator.HIGH_CARD);
			txCardsCalculator = null;
		}
	}

	@Test
	public void testTXCardsCalculator() {
		for (int i = 0; i < fiveCards.size(); ++i) {
			txCardsCalculator = new TXCardsCalculator(NumberUtil.toByteArray(fiveCards.get(i)));
			Assert.assertFalse(txCardsCalculator.getRank() < TXCardsCalculator.HIGH_CARD);
			txCardsCalculator = null;
		}
	}

	@Test
	public void testGetRank() {
		for (int i = 0; i < fiveCards.size(); ++i) {
			txCardsCalculator = new TXCardsCalculator(NumberUtil.toByteArray(fiveCards.get(i)));
			Assert.assertFalse(txCardsCalculator.getRank() < TXCardsCalculator.HIGH_CARD);
			txCardsCalculator = null;
		}
	}

	@Test
	public void testGetValue() {
		TXCardsCalculator calculator = new TXCardsCalculator(new byte[]{Poker.createCard((byte) 2, Poker.CARD_SUIT_HEART),
				Poker.createCard((byte) 3, Poker.CARD_SUIT_SPADE), Poker.createCard((byte)4, Poker.CARD_SUIT_SPADE),
				Poker.createCard((byte) 5, Poker.CARD_SUIT_SPADE), Poker.createCard((byte) 7, Poker.CARD_SUIT_SPADE)});
		Assert.assertFalse("Class TXCardsCalculator constructor has error", calculator.getRank()<TXCardsCalculator.HIGH_CARD);
		long minValue = calculator.getValue();
		for (int i = 0; i < fiveCards.size(); ++i) {
			txCardsCalculator = new TXCardsCalculator(NumberUtil.toByteArray(fiveCards.get(i)));
			Assert.assertFalse(txCardsCalculator.getValue() < minValue);
			txCardsCalculator = null;
		}
	}

	@Test
	public void testGetCards() {
		for (int i = 0; i < fiveCards.size(); ++i) {
			TXCardsCalculator once = new TXCardsCalculator(NumberUtil.toByteArray(fiveCards.get(i)));
			byte[] onceCards = once.getCards();
			TXCardsCalculator twice = new TXCardsCalculator(onceCards);
			byte[] twiceCards = twice.getCards();
			Assert.assertArrayEquals(onceCards, twiceCards);
			txCardsCalculator = null;
		}
	}
}
