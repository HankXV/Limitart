package org.slingerxv.limitart.collections;

import org.slingerxv.limitart.base.CompareChain;

import java.util.Comparator;

public class RankMapDemo {
    private static final Comparator<Bean> COMPARATOR = CompareChain.build((o1, o2) -> CompareChain.start(o2.price * o2.item.getNum(), o1.price * o1.item.num));

    public static void main(String[] args) {
        RankMap<Long, Bean> rankMap = RankMap.create(COMPARATOR, 100);
        Item i1 = new Item(2);
        Bean b1 = new Bean(1, i1, 100);
        Item i2 = new Item(3);
        Bean b2 = new Bean(2, i2, 100);
        rankMap.replaceOrPut(b1);
        rankMap.replaceOrPut(b2);
        System.out.println(rankMap.getAll());
        //！！！这里一定要注意对引用值的修改
        Bean copy = b2.copy();
        copy.item.setNum(1);
        rankMap.replaceOrPut(copy);
        System.out.println(rankMap.getAll());
    }

    public static class Bean implements RankMap.LongRankObj {
        private long id;
        private Item item;
        private int price;

        public Bean(long id, Item item, int price) {
            this.item = item;
            this.price = price;
            this.id = id;
        }

        public Bean copy() {
            return new Bean(this.id, this.item.copy(), this.price);
        }

        @Override
        public Long key() {
            return id;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "id=" + id +
                    ", item=" + item +
                    ", price=" + price +
                    '}';
        }
    }

    public static class Item {
        private int num;

        public Item copy() {
            return new Item(num);
        }

        public Item(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "num=" + num +
                    '}';
        }
    }
}
