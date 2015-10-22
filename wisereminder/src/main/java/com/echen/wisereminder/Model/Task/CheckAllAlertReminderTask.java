package com.echen.wisereminder.Model.Task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.AppNotificationActivity;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Model.Subject;
import com.echen.wisereminder.R;

import java.util.List;

/**
 * Created by echen on 2015/9/29.
 */
public class CheckAllAlertReminderTask extends Task {
    private int NOTIFICATION_ID = 1;

    public CheckAllAlertReminderTask(Context context) {
        super(context);
        m_type = TaskType.CheckReminder;
    }

    @Override
    public void doWork() throws InterruptedException {
//    NotificationManager notificationManager = (NotificationManager)m_context.getSystemService(Context.NOTIFICATION_SERVICE);
//    NotificationCompat.Builder builder = new NotificationCompat.Builder(m_context);
//        builder.setContentTitle("���Ա���")//����֪ͨ������
//                    .setContentText("��������") //����֪ͨ����ʾ����
//                    .setContentIntent(getDefaultIntent(Notification.FLAG_AUTO_CANCEL)) //����֪ͨ�������ͼ
//                            //  .setNumber(number) //����֪ͨ���ϵ�����
//                    .setTicker("����֪ͨ����") //֪ͨ�״γ�����֪ͨ��������������Ч����
//                    .setWhen(System.currentTimeMillis())//֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
//                    .setPriority(Notification.PRIORITY_DEFAULT) //���ø�֪ͨ���ȼ�
//                            //  .setAutoCancel(true)//���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��
//                    .setOngoing(false)//ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
//                    .setDefaults(Notification.DEFAULT_VIBRATE)//��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
//                            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND ������� // requires VIBRATE permission
//                    .setSmallIcon(R.drawable.ic_launcher);//����֪ͨСICON
//        notificationManager.notify(NOTIFICATION_ID, builder.build());

        List<Reminder> overdueReminders = DataManager.getInstance().getRemindersBySubject(Subject.Type.Overdue, false);
        List<Reminder> todayReminders = DataManager.getInstance().getRemindersBySubject(Subject.Type.Today, false);
        String strMessageSource = "";
        String strMessage = "";
        if (overdueReminders.size() == 0 && todayReminders.size() == 0)
        {
            return;
        }
        else if (overdueReminders.size() > 0 && todayReminders.size() > 0)
        {
            strMessageSource = m_context.getString(R.string.notification_task_both);
            strMessage = String.format(strMessageSource, todayReminders.size(), overdueReminders.size());
        }
        else
        {
            if (overdueReminders.size() > 0)
            {
                strMessageSource = m_context.getString(R.string.notification_task_overdue);
                strMessage = String.format(strMessageSource,overdueReminders.size());
            }
            else
            {
                strMessageSource = m_context.getString(R.string.notification_task_today);
                strMessage = String.format(strMessageSource,todayReminders.size());
            }
        }

        NotificationManager notificationManager = (NotificationManager) m_context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(m_context);
        RemoteViews view_custom = new RemoteViews(m_context.getPackageName(), R.layout.notification_tasktodo);
        view_custom.setImageViewResource(R.id.notifyIcon, R.drawable.img_calendar);
        view_custom.setTextViewText(R.id.txtTitle, strMessage);

        builder.setContent(view_custom)
                .setContentIntent(getDefaultIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setTicker("Task reminder")
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notify = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notify);

    }

    private PendingIntent getDefaultIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(m_context, 1, new Intent(m_context, AppNotificationActivity.class), flags);
        return pendingIntent;
    }
}
