package org.slingerxv.limitart.pool;

/**
 * @author hank
 * @version 2018/2/6 0006 17:26
 */
public class PoolDemo {
    public static void main(String[] args) {
        Pool<PoolObj> pool = Pool.create(PoolObj::new, 50);
        for (int i = 0; i < 100; ++i) {
//            PoolObj poolable = pool.get();
            pool.back(new PoolObj());
        }
    }
}
