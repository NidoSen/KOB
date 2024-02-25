package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock(); //控制对队列的异步访问
    private final Condition condition = lock.newCondition(); //条件变量
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signal(); //新增用户后唤醒正在等待的run进程，继续consume一个bot
        } finally {
            lock.unlock();
        }
    }

    //为了简化问题，只支持实现Java代码，同时为了防止bot代码出现死循环，要开一个线程（可以实现超时自动断开）
    private void consume(Bot bot) {
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot); //执行2秒
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await(); //await默认包含一个lock.unlock()的工作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot); //比较耗时，可能会执行几秒钟，所以要放到lock后面
            }
        }
    }
}
