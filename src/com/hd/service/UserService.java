package com.hd.service;

import com.hd.bean.Employee;
import com.hd.network.NetWork;

import message.MsgCallBack;

public class UserService {
	private MsgCallBack msgCallBack;
	private NetWork netWork;

	public UserService(NetWork netWork, MsgCallBack msgCallBack) {
		this.netWork = netWork;
		this.msgCallBack = msgCallBack;
	}
	
	//�ϰ��
	public void workUp(Employee employee,String socketKey) {
		// ������������ϰ��ָ��
		netWork.send("�ϰ��",socketKey);
		String response1 = netWork.clientRead(socketKey);
		print("�������ɹ����յ�" + response1 + "����");
		// ���Ա�����˺�������Ϊ���ݿ����������
		String e_account = employee.getEmployeeAccount();
		// ���䷢�͸�������
		netWork.send(e_account,socketKey);
		// ��ȡ�򿨽��
		String response2 = netWork.clientRead(socketKey);
		// ���ݷ��ؽ���Ĳ�ͬ������Ӧ����
		if (response2.equals("��Ϣ��")) {
			msgCallBack.workUpOnFailed(response2);
		} else if (response2.equals("����")) {
			msgCallBack.workUpOnFailed(response2);
		} else if (response2.contains("�򿨳ɹ�")) {
			msgCallBack.workUpSuccess(true, response2);
		} else if (response2.contains("��ʧ��")) {
			msgCallBack.workUpOnFailed(response2);
		} else if (response2.contains("�Ѿ������")) {
			msgCallBack.workUpOnFailed(response2);
		} else if(response2.contains("�°��")){
			msgCallBack.workUpOnFailed(response2);
		}
	}
	
	//�°��
	public void workDown(Employee employee,String socketKey) {
		// ������������ϰ��ָ��
		netWork.send("�°��",socketKey);
		String response1 = netWork.clientRead(socketKey);
		print("�������ɹ����յ�" + response1 + "����");
		// ���Ա�����˺�������Ϊ���ݿ����������
		String e_account = employee.getEmployeeAccount();
		// ���䷢�͸�������
		netWork.send(e_account,socketKey);
		// ��ȡ�򿨽��
		String response2 = netWork.clientRead(socketKey);
		// ���ݷ��ؽ���Ĳ�ͬ������Ӧ����
		if(response2.contains("�Ѿ�����°࿨")) {
			msgCallBack.workDownOnFailed(response2);
		}else if(response2.contains("�°�򿨳ɹ�")) {
			msgCallBack.workDownSuccess(true, response2);
		}else if(response2.contains("��ʧ��")) {
			msgCallBack.workDownOnFailed(response2);
		}else if(response2.contains("ʮһ��ǰ")) {
			msgCallBack.workDownOnFailed(response2);
		}else if(response2.contains("��Ϣ��")) {
			msgCallBack.workDownOnFailed(response2);
		}
	}
	
	//�鿴�Լ��Ŀ���ͳ��
	public void viewMyInfo(String selectDate,Employee employee,String socketKey) {
		//�������������������ָ��
		netWork.send("�鿴�ҵĿ��ڼ�¼", socketKey);
		//���շ���������
		String response1 = netWork.clientRead(socketKey);
		print("�������ɹ����յ�" + response1 + "����");
		//��ȡԱ���˺�
		String e_account = employee.getEmployeeAccount();
		//���˺����ѯ���ںϲ�
		String sendInfo = e_account+","+selectDate;
		//����Ϣ���͸�������
		netWork.send(sendInfo, socketKey);
		//���շ������Ĳ�ѯ����
		String response2 = netWork.clientRead(socketKey);
		if(response2.contains("��ѯʧ��")) {
			print(response2);
		}else if(response2.contains("��ѯ�ɹ�")) {
			String[] arr = response2.split(",");
			print(arr[0]);
			System.out.println("id         ����                               Ա��id  �ϰ��ʱ��   ״̬          �°��ʱ��       ״̬");
			for (int i = 1; i < arr.length; i++) {
				System.out.println(arr[i]);
			}
		}
	}

	public void exit() {

	}

	// �Զ����ӡ����
	public void print(String str) {
		System.out.println("[YTG]" + str);
	}
}
