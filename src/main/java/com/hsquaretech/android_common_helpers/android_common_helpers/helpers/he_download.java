package com.hsquaretech.android_common_helpers.android_common_helpers.helpers;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ProgressBar;

import com.hsquaretech.android_common_helpers.android_common_helpers.android_core_helpers.helpers.file_helper;
import com.hsquaretech.android_common_helpers.android_common_helpers.android_core_helpers.log.log;
import com.hsquaretech.android_common_helpers.android_common_helpers.android_core_helpers.helpers.imui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @category name space imlb_ => Library helper talks with mobile platform library project of "im_"
 */
public class he_download
{
	Context context;
	public static he_download imlb = null;

	protected Timer myTimer = null;

	//protected  BroadcastReceiver mBroadCastReceiver = null;

	protected DownloadManager mDownloadManager = null;

    //protected List<Long> mDownloadIds = null;

	protected Map<String, Long> downloadIds;
	protected Map<Long, Activity> downloadHolderActivities = null;
	protected Map<Long, ProgressBar> downloadProgressBars = null;
	protected Map<Long, Runnable> downloadCallback = null;
	protected Map<Long, BroadcastReceiver> mBroadCastReceiverMap = null;
	protected Map<Long, String> urlmap;

	public he_download()
	{
	}

	public static he_download singleton()
	{
		if( imlb == null )
		{
			imlb = new he_download();
		}
		return imlb;
	}

	private void removeDownloadConnectionObjects(Long key)
	{
		removeDownloadConnectionObjects(key, true);
	}

	private void removeDownloadConnectionObjects(Long key, boolean is_remove_urlmap)
	{

		if( key != null )
		{
			//	[downloadReceivedData removeObjectForKey:key];
			//	mDownloadIds.remove(key);
			if (downloadHolderActivities.containsKey(key))
			{
				downloadHolderActivities.remove(key);
			}
			if (downloadProgressBars.containsKey(key))
			{
				downloadProgressBars.remove(key);
			}
			if (downloadCallback.containsKey(key))
			{
				downloadCallback.remove(key);
			}
			if (mBroadCastReceiverMap.containsKey(key))
			{
				try
				{
					context.unregisterReceiver(mBroadCastReceiverMap.get(key));
				}
				catch (Exception e)
				{
					// handle error
					// nothing to do in case its illegal argument exception error
				}
				mBroadCastReceiverMap.remove(key);
			}
			if (is_remove_urlmap && urlmap.containsKey(key))
			{
				urlmap.remove(key);
			}
			if (downloadIds.containsKey(key))
			{
				downloadIds.remove(key);
			}

			//			[downloadExpectedBytes removeObjectForKey:key];
			//			[downloadSavePath removeObjectForKey:key];
			//			[downloadCallbackDict removeObjectForKey:key];
		}
	}

	public void createObjects(Activity context)
	{
		mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        //	mDownloadIds = new ArrayList<Long>();

		downloadIds = new ConcurrentHashMap<String ,Long >();
		downloadProgressBars = new HashMap<Long, ProgressBar>();
		downloadCallback = new HashMap<Long, Runnable>();
		downloadHolderActivities = new HashMap<Long, Activity>();
		mBroadCastReceiverMap = new HashMap<Long, BroadcastReceiver>();
		urlmap = new HashMap< Long, String>();
	}

	public void downloadFile(final Activity context, final String url, ProgressBar progressBar, Runnable runnable, String filepath)
	{
		String downloadKey = imui.createKeyFromUrl(url);

		// initializing the download manager instance ...., could be moved in constructor?
		if( mDownloadManager == null )
		{
			createObjects(context);
		}

		// Initializing the broadcast receiver ...
		final BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				final Long id = intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID);

				//set last progress
				if (downloadHolderActivities.get(id) != null)
				{
					downloadHolderActivities.get(id).runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							downloadProgressBars.get(id).setProgress(100);
							downloadCallback.get(id).run();
						}
					});
				}

				/* post dowload activities */

				if (urlmap.get(id) != null)
				{
					// 8_1_2019	String filepath = file_helper.singleton().fileSavePath(im_application.singleton(), urlmap.get(id), true );
					// 8_1_2019	String destpath = file_helper.singleton().fileSavePath(im_application.singleton(), urlmap.get(id), false );

					String filepath = file_helper.singleton().fileSavePath(context, urlmap.get(id), true );
					String destpath = file_helper.singleton().fileSavePath(context, urlmap.get(id), false );

					File srcFile = new File(filepath);
					File destFile = new File(destpath);

					// 8_1_2019	postDownload(im_application.singleton(), srcFile, destFile, filepath);

					postDownload(context, srcFile, destFile, filepath);
				}

				//flush vars since this particular download is complete
				removeDownloadConnectionObjects(id);
			}
		};

		Long key = null;
		if( downloadIds == null )
		{
			createObjects(context);
		}
		if (!downloadIds.containsKey(downloadKey))
		{
			// adding files to the download manager list ...
			key = addFileForDownloadInDownloadManager(context, url, filepath);
			downloadIds.put(downloadKey,key);
		}
		else
		{
			key = downloadIds.get(downloadKey);
		}

		//add vars
		downloadHolderActivities.put(key,context);
		downloadProgressBars.put(key, progressBar);
		downloadCallback.put(key, runnable);
		mBroadCastReceiverMap.put(key,mBroadCastReceiver);
		urlmap.put(key,url);
        // mDownloadIds.add(key);

		//register receiver
		IntentFilter intentFilter = new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE");

		try
		{
			context.registerReceiver(mBroadCastReceiver, intentFilter);
		}
		catch (Exception e)
		{
			if (context == null)
			{
				//	8_1_2019	log.singleton().logError(im_application.singleton(), 1, "Main activity context is null", e);
				log.singleton().logError(context, 1, "Main activity context is null", e);
			}
			else if (mBroadCastReceiver == null)
			{
         		//  8_1_2019    log.singleton().logError(im_application.singleton(), 1, "Broadcast receiver is null", e);
       			log.singleton().logError(context, 1, "Broadcast receiver is null", e);
			}
			else if (intentFilter == null)
			{
				//	8_1_2019	log.singleton().logError(im_application.singleton(), 1, "IntentFilter receiver is null", e);
				log.singleton().logError(context, 1, "IntentFilter receiver is null", e);
			}
			else
			{
				//	8_1_2019	log.singleton().logError(im_application.singleton(), 1, "Nothing found null", e);
				log.singleton().logError(context, 1, "Nothing found null", e);
			}

			intentFilter = null;
			removeDownloadConnectionObjects(key);
			return;
		}

		// starting the thread to track the progress of the download ..
		if( myTimer == null )
		{
			myTimer = new Timer();
			myTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					//if all is done than just flush everything especially need to cancel timer, its required since its java :)
					//if (mDownloadIds.size() == 0)
					if (downloadIds == null || downloadIds.isEmpty())
					{
						if (myTimer != null)
						{
							myTimer.cancel();
							myTimer = null;
						}

						// turned of setting to null in real time other processes access download functions and null pointer error is huge threat!
						// mDownloadManager = null;
						// downloadIds = null;
						// downloadProgressBars = null;
						// downloadCallback = null;
						// mBroadCastReceiverMap = null;
						return;
					}

					synchronized (downloadIds)
					{
						for (final Map.Entry<String, Long> entry : downloadIds.entrySet())
						{
							final DownloadManager.Query q = new DownloadManager.Query();
							q.setFilterById(entry.getValue());
							Cursor cursor = mDownloadManager.query(q);

							//
							if (cursor == null)
							{
								continue;
							}

							cursor.moveToFirst();

							//
							if (cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) == -1 || cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) == -1) {
								continue;
							}

							//
							if (cursor.getColumnCount() == 0)
							{
								//
								//	8_1_2019   log.singleton().logError(im_application.singleton(), 1, "Download query cursor null size found");
								log.singleton().logError(context, 1, "Download query cursor null size found");

								continue;
							}

							long bytes_downloaded = 0;
							long bytes_total = 0;
							try
							{
								bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
								bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
								cursor.close();
							}
							catch (Exception e)
							{
								//	8_1_2019   log.singleton().logError(im_application.singleton(), 1, "Download query cursor index out of bound exception, turn of this error if its too frequent and there is no standard solution or required so", e);
								log.singleton().logError(context, 1, "Download query cursor index out of bound exception, turn of this error if its too frequent and there is no standard solution or required so", e);
								cursor.close();
								continue;
							}

							if(bytes_total <= 0)
							{
								bytes_total = 9000000;
							}

							long dl_progress = (bytes_downloaded * 100 / bytes_total);

							if (downloadHolderActivities.get(entry.getValue()) != null)
							{
								final long finalDl_progress = dl_progress;
								downloadHolderActivities.get(entry.getValue()).runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										if (finalDl_progress < 100)
										{
											if (downloadProgressBars.get(entry.getValue()) != null)
											{
												downloadProgressBars.get(entry.getValue()).setProgress((int) finalDl_progress);
											}
											else
											{
												//	8_1_2019	log.singleton().logError(im_application.singleton(), 1, "Download progressbar found null");
												log.singleton().logError(context, 1, "Download progressbar found null");
											}
										}
									}
								});
							}
						}
					}
				}
			}, 500, 500);
		}
	}

	public void unregisterDownloadConnections(String url)
	{
		String downloadKey = imui.createKeyFromUrl(url);

		if (downloadKey != null && downloadIds != null && downloadIds.containsKey(downloadKey))
		{
			Long key = downloadIds.get(downloadKey);

			if( key != null )
			{

				//   [downloadReceivedData removeObjectForKey:key];
				//	 mDownloadIds.remove(key);

				if( downloadHolderActivities != null )
					downloadHolderActivities.remove(key);

				if( downloadProgressBars != null )
					downloadProgressBars.remove(key);

				if( downloadCallback != null )
					downloadCallback.remove(key);

				//	downloadIds.remove(key);
				//	mBroadCastReceiverMap.remove(key);
				//	[downloadExpectedBytes removeObjectForKey:key];
				//	[downloadSavePath removeObjectForKey:key];
				//	[downloadCallbackDict removeObjectForKey:key];
			}
		}
	}

	private void postDownload (Context activityObj, File srcFile, File destFile, String filepath)
	{
		try
		{
			//	if (isFromMain)
			//	{
			//		session.singleton(activityObj).set_userdata("video_id", "");
			//		MainActivity.urlArrayIndex++;
			//	}

			file_helper.moveFile(activityObj, srcFile, destFile, filepath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public long addFileForDownloadInDownloadManager(Context context, String url, String savePath)
	{
        //	url = "http://www.vogella.de/img/lars/LarsVogelArticle7.png";

		Uri uri = Uri.parse(url);

		DownloadManager.Request request = new DownloadManager.Request(uri);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
		request.setDestinationUri(Uri.fromFile(new File(savePath)));

		// hiren commented
		// final DownloadManager m = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		// return m.enqueue(request);

		try
		{
			return mDownloadManager.enqueue(request);
		}

		catch (Exception e)
		{
			//	8_1_2019	log.singleton().logError(im_application.singleton(), 1, "download enque method", e);

			log.singleton().logError(context, 1, "download enque method", e);
			return 0;
		}
	}

	public void stopAllDownload()
	{

		Long key;

		// mapurl
		if (urlmap != null && downloadIds != null)
		{
			//	for ( Map.Entry< Long, String > entry : urlmap.entrySet())
			//	{
			//	   key = downloadIds.get(imui.createKeyFromUrl(entry.getValue()));
			//	   // stop dm
			//	   mDownloadManager.remove(key);
			//
			//	   // rempve connection
			//	   removeDownloadConnectionObjects(key, false);
			//
			//  }

			Iterator it = urlmap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<Long, String> entry = (Map.Entry<Long, String>)it.next();

				key = downloadIds.get(imui.createKeyFromUrl(entry.getValue()));

				//stop dm
				if( mDownloadManager != null )
					mDownloadManager.remove(key);

				// rempve connection
				removeDownloadConnectionObjects(key, false);

				//remove url from url map only now
				it.remove();
			}
		}

    //	mDownloadManager.remove();

	}
}