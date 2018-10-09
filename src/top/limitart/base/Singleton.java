package top.limitart.base;

/**
 * 单例
 *
 * @author hank
 * @version 2018/10/8 0008 14:46
 */
@ThreadSafe
public class Singleton<T> {
    private volatile T instance;
    private Func<T> confirmInstance;

    public static <T> Singleton<T> create(@NotNull Func<T> confirmInstance) {
        return new Singleton<>(false, confirmInstance);
    }

    public static <T> Singleton<T> create(boolean lazy, @NotNull Func<T> confirmInstance) {
        return new Singleton<>(lazy, confirmInstance);
    }

    protected Singleton(boolean lazy, @NotNull Func<T> confirmInstance) {
        this.confirmInstance = confirmInstance;
        if (!lazy) {
            get();
        }
    }

    public T get() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    Conditions.notNull(confirmInstance, "singleton not initialized,maybe you should give the 'confirmInstance' or use 'getOrCreate' method");
                    instance = confirmInstance.run();
                    confirmInstance = null;
                }
            }
        }
        return instance;
    }

    public T getOrCreate(@NotNull Func<T> confirmInstance) {
        this.confirmInstance = confirmInstance;
        return get();
    }
}
