package message;



public interface MsgCallBack {
	//------�ͻ����������----
	
	//����������״̬
	public void onCreateServer(boolean ret);
	
	//����socket״̬
	public void onAccept(String ip,int port);
	
	//���ӷ�����״̬
	public void onConnecteServer(boolean ret,String myScoketId);
	
	
	
	//��ͨԱ��
	
	//��ͨԱ���ϰ�򿨳ɹ�
	public void workUpSuccess(boolean ret,String info);
	
	//��ͨԱ���ϰ��ʧ��
	public void workUpOnFailed(String str);
	
	//��ͨԱ���°�򿨳ɹ�
	public void workDownSuccess(boolean ret,String info);
	
	//��ͨԱ���°��ʧ��
	public void workDownOnFailed(String str);
	
	//����Ա
	
	//���Ա���ɹ�
	public void addEmployeeSuccess(String str);
	
	//���Ա��ʧ��
	public void addEmployeeFalied(String str);

}
