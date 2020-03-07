package utils_new.multipledownload;


import utils_new.multipledownload.task.DownloadTask;

public interface DownloadTaskExecutor {

    void init();


    void execute(DownloadTask downloadTask);


    int getMaxDownloadNumber();


    String getName();

    String getTag();


    void shutdown();
}
