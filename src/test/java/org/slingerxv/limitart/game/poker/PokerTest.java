package org.slingerxv.limitart.game.poker;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.util.MathUtil;
import org.slingerxv.limitart.util.NumberUtil;

public class PokerTest {
	private byte[] cards;
	private List<List<Byte>> fiveCards;

	@Before
	public void setUp() throws Exception {
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
				Poker.createCard((byte)i, (byte)j);
			}
		}
	}
	
	@Test
	public void testCreatePoker() {
		Poker.createPoker();
	}
	
	@Test
	public void testCreatePokerWithJoker() {
		Poker.createPokerWithJoker();
	}

	@Test
	public void testShuffle() {
		Poker.shuffle(cards);
	}

	@Test
	public void testGetCardNumber() {
		for(byte card : cards){
			Poker.getCardNumber(card);
		}
	}

	@Test
	public void testGetCardColor() {
		for(byte card : cards){
			Poker.getCardColor(card);
		}
	}

	@Test
	public void testIsAce() {
		for(byte card : cards){
			Poker.isAce(card);
		}
	}

	@Test
	public void testIsJoker() {
		for(byte card : cards){
			Poker.isJoker(card);
		}
	}

	@Test
	public void testIsBigJoker() {
		for(byte card : cards){
			Poker.isBigJoker(card);
		}
	}

	@Test
	public void testIsSmallJoker() {
		for(byte card : cards){
			Poker.isSmallJoker(card);
		}
	}

	@Test
	public void testIsSameSuit() {
		for(byte card : cards){
			for(byte anotherCard : cards){
				Poker.isSameSuit(card, anotherCard);
			}
		}
	}

	@Test
	public void testIsSameNumber() {
		for(byte card : cards){
			for(byte anotherCard : cards){
				Poker.isSameNumber(card, anotherCard);
			}
		}
	}

	@Test
	public void testCardsToLong() {
		for(int i=0;i<fiveCards.size();++i){
			Poker.cardsToLong(NumberUtil.toByteArray(fiveCards.get(i)));
		}
	}

	@Test
	public void testLongToCards() {
		for(int i=0;i<fiveCards.size();++i){
			long cardsToLong = Poker.cardsToLong(NumberUtil.toByteArray(fiveCards.get(i)));
			Poker.longToCards(cardsToLong);
		}
	}

	@Test
	public void testToStringByte() {
		for(byte card : cards){
			Poker.toString(card);
		}
	}

}
