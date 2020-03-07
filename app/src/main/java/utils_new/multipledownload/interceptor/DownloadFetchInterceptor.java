package utils_new.multipledownload.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadInfo;
import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.DownloadRequest;
import utils_new.multipledownload.TaskManager;
import utils_new.multipledownload.task.DownloadBlockTask;
import utils_new.multipledownload.task.SimpleDownloadTask;
import utils_new.multipledownload.task.Task;

public class DownloadFetchInterceptor implements DownloadInterceptor {
    private DownloadDetailsInfo downloadInfo;
    private DownloadRequest downloadRequest;
    private final List<Task> blockList = new ArrayList<>();

    @Override
    public DownloadInfo intercept(DownloadChain chain) {
        downloadRequest = chain.request();
        downloadInfo = downloadRequest.getDownloadInfo();

        if (downloadInfo.isRunning()) {
            if (downloadInfo.isSupportBreakpoint()) {
                downloadWithBreakpoint();
            } else {
                downloadWithoutBreakPoint();
            }
            clearBlockList();
        } else {
            return downloadInfo.snapshot();
        }
        return chain.proceed(downloadRequest);
    }

    private void downloadWithoutBreakPoint() {
        SimpleDownloadTask simpleDownloadTask = new SimpleDownloadTask(downloadRequest);
        synchronized (blockList) {
            blockList.add(simpleDownloadTask);
        }
        simpleDownloadTask.run();
    }

    private void downloadWithBreakpoint() {
        long completedSize = 0;
        int threadNum = downloadRequest.getThreadNum();
        List<Future> futures = new ArrayList<>(threadNum);
        synchronized (blockList) {
            for (int i = 0; i < threadNum; i++) {
                DownloadBlockTask task = new DownloadBlockTask(downloadRequest, i);
                blockList.add(task);
                completedSize += task.getCompletedSize();
                futures.add(TaskManager.submit(task));
            }
        }
        downloadInfo.setCompletedSize(completedSize);
        try {
            for (Future future : futures) {
                if (!future.isDone()) {
                    future.get();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            cancel();
            for (Future f : futures) {
                f.cancel(true);
            }
        }
    }

    public void cancel() {
        synchronized (blockList) {
            for (Task task : blockList) {
                task.cancel();
            }
        }
    }

    private void clearBlockList() {
        synchronized (blockList) {
            blockList.clear();
        }
    }
}
