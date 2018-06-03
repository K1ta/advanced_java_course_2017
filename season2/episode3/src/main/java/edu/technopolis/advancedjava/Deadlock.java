package edu.technopolis.advancedjava;

public class Deadlock {
    private static final Object FIRST_LOCK = new Object();
    private static final Object SECOND_LOCK = new Object();
    private static final Object THIRD_LOCK = new Object();
    static int state = 0;

    public static void main(String[] args) throws Exception {
        Thread ft = new Thread(Deadlock::first);
        Thread st = new Thread(Deadlock::second);
        ft.start();
        st.start();
        ft.join();
        st.join();
        //never going to reach this point
    }

    private static void first() {
        synchronized (FIRST_LOCK) {
            deadlock();
            synchronized (SECOND_LOCK) {
                //unreachable point
            }
        }
    }

    private static void second() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //
        }
        //reverse order of monitors
        synchronized (SECOND_LOCK) {
            deadlock();
            synchronized (FIRST_LOCK) {
                //unreachable point
            }
        }
    }

    private static void deadlock() {
        synchronized (THIRD_LOCK) {
            state++;
            while (state < 2) {
                try {
                    THIRD_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            THIRD_LOCK.notifyAll();
        }
    }

}