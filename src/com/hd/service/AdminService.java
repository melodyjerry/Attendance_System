package com.hd.service;

import com.hd.bean.Employee;
import com.hd.network.NetWork;

import message.MsgCallBack;

public class AdminService {
	private NetWork netWork;
	private MsgCallBack msgCallBack;
	
	public AdminService(NetWork netWork,MsgCallBack msgCallBack) {
		this.netWork = netWork;
		this.msgCallBack = msgCallBack;
	}
	
	//���Ա��
	public void addEmployee(Employee employee,String socketKey) {
		netWork.send("���Ա����Ϣ",socketKey);
		String response1 = netWork.clientRead(socketKey);
		print("�������ɹ����յ�"+response1+"����");
		String e_info = employee.toString();
		netWork.send(e_info, socketKey);
		String response2 = netWork.clientRead(socketKey);
		if(response2.contains("Ա��id�Ѿ���ʹ��")) {
			msgCallBack.addEmployeeFalied(response2);
		}else if(response2.contains("Ա���˺��Ѿ���ʹ��")) {
			msgCallBack.addEmployeeFalied(response2);
		}else if(response2.contains("�ɹ�")) {
			msgCallBack.addEmployeeSuccess(response2);
		}else if(response2.contains("ʧ��")) {
			msgCallBack.addEmployeeFalied(response2);
		}else if(response2.contains("���ݸ�ʽ")) {
			msgCallBack.addEmployeeFalied(response2);
		}
	}
	
	//�鿴ĳԱ���Ŀ���ͳ��
	public void viewAllInfo(String e_name,String month,String socketKey) {
		//�������������������ָ��
		netWork.send("�鿴Ա������ͳ��", socketKey);
		//���շ���������
		String response1 = netWork.clientRead(socketKey);
		print("�������ɹ����յ�" + response1 + "����");;
		//���˺����ѯ���ںϲ�
		String sendInfo = e_name+","+month;
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
	
	public void exit(boolean isRun) {
	
	}

	public void print(String str) {
		System.out.println("[YTG]"+str);
	}
}
