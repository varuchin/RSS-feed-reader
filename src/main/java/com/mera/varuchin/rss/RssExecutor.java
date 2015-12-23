package com.mera.varuchin.rss;


import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public final class RssExecutor {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutorService
            = Executors.newSingleThreadScheduledExecutor();

    public RssExecutor() {
    }

    public void run(Runnable task) {
        executorService.submit(task);
    }

    public void run(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        scheduledExecutorService.
                scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
    }

    public List<RssFeed> getFeeds(Callable<List<RssFeed>> task) {
        Future<List<RssFeed>> future = executorService.submit(task);

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RssFeed getFeed(Callable<RssFeed> task) {
        Future<RssFeed> future = executorService.submit(task);

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<RssItem> getItems(Callable<List<RssItem>> task) {
        Future<List<RssItem>> future = executorService.submit(task);

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RssItem getItem(Callable<RssItem> task) {
        Future<RssItem> future = executorService.submit(task);

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Integer> getMap(Callable<Map<String, Integer>> task) {
        Future<Map<String, Integer>> future = executorService.submit(task);

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
