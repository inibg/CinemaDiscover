package com.example.app.cinemadiscover;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.net.URLEncoder;

/**
 * Created by iberretta on 10/30/16.
 */
public class FileCache {

    private File cacheDir;
    private final String LOG_TAG = FileCache.class.getSimpleName();

    public FileCache(Context context){


        //Find the dir to save cached images
        cacheDir = context.getCacheDir();
        if(!cacheDir.exists())
            if (!cacheDir.mkdirs())
               Log.d(LOG_TAG, "cache dir doesn't exists: " + cacheDir.toString());

    }

    public File getFile(String url) {
        //identify images by hashcode. Not a perfect solution, good for the demo.
        //String filename = String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
    }

    public void clear(){
        File[] files = cacheDir.listFiles();
        if(files == null)
            return;
        for(File f:files)
            f.delete();
    }

}
