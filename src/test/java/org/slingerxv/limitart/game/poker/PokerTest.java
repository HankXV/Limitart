package org.slingerxv.limitart.game.poker;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.util.MathUtil;
import org.slingerxv.limitart.util.CollectionUtil;
import org.slingerxv.limitart.util.StringUtil;

public class PokerTest {
	private List<Byte> pokers;
	private byte[] cards;
	private List<List<Byte>> fiveCards;

	@Before
	public void setUp() throws Exception {
		pokers = new ArrayList<>();
		cards = Poker.createPokerWithJoker();
		byte[] pokers = Poker.createPoker();
		List<Byte> bytes = new ArrayList<>();
		for(byte poker : pokers){
			bytes.add(poker);
			if(bytes.size() >= pokers.length/2){
				break;
			}
		}
		fiveCards = MathUtil.CNM(bytes, 5);
	}

	@After
	public void tearDown() throws Exception {
		
	}
	@Test
	public void testCreateCard() {
		for(int i=2;i<15;++i){
			for(int j=1;j<5;++j){
				pokers.add(Poker.createCard((byte)i, (byte)j));
			}
		}
		Assert.assertFalse(pokers.size() != 52);
	}
	
	@Test
	public void testCreatePoker() {
		byte[] createPoker = Poker.createPoker();
		Assert.assertFalse(createPoker.length != 52);
	}
	
	@Test
	public void testCreatePokerWithJoker() {
		byte[] createPokerWithJoker = Poker.createPokerWithJoker();
		Assert.assertFalse(createPokerWithJoker.length != 54);
	}

	@Test
	public void testShuffle() {
		byte[] createPoker = Poker.createPoker();
		Poker.shuffle(cards);
		Assert.assertFalse(createPoker.length != 52);
	}

	@Test
	public void testGetCardNumber() {
		for(int i=2;i<15;++i){
			byte number = (byte)i;
			for(int j=1;j<5;++j){
				byte suit = (byte)j;
				byte createCard = Poker.createCard(number, suit);
				Assert.assertFalse(Poker.getCardNumber(createCard) != number);
			}
		}
	}

	@Test
	public void testGetCardColor() {
		for(int i=2;i<15;++i){
			byte number = (byte)i;
			for(int j=1;j<5;++j){
				byte suit = (byte)j;
				byte createCard = Poker.createCard(number, suit);
				Assert.assertFalse(Poker.getCardColor(createCard) != suit);
			}
		}
	}

	@Test
	public void testIsAce() {
		for(int i=1;i<5;++i){
			byte createCard = Poker.createCard(Poker.CARD_NUM_ACE, (byte)i);
			Assert.assertFalse(Poker.getCardNumber(createCard) != Poker.CARD_NUM_ACE);
		}
	}

	@Test
	public void testIsJoker() {
		for(int i=1;i<3;++i){
			byte createCard = Poker.createCard(Poker.CARD_NUM_ACE, (byte)i);
			Assert.assertFalse(Poker.isJoker(createCard));
		}
	}

	@Test
	public void testIsBigJoker() {
		byte createCard = Poker.createCard(Poker.CARD_NUM_ACE, Poker.CARD_SUIT_HEART);
		Assert.assertFalse(Poker.isJoker(createCard));
	}

	@Test
	public void testIsSmallJoker() {
		byte createCard = Poker.createCard(Poker.CARD_NUM_ACE, Poker.CARD_SUIT_SPADE);
		Assert.assertFalse(Poker.isJoker(createCard));
	}

	@Test
	public void testIsSameSuit() {
		for(int i=2;i<15;++i){
			byte number = (byte)i;
			for(int j=1;j<5;++j){
				byte suit = (byte)j;
				Assert.assertFalse(!Poker.isSameSuit(Poker.createCard(number, suit), Poker.createCard(Poker.CARD_NUM_ACE, suit)));
			}
		}
	}

	@Test
	public void testIsSameNumber() {
		for(int i=2;i<15;++i){
			byte number = (byte)i;
			for(int j=1;j<5;++j){
				byte suit = (byte)j;
				Assert.assertFalse(!Poker.isSameNumber(Poker.createCard(number, suit), Poker.createCard(number, Poker.CARD_SUIT_HEART)));
			}
		}
	}

	@Test
	public void testCardsToLong() {
		for(int i=0;i<fiveCards.size();++i){
			byte[] source = CollectionUtil.toByteArray(fiveCards.get(i));
			long cardsToLong = Poker.cardsToLong(source);
			Assert.assertArrayEquals(source, Poker.longToCards(cardsToLong));;
		}
	}

	@Test
	public void testLongToCards() {
		for(int i=0;i<fiveCards.size();++i){
			byte[] source = CollectionUtil.toByteArray(fiveCards.get(i));
			long cardsToLong = Poker.cardsToLong(source);
			Assert.assertArrayEquals(source, Poker.longToCards(cardsToLong));;
		}
	}

	@Test
	public void testToStringByte() {
		for(byte card : cards){
			String str = Poker.toString(card);
			Assert.assertFalse(StringUtil.isEmptyOrNull(str));
		}
	}

}
