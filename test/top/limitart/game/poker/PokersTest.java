package top.limitart.game.poker;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import top.limitart.util.CollectionUtil;
import top.limitart.util.GameMathUtil;
import top.limitart.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PokersTest {
    private List<Byte> pokers;
    private byte[] cards;
    private List<List<Byte>> fiveCards;

    @Before
    public void setUp() {
        pokers = new ArrayList<>();
        cards = Pokers.createPokerWithJoker();
        byte[] pokers = Pokers.createPoker();
        List<Byte> bytes = new ArrayList<>();
        for (byte poker : pokers) {
            bytes.add(poker);
            if (bytes.size() >= pokers.length / 2) {
                break;
            }
        }
        fiveCards = GameMathUtil.CNM(bytes, 5);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreateCard() {
        for (int i = 2; i < 15; ++i) {
            for (int j = 1; j < 5; ++j) {
                pokers.add(Pokers.createCard((byte) i, (byte) j));
            }
        }
        Assert.assertFalse(pokers.size() != 52);
    }

    @Test
    public void testCreatePoker() {
        byte[] createPoker = Pokers.createPoker();
        Assert.assertFalse(createPoker.length != 52);
    }

    @Test
    public void testCreatePokerWithJoker() {
        byte[] createPokerWithJoker = Pokers.createPokerWithJoker();
        Assert.assertFalse(createPokerWithJoker.length != 54);
    }

    @Test
    public void testShuffle() {
        byte[] createPoker = Pokers.createPoker();
        Pokers.shuffle(cards);
        Assert.assertFalse(createPoker.length != 52);
    }

    @Test
    public void testGetCardNumber() {
        for (int i = 2; i < 15; ++i) {
            byte number = (byte) i;
            for (int j = 1; j < 5; ++j) {
                byte suit = (byte) j;
                byte createCard = Pokers.createCard(number, suit);
                Assert.assertFalse(Pokers.getCardNumber(createCard) != number);
            }
        }
    }

    @Test
    public void testGetCardColor() {
        for (int i = 2; i < 15; ++i) {
            byte number = (byte) i;
            for (int j = 1; j < 5; ++j) {
                byte suit = (byte) j;
                byte createCard = Pokers.createCard(number, suit);
                Assert.assertFalse(Pokers.getCardColor(createCard) != suit);
            }
        }
    }

    @Test
    public void testIsAce() {
        for (int i = 1; i < 5; ++i) {
            byte createCard = Pokers.createCard(Pokers.CARD_NUM_ACE, (byte) i);
            Assert.assertFalse(Pokers.getCardNumber(createCard) != Pokers.CARD_NUM_ACE);
        }
    }

    @Test
    public void testIsJoker() {
        for (int i = 1; i < 3; ++i) {
            byte createCard = Pokers.createCard(Pokers.CARD_NUM_ACE, (byte) i);
            Assert.assertFalse(Pokers.isJoker(createCard));
        }
    }

    @Test
    public void testIsBigJoker() {
        byte createCard = Pokers.createCard(Pokers.CARD_NUM_ACE, Pokers.CARD_SUIT_HEART);
        Assert.assertFalse(Pokers.isJoker(createCard));
    }

    @Test
    public void testIsSmallJoker() {
        byte createCard = Pokers.createCard(Pokers.CARD_NUM_ACE, Pokers.CARD_SUIT_SPADE);
        Assert.assertFalse(Pokers.isJoker(createCard));
    }

    @Test
    public void testIsSameSuit() {
        for (int i = 2; i < 15; ++i) {
            byte number = (byte) i;
            for (int j = 1; j < 5; ++j) {
                byte suit = (byte) j;
                Assert.assertFalse(!Pokers.isSameSuit(Pokers.createCard(number, suit), Pokers.createCard(Pokers.CARD_NUM_ACE, suit)));
            }
        }
    }

    @Test
    public void testIsSameNumber() {
        for (int i = 2; i < 15; ++i) {
            byte number = (byte) i;
            for (int j = 1; j < 5; ++j) {
                byte suit = (byte) j;
                Assert.assertFalse(!Pokers.isSameNumber(Pokers.createCard(number, suit), Pokers.createCard(number, Pokers.CARD_SUIT_HEART)));
            }
        }
    }

    @Test
    public void testCardsToLong() {
        for (List<Byte> fiveCard : fiveCards) {
            byte[] source = CollectionUtil.toByteArray(fiveCard);
            long cardsToLong = Pokers.cardsToLong(source);
            Assert.assertArrayEquals(source, Pokers.longToCards(cardsToLong));
        }
    }

    @Test
    public void testLongToCards() {
        for (List<Byte> fiveCard : fiveCards) {
            byte[] source = CollectionUtil.toByteArray(fiveCard);
            long cardsToLong = Pokers.cardsToLong(source);
            Assert.assertArrayEquals(source, Pokers.longToCards(cardsToLong));
        }
    }

    @Test
    public void testToStringByte() {
        for (byte card : cards) {
            String str = Pokers.toString(card);
            Assert.assertFalse(StringUtil.empty(str));
        }
    }

}
