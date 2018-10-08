package top.limitart.concurrent;


public class TaskQueueDemo {
    public static void main(String[] args) {
        TaskQueue queue1 = TaskQueue.create("queue1");
        TaskQueue queue2 = TaskQueue.create("queue2");
        Role role = new Role();
        Map map1 = new Map(queue1);
        Map map2 = new Map(queue2);
        role.join(map1, () -> System.out.println("role join map!" + Thread.currentThread()), (e) -> {
        });
        //正常测试
        role.onAnotherSync(map2, () -> {
            System.out.println("proccess:" + Thread.currentThread());
            return true;
        }, () -> System.out.println("success:" + Thread.currentThread()), () -> System.out.println("fail:" + Thread.currentThread()));
        role.leave(map1);
        role.join(map2, () -> System.out.println("role join map!" + Thread.currentThread()), (e) -> {
        });
        //切换执行失败测试
        role.onAnotherSync(map1, () -> {
            System.out.println("proccess:" + Thread.currentThread());
            return false;
        }, () -> System.out.println("success:" + Thread.currentThread()), () -> System.out.println("fail:" + Thread.currentThread()));
        //执行超时测试
        role.onAnotherSync(map1, () -> {
            System.out.println("proccess:" + Thread.currentThread());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }, () -> System.out.println("success:" + Thread.currentThread()), () -> System.out.println("fail:" + Thread.currentThread()));
    }

    private static class Role extends TaskQueueActor<Map> {

    }

    private static class Map implements Actor.Place<TaskQueue> {
        final TaskQueue queue;

        public Map(TaskQueue queue) {
            this.queue = queue;
        }

        @Override
        public TaskQueue res() {
            return queue;
        }
    }
}
