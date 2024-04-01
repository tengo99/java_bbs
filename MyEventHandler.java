package jdbc.bbs;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
//이벤트 핸들러 ==> Application Layer ==> Controller
// UI ===== Application Layer =====> Data Layer ===> DB
public class MyEventHandler implements ActionListener{
	
	private MyBoardApp gui;//View
	private MemberDAO userDao;//Model
	private BbsDAO bbsDao;//Model
	
	public MyEventHandler(MyBoardApp app) {
		this.gui=app;
		userDao=new MemberDAO();
		bbsDao=new BbsDAO();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
		if(obj==gui.btJoin) {//회원가입
			joinMember();
		}else if(obj==gui.btClear) {//지우기
			gui.clear1();
		}else if(obj==gui.btList) {//회원목록
			listMember();
		}else if(obj==gui.btDel) {//회원탈퇴
			removeMember();
		}else if(obj==gui.bbsWrite) {//게시판 글쓰기
			bbsWrite();
		}else if(obj==gui.btLogin) {//로그인 처리
			login();
		}else if(obj==gui.bbsList) {//게시판 글목록
			bbsList();
		}else if(obj==gui.bbsDel) {//게시글 삭제
			//로그인한 사람이 자신이 쓴 글만 삭제
			bbsDelete();
		}else if(obj==gui.bbsFind) {
			//title로 검색
			bbsFind();
		}
		
		
	}//-----------------
	
	private void bbsFind() {
		try {
			BbsVO bbs=bbsDao.findByTitle(gui.tfTitle.getText());
			gui.taList.append(bbs.getNo()+" "+bbs.getTitle()+" "+bbs.getWriter()+" "+bbs.getContent()+" "+bbs.getWdate()+"\n");
			 gui.tabbedPane.setSelectedIndex(3);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void bbsDelete() {
		String no=gui.tfNo.getText();
		try {
			int n=bbsDao.deleteBbs(no);
			String msg=(n>0)?"글 삭제가 완료되었습니다":"글 삭제에 실패하였습니다. no를 확인해주세요";
			gui.showMsg(msg);
			bbsList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void bbsList() {
		 try {
			 ArrayList<BbsVO> list=bbsDao.selectBbs();
			 for(BbsVO bbsList:list) {
				 gui.taList.append(bbsList.getNo()+" "+bbsList.getTitle()+" "+bbsList.getWriter()+" "+bbsList.getContent()+" "+bbsList.getWdate()+"\n");
			 }
			 gui.tabbedPane.setSelectedIndex(3);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}


	private void bbsWrite() {
		 int no=Integer.parseInt(gui.tfNo.getText());
		 String title=gui.tfTitle.getText();
		 String writer=gui.tfWriter.getText();
		 String content=gui.taContent.getText();
		 
		 if(title==null||writer==null||content==null||title.trim().isEmpty()||writer.trim().isEmpty()||content.trim().isEmpty()) {
			 gui.showMsg("모든 항목을 빠짐없이 입력하세요");
			 gui.tfNo.requestFocus();
			 return;
		 }
		 
		 BbsVO writerVO=new BbsVO(no, title, writer, content, null);
		 try { 
		   int n=bbsDao.insertBbs(writerVO);
		   if(n>0) {
			   gui.showMsg(writer+"님 글이 등록되었습니다");
		   }else {
			gui.showMsg("글쓰기에 실패하였습니다.");
		   }
		 }catch (SQLException e) {
				gui.showMsg(e.getMessage());
				System.out.println(e.getMessage());
		 }
	}


	private void login() {
		//id,pw값 받기
		String id=gui.loginId.getText();
		char[] ch=gui.loginPwd.getPassword();
		String pw=new String(ch);
		//유효성 체크
		if(id==null || ch==null || id.trim().isEmpty() || pw.trim().isEmpty()) {
			gui.showMsg("로그인 아이디와 비밀번호를 입력하세요");
			gui.loginId.requestFocus();
			return;
		}
		try {
		//userDao의 loginCheck(id,pw) 호출
		int result=userDao.loginCheck(id, pw);
		System.out.println("result: "+result);
		
		if(result>0) {
			//결과값이 1이면 로그인 성공
			gui.showMsg(id+"님 환영합니다");
			gui.tabbedPane.setEnabledAt(2,true);//게시판 탭 활성화
			gui.tabbedPane.setEnabledAt(3, true);
			gui.setTitle(id+"님 로그인 중...");
			gui.tfWriter.setText(id);
			gui.tabbedPane.setSelectedIndex(3);
		}else {
		//음수값이면 로그인 실패
			gui.showMsg("아이디 또는 비밀번호가 일치하지 않습니다");
			gui.tabbedPane.setEnabledAt(2,false);//게시판 탭 비활성화
			gui.tabbedPane.setEnabledAt(3, false);
		}
		}catch(SQLException e) {
			gui.showMsg(e.getMessage());
		}
	}//-----------------------------------------


	private void listMember() {
		try {
			//userDao.selectAll() 호출
			ArrayList<MemberVO> userList=userDao.selectAll();
			//반환받을 ArrayList에서 회원정보 꺼내서 taMembers에 출력
			gui.showMembers(userList);
		}catch(SQLException e) {
			gui.showMsg(e.getMessage());
		}
	}//-----------------------------


	private void removeMember() {
		//1. 입력한 id값 받기
		String delId=gui.tfId.getText();
		//2.  유효성 체크
		if(delId==null || delId.trim().isEmpty()) {
			gui.showMsg("탈퇴할 회원의 ID를 입력하세요");
			gui.tfId.requestFocus();
			return;
		}
		//3.userDao의 deleteMember(id) 호출
		try {
			int n=userDao.deleteMember(delId.trim());
		//4. 그 결과 메시지 처리
			String msg=(n>0)? "회원탈퇴 완료!!" : "탈퇴 실패-없는 ID입니다";
			gui.showMsg(msg);
			
			if(n>0) {
				gui.tabbedPane.setEnabledAt(2,false);
				gui.tabbedPane.setEnabledAt(3,false);
				gui.clear1();
				gui.tabbedPane.setSelectedIndex(0);
			}
		}catch(SQLException e) {
			gui.showMsg(e.getMessage());
		}
	}//------------------------------------------------------------------


	private void joinMember() {
		//1. 입력값 받기
		String id=gui.tfId.getText();
		String name=gui.tfName.getText();
		String pw=gui.tfPw.getText();
		String tel=gui.tfTel.getText();
		//2. 유효성 체크(id,pw,name)
		if(id==null || name==null || pw==null || id.trim().isEmpty() || name.trim().isEmpty() || pw.trim().isEmpty()) {
			gui.showMsg("ID,Name,Password는 필수 입력사항입니다");
			gui.tfId.requestFocus();
			return;
		}
		//3. 입력값들을 MemberVO 객체에 담아주기
		MemberVO user=new MemberVO(id,pw,name,tel,null);
		//4. userDao의 insertMember()호출
		try {
			int n=userDao.insertMember(user);
		//5. 결과에 따른 메시지 처리
			String msg=(n>0)? "회원가입 완료-로그인으로 이동합니다":"회원가입 실패";
			gui.showMsg(msg);
			if(n>0) {
				gui.tabbedPane.setSelectedIndex(0);//로그인 탭 선택
				gui.clear1();
			}
		}catch(SQLException e) {
			gui.showMsg("아이디는 이미 사용 중 입니다: " + e.getMessage());
		}
	}//--------------------
	
}//////////////////////////////////////////

