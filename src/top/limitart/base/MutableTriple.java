package top.limitart.base;

/**
 * 可变的三个
 *
 * @author hank
 */
@ThreadUnsafe
public class MutableTriple<A, B, C> implements Triple<A, B, C> {
    private A a;
    private B b;
    private C c;

    @Override
    public @Nullable
    A getA() {
        return a;
    }

    @Override
    public @Nullable
    B getB() {
        return b;
    }

    @Override
    public @Nullable
    C getC() {
        return c;
    }

    public MutableTriple(@Nullable A a, @Nullable B b, @Nullable C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public MutableTriple() {
    }

    public void setA(@Nullable A a) {
        this.a = a;
    }

    public void setB(@Nullable B b) {
        this.b = b;
    }

    public void setC(@Nullable C c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "MutableTriple{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
