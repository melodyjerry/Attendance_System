package com.hd.main;

import com.hd.network.NetWork;

import message.Base;

//�������п���ϵͳ������
public class ServerMain extends Base {
	private NetWork netWork;
	
	
	public static void main(String[] args) {
		ServerMain serverMain = new ServerMain();
		serverMain.start();
	}
	
	public void start() {
		netWork = new NetWork(this);
		netWork.statrServer(6666);
	}

	@Override
	public void onCreateServer(boolean ret) {
		String msg = ret?"�����ɹ�":"����ʧ��";
		print(msg);
	}

	@Override
	public void onAccept(String ip, int port) {
		print(ip+":"+port+"������");
	}
	
	public void print(String str) {
		System.out.println("[YTG������]"+str);
	}
	
	
}
