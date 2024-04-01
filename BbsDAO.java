package jdbc.bbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//게시판 관련 crud 수행 => data layer
public class BbsDAO {
	
	private Connection con;
	private PreparedStatement ps;
	private ResultSet rs;
	
	
	public BbsVO findByTitle(String title) throws SQLException{
		try {
			con=DBUtil.getCon();
			String sql="select * from bbs where title=?";
			ps=con.prepareStatement(sql);
			ps.setString(1, title);
			rs=ps.executeQuery();
			
			ArrayList<BbsVO> arr=makeList(rs);
			if(arr!=null && arr.size()==1) {
				BbsVO bbs=arr.get(0);
				return bbs;//해당 id회원정보 반환
			}
			return null;//해당 id가 없는 경우
		}finally {
			close();
		}
	}//---
	
	/*회원탈퇴 처리 - D(DELETE)*/
	public int deleteBbs(String no) throws SQLException{
		try {
			con=DBUtil.getCon();
			String sql="delete from bbs where no=?";
			
			ps=con.prepareStatement(sql);
			ps.setString(1, no);
			
			int n=ps.executeUpdate();			
			return n;
		}finally {
			close();
		}
	}
	
	//게시글 쓰기 (시퀀스 - Bbs_seq)
	public int insertBbs(BbsVO vo) throws SQLException {
		try {
		con=DBUtil.getCon();
		String sql="insert into bbs values(bbs_no_seq.nextval,?,?,?,sysdate)";
		
		ps=con.prepareStatement(sql);
		
		ps.setString(1, vo.getTitle());
		ps.setString(2, vo.getWriter());
		ps.setString(3, vo.getContent());
		
		int n=ps.executeUpdate();
		return n;
		}finally {
			close();
		}
	}
	
	public ArrayList<BbsVO> selectBbs() throws SQLException{
		try {
			con=DBUtil.getCon();
			String sql="select * from bbs";
			ps=con.prepareStatement(sql);
			rs=ps.executeQuery();
			return makeList(rs);
		} finally {
			close();
		}
	}
	
	
	public ArrayList<BbsVO> makeList(ResultSet rs) throws SQLException {
		ArrayList<BbsVO> arr=new ArrayList<BbsVO>();
		while(rs.next()) {
			int no=rs.getInt("no");
			String title=rs.getString("title");
			String writer=rs.getString("writer");
			String contet=rs.getString("content");
			java.sql.Date date=rs.getDate("wdate");
			BbsVO record=new BbsVO(no, title, writer, contet, date);
			arr.add(record);
		}
		return arr;
	}

	public void close() {
		try {
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
			if(con!=null) con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//------------------------------------------
	
}/////////////////////////////////
