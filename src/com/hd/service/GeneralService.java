package com.hd.service;

import java.lang.reflect.Field;

import com.hd.network.NetWork;

public class GeneralService {
	private NetWork netWork;
	public GeneralService(NetWork netWork) {
		this.netWork = netWork;
	}
	
	//��ѭһ��һд����ֹճ��
	public <T>Object login(Class<T> clazz,String account,String pwd,int capacityId,String socketKey){
		//��ͨ�û����
		if(capacityId==1) {
			//���߷�������Ҫִ�е�¼
			netWork.send("��¼",socketKey);
			//�õ�����������
			String response1 = netWork.clientRead(socketKey);
			print("�������ɹ����յ�"+response1+"����");
			//ƴ�ӵ�¼��Ϣ
			String accountInfo = "[��ͨԱ��]"+account+","+pwd;
			//����¼��Ϣ���͸�������
			netWork.send(accountInfo,socketKey);
			//��ȡ��¼���
			String response2 = netWork.clientRead(socketKey);
			
			//��¼�ɹ��ͷ���һ����ǰԱ������
			if(response2.equals("��¼�ɹ�")) {
				Field field;
				try {
					Object obj = clazz.newInstance();
					//��ȡ��ͨԱ���ʺ��ֶ�
					field = clazz.getDeclaredField("employeeAccount");
					//��Ȩ��
					field.setAccessible(true);
					//�����ֶ�ֵ
					field.set(obj, account);
					//��ȡ��ͨԱ�������ֶ�
					field = clazz.getDeclaredField("employeePwd");
					//��Ȩ��
					field.setAccessible(true);
					//�����ֶ�ֵ
					field.set(obj, pwd);
					//���ط���ֵ
					return obj;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		//����Ա����û�
		else {
			//���߷�������Ҫִ�е�¼����
			netWork.send("��¼",socketKey);
			//�õ�����������
			String response1 = netWork.clientRead(socketKey);
			print("�������ɹ����յ�"+response1+"����");
			//ƴ�ӵ�¼��Ϣ
			String accountInfo = "[����Ա]"+account+","+pwd;
			//�����˺���Ϣ
			netWork.send(accountInfo,socketKey);
			//�õ���������������½�ɹ����
			String response2 = netWork.clientRead(socketKey);
			System.out.println(response2);
			//��¼�ɹ��򴴽���ǰ����Ա����
			if(response2.equals("��¼�ɹ�")) {
				Field field;
				try {
					Object obj = clazz.newInstance();
					//��ȡ����Աid�ֶ�
					field = clazz.getDeclaredField("adminId");
					//��Ȩ��
					field.setAccessible(true);
					//�����ֶ�ֵ
					field.set(obj, account);
					//��ȡ����Ա�����ֶ�
					field = clazz.getDeclaredField("adminPwd");
					//��Ȩ��
					field.setAccessible(true);
					//�����ֶ�ֵ
					field.set(obj, pwd);
					//���ع���Ա��������洦��
					return obj;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
			
		}
		return null;
	}
	
	//�Զ����ӡ����
	public void print(String str) {
		System.out.println("[YTG]"+str);
	}

}
