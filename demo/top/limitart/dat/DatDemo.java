package top.limitart.dat;

import java.io.File;

/**
 * @author hank
 * @version 2018/10/16 0016 16:14
 */
public class DatDemo {
    public static void main(String[] args) throws Exception {
        DataSet set = DataSet.withDir(new File("./resources"), "bytes", f -> "top.limitart.dat." + f.getName().split("[.]")[0], o -> {
            try {
                return revise(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        });
        DataContainer<D_WinRank> container = set.getContainer(D_WinRank.class);
        container.forEach(d -> System.out.println(d.getClass()));
    }

    private static DataMeta revise(DataMeta old) throws IllegalAccessException {
        if (old instanceof D_WinRank) {
            R_WinRank revise = new R_WinRank();
            revise.initFrom(old);
            return revise;
        }
        return null;
    }
}
