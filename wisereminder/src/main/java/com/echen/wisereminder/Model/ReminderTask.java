package com.echen.wisereminder.Model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by echen on 2015/9/29.
 */
public class ReminderTask extends Task {
    private int NOTIFICATION_ID = 1;

    public ReminderTask(Context context)
    {
        super(context);
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
    }

    private PendingIntent getDefaultIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(m_context, 1, new Intent(), flags);
        return pendingIntent;
    }
}
