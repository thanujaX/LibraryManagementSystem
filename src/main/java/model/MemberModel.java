package model;

import dto.MemberDTO;
import com.library.Util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MemberModel {
    public boolean saveMember(MemberDTO dto) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "INSERT INTO member (member_id, full_name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, dto.getMemberId());
        pst.setString(2, dto.getFullName());
        pst.setString(3, dto.getEmail());
        pst.setString(4, dto.getPhone());
        pst.setString(5, dto.getAddress());
        return pst.executeUpdate() > 0;
    }
    public List<MemberDTO> getAllMembers() throws Exception {
        List<MemberDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT member_id, full_name, email, phone, address FROM member");
        while (rs.next()) {
            list.add(new MemberDTO(rs.getString("member_id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone"), rs.getString("address")));
        }
        return list;
    }
    public boolean updateMember(MemberDTO dto) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "UPDATE member SET full_name=?, email=?, phone=?, address=? WHERE member_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, dto.getFullName());
        pst.setString(2, dto.getEmail());
        pst.setString(3, dto.getPhone());
        pst.setString(4, dto.getAddress());
        pst.setString(5, dto.getMemberId());
        return pst.executeUpdate() > 0;
    }
    public boolean deleteMember(String memberId) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement pst = conn.prepareStatement("DELETE FROM member WHERE member_id=?");
        pst.setString(1, memberId);
        return pst.executeUpdate() > 0;
    }
    public int getTotalMembersCount() throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM member");
        if (rs.next()) return rs.getInt(1);
        return 0;
    }
}