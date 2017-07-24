package org.slingerxv.limitart.funcs;

public class Procs {
	public static void invoke(Proc proc) {
		if (proc != null)
			proc.run();
	}

	public static <T1> void invoke(Proc1<T1> proc, T1 t1) {
		if (proc != null)
			proc.run(t1);
	}

	public static <T1, T2> void invoke(Proc2<T1, T2> proc, T1 t1, T2 t2) {
		if (proc != null)
			proc.run(t1, t2);
	}

	public static <T1, T2, T3> void invoke(Proc3<T1, T2, T3> proc, T1 t1, T2 t2, T3 t3) {
		if (proc != null)
			proc.run(t1, t2, t3);
	}

	public static <T1, T2, T3, T4> void invoke(Proc4<T1, T2, T3, T4> proc, T1 t1, T2 t2, T3 t3, T4 t4) {
		if (proc != null)
			proc.run(t1, t2, t3, t4);
	}

	public static <T1, T2, T3, T4, T5> void invoke(Proc5<T1, T2, T3, T4, T5> proc, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
		if (proc != null)
			proc.run(t1, t2, t3, t4, t5);
	}
}
